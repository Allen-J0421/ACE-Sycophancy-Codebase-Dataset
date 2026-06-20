import java.util.Objects;

abstract class DepthFirstGraphAnalysis<Result> {
    protected static final int ROOT_PARENT = -1;

    private final Graph graph;
    private final boolean[] visited;
    private final int[] discoveryTime;
    private int currentTime;

    protected DepthFirstGraphAnalysis(Graph graph) {
        this.graph = Objects.requireNonNull(graph, "graph");
        this.discoveryTime = new int[graph.vertexCount()];
        this.visited = new boolean[graph.vertexCount()];
        this.currentTime = 0;
    }

    protected final Result run() {
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                depthFirstSearch(vertex, ROOT_PARENT);
            }
        }

        return buildResult();
    }

    protected final int vertexCount() {
        return graph.vertexCount();
    }

    protected final int discoveryTimeOf(int vertex) {
        return discoveryTime[vertex];
    }

    protected final boolean isRoot(int parent) {
        return parent == ROOT_PARENT;
    }

    private void depthFirstSearch(int vertex, int parent) {
        visited[vertex] = true;
        discoveryTime[vertex] = ++currentTime;
        onDiscoverVertex(vertex, parent);

        int childCount = 0;
        for (int neighbor : graph.neighborsOf(vertex)) {
            if (!visited[neighbor]) {
                childCount++;
                depthFirstSearch(neighbor, vertex);
                afterChildTraversal(vertex, neighbor, parent);
            } else if (neighbor != parent) {
                onBackEdge(vertex, neighbor);
            }
        }

        onCompleteVertex(vertex, parent, childCount);
    }

    protected void onDiscoverVertex(int vertex, int parent) {
    }

    protected void afterChildTraversal(int vertex, int child, int parent) {
    }

    protected void onBackEdge(int vertex, int neighbor) {
    }

    protected void onCompleteVertex(int vertex, int parent, int childCount) {
    }

    protected abstract Result buildResult();
}
