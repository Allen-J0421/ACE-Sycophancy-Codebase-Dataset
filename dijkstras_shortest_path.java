import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

class Edge {
    private final int destination;
    private final int weight;

    Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
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
}

class ShortestPathResult {
    private final List<Integer> distances;
    private final int sourceNode;

    ShortestPathResult(List<Integer> distances, int sourceNode) {
        this.distances = Collections.unmodifiableList(distances);
        this.sourceNode = sourceNode;
    }

    List<Integer> getDistances() {
        return distances;
    }

    int getDistanceTo(int node) {
        return distances.get(node);
    }

    int getSourceNode() {
        return sourceNode;
    }

    @Override
    public String toString() {
        return String.format("ShortestPathResult(source=%d, distances=%s)", sourceNode, distances);
    }
}

class Graph {
    private final List<List<Edge>> adjacencyList;
    private final int vertexCount;

    Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            this.adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int source, int destination, int weight) {
        validateVertex(source);
        validateVertex(destination);
        Objects.requireNonNull(adjacencyList.get(source)).add(new Edge(destination, weight));
        Objects.requireNonNull(adjacencyList.get(destination)).add(new Edge(source, weight));
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
}

class PriorityQueueEntry implements Comparable<PriorityQueueEntry> {
    private final int distance;
    private final int node;

    PriorityQueueEntry(int distance, int node) {
        this.distance = distance;
        this.node = node;
    }

    int getDistance() {
        return distance;
    }

    int getNode() {
        return node;
    }

    @Override
    public int compareTo(PriorityQueueEntry other) {
        return Integer.compare(this.distance, other.distance);
    }

    @Override
    public String toString() {
        return String.format("Entry(distance=%d, node=%d)", distance, node);
    }
}

class DijkstraShortestPathSolver {
    private static final int INFINITY = Integer.MAX_VALUE;

    ShortestPathResult solve(Graph graph, int sourceNode) {
        validateSourceNode(graph, sourceNode);

        int[] distances = initializeDistances(graph.getVertexCount(), sourceNode);
        PriorityQueue<PriorityQueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(new PriorityQueueEntry(0, sourceNode));

        while (!priorityQueue.isEmpty()) {
            processQueueEntry(graph, distances, priorityQueue);
        }

        return new ShortestPathResult(convertToList(distances), sourceNode);
    }

    private void processQueueEntry(Graph graph, int[] distances,
                                    PriorityQueue<PriorityQueueEntry> priorityQueue) {
        PriorityQueueEntry current = priorityQueue.poll();
        int currentNode = current.getNode();
        int currentDistance = current.getDistance();

        if (currentDistance > distances[currentNode])
            return;

        for (Edge edge : graph.getAdjacencyListFor(currentNode)) {
            relaxEdge(distances, currentNode, edge, priorityQueue);
        }
    }

    private void relaxEdge(int[] distances, int currentNode, Edge edge,
                          PriorityQueue<PriorityQueueEntry> priorityQueue) {
        int neighbor = edge.getDestination();
        int weight = edge.getWeight();
        int newDistance = distances[currentNode] + weight;

        if (newDistance < distances[neighbor]) {
            distances[neighbor] = newDistance;
            priorityQueue.offer(new PriorityQueueEntry(newDistance, neighbor));
        }
    }

    private int[] initializeDistances(int vertexCount, int sourceNode) {
        int[] distances = new int[vertexCount];
        Arrays.fill(distances, INFINITY);
        distances[sourceNode] = 0;
        return distances;
    }

    private List<Integer> convertToList(int[] distances) {
        return Arrays.asList(
            Arrays.stream(distances).boxed().toArray(Integer[]::new)
        );
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

    GraphBuilder(int vertexCount) {
        this.graph = new Graph(vertexCount);
    }

    GraphBuilder addEdge(int source, int destination, int weight) {
        graph.addEdge(source, destination, weight);
        return this;
    }

    Graph build() {
        return graph;
    }
}

class Main {
    public static void main(String[] args) {
        Graph graph = new GraphBuilder(5)
            .addEdge(0, 1, 4)
            .addEdge(0, 2, 8)
            .addEdge(1, 4, 6)
            .addEdge(1, 2, 3)
            .addEdge(2, 3, 2)
            .addEdge(3, 4, 10)
            .build();

        DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
        ShortestPathResult result = solver.solve(graph, 0);

        printResult(result);
    }

    private static void printResult(ShortestPathResult result) {
        for (int distance : result.getDistances()) {
            System.out.print(distance + " ");
        }
        System.out.println();
    }
}
