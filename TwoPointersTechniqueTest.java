import java.util.Arrays;
import java.util.function.BooleanSupplier;

public final class TwoPointersTechniqueTest {

    private TwoPointersTechniqueTest() {
        // Test harness only.
    }

    public static void main(String[] args) {
        TestCase[] cases = {
            new TestCase("find pair in unsorted input", () ->
                TwoPointersTechnique.hasPairWithSum(new int[] {2, -3, 1, 0, -1}, -2)
            ),
            new TestCase("find pair in sorted input", () ->
                TwoPointersTechnique.hasPairWithSumSorted(new int[] {-3, -1, 0, 1, 2}, -2)
            ),
            new TestCase("reject missing pair", () ->
                !TwoPointersTechnique.hasPairWithSum(new int[] {1, 4, 6, 9}, 20)
            ),
            new TestCase("handle duplicates", () ->
                TwoPointersTechnique.hasPairWithSum(new int[] {3, 3, 7}, 6)
            ),
            new TestCase("preserve input array", () -> {
                int[] values = {5, 1, 4};
                int[] snapshot = values.clone();
                TwoPointersTechnique.hasPairWithSum(values, 9);
                return Arrays.equals(values, snapshot);
            }),
            new TestCase("reject invalid input", () ->
                !TwoPointersTechnique.hasPairWithSum(null, 1)
                    && !TwoPointersTechnique.hasPairWithSum(new int[] {42}, 42)
            ),
            new TestCase("avoid overflow false positive", () ->
                !TwoPointersTechnique.hasPairWithSum(
                    new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE},
                    -2
                )
            ),
        };

        for (TestCase testCase : cases) {
            testCase.run();
        }

        System.out.println("All tests passed.");
    }

    private static final class TestCase {
        private final String name;
        private final BooleanSupplier check;

        private TestCase(String name, BooleanSupplier check) {
            this.name = name;
            this.check = check;
        }

        private void run() {
            if (!check.getAsBoolean()) {
                throw new AssertionError("failed: " + name);
            }
        }
    }
}
