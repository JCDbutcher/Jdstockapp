package funcionalidadDAO;

import entity.Usuario;
import entity.Empresa;
import interfaceDAO.UsuarioDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import utils.JPAUtil;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public void guardarUsuario(Usuario usuario, Long empresaId) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null) {
                throw new IllegalStateException("La empresa asociada no existe");
            }
            usuario.setEmpresa(empresa);

            if (usuario.getId() == null) {
                em.persist(usuario);
            } else {
                if (!em.contains(usuario)) {
                    usuario = em.merge(usuario); // Asegúrate de que la entidad esté gestionada
                }
            }
            em.getTransaction().commit();
        } catch (PersistenceException pe) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar el usuario: " + pe.getMessage(), pe);
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario encontrarUsuarioPorId(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null && usuario.getEmpresa().getId().equals(empresaId)) {
                return usuario;
            }
            return null;
        } finally {
            em.close();
        }
    }

    public List<Usuario> obtenerTodosUsuarios(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Se agrega un parámetro a la consulta para filtrar por empresa_id
            String jpql = "SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId";
            List<Usuario> usuarios = em.createQuery(jpql, Usuario.class)
                    .setParameter("empresaId", empresaId)
                    .getResultList();
            em.getTransaction().commit();
            return usuarios;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al obtener todos los usuarios para la empresa " + empresaId + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    @Override
    public Usuario encontrarUsuarioPorNombreUsuario(String nombreUsuario, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario AND u.empresa.id = :empresaId", Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .setParameter("empresaId", empresaId)
                    .getSingleResult();
            return usuario;
        } catch (NoResultException nre) {
            return null; // Retorna null si no se encuentra ningún usuario con el nombre de usuario dado y empresaId
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizarUsuario(Usuario usuario, Long empresaId) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new SecurityException("Operación no permitida: el usuario no pertenece a la empresa indicada.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar el usuario", e);
        } finally {
            em.close();
        }
    }



    @Override
    public void eliminarUsuario(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null && usuario.getEmpresa().getId().equals(empresaId)) {
                em.remove(usuario);
            } else {
                throw new SecurityException("Operación no permitida o el usuario no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar el usuario", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario encontrarUsuarioPorNombreUsuarioLogin(String nombreUsuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT u FROM Usuario u JOIN FETCH u.empresa WHERE u.nombreUsuario = :nombreUsuario";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("nombreUsuario", nombreUsuario);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


}

