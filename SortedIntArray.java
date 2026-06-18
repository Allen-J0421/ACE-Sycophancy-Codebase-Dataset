import java.util.Arrays;

public final class SortedIntArray {

    private final int[] values;

    private SortedIntArray(int[] values) {
        this.values = values;
    }

    public static SortedIntArray copyOf(int[] values) {
        validateSorted(values);
        return new SortedIntArray(Arrays.copyOf(values, values.length));
    }

    public int length() {
        return values.length;
    }

    public int valueAt(int index) {
        return values[index];
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    private static void validateSorted(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        for (int index = 1; index < values.length; index++) {
            if (values[index - 1] > values[index]) {
                throw new IllegalArgumentException(
                    "two-pointer search requires values sorted in nondecreasing order");
            }
        }
    }
}
