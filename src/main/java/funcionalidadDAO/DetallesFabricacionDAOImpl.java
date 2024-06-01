package funcionalidadDAO;

import entity.DetalleTransaccion;
import entity.DetallesFabricacion;
import entity.DetallesFabricacionId;
import entity.Fabricacion;
import interfaceDAO.DetallesFabricacionDAO;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import utils.JPAUtil;

import java.util.List;

public class DetallesFabricacionDAOImpl implements DetallesFabricacionDAO {

    public void guardar(DetallesFabricacion detalles, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Merge both the Fabricacion and Producto if they are detached.
            if (!em.contains(detalles.getFabricacion())) {
                detalles.setFabricacion(em.merge(detalles.getFabricacion()));
            }
            if (!em.contains(detalles.getComponente())) {
                detalles.setComponente(em.merge(detalles.getComponente()));
            }

            if (detalles.getId() == null) {
                em.persist(detalles);
            } else {
                detalles = em.merge(detalles);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar los detalles de fabricación", e);
        } finally {
            em.close();
        }
    }
    public void actualizar(DetallesFabricacion detallesFabricacion, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(detallesFabricacion);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    @Override
    public DetallesFabricacion encontrar(DetallesFabricacionId id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            DetallesFabricacion detalles = em.find(DetallesFabricacion.class, id);
            if (detalles != null && detalles.getFabricacion().getEmpresa().getId().equals(empresaId)) {
                return detalles;
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(DetallesFabricacion detalles, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            DetallesFabricacion managedDetalles = em.find(DetallesFabricacion.class, detalles.getId());
            if (managedDetalles != null && managedDetalles.getFabricacion().getEmpresa().getId().equals(empresaId)) {
                em.remove(managedDetalles);
            } else {
                throw new SecurityException("Operación no permitida: Detalles de fabricación no asociados con la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar los detalles de fabricación", e);
        } finally {
            em.close();
        }
    }
    public List<DetallesFabricacion> buscarPorFabricacion(Long fabricacionId, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT df FROM DetallesFabricacion df " +
                    "JOIN FETCH df.componente c " +
                    "WHERE df.fabricacion.id = :fabricacionId AND df.fabricacion.empresa.id = :empresaId";
            return em.createQuery(jpql, DetallesFabricacion.class)
                    .setParameter("fabricacionId", fabricacionId)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public List<DetallesFabricacion> obtenerTodos(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT df FROM DetallesFabricacion df JOIN df.fabricacion f WHERE f.empresa.id = :empresaId", DetallesFabricacion.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

}
