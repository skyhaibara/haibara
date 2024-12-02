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
import java.util.List;
import java.util.Map;

@Path("/articles") //无JSON不回显
public class ArticleHandler {
    @Inject
    private ArticleRepository articleRepository;
    @Inject
    private CommentRepository commentRepository;
    @Inject
    private UserRepository userRepository;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        Map<String, Object> res= new HashMap<>();
        res.put("code", Response.Status.OK);
        res.put("data", articles);
        return Response.status(Response.Status.OK).entity(res).build();
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticleById(@PathParam("id") Integer id) {
        Article article = articleRepository.findByID(id);
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        res.put("data", article);
        return Response.status(Response.Status.OK).entity(res).build();
    }
    @GET
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@PathParam("id") Integer id) {
        List<Comment> comments = commentRepository.findByArticleId(id);
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        res.put("data", comments);
        return Response.status(Response.Status.OK).entity(res).build();
    }
    @POST //创建Article
    @Path("/")
    @Secured({"admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createArticle(Article article, @Context SecurityContext securityContext) {
        User author = userRepository.findByID(Integer.valueOf(securityContext.getUserPrincipal().getName()));
        article.setAuthor(author);
        article.setCreatedAt(System.currentTimeMillis()/1000);
        articleRepository.create(article);
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        return Response.status(Response.Status.OK).entity(res).build();
    }
    @PUT //更新Article
    @Path("/")
    @Secured({"admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateArticle(Article article, @Context SecurityContext securityContext) {
        article.setAuthorId(Integer.valueOf(securityContext.getUserPrincipal().getName()));
        articleRepository.update(article);
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        return Response.status(Response.Status.OK).entity(res).build();
    }

    @GET
    @Path("/")
    @Secured({"admin","user"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedArticles(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("5") int size) {
        // 校验分页参数
        if (page < 1 || size < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page number and size must be greater than 0")
                    .build();
        }

        // 调整分页参数，确保从第一页开始
        int pageAdjusted = page < 1 ? 1 : page;

        try {
            // 获取分页数据
            List<Article> articles = articleRepository.findPaginatedArticles(pageAdjusted, size);

            // 获取总条数
            long totalItems = articleRepository.countTotalArticles();

            // 计算总页数
            int totalPages = (int) Math.ceil((double) totalItems / size);

            // 返回分页数据和总条数、总页数
            Map<String, Object> res = new HashMap<>();
            res.put("code", Response.Status.OK.getStatusCode());
            res.put("data", articles);
            res.put("totalItems", totalItems);
            res.put("totalPages", totalPages);

            return Response.status(Response.Status.OK).entity(res).build();
        } catch (Exception e) {
            // 异常处理
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching articles: " + e.getMessage())
                    .build();
        }
    }

}
