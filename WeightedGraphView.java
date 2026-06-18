import java.util.List;

interface WeightedGraphView {
    List<Edge> getAdjacencyListFor(int vertex);
    int getVertexCount();
}
