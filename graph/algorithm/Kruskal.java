package graph.algorithm;

import graph.model.Edge;
import graph.model.IWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {
    private final IWeightedGraph graph;
    private final int[] parent;
    private final int[] rank;

    public Kruskal(IWeightedGraph graph) {
        this.graph = graph;
        this.parent = new int[graph.getVertexCount()];
        this.rank = new int[graph.getVertexCount()];

        for (int i = 0; i < graph.getVertexCount(); i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public List<Edge> findMST() {
        List<Edge> edges = new ArrayList<>(graph.getAllEdges());
        Collections.sort(edges);

        List<Edge> mst = new ArrayList<>();
        double totalWeight = 0;

        for (Edge edge : edges) {
            int u = edge.getSource();
            int v = edge.getDestination();

            if (find(u) != find(v)) {
                mst.add(edge);
                union(u, v);
                totalWeight += edge.getWeight();

                if (mst.size() == graph.getVertexCount() - 1) {
                    break;
                }
            }
        }

        return mst;
    }

    public double getMSTWeight() {
        List<Edge> mst = findMST();
        return mst.stream().mapToDouble(Edge::getWeight).sum();
    }

    private int find(int vertex) {
        if (parent[vertex] != vertex) {
            parent[vertex] = find(parent[vertex]);
        }
        return parent[vertex];
    }

    private void union(int u, int v) {
        int rootU = find(u);
        int rootV = find(v);

        if (rootU != rootV) {
            if (rank[rootU] < rank[rootV]) {
                parent[rootU] = rootV;
            } else if (rank[rootU] > rank[rootV]) {
                parent[rootV] = rootU;
            } else {
                parent[rootV] = rootU;
                rank[rootU]++;
            }
        }
    }
}
