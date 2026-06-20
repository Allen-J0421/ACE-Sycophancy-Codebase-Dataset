import java.util.Arrays;
import java.util.Comparator;

class KruskalMST {
    private static final int SOURCE = 0;
    private static final int DESTINATION = 1;
    private static final int WEIGHT = 2;
    private static final int EDGE_FIELD_COUNT = 3;

    public static int kruskalsMST(int vertexCount, int[][] edges) {
        validateGraph(vertexCount, edges);

        Edge[] sortedEdges = sortedEdges(edges);
        DisjointSet disjointSet = new DisjointSet(vertexCount);
        int totalCost = 0;
        int selectedEdges = 0;

        for (Edge edge : sortedEdges) {
            if (disjointSet.union(edge.source, edge.destination)) {
                totalCost += edge.weight;
                selectedEdges++;
                if (selectedEdges == vertexCount - 1) {
                    break;
                }
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {

        int[][] edges = {
            {0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}
        };

        System.out.println(kruskalsMST(4, edges));
    }

    private static Edge[] sortedEdges(int[][] edges) {
        Edge[] sortedEdges = new Edge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            sortedEdges[i] = Edge.from(edges[i]);
        }
        Arrays.sort(sortedEdges, Comparator.comparingInt(edge -> edge.weight));
        return sortedEdges;
    }

    private static void validateGraph(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must not be negative");
        }
        if (edges == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
        for (int i = 0; i < edges.length; i++) {
            if (edges[i] == null || edges[i].length != EDGE_FIELD_COUNT) {
                throw new IllegalArgumentException("edge " + i + " must contain source, destination, and weight");
            }
            if (edges[i][SOURCE] < 0 || edges[i][SOURCE] >= vertexCount
                    || edges[i][DESTINATION] < 0 || edges[i][DESTINATION] >= vertexCount) {
                throw new IllegalArgumentException("edge " + i + " references a vertex outside the graph");
            }
        }
    }

    private static class Edge {
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
    }
}

class DisjointSet {
    private final int[] parent;
    private final int[] rank;

    public DisjointSet(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    public int find(int vertex) {
        if (parent[vertex] != vertex) {
            parent[vertex] = find(parent[vertex]);
        }
        return parent[vertex];
    }

    public boolean union(int first, int second) {
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
