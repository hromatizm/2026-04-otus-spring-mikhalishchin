package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations jdbcTemplate;
    private final AuthorRowMapper authorRowMapper = new AuthorRowMapper();

    @Override
    public List<Author> findAll() {
        var sql = "SELECT id, full_name FROM authors";
        var authors = jdbcTemplate.query(sql, authorRowMapper);
        return new ArrayList<>(authors);
    }

    @Override
    public Optional<Author> findById(long id) {
        var sql = "SELECT id, full_name FROM authors WHERE ID = :id";
        var params = Map.of("id", id);
        var author = jdbcTemplate.queryForObject(sql, params, authorRowMapper);
        return Optional.ofNullable(author);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(rs.getLong("id"), rs.getString("full_name"));
        }
    }
}
