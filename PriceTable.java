class PriceTable {
    private final int[] prices;

    PriceTable(int[] prices) {
        if (prices == null || prices.length < 2) {
            throw new IllegalArgumentException("prices must have at least one length entry (index 1+)");
        }
        this.prices = prices.clone();
    }

    int rodLength() {
        return prices.length - 1;
    }

    int priceAt(int length) {
        if (length < 1 || length >= prices.length) {
            throw new IllegalArgumentException("length out of range: " + length);
        }
        return prices[length];
    }
}
