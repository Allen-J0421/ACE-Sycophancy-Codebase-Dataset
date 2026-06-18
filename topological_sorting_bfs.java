import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

class ValidationException extends Exception {
    ValidationException(String message) {
        super(message);
    }
}

class Validator {
    static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive, got: " + value);
        }
    }

    static void validateInRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                String.format("%s must be in range [%d, %d], got: %d", fieldName, min, max, value));
        }
    }

    static void validateNotNull(Object value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
    }
}

interface TopologicalSortingAlgorithm {
    TopologicalSortResult sort(GraphStructure graph);
}

interface ResultFormatter {
    String format(TopologicalSortResult result);
}

class StandardResultFormatter implements ResultFormatter {
    @Override
    public String format(TopologicalSortResult result) {
        return String.format("TopologicalSort{order=%s, complete=%s, cyclic=%s, time=%dms}",
            result.getOrder(), result.isComplete(), result.isCyclic(), result.getExecutionTimeMs());
    }
}

class VerboseResultFormatter implements ResultFormatter {
    @Override
    public String format(TopologicalSortResult result) {
        return String.format("Result: vertices=%d, processed=%d, cyclic=%s, time=%dms, order=%s",
            result.getVertexCount(), result.getProcessedVertices(), result.isCyclic(),
            result.getExecutionTimeMs(), result.getOrder());
    }
}

class CompactResultFormatter implements ResultFormatter {
    @Override
    public String format(TopologicalSortResult result) {
        return String.format("[%s] %s",
            result.isComplete() ? "OK" : "CYCLE",
            result.getOrder());
    }
}

class ExecutionTimer {
    private final long startTime;
    private final String description;

    ExecutionTimer(String description) {
        this.startTime = System.currentTimeMillis();
        this.description = description;
    }

    public long elapsedMs() {
        return System.currentTimeMillis() - startTime;
    }

    public void printElapsed() {
        System.out.printf("[%s] Elapsed: %dms%n", description, elapsedMs());
    }
}

class GraphStatistics {
    private final int edgeCount;
    private final int vertexCount;
    private final int maxIndegree;
    private final int minIndegree;

    GraphStatistics(int edgeCount, int vertexCount, int maxIndegree, int minIndegree) {
        this.edgeCount = edgeCount;
        this.vertexCount = vertexCount;
        this.maxIndegree = maxIndegree;
        this.minIndegree = minIndegree;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public double getEdgeDensity() {
        int maxPossibleEdges = vertexCount * (vertexCount - 1);
        return maxPossibleEdges > 0 ? (double) edgeCount / maxPossibleEdges : 0;
    }

    @Override
    public String toString() {
        return String.format("GraphStats{vertices=%d, edges=%d, density=%.2f, maxIndegree=%d}",
            vertexCount, edgeCount, getEdgeDensity(), maxIndegree);
    }
}

class TopologicalSortResult {
    private final List<Integer> order;
    private final int vertexCount;
    private final boolean hasCycle;
    private final long executionTimeMs;
    private final ResultFormatter formatter;

    TopologicalSortResult(List<Integer> order, int vertexCount, boolean hasCycle, long executionTimeMs) {
        this(order, vertexCount, hasCycle, executionTimeMs, new StandardResultFormatter());
    }

    TopologicalSortResult(List<Integer> order, int vertexCount, boolean hasCycle,
                         long executionTimeMs, ResultFormatter formatter) {
        this.order = Collections.unmodifiableList(new ArrayList<>(order));
        this.vertexCount = vertexCount;
        this.hasCycle = hasCycle;
        this.executionTimeMs = executionTimeMs;
        this.formatter = Objects.requireNonNull(formatter, "Formatter cannot be null");
    }

    public List<Integer> getOrder() {
        return order;
    }

