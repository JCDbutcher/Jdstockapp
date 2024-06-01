package interfaceDAO;

import entity.Proveedor;

public interface ProveedorDAO {

    /**
     * Guarda un proveedor en la base de datos asociado a una empresa específica.
     * @param proveedor el objeto Proveedor a guardar.
     * @param empresaId la ID de la empresa bajo la cual se guarda el proveedor.
     */
    void guardar(Proveedor proveedor, Long empresaId);

    /**
     * Encuentra un proveedor por su ID, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID del proveedor que se desea encontrar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el proveedor.
     * @return el proveedor encontrado o null si no se encuentra o no pertenece a la empresa especificada.
     */
    Proveedor encontrar(Long id, Long empresaId);

    /**
     * Actualiza los datos de un proveedor existente, asegurándose de que pertenezca a la empresa especificada.
     * @param proveedor el proveedor con los datos actualizados.
     * @param empresaId la ID de la empresa bajo la cual se actualiza el proveedor.
     */
    void actualizar(Proveedor proveedor, Long empresaId);

    /**
     * Elimina un proveedor de la base de datos, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID del proveedor que se desea eliminar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el proveedor eliminado.
     */
    void eliminar(Long id, Long empresaId);
}


