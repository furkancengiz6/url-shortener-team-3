package edu.kpi.developmentmethods;

import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main application class for our project
 *
 * Run it to start the application
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Entrypoint
     */
    public static void main(String[] args) {
        logger.info("Wake up, Samurai, we have an App to build!");
        Micronaut.run(Application.class, args);
    }
}
