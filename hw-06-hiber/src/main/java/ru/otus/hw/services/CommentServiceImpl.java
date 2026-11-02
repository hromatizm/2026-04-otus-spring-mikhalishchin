package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        var comment = commentRepository.findById(id);
        comment.ifPresent(c -> Hibernate.initialize(c.getBook()));
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(long bookId) {
        var comments = commentRepository.findByBookId(bookId);
        comments.forEach(c -> Hibernate.initialize(c.getBook()));
        return comments;
    }

    @Override
    @Transactional
    public Comment insert(String text, long bookId) {
        return save(0L, text, bookId);
    }

    @Override
    @Transactional
    public Comment update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id %d not found".formatted(bookId)
                ));

        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }
}