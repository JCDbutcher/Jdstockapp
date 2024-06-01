package funcionalidadDAO;

import entity.Proveedor;
import entity.Empresa;
import interfaceDAO.ProveedorDAO;
import jakarta.persistence.EntityManager;
import utils.JPAUtil;

import java.util.List;

public class ProveedorDAOImpl implements ProveedorDAO {

    @Override
    public void guardar(Proveedor proveedor, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null) {
                throw new IllegalStateException("Empresa con ID " + empresaId + " no existe.");
            }
            proveedor.setEmpresa(empresa);  // Asegurar que el proveedor está asociado a la empresa correcta.

            if (proveedor.getId() == null) {
                em.persist(proveedor);
            } else {
                if (!em.contains(proveedor)) {
                    proveedor = em.merge(proveedor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar el proveedor", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Proveedor encontrar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null && proveedor.getEmpresa().getId().equals(empresaId)) {
                return proveedor;
            }
            return null; // Retorna null si el proveedor no pertenece a la empresa especificada
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Proveedor proveedor, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null || !proveedor.getEmpresa().getId().equals(empresaId)) {
                throw new SecurityException("Operación no permitida: el proveedor no pertenece a la empresa indicada.");
            }
            em.merge(proveedor);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar el proveedor", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null && proveedor.getEmpresa().getId().equals(empresaId)) {
                em.remove(proveedor);
            } else {
                throw new SecurityException("Operación no permitida: el proveedor no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar el proveedor", e);
        } finally {
            em.close();
        }
    }
    public static List<Proveedor> obtenerTodosLosProveedores(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Proveedor p WHERE p.empresa.id = :empresaId";
            return em.createQuery(jpql, Proveedor.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
        } finally {
            em.close();
        }
}

    public List<Proveedor> buscarPorNombre(String nombre, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Proveedor p WHERE p.empresa.id = :empresaId AND lower(p.nombre) LIKE lower(:nombre)";
            List<Proveedor> proveedores = em.createQuery(jpql, Proveedor.class)
                    .setParameter("empresaId", empresaId)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
            return proveedores;
        } finally {
            em.close();
        }
    }
}
