package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class, JpaAuthorRepository.class, BookServiceImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id, все поля книги должны быть инициализированы")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = bookService.findById(expectedBook.getId())
                .orElseThrow();

        assertBooksEquals(actualBook, expectedBook);
    }

    @DisplayName("должен загружать список всех книг с авторами и жанрами")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll().stream()
                .sorted(Comparator.comparingLong(Book::getId))
                .toList();

        var expectedBooks = dbBooks.stream()
                .sorted(Comparator.comparingLong(Book::getId))
                .toList();

        assertThat(actualBooks)
                .zipSatisfy(expectedBooks, BookServiceTest::assertBooksEquals);
    }

    private static void assertBooksEquals(Book actualBook, Book expectedBook) {
        assertThat(actualBook.getId())
                .isEqualTo(expectedBook.getId());

        assertThat(actualBook.getTitle())
                .isEqualTo(expectedBook.getTitle());

        assertThat(actualBook.getAuthor().getId())
                .isEqualTo(expectedBook.getAuthor().getId());

        assertThat(actualBook.getAuthor().getFullName())
                .isEqualTo(expectedBook.getAuthor().getFullName());

        assertThat(actualBook.getGenres())
                .extracting(Genre::getName)
                .containsExactlyInAnyOrderElementsOf(
                        expectedBook.getGenres().stream()
                                .map(Genre::getName)
                                .toList()
                );
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
