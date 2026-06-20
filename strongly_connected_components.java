import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Stack;
import java.util.function.IntConsumer;

class StronglyConnectedComponents {
    private static final int EMPTY = -1;
    private static final int DUMMY_VERTEX = 0;
    private static final int SAMPLE_VERTEX_COUNT = 5;
    private static final int[][] SAMPLE_EDGES = {
        {1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}
    };
    private static final String OUTPUT_HEADER = "Strongly Connected Components:";

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

    private static void forEachNeighbor(int vertex, int[][] adjacency, IntConsumer action) {
        for (int neighbor : adjacency[vertex]) {
            if (neighbor == EMPTY) {
                break;
            }
            action.accept(neighbor);
        }
    }

    void DFS1(int vertex, int[][] adjacency, boolean[] visited, Stack<Integer> finishOrder) {
        Deque<Integer> traversalFinishOrder = new ArrayDeque<>();
        collectFinishOrder(vertex, adjacency, visited, traversalFinishOrder);
        while (!traversalFinishOrder.isEmpty()) {
            finishOrder.push(traversalFinishOrder.removeLast());
        }
    }

    private void collectFinishOrder(int vertex, int[][] adjacency, boolean[] visited, Deque<Integer> finishOrder) {
        visited[vertex] = true;
        forEachNeighbor(vertex, adjacency, neighbor -> {
            if (!visited[neighbor]) {
                collectFinishOrder(neighbor, adjacency, visited, finishOrder);
            }
        });
        finishOrder.push(vertex);
    }

    void DFS2(int vertex, int[][] reversedAdj, boolean[] visited, int[] component, int[] nextIndex) {
        nextIndex[0] = collectComponent(vertex, reversedAdj, visited, component, nextIndex[0]);
    }

    private int collectComponent(int vertex, int[][] reversedAdj, boolean[] visited, int[] component, int nextIndex) {
        visited[vertex] = true;
        component[nextIndex++] = vertex;
        for (int neighbor : reversedAdj[vertex]) {
            if (neighbor == EMPTY) {
                break;
            }
            if (!visited[neighbor]) {
                nextIndex = collectComponent(neighbor, reversedAdj, visited, component, nextIndex);
            }
        }
        return nextIndex;
    }

    private int[][] reverseGraph(int vertexCount, int[][] adjacency) {
        int[][] reversedAdj = newEmptyMatrix(vertexCount);
        int[] edgeCounts = new int[vertexCount];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            int source = vertex;
            forEachNeighbor(source, adjacency, neighbor -> reversedAdj[neighbor][edgeCounts[neighbor]++] = source);
        }

        return reversedAdj;
    }

    private Deque<Integer> buildFinishOrder(int vertexCount, int[][] adjacency) {
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> finishOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                collectFinishOrder(vertex, adjacency, visited, finishOrder);
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

                collectComponent(vertex, reversedAdj, visited, component, 0);
                components[componentCount++] = component;
            }
        }

        return Arrays.copyOf(components, componentCount);
    }

    int[][] kosaraju(int vertexCount, int[][] adjacency) {
        Deque<Integer> finishOrder = buildFinishOrder(vertexCount, adjacency);
        int[][] reversedAdj = reverseGraph(vertexCount, adjacency);

        return collectComponents(vertexCount, reversedAdj, finishOrder);
    }

    static int[][] buildAdjMatrix(int[][] edges, int vertexCount) {
        int[][] adjacency = newEmptyMatrix(vertexCount + 1);

        int[] edgeCounts = new int[vertexCount + 1];
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adjacency[from][edgeCounts[from]++] = to;
        }

        return adjacency;
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
        int[][] adjacency = buildAdjMatrix(SAMPLE_EDGES, SAMPLE_VERTEX_COUNT);

        int[][] components = obj.kosaraju(SAMPLE_VERTEX_COUNT + 1, adjacency);

        System.out.println(OUTPUT_HEADER);
        printComponents(components, DUMMY_VERTEX);
    }
}
