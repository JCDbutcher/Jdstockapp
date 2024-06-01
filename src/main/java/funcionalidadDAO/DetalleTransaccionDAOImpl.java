package funcionalidadDAO;

import entity.DetalleTransaccion;
import entity.Transaccion;
import interfaceDAO.DetalleTransaccionDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import utils.JPAUtil;

import java.util.List;

public class DetalleTransaccionDAOImpl implements DetalleTransaccionDAO {

    @Override
    public void guardar(DetalleTransaccion detalleTransaccion, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Verifica si la transacción asociada pertenece a la empresa indicada
            Transaccion transaccion = em.find(Transaccion.class, detalleTransaccion.getTransaccion().getId());
            if (transaccion == null || !transaccion.getEmpresa().getId().equals(empresaId)) {
                throw new SecurityException("Transacción no pertenece a la empresa indicada.");
            }
            em.persist(detalleTransaccion);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar el detalle de transacción", e);
        } finally {
            em.close();
        }
    }


    @Override
    public void actualizar(DetalleTransaccion detalleTransaccion, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Transaccion transaccion = em.find(Transaccion.class, detalleTransaccion.getTransaccion().getId());
            if (transaccion == null || !transaccion.getEmpresa().getId().equals(empresaId)) {
                throw new SecurityException("Transacción no pertenece a la empresa indicada.");
            }
            em.merge(detalleTransaccion);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar el detalle de transacción", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            DetalleTransaccion detalle = em.find(DetalleTransaccion.class, id);
            if (detalle != null && detalle.getTransaccion().getEmpresa().getId().equals(empresaId)) {
                em.remove(detalle);
            } else {
                throw new SecurityException("Detalle de transacción no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar el detalle de transacción", e);
        } finally {
            em.close();
        }
    }

    public List<DetalleTransaccion> buscarPorTransaccion(Long transaccionId, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM DetalleTransaccion d JOIN FETCH d.producto WHERE d.transaccion.id = :transaccionId AND d.transaccion.empresa.id = :empresaId";
            List<DetalleTransaccion> details = em.createQuery(jpql, DetalleTransaccion.class)
                    .setParameter("transaccionId", transaccionId)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
            return details;
        } finally {
            em.close();
        }
    }


    public List<DetalleTransaccion> buscarTodosPorEmpresa(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM DetalleTransaccion d WHERE d.transaccion.empresa.id = :empresaId";
            TypedQuery<DetalleTransaccion> query = em.createQuery(jpql, DetalleTransaccion.class);
            query.setParameter("empresaId", empresaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    public DetalleTransaccion encontrar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM DetalleTransaccion d "
                    + "JOIN FETCH d.transaccion "
                    + "JOIN FETCH d.producto "
                    + "WHERE d.id = :id AND d.transaccion.empresa.id = :empresaId";
            return em.createQuery(jpql, DetalleTransaccion.class)
                    .setParameter("id", id)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }



}



