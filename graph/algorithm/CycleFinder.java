package graph.algorithm;

import graph.core.IGraph;

import java.util.ArrayList;
import java.util.List;

public class CycleFinder {
    private final IGraph graph;
    private boolean[] visited;
    private int[] parent;
    private List<List<Integer>> cycles;

    public CycleFinder(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
        this.cycles = new ArrayList<>();

        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }
    }

    public List<List<Integer>> findAllCycles() {
        cycles.clear();
        visited = new boolean[graph.getVertexCount()];
        parent = new int[graph.getVertexCount()];

        for (int i = 0; i < graph.getVertexCount(); i++) {
            parent[i] = -1;
        }

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfsFindCycles(i, -1);
            }
        }

        return cycles;
    }

    public boolean hasCycle() {
        visited = new boolean[graph.getVertexCount()];

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                if (dfsDetectCycle(i, -1)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void dfsFindCycles(int vertex, int parentVertex) {
        visited[vertex] = true;

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (neighbor == parentVertex) {
                continue;
            }

            if (visited[neighbor]) {
                List<Integer> cycle = new ArrayList<>();
                cycle.add(vertex);
                cycle.add(neighbor);
                cycles.add(cycle);
            } else {
                dfsFindCycles(neighbor, vertex);
            }
        }
    }

    private boolean dfsDetectCycle(int vertex, int parentVertex) {
        visited[vertex] = true;

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (neighbor == parentVertex) {
                continue;
            }

            if (visited[neighbor]) {
                return true;
            }

            if (dfsDetectCycle(neighbor, vertex)) {
                return true;
            }
        }

        return false;
    }
}
