import java.util.List;
import java.util.Objects;

final class Validation {
    private Validation() {
    }

    static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }

    static <T> List<T> copyNonNullElements(List<T> values, String name) {
        Objects.requireNonNull(values, name + " must not be null");

        for (int index = 0; index < values.size(); index++) {
            Objects.requireNonNull(values.get(index), name + " contains null element at index " + index);
        }

        return List.copyOf(values);
    }
}
