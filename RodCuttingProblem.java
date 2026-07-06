class RodCuttingProblem {
    private final int[] price;

    RodCuttingProblem(int[] price) {
        this.price = price.clone();
    }

    int rodLength() {
        return price.length - 1;
    }

    int priceFor(int length) {
        return price[length];
    }
}
