public record Edge(int from, int to) {

    public static Edge of(int from, int to) {
        return new Edge(from, to);
    }
}
