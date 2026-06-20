import java.util.List;

public interface DirectedGraphView {
    int vertexCount();

    List<Integer> neighborsOf(int vertex);
}
