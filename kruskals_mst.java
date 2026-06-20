import java.util.Arrays;

class KruskalMST {
    private static final int SOURCE = 0;
    private static final int DESTINATION = 1;
    private static final int WEIGHT = 2;
    private static final int EDGE_FIELD_COUNT = 3;

    private KruskalMST() {
    }

    public static int kruskalsMST(int vertexCount, int[][] edges) {
        return kruskalMST(vertexCount, edges);
    }

    public static int kruskalMST(int vertexCount, int[][] edges) {
        return minimumSpanningTreeCost(vertexCount, edges);
    }

    public static int minimumSpanningTreeCost(int vertexCount, int[][] edges) {
        validateGraph(vertexCount, edges);
        return minimumSpanningTreeCost(vertexCount, copyEdgesSortedByWeight(edges));
    }

    private static int minimumSpanningTreeCost(int vertexCount, Edge[] sortedEdges) {
        DisjointSet disjointSet = new DisjointSet(vertexCount);
        int totalCost = 0;
        int selectedEdges = 0;

        for (Edge edge : sortedEdges) {
            if (disjointSet.union(edge.source, edge.destination)) {
                totalCost += edge.weight;
                selectedEdges++;
                if (hasEnoughEdgesForSpanningTree(vertexCount, selectedEdges)) {
                    break;
                }
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        System.out.println(minimumSpanningTreeCost(4, sampleEdges()));
    }

    private static Edge[] copyEdgesSortedByWeight(int[][] edges) {
        Edge[] sortedEdges = new Edge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            sortedEdges[i] = Edge.from(edges[i]);
        }
        Arrays.sort(sortedEdges);
        return sortedEdges;
    }

    private static boolean hasEnoughEdgesForSpanningTree(int vertexCount, int selectedEdges) {
        return selectedEdges == requiredEdgeCount(vertexCount);
    }

    private static int requiredEdgeCount(int vertexCount) {
        return Math.max(0, vertexCount - 1);
    }

    private static void validateGraph(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must not be negative");
        }
        if (edges == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
        for (int i = 0; i < edges.length; i++) {
            validateEdge(vertexCount, edges[i], i);
        }
    }

    private static void validateEdge(int vertexCount, int[] edge, int edgeIndex) {
        if (edge == null || edge.length != EDGE_FIELD_COUNT) {
            throw new IllegalArgumentException("edge " + edgeIndex + " must contain source, destination, and weight");
        }
        if (!isVertexInGraph(edge[SOURCE], vertexCount)
                || !isVertexInGraph(edge[DESTINATION], vertexCount)) {
            throw new IllegalArgumentException("edge " + edgeIndex + " references a vertex outside the graph");
        }
    }

    private static boolean isVertexInGraph(int vertex, int vertexCount) {
        return vertex >= 0 && vertex < vertexCount;
    }

    private static int[][] sampleEdges() {
        return new int[][] {
            {0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}
        };
    }

    private static class Edge implements Comparable<Edge> {
        private final int source;
        private final int destination;
        private final int weight;

        private Edge(int source, int destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        private static Edge from(int[] edge) {
            return new Edge(edge[SOURCE], edge[DESTINATION], edge[WEIGHT]);
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(weight, other.weight);
        }
    }

    private static class DisjointSet {
        private final int[] parent;
        private final int[] rank;

        private DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        private int find(int vertex) {
            if (parent[vertex] != vertex) {
                parent[vertex] = find(parent[vertex]);
            }
            return parent[vertex];
        }

        private boolean union(int first, int second) {
            int firstRoot = find(first);
            int secondRoot = find(second);

            if (firstRoot == secondRoot) {
                return false;
            }

            if (rank[firstRoot] < rank[secondRoot]) {
                parent[firstRoot] = secondRoot;
            } else if (rank[firstRoot] > rank[secondRoot]) {
                parent[secondRoot] = firstRoot;
            } else {
                parent[secondRoot] = firstRoot;
                rank[firstRoot]++;
            }

            return true;
        }
    }
}
