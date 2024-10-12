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
        System.setProperty("TEST_ENV", "TEST");
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
}
