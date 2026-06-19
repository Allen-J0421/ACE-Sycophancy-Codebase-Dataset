package coinchange;

public final class CoinChangeSelfTest {

    private CoinChangeSelfTest() {
    }

    public static void main(String[] args) {
        assertCount(new int[] {1, 2, 3}, 5, 5);
        assertCount(new int[] {2, 5, 3, 6}, 10, 5);
        assertCount(new int[] {3, 1, 2, 2}, 5, 5);
        assertCount(new int[] {4}, 3, 0);
        assertCount(new CoinDenominations(new int[] {1, 2, 3}), 5, 5);
        assertRejects(new int[] {1, 0, 3}, 4);
        assertRejects(new int[] {1, 2, 3}, -1);
        assertRejects((int[]) null, 4);
        assertRejects((CoinDenominations) null, 4);
    }

    private static void assertCount(int[] coins, int targetSum, int expectedWays) {
        int actualWays = CoinChange.count(coins, targetSum);
        if (actualWays != expectedWays) {
            throw new AssertionError(
                "Expected " + expectedWays + " ways for target " + targetSum + " but got " + actualWays
            );
        }
    }

    private static void assertCount(CoinDenominations denominations, int targetSum, int expectedWays) {
        int actualWays = CoinChange.count(denominations, targetSum);
        if (actualWays != expectedWays) {
            throw new AssertionError(
                "Expected " + expectedWays + " ways for target " + targetSum + " but got " + actualWays
            );
        }
    }

    private static void assertRejects(int[] coins, int targetSum) {
        try {
            CoinChange.count(coins, targetSum);
            throw new AssertionError("Expected invalid input to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertRejects(CoinDenominations denominations, int targetSum) {
        try {
            CoinChange.count(denominations, targetSum);
            throw new AssertionError("Expected invalid input to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }
}
