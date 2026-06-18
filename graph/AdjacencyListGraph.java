package graph;

import java.util.List;

final class AdjacencyListGraph extends AbstractAdjacencyGraph {
    AdjacencyListGraph(List<List<Integer>> adjacencyViews) {
        super(adjacencyViews);
    }
}
