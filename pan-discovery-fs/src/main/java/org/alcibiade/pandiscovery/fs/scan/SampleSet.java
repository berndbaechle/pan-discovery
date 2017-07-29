package org.alcibiade.pandiscovery.fs.scan;

import java.util.TreeSet;

public class SampleSet extends TreeSet<Sample> {
    public static final int SAMPLES_LIMIT = 20;

    public static SampleSet singleton(Sample sample) {
        SampleSet result = new SampleSet();
        result.add(sample);
        return result;
    }

    public static SampleSet combine(SampleSet ss1, SampleSet ss2) {
        if (ss1.size() >= SAMPLES_LIMIT) {
            return ss1;
        } else if (ss2.size() >= SAMPLES_LIMIT) {
            return ss2;
        } else {
            SampleSet result = new SampleSet();

            result.addAll(ss1);
            result.addAll(ss2);

            return result;
        }
    }
}
