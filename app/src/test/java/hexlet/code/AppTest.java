package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.NotFoundResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;


public final class AppTest {
    private static Javalin app;
    private static final int STATUS_SUCCESS = 200;
    private static MockWebServer mockWebServer;
    private String baseUrl;


    @BeforeEach
    public void setUp() throws SQLException, IOException {
        baseUrl = mockWebServer.url("/").toString();
        app = App.getApp();
    }

    @BeforeAll
    public static void startMockWebServer() throws IOException {
        String html = Files.readString(Paths.get("src/test/resources/test.html").toAbsolutePath()).trim();
        mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(STATUS_SUCCESS).setBody(html));
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(STATUS_SUCCESS);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testUrlsPage() throws Exception {
        Url url = new Url(baseUrl);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(STATUS_SUCCESS);
            assertThat(response.body().string()).contains(baseUrl);
        });
    }

    @Test
    public void testUrlForm() {
        String url = "https://test.net";
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://test.net";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(STATUS_SUCCESS);
            var response2 = client.get("/urls");
            assertThat(response2.body().string()).contains(url);
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        Url url = new Url("https://ru.hexlet.io");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            Url urlTest = UrlRepository.find(url.getName())
                    .orElseThrow(() -> new NotFoundResponse("Url no found"));
            var response = client.get("/urls/" + urlTest.getId());
            assertThat(response.code()).isEqualTo(STATUS_SUCCESS);
            assert response.body() != null;
            assertThat(response.body().string()).contains(url.getName());
        });
    }

    @Test
    public void testChecks() throws SQLException {
        Url url = new Url(baseUrl);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            Url urlTest = UrlRepository.find(url.getName())
                    .orElseThrow(() -> new NotFoundResponse("Url not found"));
            var response = client.post(NamedRoutes.checkSite(urlTest.getId()));
            var optionalCheck = UrlChecksRepository.findFirstByUrlId(urlTest.getId());
            var checks = optionalCheck.orElseThrow(() -> new NotFoundResponse("UrlCheck not found"));

            assertThat(response.code()).isEqualTo(STATUS_SUCCESS);
            assertThat(response.body().string()).contains(String.valueOf(checks.getUrlId()));

            assertThat(checks.getTitle()).isEqualTo("Test Title");
            assertThat(checks.getH1()).isEqualTo("Тестовый H1");
            assertThat(checks.getDescription()).isEqualTo("Test content");

            var responseUrls = client.get("/urls");
            assertThat(responseUrls.code()).isEqualTo(STATUS_SUCCESS);
            assertThat(responseUrls.body().string()).contains(String.valueOf(checks.getStatusCode()));
        });
    }

}


