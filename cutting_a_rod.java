import java.util.Arrays;

final class CuttingRod {

    static int cutRod(int[] priceTableWithSentinel) {
        return solve(PriceTable.fromSentinelArray(priceTableWithSentinel));
    }

    static int maxRevenueForPricesByLength(int[] pricesByLength) {
        return solve(PriceTable.fromPricesByLength(pricesByLength));
    }

    private static int solve(PriceTable priceTable) {
        return new RodCuttingSolver(priceTable).solve();
    }

    private static final class RodCuttingSolver {
        private final PriceTable priceTable;
        private final int[] bestRevenueByLength;
        private final int rodLength;

        private RodCuttingSolver(PriceTable priceTable) {
            this.priceTable = priceTable;
            this.rodLength = priceTable.maxLength();
            this.bestRevenueByLength = new int[rodLength + 1];
        }

        int solve() {
            for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
                bestRevenueByLength[currentLength] = computeBestRevenue(currentLength);
            }

            return bestRevenueByLength[rodLength];
        }

        private int computeBestRevenue(int targetLength) {
            int bestRevenue = 0;

            for (int firstCutLength = 1; firstCutLength <= targetLength; firstCutLength++) {
                bestRevenue = Math.max(
                        bestRevenue,
                        priceTable.priceFor(firstCutLength)
                                + bestRevenueByLength[targetLength - firstCutLength]
                );
            }

            return bestRevenue;
        }
    }

    private static final class PriceTable {
        private final int[] pricesByLength;

        private PriceTable(int[] pricesByLength) {
            this.pricesByLength = pricesByLength;
        }

        static PriceTable fromSentinelArray(int[] priceTableWithSentinel) {
            if (priceTableWithSentinel == null || priceTableWithSentinel.length == 0) {
                throw new IllegalArgumentException(
                        "Price table must contain a sentinel at index 0."
                );
            }

            return new PriceTable(
                    Arrays.copyOfRange(priceTableWithSentinel, 1, priceTableWithSentinel.length)
            );
        }

        static PriceTable fromPricesByLength(int[] pricesByLength) {
            if (pricesByLength == null) {
                throw new IllegalArgumentException("Prices by length must not be null.");
            }

            return new PriceTable(Arrays.copyOf(pricesByLength, pricesByLength.length));
        }

        int maxLength() {
            return pricesByLength.length;
        }

        int priceFor(int rodLength) {
            return pricesByLength[rodLength - 1];
        }
    }

    public static void main(String[] args) {
        int[] pricesByLength = {1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(maxRevenueForPricesByLength(pricesByLength));
    }
}
