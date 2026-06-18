import java.util.Arrays;
import java.util.Objects;

public final class TestAssertions {
    private TestAssertions() {
    }

    public static void assertIntArrayEquals(String scenario, int[] actualValues, int[] expectedValues) {
        if (!Arrays.equals(actualValues, expectedValues)) {
            throw new AssertionError(
                    scenario
                            + ": expected "
                            + Arrays.toString(expectedValues)
                            + " but got "
                            + Arrays.toString(actualValues));
        }
    }

    public static void assertStringEquals(String scenario, String actualValue, String expectedValue) {
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new AssertionError(
                    scenario + ": expected \"" + expectedValue + "\" but got \"" + actualValue + "\"");
        }
    }

    public static void assertThrowsNullPointer(String scenario, Runnable action) {
        Objects.requireNonNull(action, "action");

        try {
            action.run();
            throw new AssertionError(scenario + ": expected NullPointerException");
        } catch (NullPointerException expected) {
            // Expected by the public contract under test.
        }
    }
}
