package test;

import funcionalidadDAO.DetallesFabricacionDAOImpl;
import entity.DetallesFabricacion;
import entity.DetallesFabricacionId;
import entity.Fabricacion;
import entity.Producto;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;

public class DetallesFabricacionDAOTest {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        DetallesFabricacionDAOImpl detallesDAO = new DetallesFabricacionDAOImpl();

        try {
            em.getTransaction().begin();
            Fabricacion fabricacion = em.find(Fabricacion.class, 1L);
            Producto componente = em.find(Producto.class, 1L);
            em.getTransaction().commit();

            if (fabricacion == null || componente == null) {
                System.out.println("Fabricación o componente no encontrados, abortando...");
                return;
            }

            DetallesFabricacion nuevosDetalles = new DetallesFabricacion();
            nuevosDetalles.setFabricacion(fabricacion);
            nuevosDetalles.setComponente(componente);
            nuevosDetalles.setCantidadUtilizada(100);

            System.out.println("Guardando detalles de fabricación...");
            detallesDAO.guardar(nuevosDetalles, fabricacion.getEmpresa().getId());
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}



