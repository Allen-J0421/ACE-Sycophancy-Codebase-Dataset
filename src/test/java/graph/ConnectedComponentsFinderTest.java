package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graph.traversal.BreadthFirstTraversal;
import graph.traversal.DepthFirstTraversal;
import graph.traversal.TraversalStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ConnectedComponentsFinderTest {

    /** Each correctness test runs against every traversal strategy. */
    static Stream<TraversalStrategy> strategies() {
        return Stream.of(new BreadthFirstTraversal(), new DepthFirstTraversal());
    }

    /** Normalises a result to a set of vertex-sets so assertions ignore ordering. */
    private static Set<Set<Integer>> asVertexSets(Components components) {
        return components.asList().stream()
            .map(HashSet::new)
            .collect(Collectors.toSet());
    }

    private static Graph sampleGraph() {
        return new GraphBuilder(6)
            .addEdges(new int[][] {{1, 2}, {0, 3}, {2, 0}, {5, 4}})
            .build();
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void emptyGraphHasNoComponents(TraversalStrategy strategy) {
        Components result = new ConnectedComponentsFinder(strategy).find(new GraphBuilder(0).build());
        assertEquals(0, result.count());
        assertTrue(result.asList().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void isolatedVerticesEachFormTheirOwnComponent(TraversalStrategy strategy) {
        Components result = new ConnectedComponentsFinder(strategy).find(new GraphBuilder(3).build());
        assertEquals(3, result.count());
        assertEquals(Set.of(Set.of(0), Set.of(1), Set.of(2)), asVertexSets(result));
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void singlePathIsOneComponent(TraversalStrategy strategy) {
        Graph graph = new GraphBuilder(3).addEdge(0, 1).addEdge(1, 2).build();
        Components result = new ConnectedComponentsFinder(strategy).find(graph);
        assertEquals(1, result.count());
        assertEquals(Set.of(Set.of(0, 1, 2)), asVertexSets(result));
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void sampleGraphPartitionsIntoTwoComponents(TraversalStrategy strategy) {
        Components result = new ConnectedComponentsFinder(strategy).find(sampleGraph());
        assertEquals(2, result.count());
        assertEquals(Set.of(Set.of(0, 1, 2, 3), Set.of(4, 5)), asVertexSets(result));
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void everyVertexAppearsInExactlyOneComponent(TraversalStrategy strategy) {
        Graph graph = new GraphBuilder(5).addEdge(0, 1).addEdge(3, 4).build();
        Components result = new ConnectedComponentsFinder(strategy).find(graph);
        List<Integer> all = result.asList().stream().flatMap(List::stream).sorted().toList();
        assertEquals(List.of(0, 1, 2, 3, 4), all);
    }

    @Test
    void bfsAndDfsProduceTheSamePartition() {
        Graph graph = sampleGraph();
        Components bfs = new ConnectedComponentsFinder(new BreadthFirstTraversal()).find(graph);
        Components dfs = new ConnectedComponentsFinder(new DepthFirstTraversal()).find(graph);
        assertEquals(asVertexSets(bfs), asVertexSets(dfs));
    }

    @Test
    void defaultStrategyIsBreadthFirst() {
        // BFS from the sample graph visits the first component in level order.
        Components result = new ConnectedComponentsFinder().find(sampleGraph());
        assertEquals(List.of(0, 3, 2, 1), result.asList().get(0));
    }

    @Test
    void nullStrategyIsRejected() {
        assertThrows(NullPointerException.class, () -> new ConnectedComponentsFinder(null));
    }
}
