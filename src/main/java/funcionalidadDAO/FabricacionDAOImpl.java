package funcionalidadDAO;

import entity.Fabricacion;
import entity.Empresa;
import interfaceDAO.FabricacionDAO;
import jakarta.persistence.EntityManager;
import utils.JPAUtil;

import java.util.List;

public class FabricacionDAOImpl implements FabricacionDAO {

    @Override
    public void guardar(Fabricacion fabricacion, Long empresaId) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                Empresa empresa = em.find(Empresa.class, empresaId);
                if (empresa == null) {
                throw new IllegalStateException("Empresa con ID " + empresaId + " no existe.");
            }
            fabricacion.setEmpresa(empresa);  // Asociar la fabricación a la empresa correcta.
            em.persist(fabricacion);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar la fabricación", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Fabricacion encontrar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Fabricacion fabricacion = em.find(Fabricacion.class, id);
            if (fabricacion != null && fabricacion.getEmpresa().getId().equals(empresaId)) {
                return fabricacion;
            }
            return null; // Retorna null si la fabricación no pertenece a la empresa especificada
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Fabricacion fabricacion, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null || !fabricacion.getEmpresa().getId().equals(empresaId)) {
                throw new SecurityException("Operación no permitida: la fabricación no pertenece a la empresa indicada.");
            }
            em.merge(fabricacion);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar la fabricación", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Fabricacion fabricacion = em.find(Fabricacion.class, id);
            if (fabricacion != null && fabricacion.getEmpresa().getId().equals(empresaId)) {
                em.remove(fabricacion);
            } else {
                throw new SecurityException("Operación no permitida: la fabricación no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar la fabricación", e);
        } finally {
            em.close();
        }
    }

    public List<Fabricacion> obtenerTodos(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT f FROM Fabricacion f JOIN FETCH f.producto WHERE f.empresa.id = :empresaId";
            return em.createQuery(jpql, Fabricacion.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }



}


