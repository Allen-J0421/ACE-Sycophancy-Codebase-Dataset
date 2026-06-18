package graph.builder;

import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.model.WeightedGraph;

public class GraphGenerator {
    public static Graph completeGraph(int n) {
        GraphBuilder builder = new GraphBuilder(n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                builder.addEdge(i, j);
            }
        }
        return builder.build();
    }

    public static Graph cycleGraph(int n) {
        if (n < 3) {
            throw new IllegalArgumentException("Cycle requires at least 3 vertices");
        }
        GraphBuilder builder = new GraphBuilder(n);
        for (int i = 0; i < n; i++) {
            builder.addEdge(i, (i + 1) % n);
        }
        return builder.build();
    }

    public static Graph pathGraph(int n) {
        GraphBuilder builder = new GraphBuilder(n);
        for (int i = 0; i < n - 1; i++) {
            builder.addEdge(i, i + 1);
        }
        return builder.build();
    }

    public static Graph bipartiteGraph(int n1, int n2) {
        GraphBuilder builder = new GraphBuilder(n1 + n2);
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                builder.addEdge(i, n1 + j);
            }
        }
        return builder.build();
    }

    public static Graph wheelGraph(int n) {
        if (n < 4) {
            throw new IllegalArgumentException("Wheel requires at least 4 vertices");
        }
        GraphBuilder builder = new GraphBuilder(n);

        for (int i = 1; i < n; i++) {
            builder.addEdge(i, (i % (n - 1)) + 1);
        }

        for (int i = 1; i < n; i++) {
            builder.addEdge(0, i);
        }

        return builder.build();
    }

    public static WeightedGraph weightedPathGraph(int n) {
        WeightedGraph graph = new WeightedGraph(n);
        for (int i = 0; i < n - 1; i++) {
            graph.addWeightedEdge(i, i + 1, i + 1.0);
        }
        return graph;
    }

    public static WeightedGraph randomWeightedGraph(int n, int edgeCount) {
        if (edgeCount > n * (n - 1) / 2) {
            throw new IllegalArgumentException("Too many edges for graph size");
        }

        WeightedGraph graph = new WeightedGraph(n);
        int added = 0;

        while (added < edgeCount) {
            int u = (int) (Math.random() * n);
            int v = (int) (Math.random() * n);

            if (u != v && !graph.hasEdge(u, v)) {
                double weight = Math.random() * 100;
                graph.addWeightedEdge(u, v, weight);
                added++;
            }
        }

        return graph;
    }
}
