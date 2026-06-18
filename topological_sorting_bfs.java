import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

class TopologicalSortResult {
    private final List<Integer> order;
    private final int vertexCount;
    private final boolean hasCycle;

    TopologicalSortResult(List<Integer> order, int vertexCount, boolean hasCycle) {
        this.order = Collections.unmodifiableList(new ArrayList<>(order));
        this.vertexCount = vertexCount;
        this.hasCycle = hasCycle;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public boolean isCyclic() {
        return hasCycle;
    }

    public int getProcessedVertices() {
        return order.size();
    }

    public boolean isComplete() {
        return !hasCycle && order.size() == vertexCount;
    }

    @Override
    public String toString() {
        return String.format("TopologicalSort{order=%s, complete=%s, cyclic=%s}",
            order, isComplete(), hasCycle);
    }
}

class DirectedAcyclicGraph {
    private final int vertices;
    private final List<List<Integer>> adjacencyList;
    private boolean isLocked = false;

    public DirectedAcyclicGraph(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            this.adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination) {
        if (isLocked) {
            throw new IllegalStateException("Cannot modify graph after sorting");
        }
        validateVertex(source);
        validateVertex(destination);
        if (source == destination) {
            throw new IllegalArgumentException("Self-loops are not allowed in a DAG");
        }
        this.adjacencyList.get(source).add(destination);
    }

    public TopologicalSortResult topologicalSort() {
        int[] indegree = calculateIndegrees();
        Queue<Integer> queue = initializeQueue(indegree);
        List<Integer> result = new ArrayList<>();

        processNodes(queue, indegree, result);

        boolean hasCycle = result.size() != vertices;
        isLocked = true;

        return new TopologicalSortResult(result, vertices, hasCycle);
    }

    private int[] calculateIndegrees() {
        int[] indegree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            for (int neighbor : adjacencyList.get(i)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    private Queue<Integer> initializeQueue(int[] indegree) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }
        return queue;
    }

    private void processNodes(Queue<Integer> queue, int[] indegree, List<Integer> result) {
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);

            for (int neighbor : adjacencyList.get(node)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(
                String.format("Vertex %d is out of bounds [0, %d]", vertex, vertices - 1)
            );
        }
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

        public DirectedAcyclicGraph build() {
            return graph;
        }
    }
}

class TopologicalSortDemo {
    private static final int GRAPH_VERTICES = 6;
    private static final int[][] EDGES = {
        {0, 1}, {1, 2}, {2, 3}, {4, 5}, {5, 1}, {5, 2}
    };

    public static void main(String[] args) {
        DirectedAcyclicGraph graph = DirectedAcyclicGraph.builder(GRAPH_VERTICES)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 3)
            .edge(4, 5)
            .edge(5, 1)
            .edge(5, 2)
            .build();

        TopologicalSortResult result = graph.topologicalSort();
        System.out.println(result);

        if (result.isComplete()) {
            System.out.println("Topological Order: " + result.getOrder());
        } else {
            System.out.println("ERROR: Graph contains a cycle");
        }

        demonstrateCycleDetection();
    }

    private static void demonstrateCycleDetection() {
        System.out.println("\n--- Cycle Detection Demo ---");
        DirectedAcyclicGraph cyclicGraph = DirectedAcyclicGraph.builder(3)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 0)
            .build();

        TopologicalSortResult cycleResult = cyclicGraph.topologicalSort();
        System.out.println(cycleResult);
        System.out.println("Processed: " + cycleResult.getProcessedVertices() + "/3 vertices");
    }
}
