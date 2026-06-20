import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

class BipartiteGraph {
    private static final int UNCOLORED = -1;
    private static final int FIRST_COLOR = 0;

    static ArrayList<ArrayList<Integer>> constructadj(int V, int[][] edges) {
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();

        for (int vertex = 0; vertex < V; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        return adjacencyList;
    }

    static boolean isBipartite(int V, int[][] edges) {
        int[] colors = new int[V];
        Arrays.fill(colors, UNCOLORED);

        ArrayList<ArrayList<Integer>> adjacencyList = constructadj(V, edges);

        for (int vertex = 0; vertex < V; vertex++) {
            if (colors[vertex] == UNCOLORED && !colorComponent(vertex, adjacencyList, colors)) {
                return false;
            }
        }

        return true;
    }

    private static boolean colorComponent(
            int startVertex,
            ArrayList<ArrayList<Integer>> adjacencyList,
            int[] colors
    ) {
        Queue<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = FIRST_COLOR;
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();

            for (int neighbor : adjacencyList.get(currentVertex)) {
                if (colors[neighbor] == UNCOLORED) {
                    colors[neighbor] = oppositeColor(colors[currentVertex]);
                    queue.offer(neighbor);
                } else if (colors[neighbor] == colors[currentVertex]) {
                    return false;
                }
            }
        }

        return true;
    }

    private static int oppositeColor(int color) {
        return 1 - color;
    }

    public static void main(String[] args) {
        int V = 4;

        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(V, edges));
    }
}
