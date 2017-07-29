package org.alcibiade.pandiscovery.fs.scan;

import org.alcibiade.pandiscovery.fs.FsCsvExportService;
import org.alcibiade.pandiscovery.fs.RuntimeParameters;
import org.alcibiade.pandiscovery.scan.Detector;
import org.alcibiade.pandiscovery.scan.text.Confidence;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Threaded file scanning service.
 */
@Component
public class FileScanningService {
    private final Set<Detector> cardDetectors;
    private final FsCsvExportService exportService;
    private Logger logger = LoggerFactory.getLogger(FileScanningService.class);
    private Tika tika = new Tika();
    private RuntimeParameters runtimeParameters;
    private Pattern ignoredFiles = Pattern.compile(".*\\.pack");
    private Set<String> ignoredMediaTypes = Collections.singleton("application/zlib");

    @Autowired
    public FileScanningService(Set<Detector> cardDetectors,
                               FsCsvExportService exportService,
                               RuntimeParameters runtimeParameters) {
        this.cardDetectors = cardDetectors;
        this.exportService = exportService;
        this.runtimeParameters = runtimeParameters;
    }

    public void scan(Path path) {
        if (runtimeParameters.isVerbose()) {
            logger.debug("Scanning {}", path);
        }

        if (ignoredFiles.matcher(path.getFileName().toString()).matches()) {
            logger.trace("Ignoring file {}", path);
            return;
        }

        String mediaType;

        try {
            mediaType = tika.detect(path);
            if (ignoredMediaTypes.contains(mediaType)) {
                logger.trace("Ignoring file {} of type {}", path, mediaType);
                return;
            }
        } catch (IOException e) {
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }

            logger.warn("Failed detect type of {} : {}", path, t.getLocalizedMessage());
            return;
        }

        try (Reader reader = tika.parse(path)) {
            LineCounter lineCounter = new LineCounter();

            BufferedReader bufferedReader = new BufferedReader(reader);
            ScanResult result = bufferedReader.lines()
                .map(line -> new LineWrapper(path, lineCounter.getNextLineNumber(), line))
                .map(this::scan)
                .reduce(
                    ScanResult.EMPTY,
                    ScanResult::reduce
                );

            exportService.register(path, mediaType, result);
        } catch (IOException | UncheckedIOException e) {
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }

            logger.warn("Failed to scan {} : {}", path, t.getLocalizedMessage());
        }
    }

    private ScanResult scan(LineWrapper line) {

        ScanResult result = cardDetectors.stream()
            .map(detector -> detector.detectMatch(line.getText()))
            .filter(Objects::nonNull)
            .map(m -> new ScanResult(
                SampleSet.singleton(
                    new Sample(line.getFile(), line.getLineNumber(), m.getConfidence(), m.getSample())
                ), 1,
                m.getConfidence() == Confidence.HIGH ? 1 : 0,
                m.getConfidence() == Confidence.LOW ? 1 : 0)
            )
            .reduce(
                new ScanResult(new SampleSet(), 1, 0, 0),
                ScanResult::reduceOnSingleLine
            );

        if (logger.isTraceEnabled()) {
            logger.trace("{}", String.format("%3d/%3d - %s", result.getMatchesHigh(), result.getMatchesLow(), line));
        }

        return result;
    }

}
