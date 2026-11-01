package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbcTemplate;

    private final GnreRowMapper rowMapper = new GnreRowMapper();

    @Override
    public List<Genre> findAll() {
        var sql = "SELECT id, name FROM genres";
        var genres = jdbcTemplate.query(sql, rowMapper);
        return new ArrayList<>(genres);
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        var sql = "SELECT id, name FROM genres WHERE id IN (:ids)";
        var params = Map.of("ids", ids);
        var genres = jdbcTemplate.query(sql, params, rowMapper);
        return new ArrayList<>(genres);
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            return new Genre(rs.getLong("id"), rs.getString("name"));
        }
    }
}
