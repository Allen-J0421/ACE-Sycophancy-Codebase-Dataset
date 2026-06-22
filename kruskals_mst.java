import java.util.Arrays;
import java.util.Comparator;

final class KruskalMST {
    private static final int EDGE_FIELD_COUNT = 3;

    private KruskalMST() {
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        Graph graph = Graph.from(vertexCount, rawEdges);
        return new KruskalSolver(graph).compute().totalWeight();
    }

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}
        };

        System.out.println(kruskalsMST(4, edges));
    }

    private record Graph(int vertexCount, Edge[] edges) {
        private static Graph from(int vertexCount, int[][] rawEdges) {
            validateVertexCount(vertexCount);

            if (rawEdges == null) {
                throw new IllegalArgumentException("Edges cannot be null.");
            }

            Edge[] parsedEdges = new Edge[rawEdges.length];
            for (int i = 0; i < rawEdges.length; i++) {
                parsedEdges[i] = Edge.fromRaw(rawEdges[i], vertexCount);
            }

            return new Graph(vertexCount, parsedEdges);
        }

        private int requiredEdgeCount() {
            return Math.max(0, vertexCount - 1);
        }

        private boolean isTriviallyConnected() {
            return vertexCount <= 1;
        }

        private Edge[] sortedEdgesByWeight() {
            Edge[] sortedEdges = edges.clone();
            Arrays.sort(sortedEdges, Comparator.comparingInt(Edge::weight));
            return sortedEdges;
        }
    }

    private record MstResult(int totalWeight, int edgesUsed) {
        private boolean spans(Graph graph) {
            return edgesUsed == graph.requiredEdgeCount();
        }
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private record Edge(int from, int to, int weight) {
        private static Edge fromRaw(int[] rawEdge, int vertexCount) {
            if (rawEdge == null || rawEdge.length != EDGE_FIELD_COUNT) {
                throw new IllegalArgumentException("Each edge must contain exactly 3 integers.");
            }

            int from = rawEdge[0];
            int to = rawEdge[1];
            validateVertex(from, vertexCount);
            validateVertex(to, vertexCount);

            return new Edge(from, to, rawEdge[2]);
        }

        private static void validateVertex(int vertex, int vertexCount) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException(
                    "Vertex index out of bounds: " + vertex
                );
            }
        }
    }

    private static final class KruskalSolver {
        private final Graph graph;
        private final DisjointSet disjointSet;

        private KruskalSolver(Graph graph) {
            this.graph = graph;
            this.disjointSet = new DisjointSet(graph.vertexCount());
        }

        private MstResult compute() {
            int totalWeight = 0;
            int edgesUsed = 0;

            for (Edge edge : graph.sortedEdgesByWeight()) {
                if (!disjointSet.union(edge.from(), edge.to())) {
                    continue;
                }

                totalWeight += edge.weight();
                edgesUsed++;

                if (edgesUsed == graph.requiredEdgeCount()) {
                    return new MstResult(totalWeight, edgesUsed);
                }
            }

            MstResult result = new MstResult(totalWeight, edgesUsed);
            if (graph.isTriviallyConnected() || result.spans(graph)) {
                return result;
            }

            throw new IllegalArgumentException("Input graph must be connected to form an MST.");
        }
    }

    private static final class DisjointSet {
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

        private int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]);
            }
            return parent[node];
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
