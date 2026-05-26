package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaoIntegrationTest {

    private TestFileNameProvider fileNameProvider;

    @BeforeEach
    void setUp() {
        fileNameProvider = mock(TestFileNameProvider.class);
    }

    @Test
    @DisplayName("Should read questions correctly")
    void shouldReadQuestions() {
        // Arrange
        when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");

        var dao = new CsvQuestionDao(fileNameProvider);

        // Act
        dao.findAll();

        // Assert
        assertThat(dao.findAll()).hasSize(5);
    }
}
