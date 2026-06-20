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

    void DFS1(int vertex, int[][] adj, boolean[] visited, Stack<Integer> finishOrder) {
        collectFinishOrder(vertex, adj, visited, finishedVertex -> finishOrder.push(finishedVertex));
    }

    private void collectFinishOrder(int vertex, int[][] adj, boolean[] visited, IntConsumer finishOrder) {
        visited[vertex] = true;
        for (int neighbor : adj[vertex]) {
            if (neighbor == EMPTY) {
                break;
            }
            if (!visited[neighbor]) {
                collectFinishOrder(neighbor, adj, visited, finishOrder);
            }
        }
        finishOrder.accept(vertex);
    }

    void DFS2(int vertex, int[][] reversedAdj, boolean[] visited, int[] component, int[] nextIndex) {
        collectComponent(vertex, reversedAdj, visited, component, nextIndex);
    }

    private void collectComponent(int vertex, int[][] reversedAdj, boolean[] visited, int[] component, int[] nextIndex) {
        visited[vertex] = true;
        component[nextIndex[0]++] = vertex;
        for (int neighbor : reversedAdj[vertex]) {
            if (neighbor == EMPTY) {
                break;
            }
            if (!visited[neighbor]) {
                collectComponent(neighbor, reversedAdj, visited, component, nextIndex);
            }
        }
    }

    private int[][] reverseGraph(int vertexCount, int[][] adj) {
        int[][] reversedAdj = new int[vertexCount][vertexCount];
        fillWithEmptyMarkers(reversedAdj);

        int[] counts = new int[vertexCount];
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            for (int neighbor : adj[vertex]) {
                if (neighbor == EMPTY) {
                    break;
                }
                reversedAdj[neighbor][counts[neighbor]++] = vertex;
            }
        }

        return reversedAdj;
    }

    int[][] kosaraju(int vertexCount, int[][] adj) {
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> finishOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                collectFinishOrder(vertex, adj, visited, finishedVertex -> finishOrder.push(finishedVertex));
            }
        }

        int[][] reversedAdj = reverseGraph(vertexCount, adj);
        Arrays.fill(visited, false);

        int[][] components = new int[vertexCount][vertexCount];
        fillWithEmptyMarkers(components);

        int componentCount = 0;
        while (!finishOrder.isEmpty()) {
            int vertex = finishOrder.pop();
            if (!visited[vertex]) {
                int[] component = new int[vertexCount];
                Arrays.fill(component, EMPTY);
                int[] nextIndex = {0};

                collectComponent(vertex, reversedAdj, visited, component, nextIndex);
                components[componentCount++] = component;
            }
        }

        return Arrays.copyOf(components, componentCount);
    }

    static int[][] buildAdjMatrix(int[][] edges, int vertexCount) {
        int[][] adj = new int[vertexCount + 1][vertexCount + 1];
        fillWithEmptyMarkers(adj);

        int[] counts = new int[vertexCount + 1];
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adj[from][counts[from]++] = to;
        }

        return adj;
    }

    public static void main(String[] args) {
        StronglyConnectedComponents obj = new StronglyConnectedComponents();
        int V = 5;
        int[][] edges = {
            {1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}
        };

        int[][] adj = buildAdjMatrix(edges, V);

        int[][] components = obj.kosaraju(V + 1, adj);

        System.out.println("Strongly Connected Components:");
        for (int i = 0; i < components.length - 1; i++) {
            for (int j = 0; j < components[i].length && components[i][j] != EMPTY; j++) {
                System.out.print(components[i][j] + " ");
            }
            System.out.println();
        }
    }
}
