/**
 * Dynamic-programming solution to the rod-cutting problem.
 */
public final class CuttingRod {

    private CuttingRod() {
        // Utility class.
    }

    public static int cutRod(int[] prices) {
        if (prices == null || prices.length <= 1) {
            return 0;
        }

        int rodLength = prices.length - 1;
        int[] bestRevenue = new int[rodLength + 1];

        for (int length = 1; length <= rodLength; length++) {
            int bestForLength = 0;
            for (int firstCut = 1; firstCut <= length; firstCut++) {
                bestForLength = Math.max(bestForLength,
                        prices[firstCut] + bestRevenue[length - firstCut]);
            }
            bestRevenue[length] = bestForLength;
        }

        return bestRevenue[rodLength];
    }

    public static void main(String[] args) {
        int[] samplePrices = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(samplePrices));
    }
}
