package test;

import entity.Proveedor;
import funcionalidadDAO.EmpresaDAOImpl;
import funcionalidadDAO.ProveedorDAOImpl;
import utils.JPAUtil;

public class ProveedorDAOTest {

    public static void main(String[] args) {
        ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();
        Long empresaId = 1L; // Asegúrate de que este ID de empresa es correcto para tu contexto

        try {
            // Crear y guardar un proveedor
            Proveedor nuevoProveedor = new Proveedor();
            nuevoProveedor.setNombre("Proveedor ABC");
            nuevoProveedor.setDireccion("Calle Principal 123");
            nuevoProveedor.setTelefono("123-456-7890");
            System.out.println("Guardando proveedor...");
            proveedorDAO.guardar(nuevoProveedor, empresaId);
            System.out.println("Proveedor guardado con ID: " + nuevoProveedor.getId());

            // Encontrar un proveedor por ID
            Long proveedorId = nuevoProveedor.getId();
            System.out.println("Buscando proveedor con ID: " + proveedorId);
            Proveedor proveedorEncontrado = proveedorDAO.encontrar(proveedorId, empresaId);
            if (proveedorEncontrado != null) {
                System.out.println("Proveedor encontrado: " + proveedorEncontrado.getNombre());
            } else {
                System.out.println("Proveedor no encontrado.");
            }

            // Actualizar un proveedor
            if (proveedorEncontrado != null) {
                proveedorEncontrado.setTelefono("987-654-3210");
                System.out.println("Actualizando proveedor...");
                proveedorDAO.actualizar(proveedorEncontrado, empresaId);
                System.out.println("Proveedor actualizado.");
            }

            // Eliminar un proveedor
            if (proveedorEncontrado != null) {
                System.out.println("Eliminando proveedor con ID: " + proveedorEncontrado.getId());
                proveedorDAO.eliminar(proveedorEncontrado.getId(), empresaId);
                System.out.println("Proveedor eliminado.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

