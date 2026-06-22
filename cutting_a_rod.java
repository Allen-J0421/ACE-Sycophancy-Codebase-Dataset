class CuttingRod {

    static int cutRod(int[] price) {
        validatePriceTable(price);

        int rodLength = price.length - 1;
        int[] bestRevenueByLength = new int[rodLength + 1];

        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            bestRevenueByLength[currentLength] =
                    computeBestRevenue(price, bestRevenueByLength, currentLength);
        }

        return bestRevenueByLength[rodLength];
    }

    private static void validatePriceTable(int[] price) {
        if (price == null || price.length == 0) {
            throw new IllegalArgumentException("Price table must contain a sentinel at index 0.");
        }
    }

    private static int computeBestRevenue(int[] price, int[] bestRevenueByLength, int targetLength) {
        int bestRevenue = 0;

        for (int firstCutLength = 1; firstCutLength <= targetLength; firstCutLength++) {
            bestRevenue = Math.max(
                    bestRevenue,
                    price[firstCutLength] + bestRevenueByLength[targetLength - firstCutLength]
            );
        }

        return bestRevenue;
    }

    public static void main(String[] args) {
        int[] priceByLength = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(priceByLength));
    }
}
