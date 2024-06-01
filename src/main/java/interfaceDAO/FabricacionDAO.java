package interfaceDAO;


import entity.Fabricacion;

public interface FabricacionDAO {

    /**
     * Guarda una nueva fabricación en la base de datos asociada a una empresa específica.
     * @param fabricacion el objeto Fabricacion a guardar.
     * @param empresaId la ID de la empresa bajo la cual se guarda la fabricación.
     */
    void guardar(Fabricacion fabricacion, Long empresaId);

    /**
     * Encuentra una fabricación por su ID, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID de la fabricación que se desea encontrar.
     * @param empresaId la ID de la empresa a la que debe pertenecer la fabricación.
     * @return la instancia de Fabricacion encontrada, o null si no se encuentra o no pertenece a la empresa especificada.
     */
    Fabricacion encontrar(Long id, Long empresaId);

    /**
     * Actualiza los datos de una fabricación existente, asegurándose de que pertenezca a la empresa especificada.
     * @param fabricacion la instancia de Fabricacion con los datos actualizados.
     * @param empresaId la ID de la empresa bajo la cual se actualiza la fabricación.
     */
    void actualizar(Fabricacion fabricacion, Long empresaId);

    /**
     * Elimina una fabricación de la base de datos, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID de la fabricación que se desea eliminar.
     * @param empresaId la ID de la empresa a la que debe pertenecer la fabricación a eliminar.
     */
    void eliminar(Long id, Long empresaId);
}


