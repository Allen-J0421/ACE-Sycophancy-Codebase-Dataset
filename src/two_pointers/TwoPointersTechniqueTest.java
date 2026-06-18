package two_pointers;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

public final class TwoPointersTechniqueTest {

    private TwoPointersTechniqueTest() {
        // Test harness only.
    }

    public static void main(String[] args) {
        assertCase("find pair in unsorted input", () ->
            TwoPointersTechnique.hasPairWithSum(
                TwoPointersTechniqueSamples.unsortedSample(),
                TwoPointersTechniqueSamples.target()
            )
        );
        assertCase("find pair in sorted input", () ->
            TwoPointersTechnique.hasPairWithSumSorted(
                TwoPointersTechniqueSamples.sortedSample(),
                TwoPointersTechniqueSamples.target()
            )
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
        int[] values = TwoPointersTechniqueSamples.unsortedSample();
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
