public record Edge(int source, int destination) {
    @Override
    public String toString() {
        return source + " -> " + destination;
    }
}
