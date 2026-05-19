package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

public interface IOService {
    void printLine(String s);

    void printFormattedLine(String s, Object ...args);

    void printQuestion(Question question);
}
