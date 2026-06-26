class PriceTable {
    private final LengthIndexedValues prices;

    PriceTable(int[] rawPrices) {
        if (rawPrices == null || rawPrices.length < 2) {
            throw new IllegalArgumentException("prices must have at least one length entry (index 1+)");
        }
        prices = new LengthIndexedValues(rawPrices.length - 1);
        for (int i = 1; i < rawPrices.length; i++) {
            prices.set(i, rawPrices[i]);
        }
    }

    int rodLength() {
        return prices.size();
    }

    int priceAt(int length) {
        if (length < 1 || length > prices.size()) {
            throw new IllegalArgumentException("length out of range: " + length);
        }
        return prices.get(length);
    }
}
