package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.JpaAuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с авторами ")
@DataJpaTest
@Import(JpaAuthorRepository.class)
class JpaAuthorRepositoryTest {

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @Autowired
    private JpaAuthorRepository repositoryJpa;

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnAllAuthors() {
        var actual = repositoryJpa.findAll();

        assertThat(actual).containsExactlyElementsOf(dbAuthors);
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        for (var expected : dbAuthors) {
            var actual = repositoryJpa.findById(expected.getId());

            assertThat(actual).isPresent()
                    .get()
                    .isEqualTo(expected);
        }
    }

    @DisplayName("должен возвращать пустой Optional, если автора нет")
    @Test
    void shouldReturnEmptyIfAuthorNotFound() {
        assertThat(repositoryJpa.findById(100L)).isEmpty();
    }

    private static List<Author> getDbAuthors() {
        return List.of(
                new Author(1L, "Author_1"),
                new Author(2L, "Author_2"),
                new Author(3L, "Author_3")
        );
    }
}