package top.haibara.haibara.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
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

    public void delete(Integer id) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Article article = em.find(Article.class, id);
            em.remove(article);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        }finally {
            em.close();
        }
    }

    public long countTotalArticles() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM Article a", Long.class)
                    .getSingleResult();  // 执行查询并返回单一结果
        } finally {
            em.close();
        }
    }
    public List<Article> findArticlesWithPagination(int offset,int pageSize) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<Article> query = em.createQuery("SELECT a FROM Article a", Article.class);
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
}