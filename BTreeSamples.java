final class BTreeSamples {
    static final int DEFAULT_MIN_DEGREE = 3;
    static final int[] DEFAULT_KEYS = {10, 20, 5, 6, 12, 30, 7, 17};

    private BTreeSamples() {
    }

    static BTree newDefaultTree() {
        return fromKeys(DEFAULT_MIN_DEGREE, DEFAULT_KEYS);
    }

    static BTree fromKeys(int minDegree, int... keys) {
        return BTree.fromKeys(minDegree, keys);
    }
}
