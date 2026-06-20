final class ShortestPathResult {
    private final boolean negativeCycle;
    private final int[] distances;

    private ShortestPathResult(boolean negativeCycle, int[] distances) {
        this.negativeCycle = negativeCycle;
        this.distances = distances;
    }

    static ShortestPathResult success(int[] distances) {
        return new ShortestPathResult(false, distances.clone());
    }

    static ShortestPathResult negativeCycle() {
        return new ShortestPathResult(true, new int[0]);
    }

    boolean hasNegativeCycle() {
        return negativeCycle;
    }

    int[] distances() {
        return distances.clone();
    }
}
