package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({JpaBookRepository.class, JpaCommentRepository.class, CommentServiceImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @DisplayName("должен загружать комментарий по id с инициализированной книгой")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldReturnCorrectCommentById(Comment expectedComment) {
        var actualComment = commentService.findById(expectedComment.getId())
                .orElseThrow();

        assertThat(actualComment)
                .isEqualTo(expectedComment)
                .usingRecursiveComparison();
    }

    @DisplayName("должен загружать комментарии книги с инициализированной книгой")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void shouldReturnCorrectCommentsByBookId(long bookId) {
        var actualComments = commentService.findByBookId(bookId);

        var expectedComments = getDbComments().stream()
                .filter(comment -> comment.getBook().getId() == bookId)
                .sorted(Comparator.comparingLong(Comment::getId))
                .toList();

        assertThat(actualComments)
                .containsExactlyInAnyOrderElementsOf(expectedComments)
                .usingRecursiveComparison();
    }

    private static List<Comment> getDbComments() {
        var firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("BookTitle_1");

        var secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("BookTitle_2");

        var thirdBook = new Book();
        thirdBook.setId(3L);
        thirdBook.setTitle("BookTitle_3");

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