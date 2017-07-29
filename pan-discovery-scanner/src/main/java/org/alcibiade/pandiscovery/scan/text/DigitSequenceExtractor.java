package org.alcibiade.pandiscovery.scan.text;

import java.util.Collection;

/**
 * Extract digit sequences from a String.
 */
public class DigitSequenceExtractor {
    private int sequenceLength;

    public DigitSequenceExtractor(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public DigitSequenceExtractor() {
        this(16);
    }

    public Collection<Sequence> extractSequences(String text) {
        DigitAccumulator accumulator = new DigitAccumulator(this.sequenceLength);
        text.chars().forEach(accumulator::consumeCharacter);
        accumulator.consumeCharacter(' ');
        return accumulator.getSequences();
    }
}
