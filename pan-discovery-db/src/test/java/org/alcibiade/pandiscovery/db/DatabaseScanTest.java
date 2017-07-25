package org.alcibiade.pandiscovery.db;

import org.alcibiade.pandiscovery.db.dao.AbstractDatabase;
import org.alcibiade.pandiscovery.db.model.DatabaseField;
import org.alcibiade.pandiscovery.db.model.DatabaseTable;
import org.alcibiade.pandiscovery.db.model.DiscoveryReport;
import org.alcibiade.pandiscovery.db.service.DiscoveryService;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.SortedSet;

/**
 * Initialize and scan a database.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseScanTest {

    private Logger logger = LoggerFactory.getLogger(DatabaseScanTest.class);

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AbstractDatabase abstractDatabase;

    @Before
    public void setupData() {
        TableGenerator.createTestContent(jdbcTemplate);
    }

    @After
    public void cleanup() {
        TableGenerator.cleanup(jdbcTemplate);
    }

    @Test
    public void testScan() {
        logger.debug("Scanning a sample database {}", discoveryService);

        // Test database abstraction features

        String dbName = abstractDatabase.getDatabaseName();
        Assertions.assertThat(dbName).isEqualTo("DB");

        SortedSet<DatabaseTable> tables = abstractDatabase.getAllTables(null);
        Assertions.assertThat(tables).hasSize(2);
        Assertions.assertThat(tables.first().getRows()).isEqualTo(9);
        Assertions.assertThat(tables.last().getRows()).isEqualTo(2);

        // Test PAN discovery

        DiscoveryReport report = discoveryService.runDiscovery();
        Assertions.assertThat(report.getFields()).hasSize(2);

        DatabaseField t1Value = report.getFields().stream()
            .filter(f -> f.getTable().getName().equalsIgnoreCase("t1"))
            .findFirst().get();

        Assertions.assertThat(report.getMatches(t1Value).getSampleMatches()).hasSize(4);

        DatabaseField t2Value = report.getFields().stream()
            .filter(f -> f.getTable().getName().equalsIgnoreCase("t2"))
            .findFirst().get();

        Assertions.assertThat(report.getMatches(t2Value).getSampleMatches()).hasSize(1);

    }
}
