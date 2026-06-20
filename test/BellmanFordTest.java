import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for the Bellman-Ford implementation.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("Bellman-Ford shortest paths")
class BellmanFordTest {

    /** The canonical 5-vertex sample graph; distances from 0 are {0, 5, 6, 6, 7}. */
    private static WeightedGraph sampleGraph() {
        return WeightedGraph.from(5, new int[][] {
            {1, 3, 2}, {4, 3, -1}, {2, 4, 1}, {1, 2, 1}, {0, 1, 5}
        });
    }

    /** Runs the algorithm and asserts a successful (non-cyclic) result. */
    private static Distances distancesFrom(WeightedGraph graph, int source) {
        return assertInstanceOf(Distances.class, BellmanFord.shortestPaths(graph, source));
    }

    @Nested
    @DisplayName("Distances")
    class DistanceTests {

        @Test
        @DisplayName("sample graph produces the known distance vector")
        void sampleGraphProducesKnownDistances() {
            Distances distances = distancesFrom(sampleGraph(), 0);
            assertArrayEquals(new int[] {0, 5, 6, 6, 7}, distances.all());
        }

        @Test
        @DisplayName("unreachable vertices are reported")
        void unreachableVerticesAreReported() {
            // Vertex 2 is isolated; vertex 1 is reachable from 0.
            Distances distances = distancesFrom(WeightedGraph.of(3, WeightedEdge.of(0, 1, 4)), 0);

            assertTrue(distances.isReachable(0), "source");
            assertTrue(distances.isReachable(1), "neighbour");
            assertFalse(distances.isReachable(2), "isolated vertex");
            assertEquals(Distances.UNREACHABLE, distances.distanceTo(2));
        }

        @Test
        @DisplayName("negative edge weights are handled")
        void negativeWeightsAreHandled() {
            WeightedGraph graph = WeightedGraph.of(3,
                WeightedEdge.of(0, 1, 4),
                WeightedEdge.of(0, 2, 5),
                WeightedEdge.of(1, 2, -3));
            assertEquals(1, distancesFrom(graph, 0).distanceTo(2));
        }
    }

    @Nested
    @DisplayName("Path reconstruction")
    class PathTests {

        @Test
        @DisplayName("path follows the optimal route and carries its total weight")
        void pathFollowsOptimalRoute() {
            Path path = distancesFrom(sampleGraph(), 0).pathTo(3);

            assertEquals(List.of(0, 1, 2, 4, 3), path.vertices());
            assertEquals(6, path.totalWeight());
            assertEquals(4, path.edgeCount());
            assertFalse(path.isEmpty());
        }

        @Test
        @DisplayName("path weight always equals the computed distance")
        void pathWeightMatchesDistance() {
            Distances distances = distancesFrom(sampleGraph(), 0);
            for (int v = 0; v < distances.vertexCount(); v++) {
                assertEquals(distances.distanceTo(v), distances.pathTo(v).totalWeight(),
                    "weight to vertex " + v);
            }
        }

        @Test
        @DisplayName("path to the source is the source alone with zero weight")
        void pathToSourceIsTheSourceAlone() {
            Path path = distancesFrom(WeightedGraph.of(2, WeightedEdge.of(0, 1, 1)), 0).pathTo(0);

            assertEquals(List.of(0), path.vertices());
            assertEquals(0, path.totalWeight());
            assertEquals(0, path.edgeCount());
        }

        @Test
        @DisplayName("unreachable target yields Path.none()")
        void unreachableTargetHasNoPath() {
            Path path = distancesFrom(WeightedGraph.of(3, WeightedEdge.of(0, 1, 4)), 0).pathTo(2);

            assertTrue(path.isEmpty());
            assertEquals(Path.none(), path);
        }
    }

    @Nested
    @DisplayName("Negative cycles")
    class NegativeCycleTests {

        @Test
        @DisplayName("a reachable negative cycle is detected")
        void negativeCycleIsDetected() {
            WeightedGraph graph = WeightedGraph.of(3,
                WeightedEdge.of(0, 1, 1),
                WeightedEdge.of(1, 2, -1),
                WeightedEdge.of(2, 1, -1)); // 1 -> 2 -> 1 sums to -2
            assertInstanceOf(NegativeCycle.class, BellmanFord.shortestPaths(graph, 0));
        }

        @Test
        @DisplayName("the result type distinguishes the two cases")
        void resultTypeDistinguishesTheTwoCases() {
            WeightedGraph healthy = WeightedGraph.of(2, WeightedEdge.of(0, 1, 1));
            WeightedGraph cyclic = WeightedGraph.of(2,
                WeightedEdge.of(0, 1, 1),
                WeightedEdge.of(1, 0, -2));

            assertInstanceOf(Distances.class, BellmanFord.shortestPaths(healthy, 0));
            assertInstanceOf(NegativeCycle.class, BellmanFord.shortestPaths(cyclic, 0));
        }
    }

    @Nested
    @DisplayName("Input validation")
    class ValidationTests {

        @Test
        @DisplayName("out-of-range edge endpoints are rejected")
        void outOfRangeEndpointIsRejected() {
            assertThrows(IllegalArgumentException.class,
                () -> WeightedGraph.of(2, WeightedEdge.of(0, 5, 1)));
        }

        @Test
        @DisplayName("a non-positive vertex count is rejected")
        void nonPositiveVertexCountIsRejected() {
            assertThrows(IllegalArgumentException.class, () -> WeightedGraph.of(0));
        }

        @Test
        @DisplayName("an out-of-range source is rejected")
        void outOfRangeSourceIsRejected() {
            WeightedGraph graph = WeightedGraph.of(2, WeightedEdge.of(0, 1, 1));
            assertThrows(IllegalArgumentException.class,
                () -> BellmanFord.shortestPaths(graph, 7));
        }
    }
}
