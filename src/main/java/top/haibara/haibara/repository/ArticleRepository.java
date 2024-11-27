package top.haibara.haibara.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import top.haibara.haibara.model.Article;
import top.haibara.haibara.util.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class ArticleRepository {
    public List<Article> findAll() throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        List<Article> articles = null;
        try {
            em.getTransaction().begin();
            articles = em
                    .createQuery("SELECT a FROM Article a", Article.class)
                    .getResultList();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("articles_not_found", e);
        } finally {
            em.close();
        }
        return articles;
    }

    public Article findByID(Integer id) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        Article article = null;
        try {
            em.getTransaction().begin();
            article = em
                    .createQuery("SELECT a FROM Article a WHERE a.id = :id", Article.class)
                    .setParameter("id", id)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("article_not_found", e);
        } finally {
            em.close();
        }
        return article;
    }

    public void create(Article article) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(article);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        } finally {
            em.close();
        }
    }

    public void update(Article article) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(article);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        } finally {
            em.close();
        }
    }

    public List<Article> findPaginatedArticles(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page number and size must be greater than 0");
        }

        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Article a ORDER BY a.id", Article.class)
                    .setFirstResult((page - 1) * size)  // 设置查询的起始位置
                    .setMaxResults(size)                // 设置每页的条数
                    .getResultList();                   // 执行查询并返回结果
        } catch (Exception e) {
            // 你可以根据需要进一步细化异常捕获
            throw new RuntimeException("Failed to retrieve paginated articles", e);
        } finally {
            em.close();  // 确保在操作完毕后关闭 EntityManager
        }
    }

    public long countTotalArticles() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            // 返回文章总数
            return em.createQuery("SELECT COUNT(a) FROM Article a", Long.class)
                    .getSingleResult();  // 执行查询并返回单一结果
        } finally {
            em.close();  // 确保关闭 EntityManager
        }
    }
}