package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestService;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class TestServiceImplTest {

    @MockitoBean
    private IOService ioService;

    @MockitoBean
    private CsvQuestionDao csvQuestionDao;

    @InjectMocks
    private TestService testService;

    @Test
    @DisplayName("Should print answers in correct order")
    void shouldPrintAnswers() {
        var student = new Student("some_name", "some_surname");
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
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(2);

        // Act
        testService.executeTestFor(student);

        // Assert
        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");

        inOrder.verify(ioService).printQuestion(question1);
        inOrder.verify(ioService).printQuestion(question2);
        inOrder.verify(ioService).printQuestion(question3);
    }
}
