package test;

import entity.Producto;
import funcionalidadDAO.ProductoDAOImpl;
import utils.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductoDAOTest {

    public static void main(String[] args) {
        ProductoDAOImpl productoDAO = new ProductoDAOImpl();
        Long empresaId = 1L; // Asegúrate de que este ID de empresa es correcto para tu contexto

        try {
            // Crear y guardar un nuevo producto
            Producto producto = new Producto();
            producto.setNombre("Producto de Prueba");
            producto.setDescripcion("Descripción del producto de prueba");
            producto.setPrecio(new BigDecimal("99.99"));
            producto.setCantidadEnStock(50);
            producto.setSku("SKU123456");
            producto.setEsPerecedero(false);
            producto.setFechaCaducidad(LocalDate.now().plusMonths(6)); // Ejemplo de fecha de caducidad
            System.out.println("Guardando producto...");
            productoDAO.guardar(producto, empresaId);

            // Encontrar el producto por ID
            Long productoId = producto.getId(); // Suponiendo que obtienes el ID después de guardar
            System.out.println("Buscando producto con ID: " + productoId);
            Producto productoEncontrado = productoDAO.encontrar(productoId, empresaId);
            if (productoEncontrado != null) {
                System.out.println("Producto encontrado: " + productoEncontrado.getNombre());
                // Realizar cualquier verificación adicional necesaria
            } else {
                System.out.println("No se encontró el producto.");
            }

            // Actualizar el producto
            if (productoEncontrado != null) {
                productoEncontrado.setPrecio(new BigDecimal("109.99")); // Nuevo precio
                System.out.println("Actualizando producto...");
                productoDAO.actualizar(productoEncontrado, empresaId);
                System.out.println("Producto actualizado.");
            }

            // Eliminar el producto
            if (productoEncontrado != null) {
                System.out.println("Eliminando producto con ID: " + productoEncontrado.getId());
                productoDAO.eliminar(productoEncontrado.getId(), empresaId);
                System.out.println("Producto eliminado.");
            }

            // Obtener datos para el gráfico de inventario
            System.out.println("Obteniendo datos para el gráfico de inventario...");
            List<Object[]> datosPieChart = productoDAO.obtenerDatosParaPieChartInventario(empresaId);
            for (Object[] datos : datosPieChart) {
                System.out.println("Cantidad en Stock: " + datos[0] + ", Fecha de Caducidad: " + datos[1]);
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JPAUtil.closeEntityManager(); // Cerrar el EntityManagerFactory al finalizar
        }
    }
}
