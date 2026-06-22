public class CuttingRod {
    private static final int MIN_ROD_LENGTH = 1;

    public static int cutRod(int[] pricesByLength) {
        validatePriceTable(pricesByLength);
        return maximizeRevenue(pricesByLength, pricesByLength.length - 1);
    }

    public static int cutRod(int[] pricesByLength, int rodLength) {
        validatePriceTable(pricesByLength);
        validateRodLength(pricesByLength, rodLength);

        return maximizeRevenue(pricesByLength, rodLength);
    }

    private static int maximizeRevenue(int[] pricesByLength, int rodLength) {
        int[] maxRevenue = new int[rodLength + 1];

        for (int currentLength = MIN_ROD_LENGTH; currentLength <= rodLength; currentLength++) {
            maxRevenue[currentLength] = bestRevenueForLength(pricesByLength, maxRevenue, currentLength);
        }

        return maxRevenue[rodLength];
    }

    private static void validatePriceTable(int[] pricesByLength) {
        if (pricesByLength == null || pricesByLength.length == 0) {
            throw new IllegalArgumentException("pricesByLength must include a sentinel value at index 0");
        }
    }

    private static void validateRodLength(int[] pricesByLength, int rodLength) {
        if (rodLength < 0 || rodLength >= pricesByLength.length) {
            throw new IllegalArgumentException("rodLength must be within the pricesByLength table");
        }
    }

    private static int bestRevenueForLength(int[] pricesByLength, int[] maxRevenue, int currentLength) {
        int bestRevenue = 0;

        for (int firstCutLength = MIN_ROD_LENGTH; firstCutLength <= currentLength; firstCutLength++) {
            int remainingLength = currentLength - firstCutLength;
            int revenue = pricesByLength[firstCutLength] + maxRevenue[remainingLength];
            bestRevenue = Math.max(bestRevenue, revenue);
        }

        return bestRevenue;
    }

    public static void main(String[] args) {
        System.out.println(cutRod(samplePricesByLength()));
    }

    private static int[] samplePricesByLength() {
        return new int[] {0, 1, 5, 8, 9, 10, 17, 17, 20};
    }
}
