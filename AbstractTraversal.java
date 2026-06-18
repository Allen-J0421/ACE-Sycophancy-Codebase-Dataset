import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTraversal implements GraphTraversal {
    protected final List<Integer> vertices;
    protected final boolean[] visited;
    protected int componentCount;

    protected AbstractTraversal() {
        this.vertices = new ArrayList<>();
        this.visited = null;
        this.componentCount = 0;
    }

    @Override
    public TraversalResult traverse(Graph graph) {
        boolean[] visitedArray = new boolean[graph.getVertexCount()];
        List<Integer> vertexList = new ArrayList<>();
        int components = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visitedArray[i]) {
                traverseComponent(graph, i, visitedArray, vertexList);
                components++;
            }
        }

        return new TraversalResult(vertexList, components);
    }

    protected abstract void traverseComponent(Graph graph, int startVertex, boolean[] visited, List<Integer> result);
}
