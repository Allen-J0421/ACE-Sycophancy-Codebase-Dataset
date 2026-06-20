import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

final class StronglyConnectedComponents {

    private StronglyConnectedComponents() {
    }

    private static void dfsOrder(int startNode, int[][] adjacency, boolean[] visited, Deque<Integer> order) {
        Deque<TraversalFrame> stack = new ArrayDeque<>();
        visited[startNode] = true;
        stack.push(new TraversalFrame(startNode));

        while (!stack.isEmpty()) {
            TraversalFrame frame = stack.peek();
            int[] neighbors = adjacency[frame.node];

            if (frame.nextNeighborIndex < neighbors.length) {
                int neighbor = neighbors[frame.nextNeighborIndex++];
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    stack.push(new TraversalFrame(neighbor));
                }
            } else {
                stack.pop();
                order.push(frame.node);
            }
        }
    }

    private static void dfsComponent(int startNode, int[][] reverseAdjacency, boolean[] visited, List<Integer> component) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(startNode);
        visited[startNode] = true;

        while (!stack.isEmpty()) {
            int node = stack.pop();
            component.add(node);

            for (int neighbor : reverseAdjacency[node]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    stack.push(neighbor);
                }
            }
        }
    }

    static int[][] findStronglyConnectedComponents(int vertexCount, int[][] edges) {
        return kosaraju(Graph.fromEdges(vertexCount, edges));
    }

    static int[][] kosaraju(List<List<Integer>> graph) {
        return kosaraju(Graph.fromAdjacencyLists(graph));
    }

    private static int[][] kosaraju(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount];
        Deque<Integer> order = new ArrayDeque<>();

        for (int node = 0; node < graph.vertexCount; node++) {
            if (!visited[node]) {
                dfsOrder(node, graph.adjacency, visited, order);
            }
        }

        Arrays.fill(visited, false);

        List<int[]> components = new ArrayList<>();
        while (!order.isEmpty()) {
            int node = order.pop();
            if (!visited[node]) {
                List<Integer> component = new ArrayList<>();
                dfsComponent(node, graph.reverseAdjacency, visited, component);
                components.add(toIntArray(component));
            }
        }

        return components.toArray(new int[components.size()][]);
    }

    private static final class TraversalFrame {
        private final int node;
        private int nextNeighborIndex;

        private TraversalFrame(int node) {
            this.node = node;
        }
    }

    private static int[] toIntArray(List<Integer> values) {
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static final class Graph {
        private final int vertexCount;
        private final int[][] adjacency;
        private final int[][] reverseAdjacency;

        private Graph(int vertexCount, int[][] adjacency) {
            this.vertexCount = vertexCount;
            this.adjacency = adjacency;
            this.reverseAdjacency = buildReverseAdjacency(vertexCount, adjacency);
        }

        private static Graph fromEdges(int vertexCount, int[][] edges) {
            validateVertexCount(vertexCount);
            if (edges == null) {
                throw new IllegalArgumentException("edges must not be null");
            }

            int[] outgoingCounts = new int[vertexCount];

            for (int[] edge : edges) {
                validateEdge(edge, vertexCount);
                outgoingCounts[edge[0]]++;
            }

            int[][] adjacency = new int[vertexCount][];
            for (int node = 0; node < vertexCount; node++) {
                adjacency[node] = new int[outgoingCounts[node]];
            }

            int[] outgoingOffsets = new int[vertexCount];
            for (int[] edge : edges) {
                int source = edge[0];
                int target = edge[1];
                adjacency[source][outgoingOffsets[source]++] = target;
            }

            return new Graph(vertexCount, adjacency);
        }

        private static Graph fromAdjacencyLists(List<List<Integer>> graph) {
            validateGraph(graph);

            return new Graph(graph.size(), toAdjacencyArray(graph));
        }

        private static int[][] buildReverseAdjacency(int vertexCount, int[][] adjacency) {
            int[] incomingCounts = new int[vertexCount];
            for (int source = 0; source < vertexCount; source++) {
                for (int target : adjacency[source]) {
                    incomingCounts[target]++;
                }
            }

            int[][] reverseAdjacency = new int[vertexCount][];
            for (int node = 0; node < vertexCount; node++) {
                reverseAdjacency[node] = new int[incomingCounts[node]];
            }

            int[] incomingOffsets = new int[vertexCount];
            for (int source = 0; source < vertexCount; source++) {
                for (int target : adjacency[source]) {
                    reverseAdjacency[target][incomingOffsets[target]++] = source;
                }
            }

            return reverseAdjacency;
        }

        private static int[][] toAdjacencyArray(List<List<Integer>> graph) {
            int vertexCount = graph.size();
            int[][] adjacency = new int[vertexCount][];
            for (int node = 0; node < vertexCount; node++) {
                List<Integer> neighbors = graph.get(node);
                adjacency[node] = new int[neighbors.size()];
                for (int index = 0; index < neighbors.size(); index++) {
                    adjacency[node][index] = neighbors.get(index);
                }
            }
            return adjacency;
        }
    }

    private static void validateGraph(List<List<Integer>> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
        for (int node = 0; node < graph.size(); node++) {
            List<Integer> neighbors = graph.get(node);
            if (neighbors == null) {
                throw new IllegalArgumentException("graph contains a null adjacency list at node " + node);
            }
            for (int neighbor : neighbors) {
                if (neighbor < 0 || neighbor >= graph.size()) {
                    throw new IllegalArgumentException("graph contains an invalid edge from " + node + " to " + neighbor);
                }
            }
        }
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
    }

    private static void validateEdge(int[] edge, int vertexCount) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("each edge must contain exactly two vertices");
        }
        int source = edge[0];
        int target = edge[1];
        if (source < 0 || source >= vertexCount || target < 0 || target >= vertexCount) {
            throw new IllegalArgumentException("edge endpoints must be within [0, vertexCount)");
        }
    }

    public static void main(String[] args) {
        int vertexCount = 6;
        int[][] edges = {
            {0, 1},
            {1, 2},
            {2, 0},
            {1, 3},
            {3, 4},
            {4, 5},
            {5, 3}
        };

        int[][] components = findStronglyConnectedComponents(vertexCount, edges);

        System.out.println("Strongly Connected Components:");
        for (int[] component : components) {
            System.out.println(Arrays.toString(component));
        }
    }
}
