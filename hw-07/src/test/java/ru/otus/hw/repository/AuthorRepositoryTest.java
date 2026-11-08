package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с авторами")
@DataJpaTest
class AuthorRepositoryTest {

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @Autowired
    private AuthorRepository authorRepository;

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnAllAuthors() {
        var actual = authorRepository.findAll();

        assertThat(actual).containsExactlyElementsOf(dbAuthors);
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        for (var expected : dbAuthors) {
            var actual = authorRepository.findById(expected.getId());

            assertThat(actual).isPresent()
                    .get()
                    .isEqualTo(expected);
        }
    }

    @DisplayName("должен возвращать пустой Optional, если автора нет")
    @Test
    void shouldReturnEmptyIfAuthorNotFound() {
        assertThat(authorRepository.findById(100L)).isEmpty();
    }

    private static List<Author> getDbAuthors() {
        return List.of(
                new Author(1L, "Author_1"),
                new Author(2L, "Author_2"),
                new Author(3L, "Author_3")
        );
    }
}