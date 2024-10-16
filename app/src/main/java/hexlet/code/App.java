package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.SessionsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String localBD = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;";
        String testEnv = System.getenv("TEST_ENV");


        if (testEnv != null && testEnv.equals("TEST")) {
            hikariConfig.setJdbcUrl(localBD);
            System.out.println("Using local H2 database for tests.");
        } else if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            hikariConfig.setJdbcUrl(localBD);
            System.out.println("Using local H2 database as default.");
        } else {
            hikariConfig.setJdbcUrl(jdbcUrl);
            System.out.println("Using PostgreSQL database: " + jdbcUrl);
        }

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("schema.sql");

        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.setDataSource(dataSource);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", SessionsController::index);
        app.get("/urls", SessionsController::show);
        app.post("/urls", SessionsController::append);
        app.get(NamedRoutes.showPath("{id}"), SessionsController::showUrl);
        app.post(NamedRoutes.showPath("{id}"), SessionsController::checksUrl);
        app.post(NamedRoutes.checkSite("{id}"), SessionsController::checksUrl);

        return app;
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static void main(String[] args) throws IOException, SQLException {
        Javalin app = getApp();
        app.start(getPort());
    }
}
