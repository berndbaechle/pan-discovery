package org.alcibiade.pandiscovery.fs.scan;

import org.alcibiade.pandiscovery.fs.FsCsvExportService;
import org.alcibiade.pandiscovery.fs.RuntimeParameters;
import org.alcibiade.pandiscovery.scan.Detector;
import org.alcibiade.pandiscovery.scan.Luhn;
import org.alcibiade.pandiscovery.scan.VisaDetector;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileScanningServiceTest {

    @Test
    public void testServiceOperation() {
        Luhn luhn = new Luhn();
        RuntimeParameters runtimeParameters = new RuntimeParameters();

        Set<Detector> cardDetectors = new HashSet<>();
        cardDetectors.add(new VisaDetector(luhn));

        FsCsvExportService exportServiceMockup = Mockito.mock(FsCsvExportService.class);

        FileScanningService scanningService = new FileScanningService(cardDetectors, exportServiceMockup, runtimeParameters);
        scanningService.scan(Paths.get("./samples/Cards.txt"));

        Mockito.verify(exportServiceMockup, Mockito.times(1)).register(
            Mockito.anyObject(), Mockito.anyString(),
            Mockito.eq(new ScanResult("4783853934638427", "The card is 4783853934638427, yeah !", 2, 1, 0))
        );
    }
}
