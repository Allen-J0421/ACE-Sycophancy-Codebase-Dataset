import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public final class DetectCycleDirectedGraph {

    private DetectCycleDirectedGraph() {
    }

    public static boolean hasCycle(DirectedGraph graph) {
        return new CycleDetector(graph).hasCycle();
    }

    public static boolean hasCycle(List<List<Integer>> adjacencyList) {
        return hasCycle(DirectedGraph.fromAdjacencyList(adjacencyList));
    }

    private static DirectedGraph createSampleGraph() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        return graph;
    }

    public static void main(String[] args) {
        DirectedGraph graph = createSampleGraph();
        System.out.println(graph.hasCycle());
    }

    public static final class DirectedGraph {
        private final List<List<Integer>> adjacencyList;
        private final List<List<Integer>> neighborViews;
        private final int[] indegree;

        public DirectedGraph(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            adjacencyList = new ArrayList<>(vertexCount);
            neighborViews = new ArrayList<>(vertexCount);
            indegree = new int[vertexCount];
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                List<Integer> neighbors = new ArrayList<>();
                adjacencyList.add(neighbors);
                neighborViews.add(Collections.unmodifiableList(neighbors));
            }
        }

        public static DirectedGraph fromAdjacencyList(List<List<Integer>> adjacencyList) {
            DirectedGraph graph = new DirectedGraph(adjacencyList.size());
            for (int source = 0; source < adjacencyList.size(); source++) {
                for (int destination : adjacencyList.get(source)) {
                    graph.addEdge(source, destination);
                }
            }
            return graph;
        }

        public void addEdge(int source, int destination) {
            validateVertex(source);
            validateVertex(destination);
            adjacencyList.get(source).add(destination);
            indegree[destination]++;
        }

        public boolean hasCycle() {
            return DetectCycleDirectedGraph.hasCycle(this);
        }

        public int vertexCount() {
            return adjacencyList.size();
        }

        public List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return neighborViews.get(vertex);
        }

        public int[] indegreeSnapshot() {
            return indegree.clone();
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IndexOutOfBoundsException("Invalid vertex: " + vertex);
            }
        }
    }

    private static final class CycleDetector {
        private final DirectedGraph graph;
        private final int[] indegree;
        private final Queue<Integer> zeroIndegreeVertices;
        private int visitedCount;

        private CycleDetector(DirectedGraph graph) {
            this.graph = graph;
            this.indegree = graph.indegreeSnapshot();
            this.zeroIndegreeVertices = collectZeroIndegreeVertices(indegree);
        }

        private boolean hasCycle() {
            while (!zeroIndegreeVertices.isEmpty()) {
                processNextVertex();
            }
            return visitedCount != graph.vertexCount();
        }

        private void processNextVertex() {
            int vertex = zeroIndegreeVertices.poll();
            visitedCount++;

            for (int neighbor : graph.neighborsOf(vertex)) {
                decrementIndegree(neighbor);
            }
        }

        private void decrementIndegree(int vertex) {
            indegree[vertex]--;
            if (indegree[vertex] == 0) {
                zeroIndegreeVertices.add(vertex);
            }
        }

        private static Queue<Integer> collectZeroIndegreeVertices(int[] indegree) {
            Queue<Integer> queue = new ArrayDeque<>();
            for (int vertex = 0; vertex < indegree.length; vertex++) {
                if (indegree[vertex] == 0) {
                    queue.add(vertex);
                }
            }
            return queue;
        }
    }
}
