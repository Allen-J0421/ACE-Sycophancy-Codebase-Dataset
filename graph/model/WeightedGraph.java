package graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedGraph implements IWeightedGraph {
    private final int vertexCount;
    private final Map<String, Double> edges;
    private final Map<Integer, List<Edge>> adjacency;

    public WeightedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        this.vertexCount = vertexCount;
        this.edges = new HashMap<>();
        this.adjacency = new HashMap<>();

        for (int i = 0; i < vertexCount; i++) {
            adjacency.put(i, new ArrayList<>());
        }
    }

    @Override
    public void addWeightedEdge(int u, int v, double weight) {
        validateVertex(u);
        validateVertex(v);

        if (u == v) {
            throw new IllegalArgumentException("Self-loops not allowed");
        }

        String key = getEdgeKey(u, v);
        if (!edges.containsKey(key)) {
            Edge edge = new Edge(u, v, weight);
            edges.put(key, weight);
            adjacency.get(u).add(edge);
            adjacency.get(v).add(edge);
        }
    }

    @Override
    public double getEdgeWeight(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        String key = getEdgeKey(u, v);
        return edges.getOrDefault(key, -1.0);
    }

    @Override
    public List<Edge> getEdges(int vertex) {
        validateVertex(vertex);
        return new ArrayList<>(adjacency.get(vertex));
    }

    @Override
    public List<Edge> getAllEdges() {
        List<Edge> allEdges = new ArrayList<>();
        for (List<Edge> edgeList : adjacency.values()) {
            for (Edge edge : edgeList) {
                if (edge.getSource() < edge.getDestination()) {
                    allEdges.add(edge);
                }
            }
        }
        return allEdges;
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public boolean hasEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        return edges.containsKey(getEdgeKey(u, v));
    }

    private String getEdgeKey(int u, int v) {
        int min = Math.min(u, v);
        int max = Math.max(u, v);
        return min + "," + max;
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException("Vertex " + vertex + " out of bounds");
        }
    }
}
