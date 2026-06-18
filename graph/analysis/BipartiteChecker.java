package graph.analysis;

import graph.core.IGraph;

import java.util.ArrayList;
import java.util.List;

public class BipartiteChecker {
    private final IGraph graph;
    private final int[] color;
    private boolean isBipartite;

    public BipartiteChecker(IGraph graph) {
        this.graph = graph;
        this.color = new int[graph.getVertexCount()];
        this.isBipartite = checkBipartite();
    }

    private boolean checkBipartite() {
        for (int i = 0; i < color.length; i++) {
            color[i] = -1;
        }

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (color[i] == -1) {
                if (!bfsColor(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean bfsColor(int start) {
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.offer(start);
        color[start] = 0;

        while (!queue.isEmpty()) {
            int vertex = queue.poll();

            for (int neighbor : graph.getNeighbors(vertex)) {
                if (color[neighbor] == -1) {
                    color[neighbor] = 1 - color[vertex];
                    queue.offer(neighbor);
                } else if (color[neighbor] == color[vertex]) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isBipartite() {
        return isBipartite;
    }

    public List<Integer> getPartition(int partitionId) {
        if (!isBipartite) {
            throw new IllegalStateException("Graph is not bipartite");
        }
        if (partitionId != 0 && partitionId != 1) {
            throw new IllegalArgumentException("Partition ID must be 0 or 1");
        }

        List<Integer> partition = new ArrayList<>();
        for (int i = 0; i < color.length; i++) {
            if (color[i] == partitionId) {
                partition.add(i);
            }
        }
        return partition;
    }

    public int[] getColoring() {
        return color.clone();
    }
}
