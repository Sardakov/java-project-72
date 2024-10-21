package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.SQLException;

public class UrlChecksController {
    public static void create(Context ctx) throws SQLException {
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

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "danger");
        }

        var urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1Text);
        urlCheck.setDescription(description);
        urlCheck.setUrlId(Long.valueOf(idUrl));
        UrlChecksRepository.saveUrlCheck(urlCheck);
        ctx.redirect(NamedRoutes.showPath(idUrl));
    }

    private static String getElementText(Document doc, String tagName) {
        Element element = doc.selectFirst(tagName);
        return element != null ? element.text() : "";
    }

    private static String getMetaContent(Document doc, String metaName) {
        Element element = doc.selectFirst(metaName);
        return element != null ? element.attr("content") : "";
    }
}
