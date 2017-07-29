package org.alcibiade.pandiscovery.scan.text;

import java.util.*;

/**
 * Accumulate digits read from text sequences.
 */
public class DigitAccumulator {
    private static final List<Character> HARD_BREAK = Arrays.asList(';', '.', ',', ':', '\t');

    private Deque<Sequence> sequences = new ArrayDeque<>();
    private int[] queue;
    private Confidence[] seqStart;
    private int size = 0;
    private boolean onDelimiter = true;
    private boolean lastSequenceIsClosed = true;

    private int sequenceLength;

    public DigitAccumulator(int sequenceLength) {
        this.sequenceLength = sequenceLength;
        queue = new int[sequenceLength];
        seqStart = new Confidence[sequenceLength];
    }

    public void consumeCharacter(int c) {

        boolean isDigit = Character.isDigit(c);

        /*
         * Process the inbound character.
         */

        if (isDigit) {
            // If the last sequence is not yet completely isolated, we decrease its confidence.

            if (!this.sequences.isEmpty() && !lastSequenceIsClosed) {
                Sequence last = this.sequences.pollLast();
                this.sequences.addLast(new Sequence(last.getText(), Confidence.LOW));
            }

            // Digits are accumulated

            // If full, shift buffer
            if (size == sequenceLength) {
                shift();
            }

            // Append the new value
            queue[size] = c;
            seqStart[size] = onDelimiter ?
                (size == 0 ? Confidence.HIGH : Confidence.LOW)
                : null;
            size++;
        } else if (onDelimiter) {
            // Two delimiters ends a possible sequence;
            size = 0;
            lastSequenceIsClosed = true;
        } else {

            /*
             * Assess if the current buffer is a valid sequence.
             */

            if (size == sequenceLength) {
                Confidence confidence = seqStart[0];

                for (int i = 0; i < sequenceLength; i++) {
                    if (seqStart[i] != null) {
                        if (i % 4 != 0) {
                            confidence = Confidence.LOW;
                        }
                    }
                }

                if (seqStart[0] != null) {
                    String s = new String(queue, 0, sequenceLength);
                    sequences.addLast(new Sequence(s, confidence));
                    lastSequenceIsClosed = false;
                }

                shift();
            }
        }

        /*
         * Hard break characters.
         */

        if (Character.isAlphabetic(c) || HARD_BREAK.contains((char) c)) {
            size = 0;
            lastSequenceIsClosed = true;
        }

        onDelimiter = Character.isWhitespace(c) || HARD_BREAK.contains((char) c);
    }

    private void shift() {
        for (int i = 1; i < sequenceLength; i++) {
            queue[i - 1] = queue[i];
            seqStart[i - 1] = seqStart[i];
        }

        size--;
    }

    public Collection<Sequence> getSequences() {
        return sequences;
    }
}
