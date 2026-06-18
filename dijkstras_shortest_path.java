import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

class Edge {
    private final int destination;
    private final int weight;

    private Edge(int destination, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight must be non-negative");
        }
        this.destination = destination;
        this.weight = weight;
    }

    static Edge of(int destination, int weight) {
        return new Edge(destination, weight);
    }

    int getDestination() {
        return destination;
    }

    int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Edge(to=%d, weight=%d)", destination, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return destination == other.destination && weight == other.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, weight);
    }
}

class ShortestPathResult {
    private final List<Integer> distances;
    private final int sourceNode;

    private ShortestPathResult(List<Integer> distances, int sourceNode) {
        this.distances = Collections.unmodifiableList(new ArrayList<>(distances));
        this.sourceNode = sourceNode;
    }

    static ShortestPathResult of(List<Integer> distances, int sourceNode) {
        return new ShortestPathResult(distances, sourceNode);
    }

    List<Integer> getDistances() {
        return distances;
    }

    Optional<Integer> getDistanceTo(int node) {
        if (node < 0 || node >= distances.size()) {
            return Optional.empty();
        }
        return Optional.of(distances.get(node));
    }

    int getSourceNode() {
        return sourceNode;
    }

    boolean isReachable(int node) {
        return getDistanceTo(node)
            .map(d -> !d.equals(Integer.MAX_VALUE))
            .orElse(false);
    }

    @Override
    public String toString() {
        return String.format("ShortestPathResult(source=%d, distances=%s)", sourceNode, distances);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShortestPathResult)) return false;
        ShortestPathResult other = (ShortestPathResult) obj;
        return sourceNode == other.sourceNode && distances.equals(other.distances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceNode, distances);
    }
}

class Graph {
    private final List<List<Edge>> adjacencyList;
    private final int vertexCount;

    private Graph(int vertexCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive");
        }
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        IntStream.range(0, vertexCount)
            .forEach(i -> adjacencyList.add(new ArrayList<>()));
    }

    static Graph create(int vertexCount) {
        return new Graph(vertexCount);
    }

    void addEdge(int source, int destination, int weight) {
        validateVertex(source);
        validateVertex(destination);
        if (source == destination) {
            throw new IllegalArgumentException("Self-loops are not supported");
        }
        adjacencyList.get(source).add(Edge.of(destination, weight));
        adjacencyList.get(destination).add(Edge.of(source, weight));
    }

    List<Edge> getAdjacencyListFor(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    int getVertexCount() {
        return vertexCount;
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                String.format("Vertex %d is out of range [0, %d)", vertex, vertexCount)
            );
        }
    }

    @Override
    public String toString() {
        return String.format("Graph(vertices=%d, edges=%d)", vertexCount, countEdges());
    }

    private int countEdges() {
        return adjacencyList.stream().mapToInt(List::size).sum() / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Graph)) return false;
        Graph other = (Graph) obj;
        return vertexCount == other.vertexCount && adjacencyList.equals(other.adjacencyList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexCount, adjacencyList);
    }
}

class PriorityQueueEntry implements Comparable<PriorityQueueEntry> {
    private final int distance;
    private final int node;

    private PriorityQueueEntry(int distance, int node) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance must be non-negative");
        }
        this.distance = distance;
        this.node = node;
    }

    static PriorityQueueEntry of(int distance, int node) {
        return new PriorityQueueEntry(distance, node);
    }

    int getDistance() {
        return distance;
    }

    int getNode() {
        return node;
    }

    @Override
    public int compareTo(PriorityQueueEntry other) {
        Objects.requireNonNull(other);
        return Integer.compare(this.distance, other.distance);
    }

    @Override
    public String toString() {
        return String.format("Entry(distance=%d, node=%d)", distance, node);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PriorityQueueEntry)) return false;
        PriorityQueueEntry other = (PriorityQueueEntry) obj;
        return distance == other.distance && node == other.node;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, node);
    }
}