    public boolean isCyclic() {
        return hasCycle;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getProcessedVertices() {
        return order.size();
    }

    public boolean isComplete() {
        return !hasCycle && order.size() == vertexCount;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public String formatWith(ResultFormatter customFormatter) {
        return customFormatter.format(this);
    }

    @Override
    public String toString() {
        return formatter.format(this);
    }
}

class GraphStructure {
    private final int vertices;
    public final List<List<Integer>> adjacencyList;

    GraphStructure(int vertices, List<List<Integer>> adjacencyList) {
        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>();
        for (List<Integer> adj : adjacencyList) {
            this.adjacencyList.add(new ArrayList<>(adj));
        }
    }

    public int getVertexCount() {
        return vertices;
    }

    public List<Integer> getNeighbors(int vertex) {
        return new ArrayList<>(adjacencyList.get(vertex));
    }

    public GraphStatistics getStatistics() {
        int edgeCount = 0;
        int maxIndegree = 0;
        int minIndegree = Integer.MAX_VALUE;

        int[] indegree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            edgeCount += adjacencyList.get(i).size();
            for (int neighbor : adjacencyList.get(i)) {
                indegree[neighbor]++;
            }
        }

        for (int i = 0; i < vertices; i++) {
            maxIndegree = Math.max(maxIndegree, indegree[i]);
            minIndegree = Math.min(minIndegree, indegree[i]);
        }

        return new GraphStatistics(edgeCount, vertices, maxIndegree,
            minIndegree == Integer.MAX_VALUE ? 0 : minIndegree);
    }
}

class KahnTopologicalSort implements TopologicalSortingAlgorithm {
    @Override
    public TopologicalSortResult sort(GraphStructure graph) {
        long startTime = System.currentTimeMillis();

        int[] indegree = calculateIndegrees(graph);
        Queue<Integer> queue = initializeQueue(graph.getVertexCount(), indegree);
        List<Integer> result = new ArrayList<>();

        processNodes(queue, graph, indegree, result);

        boolean hasCycle = result.size() != graph.getVertexCount();
        long executionTime = System.currentTimeMillis() - startTime;

        return new TopologicalSortResult(result, graph.getVertexCount(), hasCycle, executionTime);
    }

    private int[] calculateIndegrees(GraphStructure graph) {
        int[] indegree = new int[graph.getVertexCount()];
        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (int neighbor : graph.getNeighbors(i)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    private Queue<Integer> initializeQueue(int vertices, int[] indegree) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }
        return queue;
    }

    private void processNodes(Queue<Integer> queue, GraphStructure graph,
                            int[] indegree, List<Integer> result) {
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);

            for (int neighbor : graph.getNeighbors(node)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }
    }
}

class DirectedAcyclicGraph {
    private final GraphStructure structure;
    private TopologicalSortingAlgorithm algorithm;
    private TopologicalSortResult cachedResult;
    private boolean isLocked = false;
    private final List<List<Integer>> adjacencyList;
    private final int vertices;
    private final Set<String> edgeSet;

    public DirectedAcyclicGraph(int vertices) {
        this(vertices, new KahnTopologicalSort());
    }

    public DirectedAcyclicGraph(int vertices, TopologicalSortingAlgorithm algorithm) {
        Validator.validatePositive(vertices, "vertices");
        Validator.validateNotNull(algorithm, "algorithm");

        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            this.adjacencyList.add(new ArrayList<>());
        }
        this.algorithm = algorithm;
        this.structure = new GraphStructure(vertices, adjacencyList);
        this.edgeSet = new HashSet<>();
    }

    public void addEdge(int source, int destination) {
        if (isLocked) {
            throw new IllegalStateException("Cannot modify graph after sorting");
        }
        Validator.validateInRange(source, 0, vertices - 1, "source vertex");
        Validator.validateInRange(destination, 0, vertices - 1, "destination vertex");

        if (source == destination) {
            throw new IllegalArgumentException("Self-loops are not allowed in a DAG");
        }

        String edgeKey = source + "->" + destination;
        if (edgeSet.contains(edgeKey)) {
            throw new IllegalArgumentException("Duplicate edge: " + edgeKey);
        }

        adjacencyList.get(source).add(destination);
        structure.adjacencyList.get(source).add(destination);
        edgeSet.add(edgeKey);
    }

    public TopologicalSortResult topologicalSort() {
        if (cachedResult != null) {
            return cachedResult;
        }
        GraphStructure freshStructure = new GraphStructure(vertices, adjacencyList);
        cachedResult = algorithm.sort(freshStructure);
        isLocked = true;
        return cachedResult;
    }

