package org.alcibiade.pandiscovery.scan.text;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DigitSequenceExtractorTest {

    private Logger logger = LoggerFactory.getLogger(DigitSequenceExtractorTest.class);

    @Test
    public void testExtraction() {
        DigitSequenceExtractor extractor = new DigitSequenceExtractor();

        Assertions.assertThat(extractor.extractSequences("Hello")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            "0123456789012345")).containsExactly(new Sequence("0123456789012345", Confidence.HIGH));
        Assertions.assertThat(extractor.extractSequences(
            "01;23456789012345")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            "012345678\t9012345")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            "012345678\t0123456789012345")).containsExactly(new Sequence("0123456789012345", Confidence.HIGH));
        Assertions.assertThat(extractor.extractSequences(
            "a0123456789012345")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            ",0123456789012345")).containsExactly(new Sequence("0123456789012345", Confidence.HIGH));
        Assertions.assertThat(extractor.extractSequences(
            "01234567890123456")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            "0a123b45c678d90.12;345")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            "Hello 0123456789012345")).containsExactly(new Sequence("0123456789012345", Confidence.HIGH));
        Assertions.assertThat(extractor.extractSequences(
            "Hello 0123 4567 8901 2345")).containsExactly(new Sequence("0123456789012345", Confidence.HIGH));

        Assertions.assertThat(extractor.extractSequences(
            "Hello 0123 4567  8901 2345")).isEmpty();

        Assertions.assertThat(extractor.extractSequences(
            "Hello 0123 4567-8901 2345")).containsExactly(new Sequence("0123456789012345", Confidence.HIGH));

        Assertions.assertThat(extractor.extractSequences(
            "0123456789012345 4567890123456789"))
            .containsExactly(
                new Sequence("0123456789012345", Confidence.LOW),
                new Sequence("4567890123456789", Confidence.LOW));

        Assertions.assertThat(extractor.extractSequences(
            "0123456789012345  4567890123456789"))
            .containsExactly(
                new Sequence("0123456789012345", Confidence.HIGH),
                new Sequence("4567890123456789", Confidence.HIGH));

        Assertions.assertThat(extractor.extractSequences(
            "0123456789012345 4567890123456789"))
            .containsExactly(
                new Sequence("0123456789012345", Confidence.LOW),
                new Sequence("4567890123456789", Confidence.LOW));

        Assertions.assertThat(extractor.extractSequences(
            "0 123456789012345 6"))
            .containsExactly(
                new Sequence("0123456789012345", Confidence.LOW),
                new Sequence("1234567890123456", Confidence.LOW));


        Assertions.assertThat(extractor.extractSequences(
            "IBAN: FR12 4326 5532 9027 1375 42 "))
            .containsExactly(
                new Sequence("4326553290271375", Confidence.LOW));

    }

    @Test
    public void testCustomLength() {
        DigitSequenceExtractor extractor = new DigitSequenceExtractor(4);

        Assertions.assertThat(extractor.extractSequences("Hello")).isEmpty();
        Assertions.assertThat(extractor.extractSequences(
            "0123 4567 89012345")).hasSize(2).contains(new Sequence("4567", Confidence.LOW));
    }


    @Test
    public void testPerformance() {
        List<String> inputSet = new ArrayList<>();
        Random random = new Random();

        while (inputSet.size() < 1000000) {
            StringBuilder text = new StringBuilder();

            while (text.length() < 128) {
                text.append((char) ('0' + random.nextInt(10)));

                if (random.nextInt(10) == 0) {
                    text.append((char) ('a' + random.nextInt(26)));
                }
            }

            inputSet.add(text.toString());
        }

        DigitSequenceExtractor extractor = new DigitSequenceExtractor(16);

        long tsStart = System.currentTimeMillis();
        inputSet.forEach(extractor::extractSequences);
        long tsEnd = System.currentTimeMillis();

        logger.debug("Extraction duration: {}ms", (tsEnd - tsStart));
    }
}
