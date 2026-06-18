import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class SampleGraphFactory {
    private static final int VERTEX_COUNT = 6;
    private static final List<Edge> EDGES = Collections.unmodifiableList(Arrays.asList(
            new Edge(1, 2),
            new Edge(2, 0),
            new Edge(0, 3),
            new Edge(4, 5)
    ));

    private SampleGraphFactory() {
    }

    static UndirectedGraph create() {
        return UndirectedGraph.fromEdges(VERTEX_COUNT, EDGES);
    }
}
