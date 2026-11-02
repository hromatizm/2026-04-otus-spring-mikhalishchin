package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaGenreRepository.class})
class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository repositoryJpa;

    private Map<Long, Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен получить все жанры")
    @Test
    void shouldFindAll() {
        var actualGenres = repositoryJpa.findAll();
        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(dbGenres.values());
    }

    @DisplayName("должен получить жанры о списку их id")
    @ParameterizedTest
    @MethodSource("idsArgs")
    void shouldFindAllById(List<Long> ids) {
        var actualGenres = repositoryJpa.findAllByIds(new HashSet<>(ids));
        var expectedGenres = getDbGenresByIds(ids);
        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    static Stream<Arguments> idsArgs() {
        return Stream.of(
                Arguments.of(List.of(1L)),
                Arguments.of(List.of(1L, 2L)),
                Arguments.of(List.of(1L, 2L, 3L)),
                Arguments.of(List.of(4L, 5L, 6L)),
                Arguments.of(List.of(1L, 3L, 5L))
        );
    }

    private static Map<Long, Genre> getDbGenres() {
        return Map.of(
                1L, new Genre(1L, "Genre_1"),
                2L, new Genre(2L, "Genre_2"),
                3L, new Genre(3L, "Genre_3"),
                4L, new Genre(4L, "Genre_4"),
                5L, new Genre(5L, "Genre_5"),
                6L, new Genre(6L, "Genre_6")
        );
    }

    private List<Genre> getDbGenresByIds(List<Long> ids) {
        return ids.stream()
                .map(dbGenres::get)
                .toList();
    }
}