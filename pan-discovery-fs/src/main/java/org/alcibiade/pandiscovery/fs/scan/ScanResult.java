package org.alcibiade.pandiscovery.fs.scan;


/**
 * High level scan result structure that initially represents a single line scan result,
 * but can be reduced to aggregate complete file/folder result set.
 */
public class ScanResult {

    public static ScanResult EMPTY = new ScanResult(new SampleSet(), 0, 0, 0);
    private SampleSet sampleSet;
    private long matchesLow;
    private long matchesHigh;
    private long totalLines;

    public ScanResult(SampleSet sampleSet, long totalLines, long matchesHigh, long matchesLow) {
        this.sampleSet = sampleSet;
        this.matchesLow = matchesLow;
        this.matchesHigh = matchesHigh;
        this.totalLines = totalLines;
    }

    public static ScanResult reduce(ScanResult r1, ScanResult r2) {
        return reduceInternal(r1, r2, null);
    }


    public static ScanResult reduceOnSingleLine(ScanResult r1, ScanResult r2) {
        return reduceInternal(r1, r2, 1L);
    }

    private static ScanResult reduceInternal(ScanResult r1, ScanResult r2, Long lines) {
        return new ScanResult(
            SampleSet.combine(r1.getSampleSet(), r2.getSampleSet()),
            lines != null ? lines : r1.getTotalLines() + r2.getTotalLines(),
            r1.getMatchesHigh() + r2.getMatchesHigh(),
            r1.getMatchesLow() + r2.getMatchesLow());
    }

    public long getTotalLines() {
        return totalLines;
    }

    public SampleSet getSampleSet() {
        return sampleSet;
    }

    public long getMatchesLow() {
        return matchesLow;
    }

    public long getMatchesHigh() {
        return matchesHigh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScanResult that = (ScanResult) o;

        if (matchesLow != that.matchesLow) return false;
        if (matchesHigh != that.matchesHigh) return false;
        if (totalLines != that.totalLines) return false;
        return sampleSet.equals(that.sampleSet);
    }

    @Override
    public int hashCode() {
        int result = sampleSet.hashCode();
        result = 31 * result + (int) (matchesLow ^ (matchesLow >>> 32));
        result = 31 * result + (int) (matchesHigh ^ (matchesHigh >>> 32));
        result = 31 * result + (int) (totalLines ^ (totalLines >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
            "sampleSet=" + sampleSet +
            ", matchesLow=" + matchesLow +
            ", matchesHigh=" + matchesHigh +
            ", totalLines=" + totalLines +
            '}';
    }
}
