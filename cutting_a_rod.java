import java.util.Arrays;

final class CuttingRod {

    static int cutRod(int[] priceTableWithSentinel) {
        PriceTable priceTable = PriceTable.fromSentinelArray(priceTableWithSentinel);
        return new RodCuttingSolver(priceTable).solve();
    }

    private static final class RodCuttingSolver {
        private final PriceTable priceTable;
        private final int[] bestRevenueByLength;

        private RodCuttingSolver(PriceTable priceTable) {
            this.priceTable = priceTable;
            this.bestRevenueByLength = new int[priceTable.maxLength() + 1];
        }

        int solve() {
            for (int currentLength = 1; currentLength <= priceTable.maxLength(); currentLength++) {
                bestRevenueByLength[currentLength] = computeBestRevenue(currentLength);
            }

            return bestRevenueByLength[priceTable.maxLength()];
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

            return new PriceTable(Arrays.copyOf(priceTableWithSentinel, priceTableWithSentinel.length));
        }

        int maxLength() {
            return pricesByLength.length - 1;
        }

        int priceFor(int rodLength) {
            return pricesByLength[rodLength];
        }
    }

    public static void main(String[] args) {
        int[] priceByLength = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(priceByLength));
    }
}
