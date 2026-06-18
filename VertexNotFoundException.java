class VertexNotFoundException extends GraphException {
    private final Object vertex;

    VertexNotFoundException(Object vertex) {
        super("Vertex " + vertex + " is not in this graph");
        this.vertex = vertex;
    }

    Object vertex() {
        return vertex;
    }
}
