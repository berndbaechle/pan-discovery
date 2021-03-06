package org.alcibiade.pandiscovery.db.dao;

import org.alcibiade.pandiscovery.db.model.DatabaseField;
import org.alcibiade.pandiscovery.db.model.DatabaseTable;
import org.alcibiade.pandiscovery.db.model.DiscoveryReport;
import org.alcibiade.pandiscovery.scan.DetectionResult;
import org.alcibiade.pandiscovery.scan.Detector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Database access abstraction
 */
@Component
public class AbstractDatabase {
    private static final String DEFAULT_DB_NAME = "DB";
    private Vendor vendor;

    private final int fetchSize;
    private Logger logger = LoggerFactory.getLogger(AbstractDatabase.class);
    private JdbcTemplate jdbcTemplate;
    private SchemaBlacklist schemaBlacklist;
    private SortedSet<DatabaseTable> allTables = new TreeSet<>();
    private DatabaseProcessMonitor processMonitor;

    @Autowired
    public AbstractDatabase(
        @Value("${pan-discovery.db.fetchsize:100}") int fetchSize,
        JdbcTemplate jdbcTemplate, SchemaBlacklist schemaBlacklist,
        DatabaseProcessMonitor processMonitor) {
        this.fetchSize = fetchSize;
        this.jdbcTemplate = jdbcTemplate;
        this.schemaBlacklist = schemaBlacklist;
        this.processMonitor = processMonitor;
    }

    @PostConstruct
    public void init() throws MetaDataAccessException {
        JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(), databaseMetaData -> {
            logger.info("Detected database is {} / {}",
                databaseMetaData.getDatabaseProductName(),
                databaseMetaData.getDatabaseProductVersion());

            if ("Oracle".equalsIgnoreCase(databaseMetaData.getDatabaseProductName())) {
                this.vendor = Vendor.ORACLE;
            }

            return null;
        });
    }

    @Transactional(readOnly = true)
    public String getDatabaseName() {
        String dbName = DEFAULT_DB_NAME;

        if (vendor == Vendor.ORACLE) {
            dbName = this.jdbcTemplate.queryForObject("select value from v$parameter where name = 'db_name'", String.class);
        }

        return dbName;
    }

    private enum Vendor {ORACLE}

    @Transactional(readOnly = true)
    public SortedSet<DatabaseTable> getAllTables(String prefix) {
        final String[] objectTypes = {"TABLE"};
        SortedSet<DatabaseTable> allTables = new TreeSet<>();

        try {
            JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(), databaseMetaData -> {
                try (ResultSet tablesResultSet = databaseMetaData.getTables(
                    null, null, null, objectTypes)) {

                    while (tablesResultSet.next()) {
                        String owner = tablesResultSet.getString("TABLE_SCHEM");
                        String name = tablesResultSet.getString("TABLE_NAME");
                        DatabaseTable dbTable = new DatabaseTable(owner, name);
                        if (schemaBlacklist.acceptsSchema(dbTable)) {
                            allTables.add(dbTable);
                        }
                    }

                    return null;
                }
            });
        } catch (MetaDataAccessException e) {
            logger.warn("Issue while loading database meta data: {}", e.getLocalizedMessage());
        }

        Iterator<DatabaseTable> tableIterator = allTables.iterator();
        int progressCounter = 0;

        while (tableIterator.hasNext()) {
            DatabaseTable t = tableIterator.next();
            logger.trace("Counting rows for {}", t);
            try {
                BigDecimal rows = jdbcTemplate.queryForObject("select count(*) from " + t.toString(), BigDecimal.class);
                t.setRows(rows);
                progressCounter += 1;
                processMonitor.setMessage("Counting table records", progressCounter, allTables.size());
            } catch (DataAccessException e) {
                logger.warn("Could not read table {}, ignoring it", t);
                tableIterator.remove();
            }
        }

        processMonitor.setMessage(null);

        return allTables;
    }

    @Transactional(readOnly = true)
    public ScanResult scan(DatabaseTable table, Set<Detector> detectors, DiscoveryReport report) {
        ScannerCallback callback = new ScannerCallback(table, detectors, report);

        try {
            DataSource dataSource = jdbcTemplate.getDataSource();

            if (logger.isTraceEnabled() && dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                org.apache.tomcat.jdbc.pool.DataSource tcDs = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;

                logger.trace("Reading table {} on pool: Active={}/{}, Idle={}",
                    table, tcDs.getActive(), tcDs.getMaxActive(), tcDs.getIdle());
            }

            jdbcTemplate.setFetchSize(fetchSize);
            jdbcTemplate.query("select * from " + table, callback);
        } catch (DataAccessException e) {
            logger.warn(e.getLocalizedMessage());
        }

        return new ScanResult(callback.getRowCount(), callback.getMatches());
    }

    private class ScannerCallback extends RowCountCallbackHandler {
        private DatabaseTable table;
        private Set<Detector> detectors;
        private DiscoveryReport report;
        private long matches = 0;

        public ScannerCallback(DatabaseTable table, Set<Detector> detectors, DiscoveryReport report) {
            this.table = table;
            this.detectors = detectors;
            this.report = report;
        }

        public long getMatches() {
            return matches;
        }

        @Override
        public void processRow(ResultSet rs, int rowNum) throws SQLException {
            for (int col = 0; col < getColumnCount(); col++) {
                int type = getColumnTypes()[col];

                if (type != Types.CHAR && type != Types.VARCHAR) {
                    continue;
                }

                String value = rs.getString(col + 1);
                if (value == null) {
                    continue;
                }

                for (Detector detector : detectors) {
                    DetectionResult result = detector.detectMatch(value);
                    if (result != null) {
                        DatabaseField field = new DatabaseField(table, getColumnNames()[col]);
                        report.report(field, result.getCardType(), result.getSample(), result.getSampleLine());
                        logger.trace("Reporting {} as {} in {}", value, result.getCardType(), field);
                        matches += 1;
                    }
                }
            }
        }
    }
}
