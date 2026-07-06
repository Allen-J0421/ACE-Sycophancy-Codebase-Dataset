import java.util.List;

record Path(List<Integer> vertices, int totalWeight) {

    private static final Path NONE = new Path(List.of(), 0);

    static Path of(List<Integer> vertices, int totalWeight) {
        return new Path(List.copyOf(vertices), totalWeight);
    }

    static Path none() {
        return NONE;
    }

    boolean isEmpty() {
        return vertices.isEmpty();
    }

    int edgeCount() {
        return Math.max(0, vertices.size() - 1);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Path[none]";
        return "Path[vertices=" + vertices + ", totalWeight=" + totalWeight + "]";
    }
}
