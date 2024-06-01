package test;

import entity.Empresa;
import entity.RolUsuario;
import entity.Usuario;
import funcionalidadDAO.EmpresaDAOImpl;
import funcionalidadDAO.UsuarioDAOImpl;

public class UsuarioTest {

    public static void main(String[] args) {
        UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
        EmpresaDAOImpl empresaDAO = new EmpresaDAOImpl();
        Long empresaId = 1L; // Suponiendo que el ID de la empresa es 1

        // Asegurar que la empresa existe
        Empresa empresa = empresaDAO.encontrar(empresaId);
        if (empresa == null) {
            System.out.println("No se encontró la empresa con ID: " + empresaId);
            return;
        }

        // Crear y guardar un nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Juan Pérez");
        nuevoUsuario.setNombreUsuario("juanp23");
        nuevoUsuario.setHashContrasena("hash_de_prueba");
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setRol(RolUsuario.ADMINISTRADOR.toString());
        nuevoUsuario.setEmpresa(empresa);

        System.out.println("Creando y guardando un nuevo usuario...");
        usuarioDAO.guardarUsuario(nuevoUsuario, empresaId);

        // Buscar un usuario por ID
        Long usuarioId = nuevoUsuario.getId(); // Suponiendo que obtienes el ID después de guardar
        System.out.println("Buscando usuario con ID: " + usuarioId);
        Usuario usuarioBuscado = usuarioDAO.encontrarUsuarioPorId(usuarioId, empresaId);
        if (usuarioBuscado != null) {
            System.out.println("Usuario encontrado: " + usuarioBuscado.getNombre());
        } else {
            System.out.println("No se encontró el usuario.");
        }

        // Actualizar un usuario
        if (usuarioBuscado != null) {
            usuarioBuscado.setHashContrasena("nuevo_hash_de_prueba");
            System.out.println("Actualizando usuario...");
            usuarioDAO.actualizarUsuario(usuarioBuscado, empresaId);
        }

        // Eliminar un usuario
        if (usuarioBuscado != null) {
            System.out.println("Eliminando usuario con ID: " + usuarioBuscado.getId());
            usuarioDAO.eliminarUsuario(usuarioBuscado.getId(), empresaId);
            System.out.println("Usuario eliminado.");
        }
    }
}

