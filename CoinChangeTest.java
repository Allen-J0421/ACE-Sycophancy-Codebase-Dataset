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
        assertInputUnchanged();
        assertCount(1, new int[] {}, 0);
        assertCount(0, new int[] {}, 5);
        assertParsedArgs(5, new int[] {1, 2, 3}, new String[] {"5", "1", "2", "3"});
        assertParsedArgs(0, new int[] {}, new String[] {"0"});
        assertThrows(() -> CoinChange.count(null, 5));
        assertThrows(() -> CoinChange.count(new int[] {1, 0}, 5));
        assertThrows(() -> CoinChange.count(new int[] {1, 2}, -1));
        assertThrows(() -> CoinChangeArgs.parse(new String[] {}));
        assertThrows(() -> CoinChangeArgs.parse(new String[] {"-1"}));
        assertThrows(() -> CoinChangeArgs.parse(new String[] {"5", "0"}));
        assertThrows(() -> CoinChangeArgs.parse(new String[] {"5", "-2"}));

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

    private static void assertInputUnchanged() {
        int[] coins = {3, 1, 2, 1};
        int[] original = coins.clone();
        CoinChange.count(coins, 5);
        if (coins.length != original.length) {
            throw new AssertionError("count should not change the input length");
        }
        for (int i = 0; i < coins.length; i++) {
            if (coins[i] != original[i]) {
                throw new AssertionError("count should not mutate the input array");
            }
        }
    }

    private static void assertParsedArgs(int expectedSum, int[] expectedCoins, String[] args) {
        CoinChangeArgs parsed = CoinChangeArgs.parse(args);
        if (parsed.sum() != expectedSum) {
            throw new AssertionError(
                "expected sum " + expectedSum + ", got " + parsed.sum()
            );
        }

        int[] parsedCoins = parsed.coins();
        if (parsedCoins.length != expectedCoins.length) {
            throw new AssertionError(
                "expected " + expectedCoins.length + " coins, got " + parsedCoins.length
            );
        }
        for (int i = 0; i < expectedCoins.length; i++) {
            if (parsedCoins[i] != expectedCoins[i]) {
                throw new AssertionError(
                    "expected coin " + expectedCoins[i] + " at index " + i + ", got " + parsedCoins[i]
                );
            }
        }

        if (parsedCoins.length > 0) {
            parsedCoins[0] = -1;
            int[] copiedCoins = parsed.coins();
            if (copiedCoins[0] == -1) {
                throw new AssertionError("coins() should return a defensive copy");
            }
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
