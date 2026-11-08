package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий для работы с книгами")
@DataJpaTest
class BookRepositoryTest {

    private static final long NON_EXISTING_ID = 100L;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = bookRepository.findById(expectedBook.getId());
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));
        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        var existingBook = em.find(Book.class, returnedBook.getId());
        assertThat(existingBook)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));

        var existingBook = em.find(Book.class, expectedBook.getId());

        assertThat(existingBook)
                .isNotNull()
                .isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        var savedBook = em.find(Book.class, returnedBook.getId());

        assertThat(savedBook)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var bookId = 1L;
        var existingBook = em.find(Book.class, bookId);

        assertThat(existingBook).isNotNull();
        bookRepository.deleteById(bookId);

        var deletedBook = em.find(Book.class, bookId);
        assertThat(deletedBook).isNull();
    }

    private static List<Author> getDbAuthors() {
        return List.of(
                new Author(1L, "Author_1"),
                new Author(2L, "Author_2"),
                new Author(3L, "Author_3")
        );
    }

    private static List<Genre> getDbGenres() {
        return List.of(
                new Genre(1L, "Genre_1"),
                new Genre(2L, "Genre_2"),
                new Genre(3L, "Genre_3"),
                new Genre(4L, "Genre_4"),
                new Genre(5L, "Genre_5"),
                new Genre(6L, "Genre_6")
        );
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}