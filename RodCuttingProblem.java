class RodCuttingProblem {
    private final int rodLength;
    private final PriceProvider priceProvider;

    RodCuttingProblem(int rodLength, PriceProvider priceProvider) {
        this.rodLength = rodLength;
        this.priceProvider = priceProvider;
    }

    static RodCuttingProblem fromArray(int[] price) {
        return new RodCuttingProblem(price.length - 1, length -> price[length]);
    }

    int rodLength() {
        return rodLength;
    }

    int priceFor(int length) {
        return priceProvider.priceFor(length);
    }
}
