package mst;

import java.util.ArrayList;
import java.util.List;

final class KruskalMinimumSpanningTreeSolver implements MinimumSpanningTreeSolver {
    @Override
    public MinimumSpanningTreeResult findMinimumSpanningTree(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        List<Edge> sortedEdges = graph.edges().stream()
            .sorted()
            .toList();

        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        List<Edge> selectedEdges = new ArrayList<>();
        int totalWeight = 0;

        for (Edge edge : sortedEdges) {
            if (disjointSet.union(edge.from(), edge.to())) {
                selectedEdges.add(edge);
                totalWeight += edge.weight();
                if (selectedEdges.size() == graph.vertexCount() - 1) {
                    break;
                }
            }
        }

        return new MinimumSpanningTreeResult(
            totalWeight,
            selectedEdges,
            graph.vertexCount() == 0 || selectedEdges.size() == graph.vertexCount() - 1
        );
    }

    private static final class DisjointSet {
        private final int[] parent;
        private final int[] rank;

        private DisjointSet(int size) {
            if (size < 0) {
                throw new IllegalArgumentException("Disjoint set size must be non-negative.");
            }

            this.parent = new int[size];
            this.rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        private int find(int node) {
            validateNode(node);
            if (parent[node] != node) {
                parent[node] = find(parent[node]);
            }
            return parent[node];
        }

        private boolean union(int first, int second) {
            validateNode(first);
            validateNode(second);

            int rootFirst = find(first);
            int rootSecond = find(second);
            if (rootFirst == rootSecond) {
                return false;
            }

            if (rank[rootFirst] < rank[rootSecond]) {
                parent[rootFirst] = rootSecond;
            } else if (rank[rootFirst] > rank[rootSecond]) {
                parent[rootSecond] = rootFirst;
            } else {
                parent[rootSecond] = rootFirst;
                rank[rootFirst]++;
            }
            return true;
        }

        private void validateNode(int node) {
            if (node < 0 || node >= parent.length) {
                throw new IllegalArgumentException("Node " + node + " is out of bounds.");
            }
        }
    }
}
