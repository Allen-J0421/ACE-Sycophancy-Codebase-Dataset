import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class HuffmanCodingSelfTest {
    private HuffmanCodingSelfTest() {
        // Test entry point.
    }

    public static void main(String[] args) {
        shouldEncodeClassicExample();
        shouldHandleSingleSymbol();
        shouldHandleEmptyInput();
        shouldRejectMismatchedLengths();
        shouldRejectNegativeFrequencies();
        System.out.println("All HuffmanCoding self-tests passed.");
    }

    private static void shouldEncodeClassicExample() {
        ArrayList<String> codes = HuffmanCoding.huffmanCodes(
                "abcdef",
                new int[] {5, 9, 12, 13, 16, 45});
        assertEquals(Arrays.asList("1100", "1101", "100", "101", "111", "0"), codes,
                "classic example");
    }

    private static void shouldHandleSingleSymbol() {
        ArrayList<String> codes = HuffmanCoding.huffmanCodes("a", new int[] {7});
        assertEquals(Arrays.asList("0"), codes, "single-symbol input");
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
}
