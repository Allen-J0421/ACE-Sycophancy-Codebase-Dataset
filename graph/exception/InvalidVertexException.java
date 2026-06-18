package graph.exception;

public class InvalidVertexException extends GraphException {
    private final int vertex;
    private final int validRange;

    public InvalidVertexException(int vertex, int validRange) {
        super(String.format("Vertex %d is out of bounds [0, %d)", vertex, validRange));
        this.vertex = vertex;
        this.validRange = validRange;
    }

    public int getVertex() {
        return vertex;
    }

    public int getValidRange() {
        return validRange;
    }
}
