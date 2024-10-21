package hexlet.code.controller;
import hexlet.code.dto.url.UrlPage;
import hexlet.code.dto.url.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void createUrl(Context ctx) throws SQLException {
        var url = ctx.formParam("url");
        var parsedUrl = processUrl(url);

        if (parsedUrl == null) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        } else {
            if (UrlRepository.find(parsedUrl).isEmpty()) {
                var urlLast = new Url(parsedUrl);
                UrlRepository.save(urlLast);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect("/urls");
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "info");
                ctx.redirect("/urls");
            }
        }
    }

    public static void showUrls(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlsCheck = UrlChecksRepository.getUrlCheckPart();
        var page = new UrlsPage(urls, urlsCheck);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("page/index.jte", model("page", page));
    }
    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var urlsCheck = UrlChecksRepository.getUrlCheck();
        var url = UrlRepository.findId(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var page = new UrlPage(url, urlsCheck);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("page/show.jte", model("page", page));
    }
    public static String processUrl(String inputUrl) {
        if (inputUrl == null || inputUrl.isEmpty()) {
            return "";
        }
        try {
            URI uri = new URI(inputUrl);
            URL parsedUrl = uri.toURL();

            StringBuilder result = new StringBuilder();
            result.append(parsedUrl.getProtocol()).append("://").append(parsedUrl.getHost());

            int port = parsedUrl.getPort();
            if (port != -1) {
                result.append(":").append(port);
            }

            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
