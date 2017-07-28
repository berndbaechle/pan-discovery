package org.alcibiade.pandiscovery.fs.scan;

public class LineCounter {

    private long currentNumber = 0;

    public long getNextLineNumber() {
        return ++currentNumber;
    }
}
