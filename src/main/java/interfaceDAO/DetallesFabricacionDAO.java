package interfaceDAO;


import entity.DetallesFabricacion;
import entity.DetallesFabricacionId;

import entity.DetallesFabricacion;
import entity.DetallesFabricacionId;

public interface DetallesFabricacionDAO {

    /**
     * Guarda los detalles de fabricación, asegurándose de que pertenecen a la empresa especificada.
     * @param detalles los detalles de fabricación a guardar.
     * @param empresaId la ID de la empresa a la que deben pertenecer los detalles de fabricación.
     */
    void guardar(DetallesFabricacion detalles, Long empresaId);

    /**
     * Encuentra los detalles de fabricación por su ID compuesta, asegurándose de que pertenecen a la empresa especificada.
     * @param id el ID compuesto de los detalles de fabricación.
     * @param empresaId la ID de la empresa a la que deben pertenecer los detalles de fabricación.
     * @return los detalles de fabricación encontrados o null si no existen o no pertenecen a la empresa especificada.
     */
    DetallesFabricacion encontrar(DetallesFabricacionId id, Long empresaId);

    /**
     * Elimina los detalles de fabricación, asegurándose de que pertenecen a la empresa especificada.
     * @param detalles los detalles de fabricación a eliminar.
     * @param empresaId la ID de la empresa a la que deben pertenecer los detalles de fabricación.
     */
    void eliminar(DetallesFabricacion detalles, Long empresaId);
}



