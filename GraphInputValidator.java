class GraphInputValidator {
    static void validate(int V, int[][] edges) {
        if (V <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive, got: " + V);
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edges array must not be null");
        }
        for (int[] edge : edges) {
            if (edge.length != 2) {
                throw new IllegalArgumentException("Each edge must specify exactly 2 vertices");
            }
            if (edge[0] < 0 || edge[0] >= V || edge[1] < 0 || edge[1] >= V) {
                throw new IllegalArgumentException(
                    "Edge vertex out of range [0, " + (V - 1) + "]: " + edge[0] + " - " + edge[1]);
            }
        }
    }
}
