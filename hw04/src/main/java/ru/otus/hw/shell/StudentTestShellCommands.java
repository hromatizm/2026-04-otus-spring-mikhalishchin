package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Student test shell commands")
@RequiredArgsConstructor
public class StudentTestShellCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod("Run student test")
    public void runTest() {
        testRunnerService.run();
    }

}
