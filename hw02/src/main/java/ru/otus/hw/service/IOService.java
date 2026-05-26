package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

public interface IOService {
    void printLine(String s);

    void printFormattedLine(String s, Object ...args);

    void printQuestion(Question question);

    String readString();

    String readStringWithPrompt(String prompt);

    int readIntForRange(int min, int max, String errorMessage);

    int readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage);
}
