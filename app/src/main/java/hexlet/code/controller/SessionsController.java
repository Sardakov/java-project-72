package hexlet.code.controller;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.url.UrlPage;
import hexlet.code.dto.url.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
        var idUrl = ctx.pathParam("id");
        var postUrl = UrlRepository.findId(Long.valueOf(idUrl)).get().getName();

        Long statusCode = null;
        String title = "";
        String h1Text = "";
        String description = "";

        try {
            HttpResponse<String> response = Unirest.get(postUrl).asString();
            statusCode = (long) response.getStatus();

            Document doc = Jsoup.parse(response.getBody());

            title = getElementText(doc, "title");
            h1Text = getElementText(doc, "h1");
            description = getMetaContent(doc, "meta[name=description]");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        var urlCheck = new UrlCheck(statusCode, title, h1Text, description, Long.parseLong(idUrl));
        UrlChecksRepository.saveUrlCheck(urlCheck);
        ctx.redirect("/urls/" + idUrl);
    }

    private static String getElementText(Document doc, String tagName) {
        Element element = doc.selectFirst(tagName);
        return element != null ? element.text() : "";
    }

    private static String getMetaContent(Document doc, String metaName) {
        Element element = doc.selectFirst(metaName);
        return element != null ? element.attr("content") : "";
    }

    public static void show(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlsCheck = UrlChecksRepository.getUrlCheckPart();
        var page = new UrlsPage(urls, urlsCheck);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls.jte", model("page", page));
    }
    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var urlsCheck = UrlChecksRepository.getUrlCheck();
        var url = UrlRepository.findId(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var page = new UrlPage(url, urlsCheck);
        ctx.render("url.jte", model("page", page));
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
