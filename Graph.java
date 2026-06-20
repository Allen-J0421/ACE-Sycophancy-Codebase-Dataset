import java.util.List;

public interface Graph {
    int vertexCount();

    List<Integer> neighborsOf(int vertex);

    Graph reverse();
}
