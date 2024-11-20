package top.haibara.haibara.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {
    private static EntityManagerFactory emf;

    /**
     * 初始化 Hibernate
     * 这个方法需要在应用启动时调用一次
     */
    public static void init() {
        emf = Persistence.createEntityManagerFactory("default");
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * 获取 Hibernate 的 EntityManager
     * @return 一个 EntityManager 实例
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
