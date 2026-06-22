/**
 * Dynamic-programming solution to the rod-cutting problem.
 *
 * The price table is 1-indexed: {@code prices[length]} is the value of a
 * single piece of that length, and {@code prices[0]} is unused.
 */
public final class CuttingRod {

    private CuttingRod() {
        // Utility class.
    }

    public static int cutRod(int[] prices) {
        return prices == null ? 0 : cutRod(prices, prices.length - 1);
    }

    public static int cutRod(int[] prices, int rodLength) {
        if (prices == null || rodLength <= 0) {
            return 0;
        }
        if (rodLength >= prices.length) {
            throw new IllegalArgumentException(
                    "rodLength must be less than prices.length for a 1-indexed price table");
        }

        int[] bestRevenue = new int[rodLength + 1];
        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            int bestForLength = 0;
            for (int firstCut = 1; firstCut <= currentLength; firstCut++) {
                bestForLength = Math.max(
                        bestForLength,
                        prices[firstCut] + bestRevenue[currentLength - firstCut]);
            }
            bestRevenue[currentLength] = bestForLength;
        }

        return bestRevenue[rodLength];
    }

    public static void main(String[] args) {
        int[] samplePrices = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(samplePrices));
    }
}
