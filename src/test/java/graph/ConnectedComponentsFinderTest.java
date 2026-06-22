package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ConnectedComponentsFinderTest {

    private final ConnectedComponentsFinder finder = new ConnectedComponentsFinder();

    /** Normalises a result to a set of vertex-sets so assertions ignore ordering. */
    private static Set<Set<Integer>> asVertexSets(Components components) {
        return components.asList().stream()
            .map(HashSet::new)
            .collect(Collectors.toSet());
    }

    @Test
    void emptyGraphHasNoComponents() {
        Components result = finder.find(new Graph(0));
        assertEquals(0, result.count());
        assertTrue(result.asList().isEmpty());
    }

    @Test
    void isolatedVerticesEachFormTheirOwnComponent() {
        Components result = finder.find(new Graph(3));
        assertEquals(3, result.count());
        assertEquals(Set.of(Set.of(0), Set.of(1), Set.of(2)), asVertexSets(result));
    }

    @Test
    void singlePathIsOneComponent() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        Components result = finder.find(graph);
        assertEquals(1, result.count());
        assertEquals(Set.of(Set.of(0, 1, 2)), asVertexSets(result));
    }

    @Test
    void sampleGraphPartitionsIntoTwoComponents() {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);
        Components result = finder.find(graph);
        assertEquals(2, result.count());
        assertEquals(Set.of(Set.of(0, 1, 2, 3), Set.of(4, 5)), asVertexSets(result));
    }

    @Test
    void everyVertexAppearsInExactlyOneComponent() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1);
        graph.addEdge(3, 4);
        Components result = finder.find(graph);
        List<Integer> all = result.asList().stream().flatMap(List::stream).sorted().toList();
        assertEquals(List.of(0, 1, 2, 3, 4), all);
    }
}
