import java.util.List;
import java.util.Set;

interface GraphView<V> extends Iterable<V> {
    Set<V> vertices();
    int vertexCount();
    List<V> neighbors(V vertex);
}
