import java.util.List;

public interface IntGraph {
    int vertexCount();

    List<Integer> neighborsOf(int vertex);
}
