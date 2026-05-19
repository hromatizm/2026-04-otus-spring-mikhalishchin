package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

import java.io.PrintStream;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    public StreamsIOService(PrintStream printStream) {

        this.printStream = printStream;
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public void printQuestion(Question question) {
        printFormattedLine("Question:");
        printFormattedLine("    %s", question.text());
        printFormattedLine("Answers:");

        for (int i = 0; i < question.answers().size(); i++) {
            String answer = question.answers().get(i).text();
            int answerNum = i + 1;
            printFormattedLine("    %d) %s", answerNum, answer);
        }

        printLine("");
    }
}
