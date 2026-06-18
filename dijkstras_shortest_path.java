import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {
    private static final int INFINITE_DISTANCE = Integer.MAX_VALUE;

    private static final class NodeDistance {
        private final int node;
        private final int distance;

        private NodeDistance(int node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adjacency, int source) {
        return new ArrayList<>(shortestPathsFrom(LegacyGraphAdapter.toWeightedGraph(adjacency), source));
    }

    static ArrayList<Integer> dijkstra(WeightedGraph graph, int source) {
        return new ArrayList<>(shortestPathsFrom(graph, source));
    }

    static List<Integer> shortestPathsFrom(WeightedGraph graph, int source) {
        validateSource(graph, source);

        int[] distances = initializeDistances(graph.vertexCount(), source);
        PriorityQueue<NodeDistance> queue =
                new PriorityQueue<>((a, b) -> Integer.compare(a.distance, b.distance));

        queue.offer(new NodeDistance(source, 0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();

            if (current.distance > distances[current.node]) {
                continue;
            }

            relaxEdges(graph, current, distances, queue);
        }

        return toList(distances);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adjacency, int u, int v, int weight) {
        LegacyGraphAdapter.addUndirectedEdge(adjacency, u, v, weight);
    }

    private static void validateSource(WeightedGraph graph, int source) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        if (!graph.hasVertex(source)) {
            throw new IllegalArgumentException("Source vertex is outside the graph.");
        }
    }

    private static int[] initializeDistances(int vertexCount, int source) {
        int[] distances = new int[vertexCount];
        Arrays.fill(distances, INFINITE_DISTANCE);
        distances[source] = 0;
        return distances;
    }

    private static void relaxEdges(
            WeightedGraph graph,
            NodeDistance current,
            int[] distances,
            PriorityQueue<NodeDistance> queue) {
        for (Edge edge : graph.edgesFrom(current.node)) {
            if (current.distance > INFINITE_DISTANCE - edge.weight()) {
                continue;
            }

            int candidateDistance = current.distance + edge.weight();
            if (candidateDistance < distances[edge.destination()]) {
                distances[edge.destination()] = candidateDistance;
                queue.offer(new NodeDistance(edge.destination(), candidateDistance));
            }
        }
    }

    private static ArrayList<Integer> toList(int[] distances) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int distance : distances) {
            result.add(distance);
        }
        return result;
    }

    public static void main(String[] args) {
        WeightedGraph graph = createSampleGraph();

        List<Integer> result = shortestPathsFrom(graph, 0);
        for (int distance : result) {
            System.out.print(distance + " ");
        }
        System.out.println();
    }

    private static WeightedGraph createSampleGraph() {
        WeightedGraph graph = WeightedGraph.withVertexCount(5);

        graph.addUndirectedEdge(0, 1, 4);
        graph.addUndirectedEdge(0, 2, 8);
        graph.addUndirectedEdge(1, 4, 6);
        graph.addUndirectedEdge(1, 2, 3);
        graph.addUndirectedEdge(2, 3, 2);
        graph.addUndirectedEdge(3, 4, 10);

        return graph;
    }
}

final class WeightedGraph {
    private final ArrayList<ArrayList<Edge>> adjacency;

    private WeightedGraph(int vertexCount) {
        validateVertexCount(vertexCount);

        adjacency = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    static WeightedGraph withVertexCount(int vertexCount) {
        return new WeightedGraph(vertexCount);
    }

    void addUndirectedEdge(int u, int v, int weight) {
        addDirectedEdge(u, v, weight);
        addDirectedEdge(v, u, weight);
    }

    void addDirectedEdge(int from, int to, int weight) {
        validateVertex(from);
        validateVertex(to);
        Edge.validateWeight(weight);

        adjacency.get(from).add(new Edge(to, weight));
    }

    int vertexCount() {
        return adjacency.size();
    }

    boolean hasVertex(int vertex) {
        return vertex >= 0 && vertex < adjacency.size();
    }

    List<Edge> edgesFrom(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private void validateVertex(int vertex) {
        if (!hasVertex(vertex)) {
            throw new IllegalArgumentException("Vertex is outside the graph.");
        }
    }
}

final class Edge {
    private final int destination;
    private final int weight;

    Edge(int destination, int weight) {
        validateWeight(weight);

        this.destination = destination;
        this.weight = weight;
    }

    int destination() {
        return destination;
    }

    int weight() {
        return weight;
    }

    static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra's algorithm requires non-negative weights.");
        }
    }
}

final class LegacyGraphAdapter {
    private static final int EDGE_DESTINATION_INDEX = 0;
    private static final int EDGE_WEIGHT_INDEX = 1;

    private LegacyGraphAdapter() {
    }

    static WeightedGraph toWeightedGraph(ArrayList<ArrayList<int[]>> adjacency) {
        validateAdjacencyList(adjacency);

        WeightedGraph graph = WeightedGraph.withVertexCount(adjacency.size());
        for (int from = 0; from < adjacency.size(); from++) {
            for (int[] edge : adjacency.get(from)) {
                validateEdge(adjacency.size(), edge);
                graph.addDirectedEdge(from, destination(edge), weight(edge));
            }
        }

        return graph;
    }

    static void addUndirectedEdge(ArrayList<ArrayList<int[]>> adjacency, int u, int v, int weight) {
        validateLegacyVertex(adjacency, u);
        validateLegacyVertex(adjacency, v);
        Edge.validateWeight(weight);

        adjacency.get(u).add(new int[]{v, weight});
        adjacency.get(v).add(new int[]{u, weight});
    }

    private static void validateAdjacencyList(ArrayList<ArrayList<int[]>> adjacency) {
        if (adjacency == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        for (List<int[]> edges : adjacency) {
            if (edges == null) {
                throw new IllegalArgumentException("Adjacency list cannot contain null vertex entries.");
            }
        }
    }

    private static void validateLegacyVertex(ArrayList<ArrayList<int[]>> adjacency, int vertex) {
        validateAdjacencyList(adjacency);
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IllegalArgumentException("Vertex is outside the graph.");
        }
    }

    private static void validateEdge(int vertexCount, int[] edge) {
        if (edge == null || edge.length < 2) {
            throw new IllegalArgumentException("Each edge must contain a destination and weight.");
        }
        if (destination(edge) < 0 || destination(edge) >= vertexCount) {
            throw new IllegalArgumentException("Edge destination is outside the graph.");
        }
        Edge.validateWeight(weight(edge));
    }

    private static int destination(int[] edge) {
        return edge[EDGE_DESTINATION_INDEX];
    }

    private static int weight(int[] edge) {
        return edge[EDGE_WEIGHT_INDEX];
    }
}
