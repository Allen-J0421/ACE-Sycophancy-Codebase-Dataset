package kmp;

import java.util.Arrays;
import java.util.List;

final class KmpExamples {

    static final String SAMPLE_TEXT = "aabaacaadaabaaba";
    static final String SAMPLE_PATTERN = "aaba";
    static final List<Integer> SAMPLE_MATCHES = Arrays.asList(0, 9, 12);

    private KmpExamples() {
        // Utility class.
    }
}
