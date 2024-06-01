package test;

import entity.Empresa;
import funcionalidadDAO.EmpresaDAOImpl;
import utils.JPAUtil;

public class EmpresaDAOTest {

    public static void main(String[] args) {
        EmpresaDAOImpl empresaDAO = new EmpresaDAOImpl();

        try {
            // Crear y guardar una nueva empresa
            Empresa empresa = new Empresa();
            empresa.setNombre("Empresa de Prueba");
            empresa.setDireccion("Calle Prueba 123");
            empresa.setEmail("info@empresa.com");
            System.out.println("Guardando empresa...");
            empresaDAO.guardar(empresa);

            // Encontrar la empresa por ID
            Long empresaId = empresa.getId(); // Suponiendo que obtienes el ID después de guardar
            System.out.println("Buscando empresa con ID: " + empresaId);
            Empresa empresaEncontrada = empresaDAO.encontrar(empresaId);
            if (empresaEncontrada != null) {
                System.out.println("Empresa encontrada: " + empresaEncontrada.getNombre());
                // Realizar cualquier verificación adicional necesaria
            } else {
                System.out.println("No se encontró la empresa.");
            }

            // Actualizar la empresa
            if (empresaEncontrada != null) {
                empresaEncontrada.setDireccion("Nueva Dirección 456");
                System.out.println("Actualizando empresa...");
                empresaDAO.actualizar(empresaEncontrada);
            }

            // Encontrar el ID de la empresa por nombre
            String nombreEmpresa = "Empresa de Prueba";
            Long idPorNombre = empresaDAO.encontrarIdPorNombre(nombreEmpresa);
            if (idPorNombre != null) {
                System.out.println("ID de la empresa '" + nombreEmpresa + "': " + idPorNombre);
            } else {
                System.out.println("No se encontró la empresa con nombre '" + nombreEmpresa + "'.");
            }

            // Eliminar la empresa
            if (empresaEncontrada != null) {
                System.out.println("Eliminando empresa con ID: " + empresaEncontrada.getId());
                empresaDAO.eliminar(empresaEncontrada.getId());
                System.out.println("Empresa eliminada.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JPAUtil.closeEntityManager();
        }
    }
}

