package org.alcibiade.pandiscovery.fs.scan;

import org.alcibiade.pandiscovery.fs.FsCsvExportService;
import org.alcibiade.pandiscovery.fs.RuntimeParameters;
import org.alcibiade.pandiscovery.scan.Detector;
import org.alcibiade.pandiscovery.scan.Luhn;
import org.alcibiade.pandiscovery.scan.MasterCardDetector;
import org.alcibiade.pandiscovery.scan.VisaDetector;
import org.alcibiade.pandiscovery.scan.text.Confidence;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileScanningServiceTest {

    @Test
    public void testServiceOperationOnSingle() {
        // Context components

        Luhn luhn = new Luhn();
        RuntimeParameters runtimeParameters = new RuntimeParameters();

        Set<Detector> cardDetectors = new HashSet<>();
        cardDetectors.add(new VisaDetector(luhn));

        // Export service Mockup

        FsCsvExportService exportServiceMockup = Mockito.mock(FsCsvExportService.class);

        // Instantiate the service

        FileScanningService scanningService = new FileScanningService(cardDetectors, exportServiceMockup, runtimeParameters);

        // Run a scan

        Path path = Paths.get("./samples/Cards.txt");
        scanningService.scan(path);

        // Check outputs

        Mockito.verify(exportServiceMockup, Mockito.times(1)).register(
            Mockito.anyObject(), Mockito.anyString(),
            Mockito.eq(new ScanResult(SampleSet.singleton(
                new Sample(path, 1, Confidence.HIGH, "4783853934638427")), 2, 1, 0))
        );
    }

    @Test
    public void testServiceOperationOnMultiple() {
        // Context components

        Luhn luhn = new Luhn();
        RuntimeParameters runtimeParameters = new RuntimeParameters();

        Set<Detector> cardDetectors = new HashSet<>();
        cardDetectors.add(new VisaDetector(luhn));
        cardDetectors.add(new MasterCardDetector(luhn));

        // Export service Mockup

        FsCsvExportService exportServiceMockup = Mockito.mock(FsCsvExportService.class);

        // Instantiate the service

        FileScanningService scanningService = new FileScanningService(cardDetectors, exportServiceMockup, runtimeParameters);

        // Run a scan

        Path path = Paths.get("./samples/Multiple Data.txt");
        scanningService.scan(path);

        // Check outputs

        ArgumentCaptor<ScanResult> scanResultArgumentCaptor = ArgumentCaptor.forClass(ScanResult.class);
        Mockito.verify(exportServiceMockup, Mockito.times(1))
            .register(Mockito.anyObject(), Mockito.anyString(), scanResultArgumentCaptor.capture());

        ScanResult scanResult = scanResultArgumentCaptor.getValue();

        Assertions.assertThat(scanResult.getTotalLines()).isEqualTo(59);
        Assertions.assertThat(scanResult.getMatchesHigh()).isEqualTo(40);
        Assertions.assertThat(scanResult.getMatchesLow()).isEqualTo(2);
        Assertions.assertThat(scanResult.getSampleSet()).hasSize(42);
    }
}
