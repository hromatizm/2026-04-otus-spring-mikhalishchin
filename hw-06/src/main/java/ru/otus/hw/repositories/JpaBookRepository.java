package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private static final String GRAPH_HINT = "jakarta.persistence.loadgraph";

    private static final String BOOK_AUTHOR_GRAPH = "book-author-entity-graph";

    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        var graph = em.getEntityGraph(BOOK_AUTHOR_GRAPH);
        var book = em.find(Book.class, id, Map.of(GRAPH_HINT, graph));
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var entityGraph = em.getEntityGraph(BOOK_AUTHOR_GRAPH);
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint(GRAPH_HINT, entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        if (em.find(Book.class, book.getId()) == null) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        var book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }
}