public final class CoinChangeTest {

    private CoinChangeTest() {
        // Test utility.
    }

    public static void main(String[] args) {
        assertCount(5, new int[] {1, 2, 3}, 5);
        assertCount(1, new int[] {1, 2, 3}, 0);
        assertCount(5, new int[] {2, 5, 3, 6}, 10);
        assertCount(2, new int[] {1, 1, 2}, 3);
        assertCount(1, new int[] {2, 2}, 4);
        assertCountLong(5L, new int[] {1, 2, 3}, 5);
        assertCountWaysLong(5L, 5, 1, 2, 3);
        assertCount(1, new int[] {}, 0);
        assertCount(0, new int[] {}, 5);
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

    private static void assertCountLong(long expected, int[] coins, int sum) {
        long actual = CoinChange.countLong(coins, sum);
        if (actual != expected) {
            throw new AssertionError(
                "expected " + expected + " ways for sum " + sum + ", got " + actual
            );
        }
    }

    private static void assertCountWaysLong(long expected, int sum, int... coins) {
        long actual = CoinChange.countWaysLong(sum, coins);
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
