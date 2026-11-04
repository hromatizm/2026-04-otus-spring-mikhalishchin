package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        var comment = em.find(Comment.class, id);
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.book.id = :bookId", Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        if (em.find(Comment.class, comment.getId()) == null) {
            throw new EntityNotFoundException("Comment with id %d not found".formatted(comment.getId()));
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        var comment = em.find(Comment.class, id);

        if (comment != null) {
            em.remove(comment);
        }
    }
}