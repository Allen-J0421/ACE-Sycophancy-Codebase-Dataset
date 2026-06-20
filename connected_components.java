import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;

class ConnectedComponents {

    static void bfs(ArrayList<ArrayList<Integer>> adj, int src, boolean[] visited, ArrayList<Integer> component) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[src] = true;
        queue.addLast(src);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            component.add(currentVertex);

            for (int neighbor : adj.get(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.addLast(neighbor);
                }
            }
        }
    }

    static ArrayList<ArrayList<Integer>> getComponents(ArrayList<ArrayList<Integer>> adj) {
        int vertexCount = adj.size();
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                ArrayList<Integer> component = new ArrayList<>();
                bfs(adj, vertex, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    static void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    static ArrayList<ArrayList<Integer>> createGraph(int vertexCount) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adj.add(new ArrayList<>());
        }
        return adj;
    }

    static void printComponents(ArrayList<ArrayList<Integer>> components) {
        for (ArrayList<Integer> component : components) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int vertexCount = 6;
        ArrayList<ArrayList<Integer>> adj = createGraph(vertexCount);

        addEdge(adj, 1, 2);
        addEdge(adj, 0, 3);
        addEdge(adj, 2, 0);
        addEdge(adj, 5, 4);

        ArrayList<ArrayList<Integer>> components = getComponents(adj);
        printComponents(components);
    }
}
