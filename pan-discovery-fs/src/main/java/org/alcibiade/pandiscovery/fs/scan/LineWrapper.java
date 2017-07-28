package org.alcibiade.pandiscovery.fs.scan;

import java.nio.file.Path;

public class LineWrapper {
    private Path file;
    private long lineNumber;
    private String text;

    public LineWrapper(Path file, long lineNumber, String text) {
        this.file = file;
        this.lineNumber = lineNumber;
        this.text = text;
    }

    public Path getFile() {
        return file;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getText() {
        return text;
    }
}
