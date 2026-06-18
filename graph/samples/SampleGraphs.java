package graph.samples;

import graph.Graph;
import graph.GraphBuilder;

public final class SampleGraphs {
    private SampleGraphs() {
    }

    public static Graph disconnectedUndirectedGraph() {
        return GraphBuilder.withVertices(6)
            .addUndirectedEdge(1, 2)
            .addUndirectedEdge(2, 0)
            .addUndirectedEdge(0, 3)
            .addUndirectedEdge(4, 5)
            .build();
    }
}
