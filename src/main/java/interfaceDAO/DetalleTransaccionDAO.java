package interfaceDAO;

import entity.DetalleTransaccion;

public interface DetalleTransaccionDAO {

    /**
     * Guarda un detalle de transacción, asegurándose de que pertenece a la empresa especificada.
     * @param detalleTransaccion el detalle de la transacción a guardar.
     * @param empresaId la ID de la empresa a la que pertenece el detalle de la transacción.
     */
    void guardar(DetalleTransaccion detalleTransaccion, Long empresaId);

    /**
     * Encuentra un detalle de transacción por su ID, asegurándose de que pertenece a la empresa especificada.
     * @param id el ID del detalle de la transacción.
     * @param empresaId la ID de la empresa a la que debe pertenecer el detalle de la transacción.
     * @return el detalle de la transacción encontrado o null si no existe o no pertenece a la empresa especificada.
     */
    DetalleTransaccion encontrar(Long id, Long empresaId);

    /**
     * Actualiza un detalle de transacción, asegurándose de que pertenece a la empresa especificada.
     * @param detalleTransaccion el detalle de la transacción a actualizar.
     * @param empresaId la ID de la empresa a la que pertenece el detalle de la transacción.
     */
    void actualizar(DetalleTransaccion detalleTransaccion, Long empresaId);

    /**
     * Elimina un detalle de transacción por su ID, asegurándose de que pertenece a la empresa especificada.
     * @param id el ID del detalle de la transacción a eliminar.
     * @param empresaId la ID de la empresa a la que pertenece el detalle de la transacción.
     */
    void eliminar(Long id, Long empresaId);
}



