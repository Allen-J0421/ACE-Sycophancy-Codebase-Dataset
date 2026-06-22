import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConnectedComponents")
class ConnectedComponentsTest {

    @Nested
    @DisplayName("find — argument validation")
    class Validation {

        @Test
        @DisplayName("rejects null graph")
        void nullGraph() {
            assertThrows(IllegalArgumentException.class, () -> ConnectedComponents.find(null));
        }
    }

    @Nested
    @DisplayName("find — empty and trivial graphs")
    class TrivialCases {

        @Test
        @DisplayName("empty graph returns empty component list")
        void emptyGraph() {
            List<List<Integer>> components = ConnectedComponents.find(new Graph(0));
            assertTrue(components.isEmpty());
        }

        @Test
        @DisplayName("single vertex forms one component containing that vertex")
        void singleVertex() {
            List<List<Integer>> components = ConnectedComponents.find(new Graph(1));
            assertEquals(1, components.size());
            assertEquals(List.of(0), components.get(0));
        }
    }

    @Nested
    @DisplayName("find — disconnected graphs")
    class DisconnectedGraphs {

        @Test
        @DisplayName("two isolated vertices form two components")
        void twoIsolatedVertices() {
            List<List<Integer>> components = ConnectedComponents.find(new Graph(2));
            assertEquals(2, components.size());
        }

        @Test
        @DisplayName("N isolated vertices form N components")
        void nIsolatedVertices() {
            int n = 6;
            List<List<Integer>> components = ConnectedComponents.find(new Graph(n));
            assertEquals(n, components.size());
            components.forEach(c -> assertEquals(1, c.size()));
        }

        @Test
        @DisplayName("two pairs form two components of size 2")
        void twoPairs() {
            Graph graph = new Graph(4);
            graph.addEdge(0, 1);
            graph.addEdge(2, 3);
            List<List<Integer>> components = ConnectedComponents.find(graph);
            assertEquals(2, components.size());
            components.forEach(c -> assertEquals(2, c.size()));
        }

        @Test
        @DisplayName("original example: two components of sizes 4 and 2")
        void originalExample() {
            Graph graph = new Graph(6);
            graph.addEdge(1, 2);
            graph.addEdge(0, 3);
            graph.addEdge(2, 0);
            graph.addEdge(5, 4);

            List<List<Integer>> components = ConnectedComponents.find(graph);

            assertEquals(2, components.size());
            assertTrue(components.get(0).containsAll(List.of(0, 1, 2, 3)));
            assertEquals(4, components.get(0).size());
            assertTrue(components.get(1).containsAll(List.of(4, 5)));
            assertEquals(2, components.get(1).size());
        }
    }

    @Nested
    @DisplayName("find — fully connected graphs")
    class ConnectedGraphs {

        @Test
        @DisplayName("two connected vertices form one component")
        void twoConnectedVertices() {
            Graph graph = new Graph(2);
            graph.addEdge(0, 1);
            List<List<Integer>> components = ConnectedComponents.find(graph);
            assertEquals(1, components.size());
            assertTrue(components.get(0).containsAll(List.of(0, 1)));
        }

        @Test
        @DisplayName("linear chain forms one component")
        void linearChain() {
            Graph graph = new Graph(5);
            for (int i = 0; i < 4; i++) graph.addEdge(i, i + 1);
            List<List<Integer>> components = ConnectedComponents.find(graph);
            assertEquals(1, components.size());
            assertEquals(5, components.get(0).size());
        }

        @Test
        @DisplayName("cycle forms one component")
        void cycle() {
            Graph graph = new Graph(4);
            graph.addEdge(0, 1);
            graph.addEdge(1, 2);
            graph.addEdge(2, 3);
            graph.addEdge(3, 0);
            List<List<Integer>> components = ConnectedComponents.find(graph);
            assertEquals(1, components.size());
            assertEquals(4, components.get(0).size());
        }

        @Test
        @DisplayName("star (hub + spokes) forms one component")
        void star() {
            Graph graph = new Graph(5);
            for (int spoke = 1; spoke <= 4; spoke++) graph.addEdge(0, spoke);
            List<List<Integer>> components = ConnectedComponents.find(graph);
            assertEquals(1, components.size());
            assertEquals(5, components.get(0).size());
        }
    }

    @Nested
    @DisplayName("find — result correctness")
    class ResultCorrectness {

        @Test
        @DisplayName("every vertex appears in exactly one component")
        void partitionCoversAllVertices() {
            Graph graph = new Graph(7);
            graph.addEdge(0, 1);
            graph.addEdge(3, 4);
            graph.addEdge(4, 5);

            List<List<Integer>> components = ConnectedComponents.find(graph);
            long total = components.stream().mapToLong(List::size).sum();
            assertEquals(7, total);
        }

        @Test
        @DisplayName("outer result list is unmodifiable")
        void outerListUnmodifiable() {
            List<List<Integer>> components = ConnectedComponents.find(new Graph(1));
            assertThrows(UnsupportedOperationException.class, () -> components.add(List.of()));
            assertThrows(UnsupportedOperationException.class, components::clear);
        }

        @Test
        @DisplayName("inner component lists are unmodifiable")
        void innerListsUnmodifiable() {
            Graph graph = new Graph(2);
            graph.addEdge(0, 1);
            List<List<Integer>> components = ConnectedComponents.find(graph);
            assertThrows(UnsupportedOperationException.class, () -> components.get(0).add(99));
            assertThrows(UnsupportedOperationException.class, () -> components.get(0).remove(0));
        }
    }
}
