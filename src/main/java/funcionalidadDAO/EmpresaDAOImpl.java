package funcionalidadDAO;

import entity.Empresa;
import interfaceDAO.EmpresaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import utils.JPAUtil;


public class EmpresaDAOImpl implements EmpresaDAO {

    @Override
    public void guardar(Empresa empresa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(empresa);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar la empresa", e);
        } finally {
            em.close();
        }
    }
    public Long encontrarIdPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT e.id FROM Empresa e WHERE e.nombre = :nombre", Long.class);
            query.setParameter("nombre", nombre);
            return query.getSingleResult();  // Retorna el id de la empresa o lanza NoResultException si no existe
        } catch (NoResultException nre) {
            return null;  // Retorna null si no se encuentra ninguna empresa con ese nombre
        } finally {
            em.close();
        }
    }
    @Override
    public Empresa encontrar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Empresa.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Empresa empresa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(empresa);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar la empresa", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, id);
            if (empresa != null) {
                em.remove(empresa);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar la empresa", e);
        } finally {
            em.close();
        }
    }
}

