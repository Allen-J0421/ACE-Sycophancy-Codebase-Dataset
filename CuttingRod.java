/**
 * Dynamic-programming solution to the rod-cutting problem.
 *
 * Price tables are 1-indexed, with {@code prices[0]} unused.
 */
public final class CuttingRod {

    private CuttingRod() {
        // Utility class.
    }

    public static int maxRevenue(PriceTable priceTable) {
        return priceTable == null ? 0 : maxRevenue(priceTable, priceTable.maxRodLength());
    }

    public static int maxRevenue(PriceTable priceTable, int rodLength) {
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
