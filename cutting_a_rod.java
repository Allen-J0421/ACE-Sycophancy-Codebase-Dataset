class CuttingRod {

    static int cutRod(int[] price) {
        if (price == null || price.length == 0) {
            throw new IllegalArgumentException("price must include a sentinel value at index 0");
        }

        int rodLength = price.length - 1;
        int[] maxRevenue = new int[rodLength + 1];

        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            for (int firstCutLength = 1; firstCutLength <= currentLength; firstCutLength++) {
                int remainingLength = currentLength - firstCutLength;
                int revenue = price[firstCutLength] + maxRevenue[remainingLength];
                maxRevenue[currentLength] = Math.max(maxRevenue[currentLength], revenue);
            }
        }

        return maxRevenue[rodLength];
    }

    public static void main(String[] args) {
        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(price));
    }
}
