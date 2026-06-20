import java.util.Arrays;
import java.util.List;

public class HuffmanCodingTest {
    public static void main(String[] args) {
        shouldBuildCodebookOnceForBothOrderings();
        shouldExposeImmutableCodeViews();
        shouldGenerateExpectedTraversalOrderCodes();
        shouldGenerateExpectedSymbolOrderCodes();
        shouldHandleSingleSymbol();
        shouldHandleEmptyInput();
        shouldRejectNullFrequencies();
        shouldRejectNegativeFrequencies();
        shouldRejectMismatchedInputLengths();
    }

    private static void shouldBuildCodebookOnceForBothOrderings() {
        HuffmanCoding.Codebook codebook = HuffmanCoding.codebook("abcdef", new int[]{5, 9, 12, 13, 16, 45});

        assertListEquals(
                Arrays.asList("0", "100", "101", "1100", "1101", "111"),
                codebook.inTraversalOrder(),
                "codebook should expose traversal-order codes"
        );
        assertListEquals(
                Arrays.asList("1100", "1101", "100", "101", "111", "0"),
                codebook.inSymbolOrder(),
                "codebook should expose symbol-order codes"
        );
    }

    private static void shouldExposeImmutableCodeViews() {
        HuffmanCoding.Codebook codebook = HuffmanCoding.codebook("abcdef", new int[]{5, 9, 12, 13, 16, 45});

        assertThrows(
                UnsupportedOperationException.class,
                () -> codebook.inTraversalOrder().add("oops"),
                "traversal-order view should be immutable"
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> codebook.inSymbolOrder().add("oops"),
                "symbol-order view should be immutable"
        );
    }

    private static void shouldGenerateExpectedTraversalOrderCodes() {
        assertListEquals(
                Arrays.asList("0", "100", "101", "1100", "1101", "111"),
                HuffmanCoding.huffmanCodes("abcdef", new int[]{5, 9, 12, 13, 16, 45}),
                "legacy API should preserve traversal-order output"
        );
    }

    private static void shouldGenerateExpectedSymbolOrderCodes() {
        assertListEquals(
                Arrays.asList("1100", "1101", "100", "101", "111", "0"),
                HuffmanCoding.huffmanCodesBySymbolOrder("abcdef", new int[]{5, 9, 12, 13, 16, 45}),
                "symbol-order API should align codes with the input symbol positions"
        );
    }

    private static void shouldHandleSingleSymbol() {
        assertListEquals(
                Arrays.asList("0"),
                HuffmanCoding.huffmanCodes(new int[]{7}),
                "single symbol should be encoded as 0"
        );
    }

    private static void shouldHandleEmptyInput() {
        assertListEquals(
                Arrays.asList(),
                HuffmanCoding.huffmanCodes(new int[]{}),
                "empty input should produce no codes"
        );
    }

    private static void shouldRejectMismatchedInputLengths() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HuffmanCoding.huffmanCodes("ab", new int[]{1}),
                "mismatched symbols and frequencies should be rejected"
        );
    }

    private static void shouldRejectNullFrequencies() {
        assertThrows(
                NullPointerException.class,
                () -> HuffmanCoding.huffmanCodes((int[]) null),
                "null frequencies should be rejected"
        );
    }

    private static void shouldRejectNegativeFrequencies() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HuffmanCoding.huffmanCodes(new int[]{2, -1, 3}),
                "negative frequencies should be rejected"
        );
    }

    private static void assertListEquals(List<String> expected, List<String> actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrows(
            Class<? extends Throwable> expectedType,
            ThrowingRunnable action,
            String message
    ) {
        try {
            action.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }
            throw new AssertionError(message + ": expected " + expectedType.getSimpleName()
                    + " but got " + error.getClass().getSimpleName(), error);
        }

        throw new AssertionError(message + ": expected " + expectedType.getSimpleName());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
