public final class ShortestPathResult {
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

    public boolean hasNegativeCycle() {
        return negativeCycle;
    }

    public int[] distances() {
        return distances.clone();
    }
}
