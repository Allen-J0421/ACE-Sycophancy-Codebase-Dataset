import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

final class StronglyConnectedComponents {

    private StronglyConnectedComponents() {
    }

    public static int[][] findStronglyConnectedComponents(int vertexCount, int[][] edges) {
        return computeStronglyConnectedComponents(Graph.fromEdges(vertexCount, edges));
    }

    public static int[][] kosaraju(List<List<Integer>> graph) {
        return computeStronglyConnectedComponents(Graph.fromAdjacencyLists(graph));
    }

    private static void dfsOrder(
            int startNode,
            int[][] adjacency,
            boolean[] visited,
            Deque<Integer> order,
            TraversalWorkspace workspace) {
        int stackSize = 0;

        visited[startNode] = true;
        workspace.nodeStack[stackSize++] = startNode;

        while (stackSize > 0) {
            int node = workspace.nodeStack[stackSize - 1];
            int[] neighbors = adjacency[node];
            int nextNeighborIndex = workspace.nextNeighborIndexStack[stackSize - 1];

            if (nextNeighborIndex < neighbors.length) {
                int neighbor = neighbors[nextNeighborIndex];
                workspace.nextNeighborIndexStack[stackSize - 1] = nextNeighborIndex + 1;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    workspace.nodeStack[stackSize] = neighbor;
                    workspace.nextNeighborIndexStack[stackSize] = 0;
                    stackSize++;
                }
            } else {
                stackSize--;
                order.push(node);
            }
        }
    }

    private static int dfsComponent(
            int startNode,
            int[][] reverseAdjacency,
            boolean[] visited,
            TraversalWorkspace workspace) {
        int stackSize = 0;
        int componentSize = 0;

        workspace.componentStack[stackSize++] = startNode;
        visited[startNode] = true;

        while (stackSize > 0) {
            int node = workspace.componentStack[--stackSize];
            workspace.componentValues[componentSize++] = node;

            for (int neighbor : reverseAdjacency[node]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    workspace.componentStack[stackSize++] = neighbor;
                }
            }
        }

        return componentSize;
    }

    private static int[][] computeStronglyConnectedComponents(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount];
        Deque<Integer> order = new ArrayDeque<>();
        TraversalWorkspace workspace = new TraversalWorkspace(graph.vertexCount);

        for (int node = 0; node < graph.vertexCount; node++) {
            if (!visited[node]) {
                dfsOrder(node, graph.adjacency, visited, order, workspace);
            }
        }

        Arrays.fill(visited, false);

        List<int[]> components = new ArrayList<>();
        while (!order.isEmpty()) {
            int node = order.pop();
            if (!visited[node]) {
                int componentSize = dfsComponent(node, graph.reverseAdjacency, visited, workspace);
                components.add(Arrays.copyOf(workspace.componentValues, componentSize));
            }
        }

        return components.toArray(new int[components.size()][]);
    }

    private static final class TraversalWorkspace {
        private final int[] nodeStack;
        private final int[] nextNeighborIndexStack;
        private final int[] componentStack;
        private final int[] componentValues;

        private TraversalWorkspace(int vertexCount) {
            this.nodeStack = new int[vertexCount];
            this.nextNeighborIndexStack = new int[vertexCount];
            this.componentStack = new int[vertexCount];
            this.componentValues = new int[vertexCount];
        }
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
            validateEdges(edges);

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

        private static void validateVertexCount(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }
        }

        private static void validateEdges(int[][] edges) {
            if (edges == null) {
                throw new IllegalArgumentException("edges must not be null");
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
