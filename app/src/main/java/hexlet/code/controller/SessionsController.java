package hexlet.code.controller;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.url.UrlPage;
import hexlet.code.dto.url.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;


import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class SessionsController {
    public static void index(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", model("page", page));
    }
    public static void append(Context ctx) throws SQLException {
        var url = ctx.formParam("url");
        var pUrl = processUrl(url);

        if (pUrl == null) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        } else {
            if (UrlRepository.find(pUrl).isEmpty()) {
                var urlLast = new Url(pUrl);
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

    public static void checksUrl(Context ctx) throws SQLException {
        var url = ctx.formParam("url");
        var pUrl = processUrl(url);

        if (pUrl == null) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        } else {
            if (UrlRepository.find(pUrl).isEmpty()) {
                var urlLast = new Url(pUrl);
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

    public static void show(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlsCheck = UrlRepository.getUrlCheckPart();
        var page = new UrlsPage(urls, urlsCheck);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls.jte", model("page", page));
    }
    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var urlsCheck = UrlRepository.getUrlCheck();
        var url = UrlRepository.findId(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var page = new UrlPage(url, urlsCheck);
        ctx.render("url.jte", model("page", page));
    }
    public static String processUrl(String inputUrl) {
        try {
            URI uri = new URI(inputUrl);
            URL url = uri.toURL();

            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();

            if (port == -1) {
                return protocol + "://" + host;
            } else {
                return protocol + "://" + host + ":" + port;
            }

        } catch (Exception e) {
            return null;
        }
    }
}
