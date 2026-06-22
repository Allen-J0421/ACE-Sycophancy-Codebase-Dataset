package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * White-box tests for {@link Components}. These live in the same package so the
 * package-private constructor can be exercised directly without routing through
 * a {@link Graph}.
 */
class ComponentsTest {

    private static Components twoComponents() {
        // Component 0 = {0, 2}, component 1 = {1}; partitions vertices 0..2.
        return new Components(List.of(List.of(0, 2), List.of(1)), 3);
    }

    @Test
    void countReflectsNumberOfComponents() {
        assertEquals(2, twoComponents().count());
    }

    @Test
    void componentOfReturnsOwningComponentId() {
        Components components = twoComponents();
        assertEquals(0, components.componentOf(0));
        assertEquals(1, components.componentOf(1));
        assertEquals(0, components.componentOf(2));
    }

    @Test
    void connectedIsTrueWithinAComponentAndFalseAcross() {
        Components components = twoComponents();
        assertTrue(components.connected(0, 2));
        assertFalse(components.connected(0, 1));
    }

    @Test
    void componentOfRejectsOutOfRangeVertex() {
        assertThrows(IndexOutOfBoundsException.class, () -> twoComponents().componentOf(3));
    }

    @Test
    void asListIsDeeplyUnmodifiable() {
        Components components = twoComponents();
        assertThrows(UnsupportedOperationException.class, () -> components.asList().clear());
        assertThrows(UnsupportedOperationException.class, () -> components.asList().get(0).add(99));
    }

    @Test
    void resultIsIsolatedFromTheSourceList() {
        List<List<Integer>> source = new java.util.ArrayList<>();
        source.add(new java.util.ArrayList<>(List.of(0)));
        Components components = new Components(source, 1);

        source.get(0).add(99);
        source.clear();

        assertEquals(1, components.count());
        assertEquals(List.of(0), components.asList().get(0));
    }
}
