package com.sponsorando.app.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @PostConstruct
    public void configureLogging() {
        Logger myClassLogger = (Logger) LoggerFactory.getLogger("com.sponsorando");
        myClassLogger.setLevel(Level.INFO);
    }
}
