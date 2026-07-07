class SorterFactory {

    enum Algorithm {
        BUBBLE
    }

    static <T extends Comparable<T>> Sorter<T> create(Algorithm algorithm) {
        switch (algorithm) {
            case BUBBLE: return new BubbleSort<>();
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
