package utils;

import entity.DetalleTransaccion;
import entity.Transaccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;

import java.util.List;

public class JPAUtil {
    private static EntityManagerFactory entityManagerFactory;
    private static ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory("jdstockdb");
        }
        EntityManager em = threadLocalEntityManager.get();
        if (em == null || !em.isOpen()) {
            em = entityManagerFactory.createEntityManager();
            threadLocalEntityManager.set(em);
        }
        return em;
    }


    public static void closeEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        if (em != null) {
            em.close();
            threadLocalEntityManager.remove();
        }
    }
}



