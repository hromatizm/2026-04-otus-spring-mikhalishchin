import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.mockito.Mockito.*;

public class TestServiceImplTest {

    private IOService ioService;

    private CsvQuestionDao csvQuestionDao;

    private TestService testService;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        csvQuestionDao = mock(CsvQuestionDao.class);
        testService = new TestServiceImpl(ioService, csvQuestionDao);
    }

    @Test
    @DisplayName("Should print answers in correct order")
    void shouldPrintAnswers() {
        // Arrange
        Question question1 = new Question(
                "question1",
                List.of(
                        new Answer("q1answer1", true),
                        new Answer("q1answer2", false),
                        new Answer("q1answer3", false)
                )
        );
        Question question2 = new Question(
                "question2",
                List.of(
                        new Answer("q2answer1", false),
                        new Answer("q2answer2", true),
                        new Answer("q2answer3", false)
                )
        );
        Question question3 = new Question(
                "question3",
                List.of(
                        new Answer("q3answer1", false),
                        new Answer("q3answer2", false),
                        new Answer("q3answer3", true)
                )
        );
        when(csvQuestionDao.findAll()).thenReturn(
                List.of(question1, question2, question3)
        );

        // Act
        testService.executeTest();

        // Assert
        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");

        inOrder.verify(ioService).printQuestion(question1);
        inOrder.verify(ioService).printQuestion(question2);
        inOrder.verify(ioService).printQuestion(question3);
    }
}
