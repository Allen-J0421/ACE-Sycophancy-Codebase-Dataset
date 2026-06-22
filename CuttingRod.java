/**
 * Dynamic-programming solution to the rod-cutting problem.
 *
 * The price table is 1-indexed: {@code prices[length]} is the value of a
 * single piece of that length, and {@code prices[0]} is unused.
 *
 * The public {@code cutRod(...)} methods are kept as compatibility shims over
 * the clearer {@code maxRevenue(...)} API.
 */
public final class CuttingRod {

    private CuttingRod() {
        // Utility class.
    }

    public static int cutRod(int[] prices) {
        return maxRevenue(prices);
    }

    public static int cutRod(int[] prices, int rodLength) {
        return maxRevenue(prices, rodLength);
    }

    public static int maxRevenue(int[] prices) {
        return prices == null ? 0 : maxRevenue(prices, defaultRodLength(prices));
    }

    public static int maxRevenue(int[] prices, int rodLength) {
        if (prices == null || rodLength <= 0) {
            return 0;
        }
        validatePriceTable(prices, rodLength);

        int[] revenues = new int[rodLength + 1];
        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            revenues[currentLength] = bestRevenueForLength(prices, revenues, currentLength);
        }

        return revenues[rodLength];
    }

    private static int defaultRodLength(int[] prices) {
        return prices.length - 1;
    }

    private static void validatePriceTable(int[] prices, int rodLength) {
        if (prices == null) {
            throw new IllegalArgumentException("prices must not be null");
        }
        if (rodLength < 0) {
            throw new IllegalArgumentException("rodLength must be non-negative");
        }
        if (rodLength >= prices.length) {
            throw new IllegalArgumentException(
                    "rodLength must be less than prices.length for a 1-indexed price table");
        }
    }

    private static int bestRevenueForLength(int[] prices, int[] maxRevenue, int currentLength) {
        int bestForLength = 0;
        for (int firstCut = 1; firstCut <= currentLength; firstCut++) {
            bestForLength = Math.max(
                    bestForLength,
                    prices[firstCut] + maxRevenue[currentLength - firstCut]);
        }
        return bestForLength;
    }
}
