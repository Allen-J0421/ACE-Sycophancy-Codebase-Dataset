package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests the immutable {@link Component} value type. */
class ComponentTest {

    private static Component component() {
        return new Component(2, List.of(4, 5, 6));
    }

    @Test
    void exposesIdAndSize() {
        Component component = component();
        assertEquals(2, component.id());
        assertEquals(3, component.size());
    }

    @Test
    void containsReportsMembership() {
        Component component = component();
        assertTrue(component.contains(5));
        assertFalse(component.contains(0));
    }

    @Test
    void isIterableOverItsVertices() {
        List<Integer> seen = new ArrayList<>();
        for (int vertex : component()) {
            seen.add(vertex);
        }
        assertEquals(List.of(4, 5, 6), seen);
    }

    @Test
    void streamYieldsItsVertices() {
        assertEquals(15, component().stream().sum());
        assertEquals(List.of(4, 5, 6), component().stream().boxed().toList());
    }

    @Test
    void verticesViewIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> component().vertices().add(99));
    }

    @Test
    void isIsolatedFromTheSourceList() {
        List<Integer> source = new ArrayList<>(List.of(4, 5, 6));
        Component component = new Component(2, source);
        source.add(99);
        assertEquals(List.of(4, 5, 6), component.vertices());
    }

    @Test
    void equalityIsBasedOnIdAndVertices() {
        assertEquals(new Component(2, List.of(4, 5, 6)), component());
        assertEquals(new Component(2, List.of(4, 5, 6)).hashCode(), component().hashCode());
        assertNotEquals(new Component(3, List.of(4, 5, 6)), component());
        assertNotEquals(new Component(2, List.of(4, 5)), component());
    }
}
