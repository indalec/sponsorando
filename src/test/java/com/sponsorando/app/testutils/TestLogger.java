package com.sponsorando.app.testutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);

    public static void info(String message) {
        logger.info("[TEST] " + message);
    }

    public static void debug(String message) {
        logger.debug("[TEST] " + message);
    }
}

