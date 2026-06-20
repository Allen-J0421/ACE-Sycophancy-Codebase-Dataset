package sorting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for {@link IntArrayFormatter}. */
class IntArrayFormatterTest {

    @Test
    void formatsSpaceSeparatedWithTrailingSpace() {
        assertEquals("11 12 22 ", IntArrayFormatter.format(new int[]{11, 12, 22}));
    }

    @Test
    void formatsEmptyArrayAsEmptyString() {
        assertEquals("", IntArrayFormatter.format(new int[]{}));
    }

    @Test
    void formatsSingleElement() {
        assertEquals("42 ", IntArrayFormatter.format(new int[]{42}));
    }
}
