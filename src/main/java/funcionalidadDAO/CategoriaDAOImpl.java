package funcionalidadDAO;

import entity.Categoria;
import entity.Empresa;
import interfaceDAO.CategoriaDAO;
import jakarta.persistence.EntityManager;
import utils.JPAUtil;

import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public void guardarActualizarCategoria(Categoria categoria, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.getReference(Empresa.class, empresaId);
            categoria.setEmpresa(empresa);

            if (categoria.getId() == null) {
                em.persist(categoria);
            } else {
                if (!categoria.getEmpresa().getId().equals(empresaId)) {
                    throw new SecurityException("Operación no permitida.");
                }
                em.merge(categoria);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar o actualizar la categoría", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Categoria buscarCategoria(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria != null && categoria.getEmpresa() != null && categoria.getEmpresa().getId().equals(empresaId)) {
                return categoria;
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarCategoria(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria != null && categoria.getEmpresa().getId().equals(empresaId)) {
                em.remove(categoria);
            } else {
                throw new SecurityException("Operación no permitida.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar la categoría", e);
        } finally {
            em.close();
        }
    }


    public static List<Categoria> buscarTodasCategorias(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            List<Categoria> categorias = em.createQuery("SELECT c FROM Categoria c WHERE c.empresa.id = :empresaId", Categoria.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
            em.getTransaction().commit();
            return categorias;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al buscar todas las categorías", e);
        } finally {
            em.close();
        }
    }
}
