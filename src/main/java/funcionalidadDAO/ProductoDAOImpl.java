package funcionalidadDAO;

import entity.Producto;
import entity.Empresa;
import interfaceDAO.ProductoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.Hibernate;
import utils.JPAUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public void guardar(Producto producto, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null) {
                throw new IllegalStateException("Empresa con ID " + empresaId + " no existe.");
            }
            producto.setEmpresa(empresa);  // Asegurar que el producto está asociado a la empresa correcta.

            em.persist(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar el producto", e);
        } finally {
            em.close();
        }
    }
    public List<Producto> obtenerProductosPorEstadoStock(Long empresaId, int umbralBajo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String queryStr = "SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND (p.cantidadEnStock <= :umbralBajo OR p.cantidadEnStock = 0 OR (p.esPerecedero = true AND p.fechaCaducidad <= :fechaProxima))";
            LocalDate fechaProxima = LocalDate.now().plusDays(30); // Define productos con caducidad en los próximos 30 días como "Dead Stock"
            List<Producto> productos = em.createQuery(queryStr, Producto.class)
                    .setParameter("empresaId", empresaId)
                    .setParameter("umbralBajo", umbralBajo)
                    .setParameter("fechaProxima", fechaProxima)
                    .getResultList();
            return productos;
        } finally {
            em.close();
        }
    }

    @Override
    public Producto encontrar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto != null && producto.getEmpresa().getId().equals(empresaId)) {
                return producto;
            }
            return null; // Retorna null si el producto no pertenece a la empresa especificada
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Producto producto, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null || !producto.getEmpresa().getId().equals(empresaId)) {
                throw new SecurityException("Operación no permitida: el producto no pertenece a la empresa indicada.");
            }
            em.merge(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar el producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null && producto.getEmpresa().getId().equals(empresaId)) {
                em.remove(producto);
            } else {
                throw new SecurityException("Operación no permitida: el producto no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar el producto", e);
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerTodos(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Utilizando LEFT JOIN FETCH para manejar casos donde la categoría o el proveedor puedan ser nulos
            String jpql = "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.empresa.id = :empresaId";
            List<Producto> productos = em.createQuery(jpql, Producto.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
            // No es necesario inicializar aquí ya que LEFT JOIN FETCH hace el trabajo de inicialización
            em.getTransaction().commit();
            return productos;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Propaga la excepción para manejarla más arriba en la cadena de llamadas si es necesario
        } finally {
            em.close(); // Asegura que el EntityManager se cierre para evitar fugas de recursos
        }
    }

// Metodos para estadisticas
    public List<Object[]> obtenerDatosParaPieChartInventario(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String queryStr = "SELECT p.cantidadEnStock, p.fechaCaducidad FROM Producto p WHERE p.empresa.id = :empresaId";
            return em.createQuery(queryStr, Object[].class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    public static BigDecimal calculateAverageInventory(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double result = em.createQuery("SELECT AVG(p.cantidadEnStock) FROM Producto p WHERE p.empresa.id = :empresaId", Double.class)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();
            return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public static BigDecimal calculateCostOfGoodsSold(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String queryStr = "SELECT SUM(d.cantidad * p.precio) FROM DetalleTransaccion d JOIN d.producto p WHERE p.empresa.id = :empresaId";
            BigDecimal cogs = (BigDecimal) em.createQuery(queryStr)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();
            return cogs != null ? cogs : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    public static BigDecimal calculateInventoryTurnoverRatio(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BigDecimal averageInventory = calculateAverageInventory(empresaId);
            BigDecimal cogs = calculateCostOfGoodsSold(empresaId);
            return (averageInventory.compareTo(BigDecimal.ZERO) != 0) ? cogs.divide(averageInventory, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public static BigDecimal calculateDaysToSellInventory(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BigDecimal inventoryTurnoverRatio = calculateInventoryTurnoverRatio(empresaId);
            return (inventoryTurnoverRatio.compareTo(BigDecimal.ZERO) != 0) ? BigDecimal.valueOf(365).divide(inventoryTurnoverRatio, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    public List<Producto> obtenerProductosPorEstadoStock(Long empresaId, int umbralBajo, int diasProximos) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDate fechaProxima = LocalDate.now().plusDays(diasProximos); // Define productos con caducidad en los próximos días como "Dead Stock"
            String queryStr = """
                SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND
                (p.cantidadEnStock <= :umbralBajo OR
                p.cantidadEnStock = 0 OR
                (p.esPerecedero = true AND p.fechaCaducidad <= :fechaProxima))
            """;
            List<Producto> productos = em.createQuery(queryStr, Producto.class)
                    .setParameter("empresaId", empresaId)
                    .setParameter("umbralBajo", umbralBajo)
                    .setParameter("fechaProxima", fechaProxima)
                    .getResultList();
            return productos;
        } finally {
            em.close();
        }
    }
}


