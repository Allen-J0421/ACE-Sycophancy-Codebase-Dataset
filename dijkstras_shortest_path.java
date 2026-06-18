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

    private static final class QueueEntry {
        private final int distance;
        private final int vertex;

        private QueueEntry(int distance, int vertex) {
            this.distance = distance;
            this.vertex = vertex;
        }
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        List<List<Edge>> graph = normalizeGraph(adj);
        validateSource(src, graph.size());

        int[] distances = computeShortestPaths(graph, src);
        return toArrayList(distances);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        validateAdjacencyList(adj);
        validateVertex(u, adj.size(), "u");
        validateVertex(v, adj.size(), "v");
        validateWeight(w);

        adj.get(u).add(new int[]{v, w});
        adj.get(v).add(new int[]{u, w});
    }

    private static List<List<Edge>> normalizeGraph(ArrayList<ArrayList<int[]>> adj) {
        validateAdjacencyList(adj);

        int vertexCount = adj.size();
        List<List<Edge>> graph = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            ArrayList<int[]> rawEdges = adj.get(vertex);
            if (rawEdges == null) {
                throw new IllegalArgumentException("Adjacency list contains a null neighbor list at vertex " + vertex);
            }

            List<Edge> edges = new ArrayList<>(rawEdges.size());
            for (int[] rawEdge : rawEdges) {
                validateRawEdge(rawEdge, vertex);
                int neighbor = rawEdge[0];
                int weight = rawEdge[1];

                validateVertex(neighbor, vertexCount, "neighbor");
                validateWeight(weight);
                edges.add(new Edge(neighbor, weight));
            }
            graph.add(edges);
        }
        return graph;
    }

    private static int[] computeShortestPaths(List<List<Edge>> graph, int src) {
        int[] distances = new int[graph.size()];
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

            for (Edge edge : graph.get(current.vertex)) {
                long candidateDistance = (long) current.distance + edge.weight;
                if (candidateDistance < distances[edge.to]) {
                    distances[edge.to] = (int) candidateDistance;
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

    private static ArrayList<ArrayList<int[]>> createGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative");
        }

        ArrayList<ArrayList<int[]>> graph = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void validateAdjacencyList(ArrayList<ArrayList<int[]>> adj) {
        if (adj == null) {
            throw new IllegalArgumentException("Adjacency list must not be null");
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
        int vertexCount = 5;
        int source = 0;

        ArrayList<ArrayList<int[]>> graph = createGraph(vertexCount);
        addEdge(graph, 0, 1, 4);
        addEdge(graph, 0, 2, 8);
        addEdge(graph, 1, 4, 6);
        addEdge(graph, 1, 2, 3);
        addEdge(graph, 2, 3, 2);
        addEdge(graph, 3, 4, 10);

        ArrayList<Integer> result = dijkstra(graph, source);
        for (int distance : result) {
            System.out.print(distance + " ");
        }
        System.out.println();
    }
}
