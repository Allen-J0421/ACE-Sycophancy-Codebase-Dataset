package graph.io;

import graph.core.IGraph;

public class GraphSerializer {
    public static String serialize(IGraph graph, GraphFormat format) {
        switch (format) {
            case ADJACENCY_LIST:
                return serializeAdjacencyList(graph);
            case EDGE_LIST:
                return serializeEdgeList(graph);
            case MATRIX:
                return serializeMatrix(graph);
            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
    }

    private static String serializeAdjacencyList(IGraph graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Adjacency List\n");
        sb.append("# Vertices: ").append(graph.getVertexCount()).append("\n");
        sb.append("# Edges: ").append(graph.getEdgeCount()).append("\n");

        for (int v : graph.getAllVertices()) {
            sb.append(v).append(": ");
            java.util.List<Integer> neighbors = graph.getNeighbors(v);
            for (int i = 0; i < neighbors.size(); i++) {
                sb.append(neighbors.get(i));
                if (i < neighbors.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static String serializeEdgeList(IGraph graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Edge List\n");
        sb.append("# Vertices: ").append(graph.getVertexCount()).append("\n");
        sb.append("# Edges: ").append(graph.getEdgeCount()).append("\n");

        for (int u : graph.getAllVertices()) {
            for (int v : graph.getNeighbors(u)) {
                if (u < v) {
                    sb.append(u).append(" ").append(v).append("\n");
                }
            }
        }

        return sb.toString();
    }

    private static String serializeMatrix(IGraph graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Adjacency Matrix\n");
        sb.append("# Vertices: ").append(graph.getVertexCount()).append("\n");

        int n = graph.getVertexCount();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(graph.hasEdge(i, j) ? "1" : "0");
                if (j < n - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
