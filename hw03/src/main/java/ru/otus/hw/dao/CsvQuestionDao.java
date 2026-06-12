package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<QuestionDto> dtos = parseQuestions();
        List<Question> questions = dtos
                .stream()
                .map(QuestionDto::toDomainObject)
                .toList();
        return new ArrayList<>(questions);
    }

    private List<QuestionDto> parseQuestions() {
        List<QuestionDto> dtos;
        String fileName = fileNameProvider.getTestFileName();
        ClassPathResource resource = new ClassPathResource(fileName);

        try (Reader reader = new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8)) {
            dtos = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new QuestionReadException("Failed to read questions from: " + fileName, e);
        }
        return dtos;
    }
}
