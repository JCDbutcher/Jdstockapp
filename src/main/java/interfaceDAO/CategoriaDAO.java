package interfaceDAO;


import entity.Categoria;

import java.util.List;

public interface CategoriaDAO {

    /**
     * Guarda o actualiza una categoría, asegurándose de que pertenece a la empresa especificada.
     * @param categoria la categoría a guardar o actualizar.
     * @param empresaId la ID de la empresa a la que pertenece la categoría.
     */
    void guardarActualizarCategoria(Categoria categoria, Long empresaId);

    /**
     * Busca una categoría por su ID, asegurándose de que pertenece a la empresa especificada.
     * @param id el ID de la categoría.
     * @param empresaId la ID de la empresa a la que debe pertenecer la categoría.
     * @return la categoría encontrada o null si no existe o no pertenece a la empresa especificada.
     */
    Categoria buscarCategoria(Long id, Long empresaId);

    /**
     * Elimina una categoría por su ID, asegurándose de que pertenece a la empresa especificada.
     * @param id el ID de la categoría a eliminar.
     * @param empresaId la ID de la empresa a la que pertenece la categoría.
     */
    void eliminarCategoria(Long id, Long empresaId);

    static List<Categoria> buscarTodasCategorias(Long empresaId) {
        return null;
    }
}


