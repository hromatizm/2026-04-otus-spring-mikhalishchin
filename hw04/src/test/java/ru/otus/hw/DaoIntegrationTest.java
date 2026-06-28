package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CsvQuestionDao.class})
public class DaoIntegrationTest {

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao dao;

    @Test
    @DisplayName("Should read questions correctly")
    void shouldReadQuestions() {
        // Arrange
        when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");

        var expectedQuestions = List.of(
                new Question(
                        "Question_1?",
                        List.of(
                                new Answer("Answer_1_1", true),
                                new Answer("Answer_1_2", false),
                                new Answer("Answer_1_3", false)
                        )),
                new Question(
                        "Question_2?",
                        List.of(
                                new Answer("Answer_2_1", false),
                                new Answer("Answer_2_2", true),
                                new Answer("Answer_2_3", false),
                                new Answer("Answer_2_4", false)
                        )),
                new Question(
                        "Question_3?",
                        List.of(
                                new Answer("Answer_3_1", true),
                                new Answer("Answer_3_2", false),
                                new Answer("Answer_3_3", false)
                        )),
                new Question(
                        "Question_4?",
                        List.of(
                                new Answer("Answer_4_1", false),
                                new Answer("Answer_4_2", true),
                                new Answer("Answer_4_3", false),
                                new Answer("Answer_4_4", false)
                        )),
                new Question(
                        "Question_5?",
                        List.of(
                                new Answer("Answer_5_1", true),
                                new Answer("Answer_5_2", false),
                                new Answer("Answer_5_3", false)
                        ))
        );

        // Act
        var actualQuestions = dao.findAll();

        // Assert
        assertThat(actualQuestions).hasSize(5);
        assertThat(actualQuestions).containsExactlyElementsOf(expectedQuestions);
    }
}
