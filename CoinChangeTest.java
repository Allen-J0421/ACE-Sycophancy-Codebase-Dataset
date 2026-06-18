public final class CoinChangeTest {

    private CoinChangeTest() {
        // Test utility.
    }

    public static void main(String[] args) {
        assertCount(5, new int[] {1, 2, 3}, 5);
        assertCount(1, new int[] {1, 2, 3}, 0);
        assertCount(5, new int[] {2, 5, 3, 6}, 10);
        assertThrows(() -> CoinChange.count(null, 5));
        assertThrows(() -> CoinChange.count(new int[] {1, 0}, 5));
        assertThrows(() -> CoinChange.count(new int[] {1, 2}, -1));

        System.out.println("All CoinChange tests passed.");
    }

    private static void assertCount(int expected, int[] coins, int sum) {
        int actual = CoinChange.count(coins, sum);
        if (actual != expected) {
            throw new AssertionError(
                "expected " + expected + " ways for sum " + sum + ", got " + actual
            );
        }
    }

    private static void assertThrows(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("expected IllegalArgumentException");
    }
}
