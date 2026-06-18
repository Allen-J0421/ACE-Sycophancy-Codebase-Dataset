import java.util.Arrays;
import java.util.List;

public final class SampleGraphs {
    private static final int SAMPLE_DAG_VERTEX_COUNT = 6;
    private static final List<DirectedEdge> SAMPLE_DAG_EDGES = Arrays.asList(
            DirectedEdge.of(0, 1),
            DirectedEdge.of(1, 2),
            DirectedEdge.of(2, 3),
            DirectedEdge.of(4, 5),
            DirectedEdge.of(5, 1),
            DirectedEdge.of(5, 2));

    private SampleGraphs() {
    }

    public static DirectedGraph sampleDag() {
        return DirectedGraph.fromEdges(SAMPLE_DAG_VERTEX_COUNT, SAMPLE_DAG_EDGES);
    }
}
