final class RevenueTable {
    private final LengthIndexedValues bestRevenueByLength;

    RevenueTable(int maxLength) {
        this.bestRevenueByLength = LengthIndexedValues.initializedToZero(0, maxLength);
    }

    int bestRevenueFor(int rodLength) {
        return bestRevenueByLength.valueAt(rodLength);
    }

    void recordBestRevenue(int rodLength, int revenue) {
        bestRevenueByLength.setValueAt(rodLength, revenue);
    }
}
