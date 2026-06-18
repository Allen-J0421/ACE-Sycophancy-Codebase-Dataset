public class ConnectivityQuery {
    private final Graph graph;
    private boolean[] visited;
    private int[] component;

    public ConnectivityQuery(Graph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.component = new int[graph.getVertexCount()];
        initializeComponents();
    }

    private void initializeComponents() {
        int componentId = 0;
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                assignComponent(i, componentId++);
            }
        }
    }

    private void assignComponent(int vertex, int componentId) {
        visited[vertex] = true;
        component[vertex] = componentId;

        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                assignComponent(neighbor, componentId);
            }
        }
    }

    public boolean areConnected(int u, int v) {
        return component[u] == component[v];
    }

    public int getComponent(int vertex) {
        return component[vertex];
    }

    public int getTotalComponents() {
        int max = 0;
        for (int c : component) {
            max = Math.max(max, c);
        }
        return max + 1;
    }
}
