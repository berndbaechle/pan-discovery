package org.alcibiade.pandiscovery.db.dao;

import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

/**
 * Database activity monitor.
 */

public class DatabaseProcessMonitorTest {

    @Test
    public void testMonitor() {
        Logger loggerMockup = mock(Logger.class);
        DatabaseProcessMonitor monitor = new DatabaseProcessMonitor(loggerMockup);

        monitor.displayUpdateLog();
        verify(loggerMockup, times(0)).debug(anyString());

        monitor.setMessage("Hello world !");
        verify(loggerMockup, times(0)).debug(anyString());

        monitor.setMessage("Hello world !");
        verify(loggerMockup, times(0)).debug(anyString());

        monitor.displayUpdateLog();
        verify(loggerMockup, times(1)).debug(anyString());
        verify(loggerMockup, times(0)).info(anyString());
        verify(loggerMockup, times(0)).warn(anyString());

        monitor.setMessage(null);
        monitor.displayUpdateLog();
        verify(loggerMockup, times(1)).debug(anyString());
        verify(loggerMockup, times(0)).info(anyString());
        verify(loggerMockup, times(0)).warn(anyString());
    }

    @Test
    public void testWithoutProgress() {
        Logger loggerMockup = mock(Logger.class);
        DatabaseProcessMonitor monitor = new DatabaseProcessMonitor(loggerMockup);
        monitor.setMessage("Hello", 0, 0);
        verify(loggerMockup, times(0)).debug(anyString());
        monitor.displayUpdateLog();
        verify(loggerMockup, times(1)).debug("Hello");
    }

    @Test
    public void testWithProgress() {
        Logger loggerMockup = mock(Logger.class);
        DatabaseProcessMonitor monitor = new DatabaseProcessMonitor(loggerMockup);
        monitor.setMessage("Hello", 2, 3);
        monitor.displayUpdateLog();
        verify(loggerMockup, times(1)).debug("Hello: 2 out of 3 (66%)");
    }
}
