package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public final class AppTest {
    private static Javalin app;
    private final int statusSuccess = 200;

    @BeforeEach
    public void setUp() throws Exception {
        System.clearProperty("JDBC_DATABASE_URL");
        app = App.getApp();

    }

    @Test
    void testMainPage() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(statusSuccess);
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(statusSuccess);
        });
    }

    @Test
    public void testCreateCourse() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com/23421354";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(statusSuccess);
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com/23421354";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(statusSuccess);
            assertThat(response.body().string()).contains("https://www.example.com");
        });
    }

    @Test
    public void testGetUrlById() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com/23421354";
            var postResponse = client.post("/urls", requestBody);

            assertThat(postResponse.code()).isEqualTo(statusSuccess);

            var getResponse = client.get("/urls/1");

            assertThat(getResponse.code()).isEqualTo(statusSuccess);
            assertThat(getResponse.body().string()).contains("https://www.example.com");
        });
    }
}