class DijkstraShortestPathSolver {
    private static final int INFINITY = Integer.MAX_VALUE;

    ShortestPathResult solve(Graph graph, int sourceNode) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        validateSourceNode(graph, sourceNode);

        int[] distances = initializeDistances(graph.getVertexCount(), sourceNode);
        PriorityQueue<PriorityQueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(PriorityQueueEntry.of(0, sourceNode));

        processQueue(graph, distances, priorityQueue);

        return ShortestPathResult.of(convertToList(distances), sourceNode);
    }

    private void processQueue(Graph graph, int[] distances,
                              PriorityQueue<PriorityQueueEntry> priorityQueue) {
        while (!priorityQueue.isEmpty()) {
            processQueueEntry(graph, distances, priorityQueue);
        }
    }

    private void processQueueEntry(Graph graph, int[] distances,
                                   PriorityQueue<PriorityQueueEntry> priorityQueue) {
        PriorityQueueEntry current = priorityQueue.poll();
        int currentNode = current.getNode();
        int currentDistance = current.getDistance();

        if (isOutdatedEntry(currentDistance, distances[currentNode])) {
            return;
        }

        graph.getAdjacencyListFor(currentNode).forEach(
            edge -> relaxEdge(distances, currentNode, edge, priorityQueue)
        );
    }

    private boolean isOutdatedEntry(int entryDistance, int currentDistance) {
        return entryDistance > currentDistance;
    }

    private void relaxEdge(int[] distances, int currentNode, Edge edge,
                          PriorityQueue<PriorityQueueEntry> priorityQueue) {
        int neighbor = edge.getDestination();
        int weight = edge.getWeight();
        int newDistance = distances[currentNode] + weight;

        if (newDistance < distances[neighbor]) {
            distances[neighbor] = newDistance;
            priorityQueue.offer(PriorityQueueEntry.of(newDistance, neighbor));
        }
    }

    private int[] initializeDistances(int vertexCount, int sourceNode) {
        int[] distances = new int[vertexCount];
        Arrays.fill(distances, INFINITY);
        distances[sourceNode] = 0;
        return distances;
    }

    private List<Integer> convertToList(int[] distances) {
        return new ArrayList<>(Arrays.asList(
            Arrays.stream(distances).boxed().toArray(Integer[]::new)
        ));
    }

    private void validateSourceNode(Graph graph, int sourceNode) {
        if (sourceNode < 0 || sourceNode >= graph.getVertexCount()) {
            throw new IllegalArgumentException(
                String.format("Source node %d is invalid for graph with %d vertices",
                             sourceNode, graph.getVertexCount())
            );
        }
    }
}

class GraphBuilder {
    private final Graph graph;

    private GraphBuilder(int vertexCount) {
        this.graph = Graph.create(vertexCount);
    }

    static GraphBuilder withVertexCount(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    GraphBuilder addEdge(int source, int destination, int weight) {
        Objects.requireNonNull(graph).addEdge(source, destination, weight);
        return this;
    }

    Graph build() {
        return graph;
    }

    @Override
    public String toString() {
        return String.format("GraphBuilder(%s)", graph);
    }
}

class ResultFormatter {
    static void printDistances(ShortestPathResult result) {
        result.getDistances().stream()
            .forEach(distance -> System.out.print(distance + " "));
        System.out.println();
    }

    static String formatResult(ShortestPathResult result) {
        return result.getDistances().toString();
    }
}

class Main {
    public static void main(String[] args) {
        Graph graph = GraphBuilder.withVertexCount(5)
            .addEdge(0, 1, 4)
            .addEdge(0, 2, 8)
            .addEdge(1, 4, 6)
            .addEdge(1, 2, 3)
            .addEdge(2, 3, 2)
            .addEdge(3, 4, 10)
            .build();

        DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
        ShortestPathResult result = solver.solve(graph, 0);

        ResultFormatter.printDistances(result);
    }
}
