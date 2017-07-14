package org.alcibiade.pandiscovery.db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Log progress on long database operations.
 */
@Component
public class DatabaseProcessMonitor {

    private Logger logger;
    private String message;

    public DatabaseProcessMonitor() {
        logger = LoggerFactory.getLogger(DatabaseProcessMonitor.class);
    }

    protected DatabaseProcessMonitor(Logger logger) {
        this.logger = logger;
    }

    @Scheduled(fixedRate = 5000L, initialDelay = 5000L)
    public void displayUpdateLog() {
        String m = this.message;
        if (m != null) {
            logger.debug(message);
        }
    }

    public void setMessage(String message) {
        this.setMessage(message, 0, 0);
    }

    public void setMessage(String message, int itemsDone, int itemsTotal) {
        if (itemsTotal > 0) {
            this.message = String.format("%s: %d out of %d (%d%%)",
                message, itemsDone, itemsTotal, 100 * itemsDone / itemsTotal);
        } else {
            this.message = message;
        }
    }
}
