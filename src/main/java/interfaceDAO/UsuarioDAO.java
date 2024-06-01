package interfaceDAO;

import entity.Usuario;

public interface UsuarioDAO {

    /**
     * Guarda un usuario en la base de datos asociado a una empresa específica.
     * @param usuario el objeto Usuario a guardar.
     * @param empresaId la ID de la empresa bajo la cual se guarda el usuario.
     */
    void guardarUsuario(Usuario usuario, Long empresaId);

    /**
     * Encuentra un usuario por su ID, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID del usuario que se desea encontrar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el usuario.
     * @return el usuario encontrado o null si no se encuentra o no pertenece a la empresa especificada.
     */
    Usuario encontrarUsuarioPorId(Long id, Long empresaId);

    /**
     * Encuentra un usuario por su nombre de usuario, asegurándose de que pertenezca a la empresa especificada.
     * @param nombreUsuario el nombre de usuario del usuario que se desea encontrar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el usuario.
     * @return el usuario encontrado o null si no se encuentra o no pertenece a la empresa especificada.
     */
    Usuario encontrarUsuarioPorNombreUsuario(String nombreUsuario, Long empresaId);

    /**
     * Actualiza los datos de un usuario existente, asegurándose de que pertenezca a la empresa especificada.
     * @param usuario el usuario con los datos actualizados.
     * @param empresaId la ID de la empresa bajo la cual se actualiza el usuario.
     */
    void actualizarUsuario(Usuario usuario, Long empresaId);

    /**
     * Elimina un usuario de la base de datos, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID del usuario que se desea eliminar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el usuario eliminado.
     */
    void eliminarUsuario(Long id, Long empresaId);
    /**
     * Encuentra un usuario por su nombre de usuario sin necesidad de especificar empresaId.
     * @param nombreUsuario el nombre de usuario del usuario que se desea encontrar.
     * @return el usuario encontrado o null si no se encuentra.
     */
    Usuario encontrarUsuarioPorNombreUsuarioLogin(String nombreUsuario);
}




