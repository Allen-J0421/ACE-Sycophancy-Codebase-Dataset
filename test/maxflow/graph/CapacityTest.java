package maxflow.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Capacity")
class CapacityTest {

    @Test
    @DisplayName("exposes its raw units")
    void exposesUnits() {
        assertEquals(7, Capacity.of(7).units());
        assertEquals(0, Capacity.ZERO.units());
    }

    @Test
    @DisplayName("rejects a negative quantity")
    void rejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> Capacity.of(-1));
    }

    @Test
    @DisplayName("isPositive distinguishes zero from carrying capacity")
    void isPositive() {
        assertTrue(Capacity.of(1).isPositive());
        assertFalse(Capacity.ZERO.isPositive());
    }

    @Test
    @DisplayName("min returns the smaller quantity")
    void min() {
        assertEquals(Capacity.of(3), Capacity.of(3).min(Capacity.of(5)));
        assertEquals(Capacity.of(3), Capacity.of(5).min(Capacity.of(3)));
    }

    @Test
    @DisplayName("INFINITE is the identity of min")
    void infiniteIsMinIdentity() {
        assertEquals(Capacity.of(42), Capacity.INFINITE.min(Capacity.of(42)));
        assertEquals(Capacity.of(42), Capacity.of(42).min(Capacity.INFINITE));
    }

    @Test
    @DisplayName("plus adds two quantities")
    void plus() {
        assertEquals(Capacity.of(8), Capacity.of(3).plus(Capacity.of(5)));
        assertEquals(Capacity.of(5), Capacity.ZERO.plus(Capacity.of(5)));
    }

    @Test
    @DisplayName("plus overflowing int throws rather than wrapping")
    void plusOverflowThrows() {
        assertThrows(ArithmeticException.class,
                () -> Capacity.of(Integer.MAX_VALUE).plus(Capacity.of(1)));
    }

    @Test
    @DisplayName("orders by magnitude")
    void ordersByMagnitude() {
        assertTrue(Capacity.of(3).compareTo(Capacity.of(5)) < 0);
        assertTrue(Capacity.of(5).compareTo(Capacity.of(3)) > 0);
        assertEquals(0, Capacity.of(4).compareTo(Capacity.of(4)));
    }

    @Test
    @DisplayName("renders the number, and infinity as a symbol")
    void rendering() {
        assertEquals("16", Capacity.of(16).toString());
        assertEquals("∞", Capacity.INFINITE.toString());
    }
}
