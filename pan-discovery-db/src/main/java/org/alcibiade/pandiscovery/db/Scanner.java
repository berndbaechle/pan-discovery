package org.alcibiade.pandiscovery.db;

import org.alcibiade.pandiscovery.db.command.DiscoveryCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point.
 */

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"org.alcibiade.pandiscovery.db", "org.alcibiade.pandiscovery.scan"})
public class Scanner {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Scanner.class);

        try (ConfigurableApplicationContext context = SpringApplication.run(Scanner.class, args)) {
            DiscoveryCommand command = context.getBean(DiscoveryCommand.class);
            command.runScan();
        } catch (Throwable t) {
            while (t.getCause() != null) {
                t = t.getCause();
            }

            logger.info(t.getLocalizedMessage());
        }
    }
}
