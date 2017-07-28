package org.alcibiade.pandiscovery.fs.scan;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ScanResultTest {

    @Test
    public void testReduction() {
        List<ScanResult> results = new ArrayList<>();
        results.add(new ScanResult("s1", "s1", 20, 1, 1));
        results.add(new ScanResult("s2", "s2", 17, 0, 5));
        results.add(new ScanResult("s3", "s3", 478, 3, 2));

        ScanResult reductionResult = results.stream().reduce(ScanResult.EMPTY, ScanResult::reduce);
        Assertions.assertThat(reductionResult.getTotalLines()).isEqualTo(515);
        Assertions.assertThat(reductionResult.getMatchesLow()).isEqualTo(8);
        Assertions.assertThat(reductionResult.getMatchesHigh()).isEqualTo(4);
        Assertions.assertThat(reductionResult.getSample()).isNotEmpty();
        Assertions.assertThat(reductionResult.getSampleLine()).isNotEmpty();
    }
}


