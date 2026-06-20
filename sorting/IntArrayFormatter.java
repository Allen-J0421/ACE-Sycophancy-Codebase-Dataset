package sorting;

import java.util.Objects;

/**
 * Renders primitive {@code int} arrays for display, separating formatting from
 * both the sorting algorithm and the I/O channel that ultimately prints them.
 */
public final class IntArrayFormatter {

    private IntArrayFormatter() {
        // Utility class; not instantiable.
    }

    /**
     * Formats {@code array} as space-separated values with a trailing space,
     * e.g. {@code "11 12 22 "}. This matches the layout produced by the
     * original {@code printArray} routine.
     *
     * @param array the array to format; must not be {@code null}
     * @return the formatted representation
     */
    public static String format(int[] array) {
        Objects.requireNonNull(array, "array");
        StringBuilder sb = new StringBuilder();
        for (int value : array) {
            sb.append(value).append(' ');
        }
        return sb.toString();
    }
}
