package test;

import funcionalidadDAO.DetalleTransaccionDAOImpl;
import funcionalidadDAO.ProductoDAOImpl;
import funcionalidadDAO.TransaccionDAOImpl;
import entity.DetalleTransaccion;
import entity.Producto;
import entity.Transaccion;
import utils.JPAUtil;
import java.math.BigDecimal;
import jakarta.persistence.EntityManager;

public class DetalleTransaccionDAOTest {

    public static void main(String[] args) {
        DetalleTransaccionDAOImpl detalleTransaccionDAO = new DetalleTransaccionDAOImpl();
        ProductoDAOImpl productoDAO = new ProductoDAOImpl();
        TransaccionDAOImpl transaccionDAO = new TransaccionDAOImpl();
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Long empresaId = 1L;

            // Crear y guardar un detalle de transacción
            DetalleTransaccion nuevoDetalle = new DetalleTransaccion();
            Transaccion transaccion = transaccionDAO.encontrar(1L, empresaId);
            Producto producto = productoDAO.encontrar(1L,empresaId);

            nuevoDetalle.setTransaccion(transaccion);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(5);
            nuevoDetalle.setPrecioUnitario(BigDecimal.valueOf(10.50));
            nuevoDetalle.setDescuento(BigDecimal.valueOf(2.00));
            System.out.println("Guardando detalle de transacción...");
            detalleTransaccionDAO.guardar(nuevoDetalle, empresaId);
            System.out.println("Detalle de transacción guardado con ID: " + nuevoDetalle.getId());

            // Encontrar un detalle de transacción por ID
            Long detalleId = nuevoDetalle.getId();
            System.out.println("Buscando detalle de transacción con ID: " + detalleId);
            DetalleTransaccion detalleEncontrado = detalleTransaccionDAO.encontrar(detalleId, empresaId);
            if (detalleEncontrado != null) {
                System.out.println("Detalle de transacción encontrado.");
            } else {
                System.out.println("Detalle de transacción no encontrado.");
            }

            // Actualizar un detalle de transacción
            if (detalleEncontrado != null) {
                detalleEncontrado.setCantidad(10);
                detalleEncontrado.setPrecioUnitario(BigDecimal.valueOf(9.99));
                detalleEncontrado.setDescuento(BigDecimal.ZERO);
                System.out.println("Actualizando detalle de transacción...");
                detalleTransaccionDAO.actualizar(detalleEncontrado, empresaId);
                System.out.println("Detalle de transacción actualizado.");
            }

            // Eliminar un detalle de transacción
            if (detalleEncontrado != null) {
                System.out.println("Eliminando detalle de transacción con ID: " + detalleEncontrado.getId());
                detalleTransaccionDAO.eliminar(detalleId, empresaId);
                System.out.println("Detalle de transacción eliminado.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}

