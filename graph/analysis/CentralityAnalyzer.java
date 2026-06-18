package graph.analysis;

import graph.core.IGraph;

import java.util.HashMap;
import java.util.Map;

public class CentralityAnalyzer {
    private final IGraph graph;
    private final Map<Integer, Double> degreeCentrality;
    private final Map<Integer, Double> closenessCentrality;

    public CentralityAnalyzer(IGraph graph) {
        this.graph = graph;
        this.degreeCentrality = new HashMap<>();
        this.closenessCentrality = new HashMap<>();
        calculateCentralities();
    }

    private void calculateCentralities() {
        int n = graph.getVertexCount();
        if (n == 0) {
            return;
        }

        for (int v = 0; v < n; v++) {
            degreeCentrality.put(v, (double) graph.getDegree(v) / (n - 1));
        }

        for (int v = 0; v < n; v++) {
            closenessCentrality.put(v, calculateCloseness(v));
        }
    }

    private double calculateCloseness(int vertex) {
        int n = graph.getVertexCount();
        int[] distances = bfsDistances(vertex);

        int reachable = 0;
        long sumDistances = 0;

        for (int i = 0; i < n; i++) {
            if (i != vertex && distances[i] != Integer.MAX_VALUE) {
                reachable++;
                sumDistances += distances[i];
            }
        }

        if (reachable == 0) {
            return 0;
        }

        return (double) reachable / sumDistances;
    }

    private int[] bfsDistances(int start) {
        int n = graph.getVertexCount();
        int[] distances = new int[n];
        java.util.Arrays.fill(distances, Integer.MAX_VALUE);

        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        distances[start] = 0;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : graph.getNeighbors(v)) {
                if (distances[neighbor] == Integer.MAX_VALUE) {
                    distances[neighbor] = distances[v] + 1;
                    queue.offer(neighbor);
                }
            }
        }

        return distances;
    }

    public double getDegreeCentrality(int vertex) {
        return degreeCentrality.getOrDefault(vertex, 0.0);
    }

    public double getClosenessCentrality(int vertex) {
        return closenessCentrality.getOrDefault(vertex, 0.0);
    }

    public int getMostCentralVertexByDegree() {
        return degreeCentrality.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    public int getMostCentralVertexByCloseness() {
        return closenessCentrality.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    public Map<Integer, Double> getAllDegreeCentralities() {
        return new HashMap<>(degreeCentrality);
    }

    public Map<Integer, Double> getAllClosenessCentralities() {
        return new HashMap<>(closenessCentrality);
    }
}
