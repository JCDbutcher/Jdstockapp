package funcionalidadDAO;

import entity.Cliente;
import entity.Empresa;
import interfaceDAO.ClienteDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import utils.JPAUtil;

import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void guardarActualizarCliente(Cliente cliente, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Empresa empresa = em.find(Empresa.class, empresaId);
            if (empresa == null) {
                throw new IllegalArgumentException("Empresa no encontrada con ID: " + empresaId);
            }

            // Asociar el cliente con la empresa antes de guardar o actualizar
            cliente.setEmpresa(empresa);

            if (cliente.getId() == null) { // Nuevo cliente
                em.persist(cliente);
            } else { // Cliente existente
                Cliente existente = em.find(Cliente.class, cliente.getId());
                if (existente != null && existente.getEmpresa().getId().equals(empresaId)) {
                    em.merge(cliente);
                } else {
                    throw new SecurityException("Operación no permitida o el cliente no pertenece a la empresa indicada.");
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar o actualizar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Cliente buscarCliente(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null && cliente.getEmpresa().getId().equals(empresaId)) {
                return cliente;
            } else {
                return null; // Cliente no encontrado o no pertenece a la empresa
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarCliente(Long id, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null && cliente.getEmpresa().getId().equals(empresaId)) {
                em.remove(cliente);
            } else {
                throw new SecurityException("Operación no permitida o el cliente no pertenece a la empresa indicada.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar el cliente", e);
        } finally {
            em.close();
        }
    }
    public static List<Cliente> buscarTodosClientes(Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId", Cliente.class);
            query.setParameter("empresaId", empresaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cliente> buscarClientesPorNombre(String nombre, Long empresaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Preparar una consulta para buscar clientes por nombre con coincidencia parcial y que pertenecen a una empresa específica
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.empresa.id = :empresaId AND LOWER(c.nombre) LIKE LOWER(:nombre)", Cliente.class);
            query.setParameter("empresaId", empresaId);
            query.setParameter("nombre", "%" + nombre + "%"); // Buscar coincidencias que contengan 'nombre' en cualquier parte del nombre del cliente
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

