package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, createdAt) VALUES (?, ?)";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            Timestamp timestamp = Timestamp.valueOf(now);
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, timestamp);
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static void saveUrlCheck(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks (url_id, statusCode, title, h1, "
                + "description, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            final int indexUrlId = 1;
            final int indexStatusCode = 2;
            final int indexTitle = 3;
            final int indexH1 = 4;
            final int indexDescription = 5;
            final int indexTimestamp = 6;

            preparedStatement.setLong(indexUrlId, urlCheck.getUrlId());
            preparedStatement.setInt(indexStatusCode, Math.toIntExact(urlCheck.getStatusCode()));
            preparedStatement.setString(indexTitle, urlCheck.getTitle());
            preparedStatement.setString(indexH1, urlCheck.getH1());
            preparedStatement.setString(indexDescription, urlCheck.getDescription());
            var createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(indexTimestamp, Timestamp.valueOf(createdAt));

            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(Timestamp.valueOf(createdAt));
            } else {
                throw new SQLException("DB did not return an id after saving UrlCheck");
            }
        }
    }

    //заменить на что-то по легче
    public static Optional<Url> find(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var title = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new Url(title, createdAt);
                url.setId(id);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static Optional<Url> findId(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new Url(name, createdAt);
                url.setId(id);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var title = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new Url(title, createdAt);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }

    public static List<UrlCheck> getUrlCheck() throws SQLException {
        var sql = "SELECT * FROM url_checks";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getLong("statusCode");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var urlId = resultSet.getLong("url_id");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }

    public static List<UrlCheck> getUrlCheckPart() throws SQLException {
        var sql = "SELECT * FROM url_checks";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getLong("statusCode");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new UrlCheck(statusCode, createdAt);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }
}
