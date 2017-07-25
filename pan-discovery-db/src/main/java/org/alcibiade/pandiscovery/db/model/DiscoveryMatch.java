package org.alcibiade.pandiscovery.db.model;

import org.alcibiade.pandiscovery.scan.CardType;

/**
 * Single discovery match.
 */
public class DiscoveryMatch {

    private CardType cardType;

    private String rawValue;

    private String pan;

    public DiscoveryMatch(CardType cardType, String pan, String rawValue) {
        this.cardType = cardType;
        this.rawValue = rawValue;
        this.pan = pan;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getRawValue() {
        return rawValue;
    }

    public String getPan() {
        return pan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscoveryMatch that = (DiscoveryMatch) o;

        if (cardType != that.cardType) return false;
        if (!rawValue.equals(that.rawValue)) return false;
        return pan.equals(that.pan);
    }

    @Override
    public int hashCode() {
        int result = cardType.hashCode();
        result = 31 * result + rawValue.hashCode();
        result = 31 * result + pan.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DiscoveryMatch{" +
            "cardType=" + cardType +
            ", rawValue='" + rawValue + '\'' +
            ", pan='" + pan + '\'' +
            '}';
    }
}
