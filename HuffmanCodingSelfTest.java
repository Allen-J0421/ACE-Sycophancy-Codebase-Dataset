import java.util.ArrayList;
import java.util.Collections;

public final class HuffmanCodingSelfTest {
    private HuffmanCodingSelfTest() {
        // Test entry point.
    }

    public static void main(String[] args) {
        runTest("classic example", HuffmanCodingSelfTest::shouldEncodeClassicExample);
        runTest("single symbol", HuffmanCodingSelfTest::shouldHandleSingleSymbol);
        runTest("empty input", HuffmanCodingSelfTest::shouldHandleEmptyInput);
        runTest("mismatched lengths", HuffmanCodingSelfTest::shouldRejectMismatchedLengths);
        runTest("negative frequencies", HuffmanCodingSelfTest::shouldRejectNegativeFrequencies);
        runTest("equal frequencies", HuffmanCodingSelfTest::shouldHandleEqualFrequencies);
        System.out.println("All HuffmanCoding self-tests passed.");
    }

    private static void shouldEncodeClassicExample() {
        assertCodes(
                HuffmanExamples.CLASSIC_SYMBOLS,
                HuffmanExamples.CLASSIC_FREQUENCIES,
                HuffmanExamples.CLASSIC_CODES,
                "classic example");
    }

    private static void shouldHandleSingleSymbol() {
        assertCodes(
                HuffmanExamples.SINGLE_SYMBOL,
                HuffmanExamples.SINGLE_FREQUENCIES,
                Collections.singletonList("0"),
                "single-symbol input");
    }

    private static void shouldHandleEmptyInput() {
        ArrayList<String> codes = HuffmanCoding.huffmanCodes("", new int[0]);
        assertEquals(Collections.<String>emptyList(), codes, "empty input");
    }

    private static void shouldRejectMismatchedLengths() {
        expectFailure(
                () -> HuffmanCoding.huffmanCodes("ab", new int[] {1}),
                "mismatched lengths");
    }

    private static void shouldRejectNegativeFrequencies() {
        expectFailure(
                () -> HuffmanCoding.huffmanCodes("ab", new int[] {1, -2}),
                "negative frequency");
    }

    private static void shouldHandleEqualFrequencies() {
        assertCodes("ab", new int[] {1, 1}, java.util.Arrays.asList("0", "1"), "equal frequencies");
    }

    private static void assertCodes(
            String symbols,
            int[] frequencies,
            java.util.List<String> expected,
            String scenario) {
        ArrayList<String> actual = HuffmanCoding.huffmanCodes(symbols, frequencies);
        assertEquals(expected, actual, scenario);
    }

    private static void assertEquals(
            java.util.List<String> expected,
            java.util.List<String> actual,
            String scenario) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    scenario + " failed: expected " + expected + " but got " + actual);
        }
    }

    private static void expectFailure(Runnable action, String scenario) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError(scenario + " failed: expected IllegalArgumentException");
    }

    private static void runTest(String name, Runnable test) {
        try {
            test.run();
        } catch (Throwable error) {
            throw new AssertionError(name + " failed", error);
        }
    }
}
