import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface WeightedGraphView {
    List<Edge> getAdjacencyListFor(int vertex);
    int getVertexCount();
}

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

class Path {
    private final Deque<Integer> nodes;
    private final int totalDistance;

    private Path(Deque<Integer> nodes, int totalDistance) {
        this.nodes = new LinkedList<>(nodes);
        this.totalDistance = totalDistance;
    }

    static Path of(Deque<Integer> nodes, int totalDistance) {
        return new Path(nodes, totalDistance);
    }

    List<Integer> getNodes() {
        return Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    int getTotalDistance() {
        return totalDistance;
    }

    @Override
    public String toString() {
        return String.format("Path(nodes=%s, distance=%d)", nodes, totalDistance);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Path)) return false;
        Path other = (Path) obj;
        return nodes.equals(other.nodes) && totalDistance == other.totalDistance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, totalDistance);
    }
}

class ShortestPathResult {
    private final List<Integer> distances;
    private final int[] predecessors;
    private final int sourceNode;
    private final WeightedGraphView graph;
    private static final int INFINITY = Integer.MAX_VALUE;

    private ShortestPathResult(List<Integer> distances, int[] predecessors,
                               int sourceNode, WeightedGraphView graph) {
        this.distances = Collections.unmodifiableList(new ArrayList<>(distances));
        this.predecessors = predecessors.clone();
        this.sourceNode = sourceNode;
        this.graph = graph;
    }

    static ShortestPathResult of(List<Integer> distances, int[] predecessors,
                                 int sourceNode, WeightedGraphView graph) {
        return new ShortestPathResult(distances, predecessors, sourceNode, graph);
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

    Optional<Path> getPathTo(int destination) {
        if (!isReachable(destination)) {
            return Optional.empty();
        }

        Deque<Integer> path = new LinkedList<>();
        int current = destination;

        while (current != sourceNode) {
            path.addFirst(current);
            current = predecessors[current];
        }
        path.addFirst(sourceNode);

        return Optional.of(Path.of(path, getDistanceTo(destination).orElse(INFINITY)));
    }

    int getSourceNode() {
        return sourceNode;
    }

    boolean isReachable(int node) {
        return getDistanceTo(node)
            .map(d -> !d.equals(INFINITY))
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

class Graph implements WeightedGraphView {
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

    @Override
    public List<Edge> getAdjacencyListFor(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    @Override
    public int getVertexCount() {
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

class DistanceTable {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final int[] distances;
    private final int sourceNode;

    private DistanceTable(int vertexCount, int sourceNode) {
        this.distances = new int[vertexCount];
        Arrays.fill(distances, INFINITY);
        this.sourceNode = sourceNode;
        this.distances[sourceNode] = 0;
    }

    static DistanceTable create(int vertexCount, int sourceNode) {
        return new DistanceTable(vertexCount, sourceNode);
    }

    void updateDistance(int node, int newDistance) {
        distances[node] = newDistance;
    }

    int getDistance(int node) {
        return distances[node];
    }

    int[] toArray() {
        return distances.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(distances);
    }
}

class DijkstraShortestPathSolver {
    private static final int INFINITY = Integer.MAX_VALUE;

    ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        validateSourceNode(graph, sourceNode);

        DistanceTable distanceTable = DistanceTable.create(graph.getVertexCount(), sourceNode);
        int[] predecessors = initializePredecessors(graph.getVertexCount());
        PriorityQueue<PriorityQueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(PriorityQueueEntry.of(0, sourceNode));

        processQueue(graph, distanceTable, predecessors, priorityQueue);

        List<Integer> distances = convertToList(distanceTable.toArray());
        return ShortestPathResult.of(distances, predecessors, sourceNode, graph);
    }

    private void processQueue(WeightedGraphView graph, DistanceTable distanceTable,
                              int[] predecessors, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        while (!priorityQueue.isEmpty()) {
            processQueueEntry(graph, distanceTable, predecessors, priorityQueue);
        }
    }

    private void processQueueEntry(WeightedGraphView graph, DistanceTable distanceTable,
                                   int[] predecessors, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        PriorityQueueEntry current = priorityQueue.poll();
        int currentNode = current.getNode();
        int currentDistance = current.getDistance();

        if (isOutdatedEntry(currentDistance, distanceTable.getDistance(currentNode))) {
            return;
        }

        graph.getAdjacencyListFor(currentNode).forEach(
            edge -> relaxEdge(distanceTable, predecessors, currentNode, edge, priorityQueue)
        );
    }

    private boolean isOutdatedEntry(int entryDistance, int currentDistance) {
        return entryDistance > currentDistance;
    }

    private void relaxEdge(DistanceTable distanceTable, int[] predecessors, int currentNode,
                          Edge edge, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        int neighbor = edge.getDestination();
        int weight = edge.getWeight();
        int newDistance = distanceTable.getDistance(currentNode) + weight;

        if (newDistance < distanceTable.getDistance(neighbor)) {
            distanceTable.updateDistance(neighbor, newDistance);
            predecessors[neighbor] = currentNode;
            priorityQueue.offer(PriorityQueueEntry.of(newDistance, neighbor));
        }
    }

    private int[] initializePredecessors(int vertexCount) {
        int[] predecessors = new int[vertexCount];
        Arrays.fill(predecessors, -1);
        return predecessors;
    }

    private List<Integer> convertToList(int[] distances) {
        return new ArrayList<>(Arrays.asList(
            Arrays.stream(distances).boxed().toArray(Integer[]::new)
        ));
    }

    private void validateSourceNode(WeightedGraphView graph, int sourceNode) {
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

    static void printPaths(ShortestPathResult result) {
        IntStream.range(0, result.getDistances().size())
            .forEach(node -> result.getPathTo(node)
                .ifPresent(path -> System.out.println(
                    String.format("To node %d: %s (distance=%d)",
                                node, path.getNodes(), path.getTotalDistance())
                )));
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
