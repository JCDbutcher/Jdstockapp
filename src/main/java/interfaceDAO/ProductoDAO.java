package interfaceDAO;

import entity.Producto;

import java.util.List;

public interface ProductoDAO {

    /**
     * Guarda un producto en la base de datos asociado a una empresa específica.
     * @param producto el objeto Producto a guardar.
     * @param empresaId la ID de la empresa bajo la cual se guarda el producto.
     */
    void guardar(Producto producto, Long empresaId);

    /**
     * Encuentra un producto por su ID, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID del producto que se desea encontrar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el producto.
     * @return el producto encontrado o null si no se encuentra o no pertenece a la empresa especificada.
     */
    Producto encontrar(Long id, Long empresaId);

    /**
     * Actualiza los datos de un producto existente, asegurándose de que pertenezca a la empresa especificada.
     * @param producto el producto con los datos actualizados.
     * @param empresaId la ID de la empresa bajo la cual se actualiza el producto.
     */
    void actualizar(Producto producto, Long empresaId);

    /**
     * Elimina un producto de la base de datos, asegurándose de que pertenezca a la empresa especificada.
     * @param id el ID del producto que se desea eliminar.
     * @param empresaId la ID de la empresa a la que debe pertenecer el producto eliminado.
     */
    void eliminar(Long id, Long empresaId);

    List<Producto> obtenerTodos(Long empresaId);
}

