package top.haibara.haibara.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {
    private static EntityManagerFactory emf;
    public static void init() {
        emf = Persistence.createEntityManagerFactory("default");
    }
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
