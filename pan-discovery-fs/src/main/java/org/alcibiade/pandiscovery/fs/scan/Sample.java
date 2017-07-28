package org.alcibiade.pandiscovery.fs.scan;

import org.alcibiade.pandiscovery.scan.text.Confidence;

import java.nio.file.Path;

public class Sample implements Comparable<Sample> {

    private Path file;
    private long line;
    private Confidence confidence;
    private String detectedPan;

    public Sample(Path file, long line, Confidence confidence, String detectedPan) {
        this.file = file;
        this.line = line;
        this.confidence = confidence;
        this.detectedPan = detectedPan;
    }

    public Path getFile() {
        return file;
    }

    public long getLine() {
        return line;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public String getDetectedPan() {
        return detectedPan;
    }

    @Override
    public int compareTo(Sample o) {
        return (int) (line - o.line);
    }

    @Override
    public String toString() {
        return "Sample{" +
            "file=" + file +
            ", line=" + line +
            ", detectedPan='" + detectedPan + '\'' +
            '}';
    }
}
