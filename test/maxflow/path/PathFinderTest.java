package maxflow.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import maxflow.graph.Capacity;
import maxflow.graph.FlowNetwork;
import maxflow.graph.ResidualGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Augmenting-path finders")
class PathFinderTest {

    private static ResidualGraph residualOf(FlowNetwork network) {
        return new ResidualGraph(network);
    }

    @Test
    @DisplayName("find a path and compute its bottleneck as the minimum residual capacity")
    void computesBottleneck() {
        FlowNetwork network = FlowNetwork.builder(3)
                .addEdge(0, 1, 5)
                .addEdge(1, 2, 3)
                .build();

        Optional<AugmentingPath> found = new BreadthFirstPathFinder().findPath(residualOf(network), 0, 2);

        assertTrue(found.isPresent());
        assertEquals(List.of(0, 1, 2), found.get().vertices());
        assertEquals(Capacity.of(3), found.get().bottleneck(), "bottleneck is the smaller of 5 and 3");
    }

    @Test
    @DisplayName("return empty when the sink is unreachable")
    void emptyWhenUnreachable() {
        FlowNetwork network = FlowNetwork.builder(3).addEdge(0, 1, 5).build();
        assertTrue(new BreadthFirstPathFinder().findPath(residualOf(network), 0, 2).isEmpty());
        assertTrue(new DepthFirstPathFinder().findPath(residualOf(network), 0, 2).isEmpty());
    }

    @Test
    @DisplayName("breadth-first search returns a path with the fewest edges")
    void breadthFirstFindsShortestPath() {
        // A direct edge 0->3 (one hop) competes with a longer 0->1->2->3 route.
        FlowNetwork network = FlowNetwork.builder(4)
                .addEdge(0, 1, 5)
                .addEdge(1, 2, 5)
                .addEdge(2, 3, 5)
                .addEdge(0, 3, 5)
                .build();

        AugmentingPath path = new BreadthFirstPathFinder().findPath(residualOf(network), 0, 3).orElseThrow();
        assertEquals(List.of(0, 3), path.vertices(), "BFS should take the single-hop route");
    }

    @Test
    @DisplayName("a returned path is well-formed: source first, sink last, every edge traversable")
    void pathIsWellFormed() {
        FlowNetwork network = FlowNetwork.builder(4)
                .addEdge(0, 1, 4)
                .addEdge(1, 2, 4)
                .addEdge(2, 3, 4)
                .build();
        ResidualGraph residual = residualOf(network);

        for (AugmentingPathFinder finder : List.of(new BreadthFirstPathFinder(), new DepthFirstPathFinder())) {
            List<Integer> vertices = finder.findPath(residual, 0, 3).orElseThrow().vertices();
            assertEquals(0, vertices.get(0), finder.name() + ": starts at source");
            assertEquals(3, vertices.get(vertices.size() - 1), finder.name() + ": ends at sink");
            for (int i = 0; i + 1 < vertices.size(); i++) {
                assertTrue(residual.hasResidualCapacity(vertices.get(i), vertices.get(i + 1)),
                        finder.name() + ": every step has residual capacity");
            }
        }
    }

    @Test
    @DisplayName("expose a human-readable strategy name")
    void exposeName() {
        assertTrue(new BreadthFirstPathFinder().name().contains("breadth-first"));
        assertTrue(new DepthFirstPathFinder().name().contains("depth-first"));
        assertFalse(new BreadthFirstPathFinder().name().isBlank());
    }
}
