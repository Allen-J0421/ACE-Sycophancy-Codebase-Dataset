import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

class TopologicalSort {
    private static final KahnTopologicalSorter SORTER = new KahnTopologicalSorter();

    static List<Integer> topoSort(DirectedGraph graph) {
        return SORTER.sort(graph);
    }

    private static DirectedGraph buildSampleGraph() {
        return DirectedGraph.fromEdges(
                6,
                new int[][] {
                    {0, 1},
                    {1, 2},
                    {2, 3},
                    {4, 5},
                    {5, 1},
                    {5, 2}
                });
    }

    private static String formatOrder(List<Integer> order) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : order) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        DirectedGraph graph = buildSampleGraph();
        List<Integer> order = topoSort(graph);
        System.out.println(formatOrder(order));
    }

    private static final class KahnTopologicalSorter {
        List<Integer> sort(DirectedGraph graph) {
            int[] indegree = graph.buildIndegreeTable();
            Deque<Integer> readyVertices = collectZeroIndegreeVertices(indegree);
            List<Integer> order = new ArrayList<>(graph.vertexCount());

            while (!readyVertices.isEmpty()) {
                int vertex = readyVertices.removeFirst();
                order.add(vertex);
                for (int next : graph.neighborsOf(vertex)) {
                    indegree[next]--;
                    if (indegree[next] == 0) {
                        readyVertices.addLast(next);
                    }
                }
            }

            validateAcyclic(graph, order);
            return order;
        }

        private Deque<Integer> collectZeroIndegreeVertices(int[] indegree) {
            Deque<Integer> readyVertices = new ArrayDeque<>();
            for (int vertex = 0; vertex < indegree.length; vertex++) {
                if (indegree[vertex] == 0) {
                    readyVertices.addLast(vertex);
                }
            }
            return readyVertices;
        }

        private void validateAcyclic(DirectedGraph graph, List<Integer> order) {
            if (order.size() != graph.vertexCount()) {
                throw new IllegalStateException("Topological sort requires a directed acyclic graph.");
            }
        }
    }

    private static final class DirectedGraph {
        private final List<List<Integer>> adjacencyList;

        private DirectedGraph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        static DirectedGraph withVertexCount(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count must be non-negative.");
            }

            List<List<Integer>> adjacencyList = new ArrayList<>();
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
            return new DirectedGraph(adjacencyList);
        }

        static DirectedGraph fromEdges(int vertexCount, int[][] edges) {
            DirectedGraph graph = withVertexCount(vertexCount);
            for (int[] edge : edges) {
                validateEdgeShape(edge);
                graph.addEdge(edge[0], edge[1]);
            }
            return graph;
        }

        int vertexCount() {
            return adjacencyList.size();
        }

        List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return adjacencyList.get(vertex);
        }

        int[] buildIndegreeTable() {
            int[] indegree = new int[vertexCount()];
            for (int vertex = 0; vertex < vertexCount(); vertex++) {
                for (int next : neighborsOf(vertex)) {
                    indegree[next]++;
                }
            }
            return indegree;
        }

        void addEdge(int source, int destination) {
            validateVertex(source);
            validateVertex(destination);
            adjacencyList.get(source).add(destination);
        }

        private static void validateEdgeShape(int[] edge) {
            if (edge == null || edge.length != 2) {
                throw new IllegalArgumentException("Each edge must contain exactly two vertices.");
            }
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
            }
        }
    }
}
