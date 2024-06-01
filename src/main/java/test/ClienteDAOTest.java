package test;

import funcionalidadDAO.ClienteDAOImpl;
import entity.Cliente;
import entity.Empresa;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;

public class ClienteDAOTest {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        ClienteDAOImpl clienteDAO = new ClienteDAOImpl();

        try {
            em.getTransaction().begin();
            // Asumiendo que ya existe una empresa con id = 1 en la base de datos
            Empresa empresa = em.find(Empresa.class, 1L);
            if (empresa == null) {
                System.out.println("Empresa no encontrada, creando una nueva...");
                empresa = new Empresa();
                empresa.setNombre("Empresa Prueba");
                empresa.setDireccion("Calle Prueba 123");
                empresa.setEmail("contacto@prueba.com");
                em.persist(empresa);
                em.getTransaction().commit();
                em.getTransaction().begin();
            }

            // Crear y guardar un nuevo cliente asociado a la empresa
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre("Cliente XYZ");
            nuevoCliente.setDireccion("Avenida Principal 456");
            nuevoCliente.setTelefono("987-654-321");
            nuevoCliente.setEmail("cliente@example.com");
            nuevoCliente.setEmpresa(empresa);
            System.out.println("Guardando cliente...");
            clienteDAO.guardarActualizarCliente(nuevoCliente, empresa.getId());
            System.out.println("Cliente guardado con ID: " + nuevoCliente.getId());

            // Encontrar un cliente por ID
            Long clienteId = nuevoCliente.getId();
            System.out.println("Buscando cliente con ID: " + clienteId);
            Cliente clienteEncontrado = clienteDAO.buscarCliente(clienteId, empresa.getId());
            if (clienteEncontrado != null) {
                System.out.println("Cliente encontrado: " + clienteEncontrado.getNombre());
            } else {
                System.out.println("Cliente no encontrado.");
            }

            // Actualizar un cliente
            if (clienteEncontrado != null) {
                clienteEncontrado.setTelefono("Nuevo Teléfono: 123-456-789");
                System.out.println("Actualizando cliente...");
                clienteDAO.guardarActualizarCliente(clienteEncontrado, empresa.getId());
                System.out.println("Cliente actualizado.");
            }

            // Eliminar un cliente
            if (clienteEncontrado != null) {
                System.out.println("Eliminando cliente con ID: " + clienteEncontrado.getId());
                clienteDAO.eliminarCliente(clienteEncontrado.getId(), empresa.getId());
                System.out.println("Cliente eliminado.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            JPAUtil.closeEntityManager();
        }
    }
}


