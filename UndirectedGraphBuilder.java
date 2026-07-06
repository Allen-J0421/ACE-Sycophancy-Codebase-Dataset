import java.util.ArrayList;
import java.util.List;

class UndirectedGraphBuilder implements GraphBuilder {
    private final int V;
    private final List<int[]> edges = new ArrayList<>();
    private final List<BuildCommand> history = new ArrayList<>();

    UndirectedGraphBuilder(int V) {
        this.V = V;
    }

    @Override
    public GraphBuilder addEdge(int u, int v) {
        BuildCommand cmd = new AddEdgeCommand(u, v);
        cmd.execute(edges);
        history.add(cmd);
        return this;
    }

    @Override
    public GraphBuilder undo() {
        if (!history.isEmpty()) {
            BuildCommand last = history.remove(history.size() - 1);
            last.undo(edges);
        }
        return this;
    }

    @Override
    public Graph build() {
        return buildFromEdges(edges);
    }

    @Override
    public Graph replay() {
        List<int[]> freshEdges = new ArrayList<>();
        for (BuildCommand cmd : history) {
            cmd.execute(freshEdges);
        }
        return buildFromEdges(freshEdges);
    }

    private Graph buildFromEdges(List<int[]> edgeList) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] edge : edgeList) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        return new Graph(adj);
    }
}
