package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

public interface LocalizedIOService extends LocalizedMessagesService, IOService {

    void printLineLocalized(String code);

    void printFormattedLineLocalized(String code, Object ...args);

    String readStringWithPromptLocalized(String promptCode);

    void printQuestionLocalized(Question question);

    int readIntForRangeLocalized(int min, int max, String errorMessageCode);

    int readIntForRangeWithPromptLocalized(int min, int max, String promptCode, String errorMessageCode);
}
