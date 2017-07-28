package org.alcibiade.pandiscovery.fs.scan;


/**
 * High level scan result structure that initially represents a single line scan result,
 * but can be reduced to aggregate complete file/folder result set.
 */
public class ScanResult {

    public static ScanResult EMPTY = new ScanResult(null, null, 0, 0, 0);
    private String sample;
    private String sampleLine;
    private long matchesLow;
    private long matchesHigh;
    private long totalLines;

    public ScanResult(String sample, String sampleLine, long totalLines, long matchesHigh, long matchesLow) {
        this.sample = sample;
        this.sampleLine = sampleLine;
        this.matchesLow = matchesLow;
        this.matchesHigh = matchesHigh;
        this.totalLines = totalLines;
    }

    public static ScanResult reduce(ScanResult r1, ScanResult r2) {
        String sample = r1.getSample() != null ? r1.getSample() : r2.getSample();
        String sampleLine = r1.getSample() != null ? r1.getSampleLine() : r2.getSampleLine();
        return new ScanResult(sample, sampleLine,
            r1.getTotalLines() + r2.getTotalLines(),
            r1.getMatchesHigh() + r2.getMatchesHigh(),
            r1.getMatchesLow() + r2.getMatchesLow());
    }

    public static ScanResult reduceOnSingleLine(ScanResult r1, ScanResult r2) {
        String sample = r1.getSample() != null ? r1.getSample() : r2.getSample();
        String sampleLine = r1.getSample() != null ? r1.getSampleLine() : r2.getSampleLine();
        return new ScanResult(sample, sampleLine,
            1,
            r1.getMatchesHigh() + r2.getMatchesHigh(),
            r1.getMatchesLow() + r2.getMatchesLow());
    }

    public long getTotalLines() {
        return totalLines;
    }

    public String getSample() {
        return sample;
    }

    public String getSampleLine() {
        return sampleLine;
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
        if (sample != null ? !sample.equals(that.sample) : that.sample != null) return false;
        return sampleLine != null ? sampleLine.equals(that.sampleLine) : that.sampleLine == null;
    }

    @Override
    public int hashCode() {
        int result = sample != null ? sample.hashCode() : 0;
        result = 31 * result + (sampleLine != null ? sampleLine.hashCode() : 0);
        result = 31 * result + (int) (matchesLow ^ (matchesLow >>> 32));
        result = 31 * result + (int) (matchesHigh ^ (matchesHigh >>> 32));
        result = 31 * result + (int) (totalLines ^ (totalLines >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
            "sample='" + sample + '\'' +
            ", sampleLine='" + sampleLine + '\'' +
            ", matchesLow=" + matchesLow +
            ", matchesHigh=" + matchesHigh +
            ", totalLines=" + totalLines +
            '}';
    }
}
