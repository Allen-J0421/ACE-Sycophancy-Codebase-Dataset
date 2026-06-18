public class GraphException extends RuntimeException {
    public GraphException(String message) {
        super(message);
    }

    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class InvalidVertexException extends GraphException {
        public InvalidVertexException(int vertex, int vertexCount) {
            super(String.format("Invalid vertex %d. Valid range: [0, %d)", vertex, vertexCount));
        }
    }

    public static class InvalidGraphConfigurationException extends GraphException {
        public InvalidGraphConfigurationException(String message) {
            super("Invalid graph configuration: " + message);
        }
    }
}
