public record PairMatch(int leftIndex, int rightIndex, int leftValue, int rightValue) {

    public PairMatch {
        if (leftIndex < 0) {
            throw new IllegalArgumentException("leftIndex must not be negative");
        }

        if (rightIndex <= leftIndex) {
            throw new IllegalArgumentException("rightIndex must be greater than leftIndex");
        }
    }

    public long sum() {
        return (long) leftValue + rightValue;
    }
}
