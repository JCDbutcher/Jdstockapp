package interfaceDAO;

import entity.Cliente;

public interface ClienteDAO {

    /**
     * Guarda o actualiza un cliente, asegurándose de que pertenece a la empresa especificada.
     * @param cliente el cliente a guardar o actualizar.
     * @param empresaId la ID de la empresa a la que pertenece el cliente.
     */
    void guardarActualizarCliente(Cliente cliente, Long empresaId);

    /**
     * Busca un cliente por su ID, asegurándose de que pertenece a la empresa especificada.
     * @param id el ID del cliente.
     * @param empresaId la ID de la empresa a la que debe pertenecer el cliente.
     * @return el cliente encontrado o null si no existe o no pertenece a la empresa especificada.
     */
    Cliente buscarCliente(Long id, Long empresaId);

    /**
     * Elimina un cliente por su ID, asegurándose de que pertenece a la empresa especificada.
     * @param id el ID del cliente a eliminar.
     * @param empresaId la ID de la empresa a la que pertenece el cliente.
     */
    void eliminarCliente(Long id, Long empresaId);
}


