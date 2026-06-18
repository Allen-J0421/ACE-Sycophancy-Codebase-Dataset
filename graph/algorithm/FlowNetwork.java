package graph.algorithm;

import graph.model.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowNetwork {
    private final int vertexCount;
    private final Map<String, Double> capacity;
    private final Map<String, Double> flow;
    private final Map<Integer, List<Integer>> adjList;

    public FlowNetwork(int vertexCount) {
        this.vertexCount = vertexCount;
        this.capacity = new HashMap<>();
        this.flow = new HashMap<>();
        this.adjList = new HashMap<>();

        for (int i = 0; i < vertexCount; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, double cap) {
        String edge = u + "-" + v;
        String revEdge = v + "-" + u;

        capacity.putIfAbsent(edge, 0.0);
        capacity.put(edge, capacity.get(edge) + cap);
        capacity.putIfAbsent(revEdge, 0.0);

        flow.putIfAbsent(edge, 0.0);
        flow.putIfAbsent(revEdge, 0.0);

        if (!adjList.get(u).contains(v)) {
            adjList.get(u).add(v);
        }
        if (!adjList.get(v).contains(u)) {
            adjList.get(v).add(u);
        }
    }

    public double getCapacity(int u, int v) {
        return capacity.getOrDefault(u + "-" + v, 0.0);
    }

    public double getFlow(int u, int v) {
        return flow.getOrDefault(u + "-" + v, 0.0);
    }

    public void setFlow(int u, int v, double f) {
        flow.put(u + "-" + v, f);
    }

    public double getResidualCapacity(int u, int v) {
        String edge = u + "-" + v;
        return capacity.getOrDefault(edge, 0.0) - flow.getOrDefault(edge, 0.0);
    }

    public List<Integer> getAdjacent(int v) {
        return adjList.getOrDefault(v, new ArrayList<>());
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Map<String, Double> getFlowMap() {
        return new HashMap<>(flow);
    }

    public double getTotalFlow() {
        double total = 0;
        for (int v : adjList.get(0)) {
            total += getFlow(0, v);
        }
        return total;
    }
}
