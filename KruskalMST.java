import java.util.Arrays;

final class KruskalMST {
    private KruskalMST() {
        // Utility class.
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        return minimumSpanningTreeCost(vertexCount, parseEdges(rawEdges));
    }

    static int minimumSpanningTreeCost(int vertexCount, Edge[] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        Edge[] sortedEdges = Arrays.copyOf(edges, edges.length);
        Arrays.sort(sortedEdges);

        DisjointSet disjointSet = new DisjointSet(vertexCount);
        int totalCost = 0;
        int edgesUsed = 0;

        for (Edge edge : sortedEdges) {
            validateVertex(edge.from, vertexCount);
            validateVertex(edge.to, vertexCount);

            if (disjointSet.union(edge.from, edge.to)) {
                totalCost += edge.weight;
                if (++edgesUsed == vertexCount - 1) {
                    break;
                }
            }
        }

        return totalCost;
    }

    private static Edge[] parseEdges(int[][] rawEdges) {
        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        Edge[] edges = new Edge[rawEdges.length];
        for (int i = 0; i < rawEdges.length; i++) {
            edges[i] = Edge.fromMatrixRow(rawEdges[i]);
        }
        return edges;
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of bounds for graph with " + vertexCount + " vertices."
            );
        }
    }

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10},
            {1, 3, 15},
            {2, 3, 4},
            {2, 0, 6},
            {0, 3, 5}
        };

        System.out.println(kruskalsMST(4, edges));
    }
}
