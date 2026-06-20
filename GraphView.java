import java.util.List;

interface GraphView {

    int vertexCount();

    List<Integer> neighborsOf(int vertex);
}
