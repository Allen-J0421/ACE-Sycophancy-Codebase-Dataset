import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    private static final int UNREACHABLE = Integer.MAX_VALUE;

    private Dijkstra() {
    }

    private static final class Edge {
        private final int to;
        private final int weight;

        private Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private static final class Graph {
        private final List<List<Edge>> adjacency;

        private Graph(List<List<Edge>> adjacency) {
            this.adjacency = adjacency;
        }

        private static Graph withVertexCount(int vertexCount) {
            validateVertexCount(vertexCount);

            List<List<Edge>> adjacency = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacency.add(new ArrayList<>());
            }
            return new Graph(adjacency);
        }

        private static Graph fromLegacy(ArrayList<ArrayList<int[]>> adj) {
            validateLegacyAdjacencyList(adj);

            int vertexCount = adj.size();
            Graph graph = withVertexCount(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                ArrayList<int[]> rawEdges = adj.get(vertex);
                if (rawEdges == null) {
                    throw new IllegalArgumentException(
                            "Adjacency list contains a null neighbor list at vertex " + vertex);
                }

                List<Edge> edges = graph.adjacency.get(vertex);
                for (int[] rawEdge : rawEdges) {
                    validateRawEdge(rawEdge, vertex);
                    int neighbor = rawEdge[0];
                    int weight = rawEdge[1];

                    validateVertex(neighbor, vertexCount, "neighbor");
                    validateWeight(weight);
                    edges.add(new Edge(neighbor, weight));
                }
            }
            return graph;
        }

        private void addUndirectedEdge(int u, int v, int weight) {
            validateVertex(u, vertexCount(), "u");
            validateVertex(v, vertexCount(), "v");
            validateWeight(weight);

            adjacency.get(u).add(new Edge(v, weight));
            adjacency.get(v).add(new Edge(u, weight));
        }

        private List<Edge> neighborsOf(int vertex) {
            return adjacency.get(vertex);
        }

        private int vertexCount() {
            return adjacency.size();
        }
    }

    private static final class QueueEntry {
        private final int distance;
        private final int vertex;

        private QueueEntry(int distance, int vertex) {
            this.distance = distance;
            this.vertex = vertex;
        }
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return dijkstra(Graph.fromLegacy(adj), src);
    }

    private static ArrayList<Integer> dijkstra(Graph graph, int src) {
        validateGraph(graph);
        validateSource(src, graph.vertexCount());

        int[] distances = computeShortestPaths(graph, src);
        return toArrayList(distances);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        validateLegacyAdjacencyList(adj);
        validateVertex(u, adj.size(), "u");
        validateVertex(v, adj.size(), "v");
        validateWeight(w);

        adj.get(u).add(new int[]{v, w});
        adj.get(v).add(new int[]{u, w});
    }

    private static int[] computeShortestPaths(Graph graph, int src) {
        int[] distances = new int[graph.vertexCount()];
        Arrays.fill(distances, UNREACHABLE);
        distances[src] = 0;

        PriorityQueue<QueueEntry> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(entry -> entry.distance));
        minHeap.offer(new QueueEntry(0, src));

        while (!minHeap.isEmpty()) {
            QueueEntry current = minHeap.poll();
            if (current.distance != distances[current.vertex]) {
                continue;
            }

            for (Edge edge : graph.neighborsOf(current.vertex)) {
                int nextDistance = addDistances(current.distance, edge.weight);
                if (nextDistance < distances[edge.to]) {
                    distances[edge.to] = nextDistance;
                    minHeap.offer(new QueueEntry(distances[edge.to], edge.to));
                }
            }
        }

        return distances;
    }

    private static ArrayList<Integer> toArrayList(int[] distances) {
        ArrayList<Integer> result = new ArrayList<>(distances.length);
        for (int distance : distances) {
            result.add(distance);
        }
        return result;
    }

    private static Graph createSampleGraph() {
        Graph graph = Graph.withVertexCount(5);
        graph.addUndirectedEdge(0, 1, 4);
        graph.addUndirectedEdge(0, 2, 8);
        graph.addUndirectedEdge(1, 4, 6);
        graph.addUndirectedEdge(1, 2, 3);
        graph.addUndirectedEdge(2, 3, 2);
        graph.addUndirectedEdge(3, 4, 10);
        return graph;
    }

    private static String formatDistances(List<Integer> distances) {
        StringBuilder formatted = new StringBuilder();
        for (int index = 0; index < distances.size(); index++) {
            if (index > 0) {
                formatted.append(' ');
            }
            formatted.append(distances.get(index));
        }
        return formatted.toString();
    }

    private static int addDistances(int left, int right) {
        long sum = (long) left + right;
        if (sum > Integer.MAX_VALUE) {
            throw new ArithmeticException("Shortest path exceeds supported integer range");
        }
        return (int) sum;
    }

    private static void validateGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
    }

    private static void validateLegacyAdjacencyList(ArrayList<ArrayList<int[]>> adj) {
        if (adj == null) {
            throw new IllegalArgumentException("Adjacency list must not be null");
        }
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative");
        }
    }

    private static void validateSource(int src, int vertexCount) {
        validateVertex(src, vertexCount, "src");
    }

    private static void validateVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(label + " vertex out of range: " + vertex);
        }
    }

    private static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra's algorithm requires non-negative edge weights");
        }
    }

    private static void validateRawEdge(int[] rawEdge, int fromVertex) {
        if (rawEdge == null || rawEdge.length < 2) {
            throw new IllegalArgumentException(
                    "Edge at vertex " + fromVertex + " must contain destination and weight");
        }
    }

    public static void main(String[] args) {
        int source = 0;
        Graph graph = createSampleGraph();
        ArrayList<Integer> result = dijkstra(graph, source);
        System.out.println(formatDistances(result));
    }
}
