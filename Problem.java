import java.util.List;
import java.util.Objects;

record Problem(int capacity, List<Item> items) {
    Problem {
        Validation.requireNonNegative(capacity, "capacity");
        items = Validation.copyNonNullElements(items, "items");
    }

    static Problem of(int capacity, List<Item> items) {
        Objects.requireNonNull(items, "items must not be null");
        return new Problem(capacity, items);
    }

    static Problem of(int capacity, Item... items) {
        Objects.requireNonNull(items, "items must not be null");
        return of(capacity, List.of(items));
    }
}
