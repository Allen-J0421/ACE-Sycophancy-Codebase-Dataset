import java.util.Arrays;
import java.util.List;

final class HuffmanExamples {
    static final String DEMO_SYMBOLS = "abcdef";
    static final int[] DEMO_FREQUENCIES = {5, 9, 12, 13, 16, 45};

    static final String CLASSIC_SYMBOLS = DEMO_SYMBOLS;
    static final int[] CLASSIC_FREQUENCIES = DEMO_FREQUENCIES;
    static final List<String> CLASSIC_CODES =
            Arrays.asList("1100", "1101", "100", "101", "111", "0");

    static final String SINGLE_SYMBOL = "a";
    static final int[] SINGLE_FREQUENCIES = {7};

    private HuffmanExamples() {
        // Shared sample data.
    }
}
