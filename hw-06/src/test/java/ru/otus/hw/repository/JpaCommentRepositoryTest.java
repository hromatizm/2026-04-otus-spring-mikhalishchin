package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@DisplayName("Репозиторий на основе JPA для работы с комментариями ")
@DataJpaTest
@Import(JpaCommentRepository.class)
class JpaCommentRepositoryTest {

    private static final long NON_EXISTING_ID = 100L;

    @Autowired
    private JpaCommentRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbComments = getDbComments();
    }

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        for (var expected : dbComments) {
            var actual = repositoryJpa.findById(expected.getId());

            assertThat(actual).isPresent();
            assertCommentsEquals(actual.orElseThrow(), expected);
        }
    }

    @DisplayName("должен возвращать пустой Optional, если комментария нет")
    @Test
    void shouldReturnEmptyIfCommentNotFound() {
        assertThat(repositoryJpa.findById(NON_EXISTING_ID)).isEmpty();
    }

    @DisplayName("должен загружать комментарии по id книги")
    @Test
    void shouldReturnCommentsByBookId() {
        for (long bookId = 1L; bookId <= 3L; bookId++) {
            var currentBookId = bookId;

            var expected = dbComments.stream()
                    .filter(comment ->
                            comment.getBook().getId() == currentBookId)
                    .map(comment -> tuple(
                            comment.getId(),
                            comment.getText(),
                            comment.getBook().getId()
                    ))
                    .toList();

            var actual = repositoryJpa.findByBookId(bookId);

            assertThat(actual)
                    .as("Комментарии книги с id = %d", bookId)
                    .extracting(
                            Comment::getId,
                            Comment::getText,
                            comment -> comment.getBook().getId()
                    )
                    .containsExactlyInAnyOrderElementsOf(expected);
        }
    }

    @DisplayName("должен возвращать пустой список для несуществующей книги")
    @Test
    void shouldReturnEmptyListForBookWithoutComments() {
        var comments = repositoryJpa.findByBookId(NON_EXISTING_ID);

        assertThat(comments).isEmpty();
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = em.find(Book.class, 3L);
        var comment = new Comment(0L, "New comment", book);

        var saved = repositoryJpa.save(comment);
        var savedId = saved.getId();

        assertThat(savedId).isPositive();

        var actual = em.find(Comment.class, savedId);

        assertThat(actual).isNotNull();
        assertThat(actual.getText()).isEqualTo("New comment");
        assertThat(actual.getBook().getId()).isEqualTo(3L);
    }

    @DisplayName("должен обновлять существующий комментарий")
    @Test
    void shouldUpdateExistingComment() {
        var book = em.find(Book.class, 2L);
        var updatedComment = new Comment(3L, "Updated comment", book);

        var saved = repositoryJpa.save(updatedComment);

        assertThat(saved.getId()).isEqualTo(3L);

        var actual = em.find(Comment.class, 3L);

        assertThat(actual).isNotNull();
        assertThat(actual.getText()).isEqualTo("Updated comment");
        assertThat(actual.getBook().getId()).isEqualTo(2L);
    }

    @DisplayName("должен выбрасывать исключение при обновлении отсутствующего комментария")
    @Test
    void shouldThrowIfUpdatedCommentNotFound() {
        var book = em.find(Book.class, 1L);
        var comment = new Comment(
                NON_EXISTING_ID,
                "Updated comment",
                book
        );

        assertThatThrownBy(() -> repositoryJpa.save(comment))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        "Comment with id %d not found"
                                .formatted(NON_EXISTING_ID)
                );
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        assertThat(em.find(Comment.class, 1L)).isNotNull();

        repositoryJpa.deleteById(1L);

        assertThat(em.find(Comment.class, 1L)).isNull();
        assertThat(em.find(Comment.class, 2L)).isNotNull();
        assertThat(em.find(Comment.class, 3L)).isNotNull();
    }

    private static void assertCommentsEquals(
            Comment actual,
            Comment expected
    ) {
        assertThat(actual.getId())
                .isEqualTo(expected.getId());

        assertThat(actual.getText())
                .isEqualTo(expected.getText());

        assertThat(actual.getBook().getId())
                .isEqualTo(expected.getBook().getId());
    }

    private static List<Comment> getDbComments() {
        var firstBook = new Book();
        firstBook.setId(1L);

        var secondBook = new Book();
        secondBook.setId(2L);

        var thirdBook = new Book();
        thirdBook.setId(3L);

        return List.of(
                new Comment(1L, "Comment_1_1", firstBook),
                new Comment(2L, "Comment_1_2", secondBook),
                new Comment(3L, "Comment_2_2", secondBook),
                new Comment(4L, "Comment_1_3", thirdBook),
                new Comment(5L, "Comment_2_3", thirdBook),
                new Comment(6L, "Comment_3_3", thirdBook)
        );
    }
}