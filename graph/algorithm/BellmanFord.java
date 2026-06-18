package graph.algorithm;

import graph.model.IDirectedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BellmanFord {
    private final IDirectedGraph graph;
    private final double[] distances;
    private final int[] parent;
    private boolean hasNegativeCycle;

    public BellmanFord(IDirectedGraph graph) {
        this.graph = graph;
        this.distances = new double[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
    }

    public void computeShortestPaths(int source) {
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        distances[source] = 0;

        for (int i = 0; i < graph.getVertexCount() - 1; i++) {
            for (int u = 0; u < graph.getVertexCount(); u++) {
                if (distances[u] != Double.POSITIVE_INFINITY) {
                    for (int v : graph.getOutgoing(u)) {
                        double weight = graph.getEdgeWeight(u, v);
                        if (distances[u] + weight < distances[v]) {
                            distances[v] = distances[u] + weight;
                            parent[v] = u;
                        }
                    }
                }
            }
        }

        hasNegativeCycle = detectNegativeCycle();
    }

    private boolean detectNegativeCycle() {
        for (int u = 0; u < graph.getVertexCount(); u++) {
            if (distances[u] != Double.POSITIVE_INFINITY) {
                for (int v : graph.getOutgoing(u)) {
                    double weight = graph.getEdgeWeight(u, v);
                    if (distances[u] + weight < distances[v]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public double getDistance(int vertex) {
        if (hasNegativeCycle) {
            throw new IllegalStateException("Graph contains negative weight cycle");
        }
        return distances[vertex];
    }

    public List<Integer> getPath(int destination) {
        if (hasNegativeCycle) {
            throw new IllegalStateException("Graph contains negative weight cycle");
        }

        List<Integer> path = new ArrayList<>();
        int current = destination;

        while (current != -1) {
            path.add(0, current);
            current = parent[current];
        }

        if (distances[destination] == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();
        }

        return path;
    }

    public boolean hasNegativeCycle() {
        return hasNegativeCycle;
    }

    public boolean hasPathTo(int vertex) {
        return distances[vertex] != Double.POSITIVE_INFINITY;
    }
}
