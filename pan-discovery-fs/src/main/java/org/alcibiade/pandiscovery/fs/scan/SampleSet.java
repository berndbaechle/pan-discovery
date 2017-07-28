package org.alcibiade.pandiscovery.fs.scan;

import java.util.TreeSet;

public class SampleSet extends TreeSet<Sample> {


    public static SampleSet singleton(Sample sample) {
        SampleSet result = new SampleSet();
        result.add(sample);
        return result;
    }

    public static SampleSet combine(SampleSet ss1, SampleSet ss2) {
        SampleSet result = new SampleSet();
        result.addAll(ss1);
        result.addAll(ss2);
        return result;
    }
}
