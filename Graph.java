import java.util.List;

public interface Graph {
    int vertexCount();

    List<Integer> neighbors(int vertex);
}
