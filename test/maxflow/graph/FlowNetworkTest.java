package maxflow.graph;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FlowNetwork")
class FlowNetworkTest {

    @Nested
    @DisplayName("construction via builder")
    class Builder {

        @Test
        @DisplayName("records the capacities of added edges")
        void recordsCapacities() {
            FlowNetwork network = FlowNetwork.builder(3)
                    .addEdge(0, 1, 16)
                    .addEdge(1, 2, 10)
                    .build();

            assertEquals(16, network.capacity(0, 1));
            assertEquals(10, network.capacity(1, 2));
        }

        @Test
        @DisplayName("reports zero capacity for absent edges")
        void absentEdgeHasZeroCapacity() {
            FlowNetwork network = FlowNetwork.builder(3).addEdge(0, 1, 5).build();
            assertEquals(0, network.capacity(0, 2));
            assertEquals(0, network.capacity(2, 0));
        }

        @Test
        @DisplayName("overwrites a previously added edge")
        void overwritesEdge() {
            FlowNetwork network = FlowNetwork.builder(2)
                    .addEdge(0, 1, 5)
                    .addEdge(0, 1, 9)
                    .build();
            assertEquals(9, network.capacity(0, 1));
        }

        @Test
        @DisplayName("rejects a non-positive vertex count")
        void rejectsNonPositiveVertexCount() {
            assertThrows(IllegalArgumentException.class, () -> FlowNetwork.builder(0));
            assertThrows(IllegalArgumentException.class, () -> FlowNetwork.builder(-1));
        }

        @Test
        @DisplayName("rejects out-of-range endpoints")
        void rejectsOutOfRangeEndpoints() {
            FlowNetwork.Builder builder = FlowNetwork.builder(2);
            assertThrows(IllegalArgumentException.class, () -> builder.addEdge(-1, 1, 5));
            assertThrows(IllegalArgumentException.class, () -> builder.addEdge(0, 2, 5));
        }

        @Test
        @DisplayName("rejects self-loops")
        void rejectsSelfLoops() {
            FlowNetwork.Builder builder = FlowNetwork.builder(2);
            assertThrows(IllegalArgumentException.class, () -> builder.addEdge(1, 1, 5));
        }

        @Test
        @DisplayName("rejects negative capacity")
        void rejectsNegativeCapacity() {
            FlowNetwork.Builder builder = FlowNetwork.builder(2);
            assertThrows(IllegalArgumentException.class, () -> builder.addEdge(0, 1, -1));
        }

        @Test
        @DisplayName("produces a snapshot unaffected by later builder mutation")
        void buildTakesSnapshot() {
            FlowNetwork.Builder builder = FlowNetwork.builder(2).addEdge(0, 1, 5);
            FlowNetwork first = builder.build();
            builder.addEdge(0, 1, 99);
            assertEquals(5, first.capacity(0, 1), "earlier network must not see later edits");
        }
    }

    @Nested
    @DisplayName("construction from a matrix")
    class FromMatrix {

        @Test
        @DisplayName("copies every capacity")
        void copiesCapacities() {
            int[][] matrix = {
                    {0, 16, 13},
                    {0, 0, 10},
                    {0, 0, 0},
            };
            FlowNetwork network = FlowNetwork.fromMatrix(matrix);
            assertEquals(3, network.vertexCount());
            assertEquals(16, network.capacity(0, 1));
            assertEquals(13, network.capacity(0, 2));
            assertEquals(10, network.capacity(1, 2));
        }

        @Test
        @DisplayName("rejects a non-square matrix")
        void rejectsNonSquare() {
            int[][] jagged = {
                    {0, 1, 2},
                    {0, 0},
                    {0, 0, 0},
            };
            assertThrows(IllegalArgumentException.class, () -> FlowNetwork.fromMatrix(jagged));
        }

        @Test
        @DisplayName("rejects a negative capacity")
        void rejectsNegative() {
            int[][] matrix = {
                    {0, -1},
                    {0, 0},
            };
            assertThrows(IllegalArgumentException.class, () -> FlowNetwork.fromMatrix(matrix));
        }
    }

    @Nested
    @DisplayName("queries")
    class Queries {

        @Test
        @DisplayName("isVertex reflects the valid index range")
        void isVertex() {
            FlowNetwork network = FlowNetwork.builder(3).build();
            assertTrue(network.isVertex(0));
            assertTrue(network.isVertex(2));
            assertFalse(network.isVertex(-1));
            assertFalse(network.isVertex(3));
        }

        @Test
        @DisplayName("toMatrix returns a defensive copy")
        void toMatrixIsDefensiveCopy() {
            FlowNetwork network = FlowNetwork.builder(2).addEdge(0, 1, 7).build();
            int[][] copy = network.toMatrix();
            copy[0][1] = 123;
            assertEquals(7, network.capacity(0, 1), "mutating the copy must not affect the network");
            assertArrayEquals(new int[] {0, 7}, network.toMatrix()[0]);
        }
    }
}
