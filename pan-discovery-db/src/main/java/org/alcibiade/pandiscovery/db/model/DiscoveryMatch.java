package org.alcibiade.pandiscovery.db.model;

import org.alcibiade.pandiscovery.scan.CardType;

/**
 * Single discovery match.
 */
public class DiscoveryMatch {

    private CardType cardType;

    private String rawValue;

    public DiscoveryMatch(CardType cardType, String rawValue) {
        this.cardType = cardType;
        this.rawValue = rawValue;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscoveryMatch that = (DiscoveryMatch) o;

        if (cardType != that.cardType) return false;
        return rawValue != null ? rawValue.equals(that.rawValue) : that.rawValue == null;
    }

    @Override
    public int hashCode() {
        int result = cardType != null ? cardType.hashCode() : 0;
        result = 31 * result + (rawValue != null ? rawValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DiscoveryMatch{" +
            "cardType=" + cardType +
            ", rawValue='" + rawValue + '\'' +
            '}';
    }
}
