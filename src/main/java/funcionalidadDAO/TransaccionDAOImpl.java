package funcionalidadDAO;

import entity.ShippingStatus;
import entity.Transaccion;
import entity.Empresa;
import interfaceDAO.TransaccionDAO;
import jakarta.persistence.*;
import utils.JPAUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TransaccionDAOImpl implements TransaccionDAO {

    @Override
    public void guardar(Transaccion transaccion, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Asumir que el estado inicial es una constante o se calcula de alguna forma
            ShippingStatus estadoInicial = ShippingStatus.IN_PROGRESS;  // Ejemplo de estado inicial

            if (transaccion.getEstadoEnvio() == null) {
                transaccion.setEstadoEnvio(estadoInicial);
            }

            if (transaccion.getId() == null) {
                Empresa empresa = em.find(Empresa.class, empresaId);  // Solo buscar si es necesario
                if (empresa == null) {
                    throw new IllegalStateException("La empresa con ID " + empresaId + " no existe.");
                }
                transaccion.setEmpresa(empresa);
                em.persist(transaccion);
            } else {
                em.merge(transaccion);
            }
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar la transacción: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Transaccion encontrar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Transaccion transaccion = em.find(Transaccion.class, id);
            if (transaccion != null && transaccion.getEmpresa().getId().equals(empresaId)) {
                return transaccion;
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Transaccion transaccion, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (!transaccion.getEmpresa().getId().equals(empresaId)) {
                throw new SecurityException("Operación no permitida: la transacción no pertenece a la empresa indicada.");
            }
            em.merge(transaccion);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar la transacción: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Transaccion transaccion = em.find(Transaccion.class, id);
            if (transaccion != null && transaccion.getEmpresa().getId().equals(empresaId)) {
                em.remove(transaccion);
            } else {
                throw new SecurityException("Operación no permitida: la transacción no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar la transacción: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    public List<Transaccion> obtenerTodos(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Transaccion t JOIN FETCH t.cliente WHERE t.empresa.id = :empresaId";
            return em.createQuery(jpql, Transaccion.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Método para obtener datos para el pie chart de estado de envío
    public List<Object[]> obtenerDatosParaPieChart(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT t.estadoEnvio, COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId GROUP BY t.estadoEnvio";
            List<Object[]> results = em.createQuery(jpql, Object[].class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
            return results;
        } finally {
            em.close();
        }
    }

    public static BigDecimal calculateRateOfReturn(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId AND t.tipo = 'Return'";
            long returnsCount = (long) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();

            jpql = "SELECT COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId";
            long totalCount = (long) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();

            return totalCount > 0 ? BigDecimal.valueOf(returnsCount).divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public static BigDecimal calculatePerfectOrderRate(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId AND t.esPerfecto = true";
            long perfectOrdersCount = (long) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();

            jpql = "SELECT COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId";
            long totalCount = (long) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();

            return totalCount > 0 ? BigDecimal.valueOf(perfectOrdersCount).divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    public static BigDecimal calculateLeadTime(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT AVG(DATEDIFF(t.fechaEntrega, t.fechaPedido)) FROM Transaccion t WHERE t.empresa.id = :empresaId AND t.tipo = 'Compra'";
            Double averageLeadTime = (Double) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();
            return (averageLeadTime != null) ? BigDecimal.valueOf(averageLeadTime).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public static BigDecimal calculateServiceLevel(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId AND t.esPerfecto = true AND t.tipo = 'Venta'";
            long perfectSalesCount = (long) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();

            jpql = "SELECT COUNT(t) FROM Transaccion t WHERE t.empresa.id = :empresaId AND t.tipo = 'Venta'";
            long totalSalesCount = (long) em.createQuery(jpql)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();

            return totalSalesCount > 0 ? BigDecimal.valueOf(perfectSalesCount).divide(BigDecimal.valueOf(totalSalesCount), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

}



