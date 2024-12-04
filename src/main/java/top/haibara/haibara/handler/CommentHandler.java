package top.haibara.haibara.handler;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import top.haibara.haibara.model.Article;
import top.haibara.haibara.model.Comment;
import top.haibara.haibara.model.User;
import top.haibara.haibara.repository.ArticleRepository;
import top.haibara.haibara.repository.CommentRepository;
import top.haibara.haibara.repository.UserRepository;
import top.haibara.haibara.security.Secured;

import java.util.HashMap;
import java.util.Map;

@Path("/comments")
public class CommentHandler {
    @Inject
    private CommentRepository commentRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ArticleRepository articleRepository;

    @POST
    @Path("/")
    @Secured({"user", "admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComment(Comment comment , @Context SecurityContext securityContext) {
        User user = userRepository.findByID(Integer.valueOf(securityContext.getUserPrincipal().getName()));
        comment.setUser(user);
        Article article = articleRepository.findByID(comment.getArticleId());
        comment.setArticle(article);
        comment.setCreatedAt(System.currentTimeMillis() / 1000);
        commentRepository.create(comment);
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        return Response.status(Response.Status.OK).entity(res).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured({"admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteComment(@PathParam("id") int id, @Context SecurityContext securityContext) {
        Comment comment = commentRepository.findByID(id);
        if (comment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Comment not found"))
                    .build();
        }
        try {
            commentRepository.delete(comment);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete comment"))
                    .build();
        }
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK.getStatusCode());
        res.put("message", "Comment deleted successfully");
        return Response.status(Response.Status.OK).entity(res).build();
    }

}
