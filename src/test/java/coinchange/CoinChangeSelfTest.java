package coinchange;

public final class CoinChangeSelfTest {

    private CoinChangeSelfTest() {
    }

    public static void main(String[] args) {
        assertCount(new int[] {1, 2, 3}, 5, 5);
        assertCount(new int[] {2, 5, 3, 6}, 10, 5);
        assertCount(new int[] {4}, 3, 0);
        assertRejects(new int[] {1, 0, 3}, 4);
        assertRejects(new int[] {1, 2, 3}, -1);
    }

    private static void assertCount(int[] coins, int targetSum, int expectedWays) {
        int actualWays = CoinChange.count(coins, targetSum);
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
}
