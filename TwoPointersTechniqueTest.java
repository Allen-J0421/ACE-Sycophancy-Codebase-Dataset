import java.util.Arrays;
import java.util.function.BooleanSupplier;

public final class TwoPointersTechniqueTest {

    private static final int[] UNSORTED_SAMPLE = {2, -3, 1, 0, -1};
    private static final int[] SORTED_SAMPLE = {-3, -1, 0, 1, 2};

    private TwoPointersTechniqueTest() {
        // Test harness only.
    }

    public static void main(String[] args) {
        assertCase("find pair in unsorted input", () ->
            TwoPointersTechnique.hasPairWithSum(UNSORTED_SAMPLE, -2)
        );
        assertCase("find pair in sorted input", () ->
            TwoPointersTechnique.hasPairWithSumSorted(SORTED_SAMPLE, -2)
        );
        assertCase("reject missing pair", () ->
            !TwoPointersTechnique.hasPairWithSum(new int[] {1, 4, 6, 9}, 20)
        );
        assertCase("handle duplicates", () ->
            TwoPointersTechnique.hasPairWithSum(new int[] {3, 3, 7}, 6)
        );
        assertCase("preserve input array", TwoPointersTechniqueTest::preservesInputArray);
        assertCase("reject invalid input", () ->
            !TwoPointersTechnique.hasPairWithSum(null, 1)
                && !TwoPointersTechnique.hasPairWithSum(new int[] {42}, 42)
        );
        assertCase("avoid overflow false positive", () ->
            !TwoPointersTechnique.hasPairWithSum(
                new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE},
                -2
            )
        );

        System.out.println("All tests passed.");
    }

    private static boolean preservesInputArray() {
        int[] values = {5, 1, 4};
        int[] snapshot = values.clone();

        TwoPointersTechnique.hasPairWithSum(values, 9);

        return Arrays.equals(values, snapshot);
    }

    private static void assertCase(String name, BooleanSupplier check) {
        if (!check.getAsBoolean()) {
            throw new AssertionError("failed: " + name);
        }
    }
}
