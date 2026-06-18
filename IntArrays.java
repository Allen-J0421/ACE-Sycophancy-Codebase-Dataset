import java.util.Arrays;

final class IntArrays {

    private IntArrays() {
        // Utility class.
    }

    static int[] requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        return values;
    }

    static int[] copyOf(int[] values) {
        int[] safeValues = requireValues(values);
        return Arrays.copyOf(safeValues, safeValues.length);
    }

    static String format(int[] values) {
        return Arrays.toString(requireValues(values));
    }
}
