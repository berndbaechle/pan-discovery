package org.alcibiade.pandiscovery.scan;

/**
 * Result of a single detection run.
 */
public class DetectionResult {

    private CardType cardType;
    private String sample;
    private String sampleLine;

    public DetectionResult(CardType cardType, String sample, String sampleLine) {
        this.cardType = cardType;
        this.sample = sample;
        this.sampleLine = sampleLine;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getSample() {
        return sample;
    }

    public String getSampleLine() {
        return sampleLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetectionResult that = (DetectionResult) o;

        if (cardType != that.cardType) return false;
        if (!sample.equals(that.sample)) return false;
        return sampleLine.equals(that.sampleLine);
    }

    @Override
    public int hashCode() {
        int result = cardType.hashCode();
        result = 31 * result + sample.hashCode();
        result = 31 * result + sampleLine.hashCode();
        return result;
    }
}
