import java.util.Objects;

class EdgeWeightValidator implements Validator<Integer> {
    @Override
    public void validate(Integer weight) throws IllegalArgumentException {
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight must be non-negative");
        }
    }
}

class Edge {
    private final int destination;
    private final int weight;
    private static final EdgeWeightValidator WEIGHT_VALIDATOR = new EdgeWeightValidator();

    private Edge(int destination, int weight) {
        WEIGHT_VALIDATOR.validate(weight);
        this.destination = destination;
        this.weight = weight;
    }

    static Edge of(int destination, int weight) {
        return new Edge(destination, weight);
    }

    int getDestination() {
        return destination;
    }

    int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Edge(to=%d, weight=%d)", destination, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return destination == other.destination && weight == other.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, weight);
    }
}
