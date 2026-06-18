import java.util.Arrays;

final class CoinChangeDenominations {

    private CoinChangeDenominations() {
        // Utility class.
    }

    static int[] normalize(int[] coins) {
        if (coins == null) {
            throw new IllegalArgumentException("coins must not be null");
        }
        if (coins.length == 0) {
            return coins;
        }

        int[] normalized = coins.clone();
        Arrays.sort(normalized);

        int uniqueCount = 0;
        int previous = 0;
        for (int coin : normalized) {
            if (coin <= 0) {
                throw new IllegalArgumentException("coin values must be positive");
            }
            if (uniqueCount == 0 || coin != previous) {
                normalized[uniqueCount++] = coin;
                previous = coin;
            }
        }

        return Arrays.copyOf(normalized, uniqueCount);
    }
}
