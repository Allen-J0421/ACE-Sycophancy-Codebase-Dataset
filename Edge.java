public record Edge(Vertex from, Vertex to) {

    public static Edge of(int from, int to) {
        return new Edge(Vertex.of(from), Vertex.of(to));
    }
}
