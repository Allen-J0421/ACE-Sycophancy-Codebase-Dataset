final class RevenueTable {
    private final int[] bestRevenueByLength;

    RevenueTable(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Maximum rod length must not be negative.");
        }

        this.bestRevenueByLength = new int[maxLength + 1];
    }

    int bestRevenueFor(int rodLength) {
        validateLength(rodLength);
        return bestRevenueByLength[rodLength];
    }

    void recordBestRevenue(int rodLength, int revenue) {
        validateLength(rodLength);
        bestRevenueByLength[rodLength] = revenue;
    }

    private void validateLength(int rodLength) {
        if (rodLength < 0 || rodLength >= bestRevenueByLength.length) {
            throw new IllegalArgumentException("Rod length is out of bounds: " + rodLength);
        }
    }
}
