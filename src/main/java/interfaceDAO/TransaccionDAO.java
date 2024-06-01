package interfaceDAO;

import entity.Transaccion;

public interface TransaccionDAO {

    /**
     * Guarda una transacción en la base de datos asociada a una empresa específica.
     * @param transaccion el objeto Transaccion a guardar.
     * @param empresaId la ID de la empresa bajo la cual se guarda la transacción.
     */
    void guardar(Transaccion transaccion, Long empresaId);

    /**
     * Encuentra una transacción por su ID, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID de la transacción que se desea encontrar.
     * @param empresaId la ID de la empresa a la que debe pertenecer la transacción.
     * @return la transacción encontrada o null si no se encuentra o no pertenece a la empresa especificada.
     */
    Transaccion encontrar(Long id, Long empresaId);

    /**
     * Actualiza los datos de una transacción existente, asegurándose de que pertenezca a la empresa especificada.
     * @param transaccion la transacción con los datos actualizados.
     * @param empresaId la ID de la empresa bajo la cual se actualiza la transacción.
     */
    void actualizar(Transaccion transaccion, Long empresaId);

    /**
     * Elimina una transacción de la base de datos, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID de la transacción que se desea eliminar.
     * @param empresaId la ID de la empresa a la que debe pertenecer la transacción eliminada.
     */
    void eliminar(Long id, Long empresaId);
}


