import java.util.List;

class AddEdgeCommand implements BuildCommand {
    private final int u;
    private final int v;

    AddEdgeCommand(int u, int v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public void execute(List<int[]> edges) {
        edges.add(new int[]{u, v});
    }

    @Override
    public void undo(List<int[]> edges) {
        edges.remove(edges.size() - 1);
    }
}
