package top.haibara.haibara.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import top.haibara.haibara.model.Comment;
import top.haibara.haibara.util.HibernateUtil;

import java.util.List;

@ApplicationScoped
public class CommentRepository {
    public List<Comment> findByArticleId(Integer id) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        List<Comment> comments = null;
        try {
            em.getTransaction().begin();
            comments = em
                    .createQuery("SELECT c FROM Comment c WHERE c.articleId = :articleId", Comment.class)
                    .setParameter("articleId", id)
                    .getResultList();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        } finally {
            em.close();
        }
        return comments;
    }

    public void create(Comment comment) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(comment);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        } finally {
            em.close();
        }
    }
    public Comment findByID(Integer id) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        Comment comment = null;
        try {
            comment = em.find(Comment.class, id);
            if (comment == null) {
                throw new RuntimeException("comment_not_found");
            }
        } catch (PersistenceException e) {
            throw new RuntimeException("Error occurred while finding comment by ID.", e);
        } finally {
            em.close();
        }
        return comment;
    }

    public void delete(Comment comment) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Comment managedComment = em.contains(comment) ? comment : em.find(Comment.class, comment.getId());
            if (managedComment != null) {
                em.remove(managedComment);
            } else {
                throw new PersistenceException("Entity not found in database.");
            }
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error occurred while deleting the comment.", e);
        } finally {
            em.close();
        }
    }

}
