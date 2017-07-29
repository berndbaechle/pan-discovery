package org.alcibiade.pandiscovery.fs;

import org.alcibiade.pandiscovery.fs.scan.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Export filesystem report.
 */
@Component
public class FsCsvExportService {
    private Logger logger = LoggerFactory.getLogger(FsCsvExportService.class);

    private Date reportDateStart;
    private long filesExplored = 0;
    private long pansDetected = 0;
    private Path csvFilePathFiles;
    private Path csvFilePathSamples;
    private RuntimeParameters runtimeParameters;

    @Autowired
    public FsCsvExportService(RuntimeParameters runtimeParameters) {
        this.runtimeParameters = runtimeParameters;
    }

    @PostConstruct
    public void init() throws IOException {
        reportDateStart = new Date();
        String d = new SimpleDateFormat("yyyy-MM-dd_HHmm").format(reportDateStart);
        this.csvFilePathFiles = Paths.get("PAN_Discovery_" + d + "-files.csv");
        this.csvFilePathSamples = Paths.get("PAN_Discovery_" + d + "-samples.csv");
        logger.info("Results will be logged in {} and {}", this.csvFilePathFiles, this.csvFilePathSamples);

        Files.write(csvFilePathFiles,
            Collections.singleton("File;Total Lines;High Confidence Matches; High Confidence Matches;Content Type"),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);

        Files.write(csvFilePathSamples,
            Collections.singleton("File;Line;Confidence;PAN"),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Path getCsvFilePathFiles() {
        return csvFilePathFiles;
    }

    public Path getCsvFilePathSamples() {
        return csvFilePathSamples;
    }

    public long getFilesExplored() {
        return filesExplored;
    }

    public long getPansDetected() {
        return pansDetected;
    }

    public void register(Path file, String contentType, ScanResult result) {

        long totalMatches = result.getMatchesHigh() + result.getMatchesLow();

        if (runtimeParameters.isVerbose() && totalMatches > 0) {
            logger.info(String.format("%5d results in %s", totalMatches, file));
        }

        filesExplored += 1;
        pansDetected += totalMatches;

        if (totalMatches > 0) {
            try {
                writeFileRecord(file, contentType, result);
                writeSamplesRecord(result);
            } catch (IOException e) {
                throw new IllegalStateException("Could not write reports", e);
            }
        }
    }

    private void writeFileRecord(Path file, String contentType, ScanResult result) throws IOException {
        String row = String.format("%s;%d;%d;%d;%s",
            file.toString(),
            result.getTotalLines(),
            result.getMatchesHigh(),
            result.getMatchesLow(),
            contentType
        );

        List<String> rows = new ArrayList<>();
        rows.add(row);

        Files.write(csvFilePathFiles,
            rows,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND);
    }

    private void writeSamplesRecord(ScanResult scanResult) throws IOException {

        List<String> rows = scanResult.getSampleSet().stream()
            .map(sample -> String.format("%s;%d;%s;%s",
                sample.getFile().toString(),
                sample.getLine(),
                sample.getConfidence(),
                sample.getDetectedPan()))
            .collect(Collectors.toList());

        Files.write(csvFilePathSamples,
            rows,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND);
    }
}
