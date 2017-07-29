package org.alcibiade.pandiscovery.scan;

import org.alcibiade.pandiscovery.scan.text.Confidence;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class VisaDetectorTest {

    @Test
    public void testVisaDetection() {
        Detector detector = new VisaDetector(new Luhn());
        Assertions.assertThat(detector.detectMatch("Hello")).isNull();
        Assertions.assertThat(detector.detectMatch("5417057527335935")).isNull();
        Assertions.assertThat(detector.detectMatch("6011145506624417")).isNull();
        Assertions.assertThat(detector.detectMatch("6011145506624417")).isNull();
        Assertions.assertThat(detector.detectMatch("+43 662 83 990 660")).isNull();
        Assertions.assertThat(detector.detectMatch("+43 676 8541 8823")).isNull();

        Assertions.assertThat(detector.detectMatch("The card is 4783853934638427, yeah !")).isNotNull();
        Assertions.assertThat(detector.detectMatch("01234567    47838539 34638427 01234567")).isNotNull();

        // PAN should start with a non numeric character
        Assertions.assertThat(detector.detectMatch("4783853934638427, yeah !")).isNotNull();
        Assertions.assertThat(detector.detectMatch("04783853934638427, yeah !")).isNull();
        Assertions.assertThat(detector.detectMatch("0 4783853934638427, yeah !")).isNotNull();
        Assertions.assertThat(detector.detectMatch("C4783853934638427, yeah !")).isNull();

        // PAN should end with a non numeric character
        Assertions.assertThat(detector.detectMatch("Card is 4783853934638427")).isNotNull();
        Assertions.assertThat(detector.detectMatch("Card is 4783853934638427, yeah !")).isNotNull();
        Assertions.assertThat(detector.detectMatch("Card is 4783 8539 3463 8427, yeah !")).isNotNull();
        Assertions.assertThat(detector.detectMatch("Card is 4783 8539 3463 84270, yeah !")).isNull();
        Assertions.assertThat(detector.detectMatch("Card is 47838539346384270, yeah !")).isNull();

        // Sample coming from generated CSV list and was initially not detected
        Assertions.assertThat(detector.detectMatch("4539998389724102")).isNotNull();
        Assertions.assertThat(detector.detectMatch(",4539998389724102")).isNotNull();
        Assertions.assertThat(detector.detectMatch("Visa,4539998389724102")).isNotNull();
    }

    @Test
    public void testVisaDetectionRanking() {
        Detector detector = new VisaDetector(new Luhn());

        Assertions.assertThat(
            detector.detectMatch("FR76 3000 4004 5100 0209 8404 862").getConfidence())
            .isEqualTo(Confidence.LOW);

        Assertions.assertThat(
            detector.detectMatch("40045100020984 04").getConfidence())
            .isEqualTo(Confidence.LOW);

        Assertions.assertThat(
            detector.detectMatch("9 4004510002098404").getConfidence())
            .isEqualTo(Confidence.LOW);

        Assertions.assertThat(
            detector.detectMatch("Visa,4539998389724102").getConfidence())
            .isEqualTo(Confidence.HIGH);

        Assertions.assertThat(
            detector.detectMatch("IBAN: FR12 4326 5532 9027 1375 42").getConfidence())
            .isEqualTo(Confidence.LOW);
    }
}
