package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.StreamsIOService;

@Configuration
public class TestConfig {

    @Bean
    public IOService ioService() {
        return new StreamsIOService(System.out);
    }
}