    public GraphStatistics getStatistics() {
        return structure.getStatistics();
    }

    public int getVertexCount() {
        return vertices;
    }

    public int getEdgeCount() {
        return edgeSet.size();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public static Builder builder(int vertices) {
        return new Builder(vertices);
    }

    public static class Builder {
        private final DirectedAcyclicGraph graph;

        Builder(int vertices) {
            this.graph = new DirectedAcyclicGraph(vertices);
        }

        public Builder edge(int source, int destination) {
            graph.addEdge(source, destination);
            return this;
        }

        public Builder edges(int[][] edgeList) {
            for (int[] edge : edgeList) {
                if (edge.length != 2) {
                    throw new IllegalArgumentException("Each edge must have exactly 2 vertices");
                }
                graph.addEdge(edge[0], edge[1]);
            }
            return this;
        }

        public Builder withAlgorithm(TopologicalSortingAlgorithm algorithm) {
            graph.algorithm = algorithm;
            return this;
        }

        public DirectedAcyclicGraph build() {
            return graph;
        }
    }
}

class TopologicalSortDemo {
    private static final int DEMO_GRAPH_VERTICES = 6;
    private static final int[][] STANDARD_EDGES = {
        {0, 1}, {1, 2}, {2, 3}, {4, 5}, {5, 1}, {5, 2}
    };

    public static void main(String[] args) {
        demonstrateBasicSort();
        demonstrateGraphStatistics();
        demonstrateCycleDetection();
        demonstrateResultCaching();
        demonstrateResultFormatting();
    }

    private static void demonstrateBasicSort() {
        System.out.println("=== Basic Topological Sort ===");
        DirectedAcyclicGraph graph = buildGraphWithEdges(DEMO_GRAPH_VERTICES, STANDARD_EDGES);
        TopologicalSortResult result = graph.topologicalSort();
        System.out.println(result);

        if (result.isComplete()) {
            System.out.println("Topological Order: " + result.getOrder());
        } else {
            System.out.println("ERROR: Graph contains a cycle");
        }
    }

    private static void demonstrateGraphStatistics() {
        System.out.println("\n=== Graph Statistics ===");
        DirectedAcyclicGraph graph = buildGraphWithEdges(DEMO_GRAPH_VERTICES, STANDARD_EDGES);
        GraphStatistics stats = graph.getStatistics();
        System.out.println(stats);
    }

    private static void demonstrateCycleDetection() {
        System.out.println("\n=== Cycle Detection ===");
        int[][] cyclicEdges = {{0, 1}, {1, 2}, {2, 0}};
        DirectedAcyclicGraph cyclicGraph = buildGraphWithEdges(3, cyclicEdges);
        TopologicalSortResult cycleResult = cyclicGraph.topologicalSort();
        System.out.println(cycleResult);
        System.out.println("Processed: " + cycleResult.getProcessedVertices() + "/3 vertices");
    }

    private static void demonstrateResultCaching() {
        System.out.println("\n=== Result Caching ===");
        DirectedAcyclicGraph graph = DirectedAcyclicGraph.builder(4)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 3)
            .build();

        long time1 = graph.topologicalSort().getExecutionTimeMs();
        long time2 = graph.topologicalSort().getExecutionTimeMs();

        System.out.println("First sort: " + time1 + "ms");
        System.out.println("Second sort (cached): " + time2 + "ms");
        System.out.println("Caching enabled: " + (time2 <= time1));
    }

    private static void demonstrateResultFormatting() {
        System.out.println("\n=== Result Formatting ===");
        DirectedAcyclicGraph graph = buildGraphWithEdges(DEMO_GRAPH_VERTICES, STANDARD_EDGES);
        TopologicalSortResult result = graph.topologicalSort();

        System.out.println("Standard format: " + result);
        System.out.println("Verbose format: " + result.formatWith(new VerboseResultFormatter()));
    }

    private static DirectedAcyclicGraph buildGraphWithEdges(int vertices, int[][] edges) {
        return DirectedAcyclicGraph.builder(vertices)
            .edges(edges)
            .build();
    }
}

class DemoRunner {
    public static void runDemo(String title, Runnable demo) {
        System.out.println("\n=== " + title + " ===");
        try {
            demo.run();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
