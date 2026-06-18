import java.util.Collections;
import java.util.List;

public class UndirectedGraph extends AbstractGraph {

    public UndirectedGraph(int vertexCount) {
        super(vertexCount);
    }

    @Override
    public void addEdge(int u, int v) {
        validateEdge(u, v);
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    @Override
    public List<Integer> getAdjacent(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }
}
