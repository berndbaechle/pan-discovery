package org.alcibiade.pandiscovery.scan.text;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Test;

public class DigitAccumulatorTest {

    @Test
    public void testVariableConfidence() {
        singleSequenceTest(4, "1234 ", "1234", Confidence.HIGH);
        singleSequenceTest(4, "0 1234 ", "1234", Confidence.LOW);
        singleSequenceTest(4, "1234 0 ", "1234", Confidence.LOW);
        singleSequenceTest(4, "0 1234 0 ", "1234", Confidence.LOW);

        singleSequenceTest(8, "12345678 ", "12345678", Confidence.HIGH);
        singleSequenceTest(8, "1234 5678 ", "12345678", Confidence.HIGH);
        singleSequenceTest(8, "12 34 5678 ", "12345678", Confidence.LOW);

        multiSequenceTest(8, "12345678 12345678 ", 2, Confidence.LOW);
        multiSequenceTest(8, "1234 5678 1234 5678 ", 3, Confidence.LOW);

        multiSequenceTest(16, "IBAN: FR12 4326 5532 9027 1375 42", 1, Confidence.LOW);
    }


    private void singleSequenceTest(int sequenceLength, String inputString, String expectedSequence, Confidence expectedConfidence) {
        DigitAccumulator accumulator = new DigitAccumulator(sequenceLength);

        for (char c : inputString.toCharArray()) {
            accumulator.consumeCharacter(c);
        }

        Assertions.assertThat(accumulator.getSequences()).containsExactly(new Sequence(expectedSequence, expectedConfidence));
    }

    private void multiSequenceTest(int sequenceLength, String inputString, int expectedSequences, Confidence expectedConfidence) {
        DigitAccumulator accumulator = new DigitAccumulator(sequenceLength);

        for (char c : inputString.toCharArray()) {
            accumulator.consumeCharacter(c);
        }

        Assertions.assertThat(accumulator.getSequences())
            .hasSize(expectedSequences)
            .is(new Condition<Iterable<? extends Sequence>>() {
                @Override
                public boolean matches(Iterable<? extends Sequence> sequences) {
                    boolean matching = true;

                    for (Sequence sequence : sequences) {
                        if (sequence.getConfidence() != expectedConfidence) {
                            matching = false;
                        }
                    }

                    return matching;
                }
            })
        ;
    }
}
