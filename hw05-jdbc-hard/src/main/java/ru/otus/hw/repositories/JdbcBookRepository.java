package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbcTemplate;

    private final BookRowMapper bookRowMapper = new BookRowMapper();

    private final BookResultSetExtractor bookResultSetExtractor = new BookResultSetExtractor();

    @Override
    public Optional<Book> findById(long id) {
        var sql = """
                SELECT
                    b.id AS id,
                    b.title AS title,
                    b.author_id AS author_id,
                    a.full_name AS author_full_name,
                    g.id AS genre_id,
                    g.name AS genre_name
                FROM books AS b
                JOIN authors AS a
                    ON b.author_id = a.id
                JOIN books_genres AS bg
                    ON b.id = bg.book_id
                JOIN genres AS g
                    ON bg.genre_id = g.id
                WHERE b.id = :id
                """;
        var params = Map.of("id", id);
        var book = jdbcTemplate.query(sql, params, bookResultSetExtractor);
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var sql = "DELETE FROM books WHERE id = :id";
        var params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        var sql = """
                SELECT
                    b.id AS id,
                    b.title AS title,
                    b.author_id AS author_id,
                    a.full_name AS author_full_name
                FROM books AS b
                JOIN authors AS a
                    ON b.author_id = a.id
                """;
        var books = jdbcTemplate.query(sql, bookRowMapper);
        return new ArrayList<>(books);
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return new ArrayList<>();
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genresMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
        Map<Long, List<BookGenreRelation>> relationGroups =
                relations.stream().collect(Collectors.groupingBy(BookGenreRelation::bookId));
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
        booksWithoutGenres.forEach(book -> {
            var group = relationGroups.get(book.getId());
            if (group != null) {
                extracted(book, group, genresMap);
            }
        });
    }

    private static void extracted(Book book, List<BookGenreRelation> group, Map<Long, Genre> genresMap) {
        book.getGenres()
                .addAll(group.stream()
                        .map(relation -> genresMap.get(relation.genreId()))
                        .toList()
                );
    });


}

private Book insert(Book book) {
    var keyHolder = new GeneratedKeyHolder();
    var sql = """
            INSERT INTO books (title, author_id)
            VALUES (:title, :authorId)
            """;
    var params = new MapSqlParameterSource();
    params.addValue("title", book.getTitle());
    params.addValue("authorId", book.getAuthor().getId());
    jdbcTemplate.update(sql, params, keyHolder);

    //noinspection DataFlowIssue
    book.setId(keyHolder.getKeyAs(Long.class));
    batchInsertGenresRelationsFor(book);
    return book;
}

private Book update(Book book) {
    checkExistence(book);
    executeUpdate(book);
    removeGenresRelationsFor(book);
    batchInsertGenresRelationsFor(book);

    return book;
}

private void executeUpdate(Book book) {
    var sql = """
            UPDATE books
            SET title = :title, author_id = :author_id
            WHERE id = :id
            """;
    var params = Map.of(
            "title", book.getTitle(),
            "author_id", book.getAuthor().getId()
    );
    jdbcTemplate.update(sql, params);
}

private void checkExistence(Book book) {
    var findSql = "SELECT id FROM books WHERE id = :id";
    var params = Map.of("id", book.getId());
    var id = jdbcTemplate.queryForObject(findSql, params, Long.class);
    if (id == null) {
        throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
    }
}

private void batchInsertGenresRelationsFor(Book book) {
    // Использовать метод batchUpdate
}

private void removeGenresRelationsFor(Book book) {
    //...
}

private static class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                new Author(
                        rs.getLong("author_id"),
                        rs.getString("author_full_name")
                ),
                Collections.emptyList()
        );
    }
}

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

    @Override
    public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
        Book book = null;
        List<Genre> genres = new ArrayList<>();

        while (rs.next()) {
            if (book == null) {
                book = new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        new Author(
                                rs.getLong("author_id"),
                                rs.getString("author_full_name")
                        ),
                        genres
                );
            }

            long genreId = rs.getLong("genre_id");
            if (!rs.wasNull()) {
                genres.add(new Genre(genreId, rs.getString("genre_name")));
            }
        }

        return book;
    }
}

private record BookGenreRelation(long bookId, long genreId) {
}
}
