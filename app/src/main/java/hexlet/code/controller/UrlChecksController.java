package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class UrlChecksController {
    public static void create(Context ctx) throws Exception {
        Long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var postUrl = UrlRepository.findId(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        try {
            HttpResponse<String> response = Unirest.get(postUrl.getName()).asString();
            Document document = Jsoup.parse(response.getBody());

            Long statusCode = (long) response.getStatus();
            String title = document.title();
            String h1 = document.getElementsByTag("h1").text();
            String description = document.getElementsByAttributeValue("name", "description").attr("content");

            var urlCheck = new UrlCheck();
            urlCheck.setStatusCode(statusCode);
            urlCheck.setTitle(title);
            urlCheck.setH1(h1);
            urlCheck.setDescription(description);
            urlCheck.setUrlId(id);
            UrlChecksRepository.saveUrlCheck(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
        }
        ctx.redirect(NamedRoutes.showPath(id));
    }
}
