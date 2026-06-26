class BestCut {
    private final int cutLength;
    private final int revenue;

    BestCut(int cutLength, int revenue) {
        this.cutLength = cutLength;
        this.revenue = revenue;
    }

    int cutLength() {
        return cutLength;
    }

    int revenue() {
        return revenue;
    }
}
