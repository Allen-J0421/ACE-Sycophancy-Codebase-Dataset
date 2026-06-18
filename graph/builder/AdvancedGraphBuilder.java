package graph.builder;

import graph.model.DirectedGraph;
import graph.model.WeightedGraph;

public class AdvancedGraphBuilder {
    public static DirectedGraph directedDAG(int vertices, int edges) {
        DirectedGraph graph = new DirectedGraph(vertices);
        int added = 0;

        for (int i = 0; i < vertices && added < edges; i++) {
            for (int j = i + 1; j < vertices && added < edges; j++) {
                double weight = Math.random() * 10;
                graph.addDirectedEdge(i, j, weight);
                added++;
            }
        }

        return graph;
    }

    public static DirectedGraph directedCycle(int vertices) {
        DirectedGraph graph = new DirectedGraph(vertices);
        for (int i = 0; i < vertices; i++) {
            graph.addDirectedEdge(i, (i + 1) % vertices, 1.0);
        }
        return graph;
    }

    public static DirectedGraph directedPath(int vertices) {
        DirectedGraph graph = new DirectedGraph(vertices);
        for (int i = 0; i < vertices - 1; i++) {
            graph.addDirectedEdge(i, i + 1, 1.0);
        }
        return graph;
    }

    public static DirectedGraph randomDirectedGraph(int vertices, int edges) {
        DirectedGraph graph = new DirectedGraph(vertices);
        int added = 0;

        while (added < edges) {
            int u = (int) (Math.random() * vertices);
            int v = (int) (Math.random() * vertices);

            if (u != v && !graph.hasEdge(u, v)) {
                double weight = Math.random() * 10;
                graph.addDirectedEdge(u, v, weight);
                added++;
            }
        }

        return graph;
    }

    public static WeightedGraph completeWeightedGraph(int vertices) {
        WeightedGraph graph = new WeightedGraph(vertices);
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                double weight = Math.random() * 10;
                graph.addWeightedEdge(i, j, weight);
            }
        }
        return graph;
    }

    public static WeightedGraph gridGraph(int rows, int cols) {
        int vertices = rows * cols;
        WeightedGraph graph = new WeightedGraph(vertices);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int idx = i * cols + j;

                if (j + 1 < cols) {
                    graph.addWeightedEdge(idx, idx + 1, 1.0);
                }
                if (i + 1 < rows) {
                    graph.addWeightedEdge(idx, idx + cols, 1.0);
                }
            }
        }

        return graph;
    }
}
