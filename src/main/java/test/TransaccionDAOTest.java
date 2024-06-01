package test;

import entity.Empresa;
import entity.Transaccion;
import funcionalidadDAO.EmpresaDAOImpl;
import funcionalidadDAO.TransaccionDAOImpl;
import utils.JPAUtil;

import java.time.LocalDate;

public class TransaccionDAOTest {

    public static void main(String[] args) {
        TransaccionDAOImpl transaccionDAO = new TransaccionDAOImpl();
        EmpresaDAOImpl empresaDAO = new EmpresaDAOImpl();
        Long empresaId = 1L;  // Asumiendo que el ID de la empresa es 1

        try {
            Empresa empresa = empresaDAO.encontrar(empresaId);
            if (empresa == null) {
                System.out.println("No se encontró la empresa con ID: " + empresaId);
                return;
            }

            // Crear y guardar una transacción
            Transaccion nuevaTransaccion = new Transaccion();
            nuevaTransaccion.setEmpresa(empresa);
            nuevaTransaccion.setTipo("Venta");
            nuevaTransaccion.setFechaEntrega(LocalDate.now());
            nuevaTransaccion.setFechaPedido(LocalDate.now());
            nuevaTransaccion.setEsPerfecto(true);

            System.out.println("Guardando transacción...");
            transaccionDAO.guardar(nuevaTransaccion, empresaId);
            System.out.println("Transacción guardada con ID: " + nuevaTransaccion.getId());

            // Encontrar una transacción por ID
            Long transaccionId = nuevaTransaccion.getId();
            System.out.println("Buscando transacción con ID: " + transaccionId);
            Transaccion transaccionEncontrada = transaccionDAO.encontrar(transaccionId, empresaId);
            if (transaccionEncontrada != null) {
                System.out.println("Transacción encontrada: " + transaccionEncontrada.getTipo());
            } else {
                System.out.println("Transacción no encontrada.");
            }

            // Actualizar una transacción
            if (transaccionEncontrada != null) {
                transaccionEncontrada.setEsPerfecto(false);
                System.out.println("Actualizando transacción...");
                transaccionDAO.actualizar(transaccionEncontrada, empresaId);
                System.out.println("Transacción actualizada.");
            }

            // Eliminar una transacción
            if (transaccionEncontrada != null) {
                System.out.println("Eliminando transacción con ID: " + transaccionEncontrada.getId());
                transaccionDAO.eliminar(transaccionEncontrada.getId(), empresaId);
                System.out.println("Transacción eliminada.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar el EntityManagerFactory al finalizar
            JPAUtil.closeEntityManager();
        }
    }
}
