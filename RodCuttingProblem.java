class RodCuttingProblem {
    private final PriceTable prices;

    RodCuttingProblem(PriceTable prices) {
        this.prices = prices;
    }

    int rodLength() {
        return prices.rodLength();
    }

    int priceAt(int length) {
        return prices.priceAt(length);
    }
}
