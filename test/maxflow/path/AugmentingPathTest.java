package maxflow.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AugmentingPath")
class AugmentingPathTest {

    @Test
    @DisplayName("exposes its vertices and bottleneck")
    void exposesState() {
        AugmentingPath path = new AugmentingPath(List.of(0, 2, 5), 7);
        assertEquals(List.of(0, 2, 5), path.vertices());
        assertEquals(7, path.bottleneck());
    }

    @Test
    @DisplayName("rejects a path with fewer than two vertices")
    void rejectsTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new AugmentingPath(List.of(0), 5));
    }

    @Test
    @DisplayName("rejects a non-positive bottleneck")
    void rejectsNonPositiveBottleneck() {
        assertThrows(IllegalArgumentException.class, () -> new AugmentingPath(List.of(0, 1), 0));
        assertThrows(IllegalArgumentException.class, () -> new AugmentingPath(List.of(0, 1), -3));
    }

    @Test
    @DisplayName("holds an immutable copy of its vertices")
    void verticesAreImmutable() {
        AugmentingPath path = new AugmentingPath(List.of(0, 1), 4);
        assertThrows(UnsupportedOperationException.class, () -> path.vertices().add(9));
    }

    @Test
    @DisplayName("renders a readable description")
    void toStringIsReadable() {
        assertEquals("0 -> 1 -> 2 (bottleneck 4)", new AugmentingPath(List.of(0, 1, 2), 4).toString());
    }
}
