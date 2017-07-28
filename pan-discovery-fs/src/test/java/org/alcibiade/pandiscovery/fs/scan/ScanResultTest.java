package org.alcibiade.pandiscovery.fs.scan;

import org.alcibiade.pandiscovery.scan.text.Confidence;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScanResultTest {

    @Test
    public void testReduction() {
        List<ScanResult> results = new ArrayList<>();
        results.add(new ScanResult(
            SampleSet.singleton(new Sample(Paths.get("folder"), 4, Confidence.HIGH, "1234123412341234")), 20, 1, 1));
        results.add(new ScanResult(new SampleSet(), 17, 0, 5));
        results.add(new ScanResult(new SampleSet(), 478, 3, 2));

        ScanResult reductionResult = results.stream().reduce(ScanResult.EMPTY, ScanResult::reduce);
        Assertions.assertThat(reductionResult.getTotalLines()).isEqualTo(515);
        Assertions.assertThat(reductionResult.getMatchesLow()).isEqualTo(8);
        Assertions.assertThat(reductionResult.getMatchesHigh()).isEqualTo(4);
        Assertions.assertThat(reductionResult.getSampleSet()).isNotEmpty();
    }
}


