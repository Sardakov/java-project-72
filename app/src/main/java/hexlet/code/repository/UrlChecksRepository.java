package hexlet.code.repository;
import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlChecksRepository {
    public static void saveUrlCheck(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks (url_id, status_code, title, h1, "
                + "description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
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

    public static List<UrlCheck> getUrlCheck() throws SQLException {
        var sql = "SELECT * FROM url_checks";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getLong("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var urlId = resultSet.getLong("url_id");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }

    public static Optional<UrlCheck> findFirstByUrlId(Long idUrl) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? LIMIT 1";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idUrl);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getLong("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var urlId = resultSet.getLong("url_id");
                var createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
                urlCheck.setId(id);
                return Optional.of(urlCheck);
            }
        }
        return Optional.empty();
    }

    public static List<UrlCheck> getUrlCheckPart() throws SQLException {
        var sql = "SELECT * FROM url_checks";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getLong("status_code");
                var createdAt = resultSet.getTimestamp("created_at");
                var urlId = resultSet.getLong("url_id");
                var url = new UrlCheck(statusCode, createdAt);
                url.setId(id);
                url.setUrlId(urlId);
                result.add(url);
            }
            return result;
        }
    }
}
