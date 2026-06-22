public final class PriceTable {

    private final int[] prices;

    private PriceTable(int[] prices) {
        this.prices = prices;
    }

    public static PriceTable of(int[] prices) {
        if (prices == null) {
            throw new IllegalArgumentException("prices must not be null");
        }
        if (prices.length == 0) {
            throw new IllegalArgumentException("prices must contain at least the unused zero slot");
        }
        return new PriceTable(prices.clone());
    }

    public int maxRodLength() {
        return prices.length - 1;
    }

    public boolean hasPriceFor(int rodLength) {
        return rodLength > 0 && rodLength < prices.length;
    }

    public int priceFor(int rodLength) {
        return hasPriceFor(rodLength) ? prices[rodLength] : 0;
    }
}
