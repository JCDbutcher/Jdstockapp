package test;

import funcionalidadDAO.FabricacionDAOImpl;
import entity.Fabricacion;
import entity.Producto;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

public class FabricacionDAOTest {

    public static void main(String[] args) {
        FabricacionDAOImpl fabricacionDAO = new FabricacionDAOImpl();
        Long empresaId = 1L; // Asegúrate de que este ID de empresa es correcto para tu contexto

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Asumiendo que ya existe un producto con id específico en la base de datos
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, 1L); // Suponemos que este ID de producto existe
            em.getTransaction().commit();

            if (producto == null) {
                System.out.println("Producto no encontrado, abortando...");
                return;
            }

            // Crear y guardar una nueva fabricación asociada a la empresa
            Fabricacion fabricacion = new Fabricacion();
            fabricacion.setProducto(producto);
            fabricacion.setCantidadProducida(100);
            fabricacion.setFechaFabricacion(LocalDate.now());

            System.out.println("Guardando fabricación...");
            fabricacionDAO.guardar(fabricacion, empresaId);

            // Encontrar la fabricación por ID
            Long fabricacionId = fabricacion.getId(); // Suponemos que obtienes el ID después de guardar
            System.out.println("Buscando fabricación con ID: " + fabricacionId);
            Fabricacion fabricacionEncontrada = fabricacionDAO.encontrar(fabricacionId, empresaId);
            if (fabricacionEncontrada != null) {
                System.out.println("Fabricación encontrada: " + fabricacionEncontrada.getId());
            } else {
                System.out.println("No se encontró la fabricación.");
            }

            // Actualizar la fabricación
            if (fabricacionEncontrada != null) {
                fabricacionEncontrada.setCantidadProducida(150); // Nueva cantidad producida
                System.out.println("Actualizando fabricación...");
                fabricacionDAO.actualizar(fabricacionEncontrada, empresaId);
                System.out.println("Fabricación actualizada.");
            }

            // Eliminar la fabricación
            if (fabricacionEncontrada != null) {
                System.out.println("Eliminando fabricación con ID: " + fabricacionEncontrada.getId());
                fabricacionDAO.eliminar(fabricacionEncontrada.getId(), empresaId);
                System.out.println("Fabricación eliminada.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
            JPAUtil.closeEntityManager(); // Cerrar el EntityManagerFactory al finalizar
        }
    }
}

