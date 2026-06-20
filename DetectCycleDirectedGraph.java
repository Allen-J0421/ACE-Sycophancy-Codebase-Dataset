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
        return DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(2, 3)
                .build();
    }

    public static void main(String[] args) {
        DirectedGraph graph = createSampleGraph();
        System.out.println(graph.hasCycle());
    }

    public static final class DirectedGraph {
        private final List<List<Integer>> adjacencyList;
        private final int[] indegree;

        private DirectedGraph(List<List<Integer>> adjacencyList, int[] indegree) {
            this.adjacencyList = adjacencyList;
            this.indegree = indegree;
        }

        public static Builder builder(int vertexCount) {
            return new Builder(vertexCount);
        }

        public static DirectedGraph fromAdjacencyList(List<List<Integer>> adjacencyList) {
            Builder graph = builder(adjacencyList.size());
            for (int source = 0; source < adjacencyList.size(); source++) {
                for (int destination : adjacencyList.get(source)) {
                    graph.addEdge(source, destination);
                }
            }
            return graph.build();
        }

        public boolean hasCycle() {
            return DetectCycleDirectedGraph.hasCycle(this);
        }

        public int vertexCount() {
            return adjacencyList.size();
        }

        public List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return adjacencyList.get(vertex);
        }

        public int[] indegreeSnapshot() {
            return indegree.clone();
        }

        private void validateVertex(int vertex) {
            validateVertex(vertex, adjacencyList.size());
        }

        private static void validateVertex(int vertex, int vertexCount) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IndexOutOfBoundsException(
                        "Invalid vertex: " + vertex + " for graph size " + vertexCount);
            }
        }

        public static final class Builder {
            private final List<List<Integer>> adjacencyList;
            private final int[] indegree;

            private Builder(int vertexCount) {
                if (vertexCount < 0) {
                    throw new IllegalArgumentException("vertexCount must be non-negative");
                }

                adjacencyList = new ArrayList<>(vertexCount);
                indegree = new int[vertexCount];
                for (int vertex = 0; vertex < vertexCount; vertex++) {
                    adjacencyList.add(new ArrayList<>());
                }
            }

            public Builder addEdge(int source, int destination) {
                validateVertex(source, adjacencyList.size());
                validateVertex(destination, adjacencyList.size());
                adjacencyList.get(source).add(destination);
                indegree[destination]++;
                return this;
            }

            public DirectedGraph build() {
                List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
                for (List<Integer> neighbors : adjacencyList) {
                    immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
                }

                return new DirectedGraph(
                        Collections.unmodifiableList(immutableAdjacencyList),
                        indegree.clone());
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
