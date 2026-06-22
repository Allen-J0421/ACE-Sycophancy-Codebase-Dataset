/**
 * Dynamic-programming solution to the rod-cutting problem.
 *
 * The public {@code cutRod(...)} methods are kept as compatibility shims over
 * the clearer {@code maxRevenue(...)} API.
 *
 * Price tables are 1-indexed, with {@code prices[0]} unused.
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
        return prices == null ? 0 : maxRevenueForTable(PriceTable.of(prices));
    }

    public static int maxRevenue(int[] prices, int rodLength) {
        if (prices == null || rodLength <= 0) {
            return 0;
        }

        return maxRevenueForTable(PriceTable.of(prices), rodLength);
    }

    public static int maxRevenueForTable(PriceTable priceTable) {
        return priceTable == null ? 0 : maxRevenueForTable(priceTable, priceTable.maxRodLength());
    }

    public static int maxRevenueForTable(PriceTable priceTable, int rodLength) {
        if (priceTable == null || rodLength <= 0) {
            return 0;
        }

        int[] revenues = new int[rodLength + 1];
        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            revenues[currentLength] = bestRevenueForLength(priceTable, revenues, currentLength);
        }

        return revenues[rodLength];
    }

    private static int bestRevenueForLength(
            PriceTable priceTable, int[] maxRevenue, int currentLength) {
        int bestForLength = 0;
        int maxCutLength = Math.min(currentLength, priceTable.maxRodLength());
        for (int firstCut = 1; firstCut <= maxCutLength; firstCut++) {
            int revenue = priceTable.priceFor(firstCut) + maxRevenue[currentLength - firstCut];
            if (revenue > bestForLength) {
                bestForLength = revenue;
            }
        }

        return bestForLength;
    }
}
