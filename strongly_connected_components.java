import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Stack;
import java.util.function.IntConsumer;

class StronglyConnectedComponents {
    private static final int EMPTY = -1;

    private static void fillWithEmptyMarkers(int[][] matrix) {
        for (int[] row : matrix) {
            Arrays.fill(row, EMPTY);
        }
    }

    private static int[][] newEmptyMatrix(int size) {
        int[][] matrix = new int[size][size];
        fillWithEmptyMarkers(matrix);
        return matrix;
    }

    private static int[] newEmptyComponent(int size) {
        int[] component = new int[size];
        Arrays.fill(component, EMPTY);
        return component;
    }

    private static void forEachNeighbor(int vertex, int[][] adj, IntConsumer action) {
        for (int neighbor : adj[vertex]) {
            if (neighbor == EMPTY) {
                break;
            }
            action.accept(neighbor);
        }
    }

    void DFS1(int vertex, int[][] adj, boolean[] visited, Stack<Integer> finishOrder) {
        collectFinishOrder(vertex, adj, visited, finishedVertex -> finishOrder.push(finishedVertex));
    }

    private void collectFinishOrder(int vertex, int[][] adj, boolean[] visited, IntConsumer finishOrder) {
        visited[vertex] = true;
        forEachNeighbor(vertex, adj, neighbor -> {
            if (!visited[neighbor]) {
                collectFinishOrder(neighbor, adj, visited, finishOrder);
            }
        });
        finishOrder.accept(vertex);
    }

    void DFS2(int vertex, int[][] reversedAdj, boolean[] visited, int[] component, int[] nextIndex) {
        collectComponent(vertex, reversedAdj, visited, component, nextIndex);
    }

    private void collectComponent(int vertex, int[][] reversedAdj, boolean[] visited, int[] component, int[] nextIndex) {
        visited[vertex] = true;
        component[nextIndex[0]++] = vertex;
        forEachNeighbor(vertex, reversedAdj, neighbor -> {
            if (!visited[neighbor]) {
                collectComponent(neighbor, reversedAdj, visited, component, nextIndex);
            }
        });
    }

    private int[][] reverseGraph(int vertexCount, int[][] adj) {
        int[][] reversedAdj = newEmptyMatrix(vertexCount);
        int[] counts = new int[vertexCount];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            int source = vertex;
            forEachNeighbor(source, adj, neighbor -> reversedAdj[neighbor][counts[neighbor]++] = source);
        }

        return reversedAdj;
    }

    private Deque<Integer> buildFinishOrder(int vertexCount, int[][] adj) {
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> finishOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                collectFinishOrder(vertex, adj, visited, finishedVertex -> finishOrder.push(finishedVertex));
            }
        }

        return finishOrder;
    }

    private int[][] collectComponents(int vertexCount, int[][] reversedAdj, Deque<Integer> finishOrder) {
        boolean[] visited = new boolean[vertexCount];

        int[][] components = newEmptyMatrix(vertexCount);

        int componentCount = 0;
        while (!finishOrder.isEmpty()) {
            int vertex = finishOrder.pop();
            if (!visited[vertex]) {
                int[] component = newEmptyComponent(vertexCount);
                int[] nextIndex = {0};

                collectComponent(vertex, reversedAdj, visited, component, nextIndex);
                components[componentCount++] = component;
            }
        }

        return Arrays.copyOf(components, componentCount);
    }

    int[][] kosaraju(int vertexCount, int[][] adj) {
        Deque<Integer> finishOrder = buildFinishOrder(vertexCount, adj);
        int[][] reversedAdj = reverseGraph(vertexCount, adj);

        return collectComponents(vertexCount, reversedAdj, finishOrder);
    }

    static int[][] buildAdjMatrix(int[][] edges, int vertexCount) {
        int[][] adj = newEmptyMatrix(vertexCount + 1);

        int[] counts = new int[vertexCount + 1];
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adj[from][counts[from]++] = to;
        }

        return adj;
    }

    private static boolean containsVertex(int[] component, int vertex) {
        for (int member : component) {
            if (member == EMPTY) {
                break;
            }
            if (member == vertex) {
                return true;
            }
        }
        return false;
    }

    private static void printComponents(int[][] components, int excludedVertex) {
        for (int[] component : components) {
            if (containsVertex(component, excludedVertex)) {
                continue;
            }
            for (int vertex : component) {
                if (vertex == EMPTY) {
                    break;
                }
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        StronglyConnectedComponents obj = new StronglyConnectedComponents();
        int vertexCount = 5;
        int[][] edges = {
            {1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}
        };

        int[][] adj = buildAdjMatrix(edges, vertexCount);

        int[][] components = obj.kosaraju(vertexCount + 1, adj);

        System.out.println("Strongly Connected Components:");
        printComponents(components, 0);
    }
}
