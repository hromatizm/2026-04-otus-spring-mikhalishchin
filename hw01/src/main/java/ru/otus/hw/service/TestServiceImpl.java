package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final CsvQuestionDao csvQuestionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = csvQuestionDao.findAll();
        printAll(questions);
    }

    private void printAll(List<Question> questions) {
        questions.forEach(question -> {
            ioService.printFormattedLine("Question:");
            ioService.printFormattedLine("    %s", question.text());
            ioService.printFormattedLine("Answers:");

            for (int i = 0; i < question.answers().size(); i++) {
                String answer = question.answers().get(i).text();
                int answerNum = i + 1;
                ioService.printFormattedLine("    %d) %s", answerNum, answer);
            }

            ioService.printLine("");
        });
    }
}
