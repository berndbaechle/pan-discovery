package org.alcibiade.pandiscovery.scan.text;

public class Sequence {

    private String text;
    private Confidence confidence;

    public Sequence(String text, Confidence confidence) {
        this.text = text;
        this.confidence = confidence;
    }

    public String getText() {
        return text;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sequence sequence = (Sequence) o;

        if (!text.equals(sequence.text)) return false;
        return confidence == sequence.confidence;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + confidence.hashCode();
        return result;
    }
}
