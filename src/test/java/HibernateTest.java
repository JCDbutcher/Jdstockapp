
import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HibernateTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jdstockdb");
        EntityManager em = emf.createEntityManager();

        try {
            // Inicia la transacción
            em.getTransaction().begin();

            // Crear y persistir una nueva Empresa
            Empresa empresa = new Empresa();
            empresa.setNombre("Empresa Prueba");
            empresa.setDireccion("1234 Calle Principal");
            empresa.setEmail("contacto@empresaprueba.com");
            empresa.setTelefono("1234567890");
            em.persist(empresa);

            // Crear y persistir un nuevo Producto
            Producto producto = new Producto();
            producto.setNombre("Producto Test");
            producto.setDescripcion("Descripción del producto test");
            producto.setPrecio(BigDecimal.valueOf(19.99));
            producto.setCantidadEnStock(100);
            producto.setEmpresa(empresa);  // Asociar la empresa creada anteriormente
            em.persist(producto);

            // Crear y persistir un Cliente
            Cliente cliente = new Cliente();
            cliente.setNombre("Cliente Test");
            cliente.setDireccion("4321 Calle Secundaria");
            cliente.setEmpresa(empresa);
            cliente.setEmail("cliente@test.com");
            cliente.setTelefono("0987654321");
            em.persist(cliente);

            // Crear y persistir una Transaccion
            Transaccion transaccion = new Transaccion();
            transaccion.setEmpresa(empresa);
            transaccion.setCliente(cliente);
            transaccion.setTipo("Venta");
//            transaccion.setFecha(LocalDate.now());
            em.persist(transaccion);

            // Commit de la transacción
            em.getTransaction().commit();

            // Buscar y mostrar el producto
            Producto productoEncontrado = em.find(Producto.class, producto.getId());
            System.out.println("Producto encontrado: " + productoEncontrado.getNombre());

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
