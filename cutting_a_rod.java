class CuttingRod {
    private static final int EMPTY_ROD_REVENUE = 0;
    private static final int FIRST_SELLABLE_LENGTH = 1;
    private static final int SENTINEL_INDEX = 0;

    static int cutRod(int[] pricesByLength) {
        validatePriceTable(pricesByLength);
        return cutRod(pricesByLength, pricesByLength.length - 1);
    }

    static int cutRod(int[] pricesByLength, int rodLength) {
        validatePriceTable(pricesByLength);
        validateRodLength(pricesByLength, rodLength);

        int[] maxRevenue = new int[rodLength + 1];
        maxRevenue[SENTINEL_INDEX] = EMPTY_ROD_REVENUE;

        for (int currentLength = FIRST_SELLABLE_LENGTH; currentLength <= rodLength; currentLength++) {
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
        int bestRevenue = EMPTY_ROD_REVENUE;

        for (int firstCutLength = FIRST_SELLABLE_LENGTH; firstCutLength <= currentLength; firstCutLength++) {
            int remainingLength = currentLength - firstCutLength;
            int revenue = pricesByLength[firstCutLength] + maxRevenue[remainingLength];
            bestRevenue = Math.max(bestRevenue, revenue);
        }

        return bestRevenue;
    }

    public static void main(String[] args) {
        int[] pricesByLength = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(pricesByLength));
    }
}
