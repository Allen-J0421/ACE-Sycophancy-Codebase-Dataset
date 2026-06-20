public record Vertex(int id) {

    public static Vertex of(int id) {
        return new Vertex(id);
    }
}
