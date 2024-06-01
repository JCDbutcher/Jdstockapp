package test;

import funcionalidadDAO.CategoriaDAOImpl;
import entity.Categoria;
import entity.Empresa;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;

public class CategoriaDAOTest {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        CategoriaDAOImpl categoriaDAO = new CategoriaDAOImpl();
        System.out.println(System.getProperty("java.class.path"));

        try {
            // Asumiendo que ya existe una empresa con id = 1 en la base de datos
            Empresa empresa = em.find(Empresa.class, 1L);
            if (empresa == null) {
                System.out.println("Empresa no encontrada, creando una nueva...");
                empresa = new Empresa();
                empresa.setNombre("Empresa Ejemplo");
                empresa.setDireccion("Calle Ejemplo 123");
                empresa.setEmail("contacto@empresa.com");
                em.getTransaction().begin();
                em.persist(empresa);
                em.getTransaction().commit();
            }

            // Crear y guardar una nueva categoría asociada a la empresa
            Categoria nuevaCategoria = new Categoria();
            nuevaCategoria.setNombre("Electrónica");
            nuevaCategoria.setDescripcion("Artículos electrónicos, desde gadgets hasta componentes.");
            nuevaCategoria.setEmpresa(empresa);  // Asociar la empresa
            System.out.println("Guardando categoría...");
            categoriaDAO.guardarActualizarCategoria(nuevaCategoria, empresa.getId());

            // Asegurar que la nueva categoría ha sido guardada para obtener su ID
            System.out.println("Buscando categoría con ID: " + nuevaCategoria.getId());
            Categoria categoriaBuscada = categoriaDAO.buscarCategoria(nuevaCategoria.getId(), empresa.getId());
            if (categoriaBuscada != null) {
                System.out.println("Categoría encontrada: " + categoriaBuscada.getNombre());
            } else {
                System.out.println("No se encontró la categoría.");
            }

            // Actualizar una categoría
            if (categoriaBuscada != null) {
                categoriaBuscada.setDescripcion("Artículos electrónicos, incluyendo dispositivos móviles y accesorios.");
                System.out.println("Actualizando categoría...");
                categoriaDAO.guardarActualizarCategoria(categoriaBuscada, empresa.getId());
            }

            // Eliminar una categoría
            if (categoriaBuscada != null) {
                System.out.println("Eliminando categoría con ID: " + categoriaBuscada.getId());
                categoriaDAO.eliminarCategoria(categoriaBuscada.getId(), empresa.getId());
                System.out.println("Categoría eliminada.");
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}



