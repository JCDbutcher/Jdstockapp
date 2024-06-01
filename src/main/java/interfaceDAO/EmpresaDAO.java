package interfaceDAO;

import entity.Empresa;

public interface EmpresaDAO {

    /**
     * Guarda una empresa en la base de datos.
     * @param empresa la empresa a guardar.
     */
    void guardar(Empresa empresa);

    /**
     * Encuentra una empresa por su ID.
     * @param id el ID de la empresa.
     * @return la empresa encontrada o null si no existe.
     */
    Empresa encontrar(Long id);

    /**
     * Encuentra el ID de una empresa por su nombre.
     * @param nombre el nombre de la empresa.
     * @return el ID de la empresa o null si no se encuentra.
     */
    Long encontrarIdPorNombre(String nombre);

    /**
     * Actualiza los datos de una empresa existente.
     * @param empresa la empresa a actualizar.
     */
    void actualizar(Empresa empresa);

    /**
     * Elimina una empresa por su ID.
     * @param id el ID de la empresa a eliminar.
     */
    void eliminar(Long id);
}

