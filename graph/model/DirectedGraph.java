package graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectedGraph implements IDirectedGraph {
    private final int vertexCount;
    private final Map<String, Double> edges;
    private final Map<Integer, List<Integer>> outgoing;
    private final Map<Integer, List<Integer>> incoming;

    public DirectedGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        this.vertexCount = vertexCount;
        this.edges = new HashMap<>();
        this.outgoing = new HashMap<>();
        this.incoming = new HashMap<>();

        for (int i = 0; i < vertexCount; i++) {
            outgoing.put(i, new ArrayList<>());
            incoming.put(i, new ArrayList<>());
        }
    }

    @Override
    public void addDirectedEdge(int from, int to, double weight) {
        validateVertex(from);
        validateVertex(to);

        if (from == to) {
            throw new IllegalArgumentException("Self-loops not allowed");
        }

        String key = from + "->" + to;
        if (!edges.containsKey(key)) {
            edges.put(key, weight);
            outgoing.get(from).add(to);
            incoming.get(to).add(from);
        }
    }

    @Override
    public double getEdgeWeight(int from, int to) {
        validateVertex(from);
        validateVertex(to);
        return edges.getOrDefault(from + "->" + to, -1.0);
    }

    @Override
    public List<Integer> getOutgoing(int vertex) {
        validateVertex(vertex);
        return new ArrayList<>(outgoing.get(vertex));
    }

    @Override
    public List<Integer> getIncoming(int vertex) {
        validateVertex(vertex);
        return new ArrayList<>(incoming.get(vertex));
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
    public boolean hasEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);
        return edges.containsKey(from + "->" + to);
    }

    @Override
    public int getInDegree(int vertex) {
        validateVertex(vertex);
        return incoming.get(vertex).size();
    }

    @Override
    public int getOutDegree(int vertex) {
        validateVertex(vertex);
        return outgoing.get(vertex).size();
    }

    @Override
    public IDirectedGraph reverse() {
        DirectedGraph reversed = new DirectedGraph(vertexCount);
        for (Map.Entry<String, Double> entry : edges.entrySet()) {
            String[] parts = entry.getKey().split("->");
            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);
            reversed.addDirectedEdge(to, from, entry.getValue());
        }
        return reversed;
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException("Vertex " + vertex + " out of bounds");
        }
    }
}
