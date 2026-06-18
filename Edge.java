final class Edge {
    private final int destination;
    private final int weight;

    Edge(int destination, int weight) {
        validateWeight(weight);

        this.destination = destination;
        this.weight = weight;
    }

    int destination() {
        return destination;
    }

    int weight() {
        return weight;
    }

    static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra's algorithm requires non-negative weights.");
        }
    }
}
