import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SelectionPath {
    private static final SelectionPath EMPTY = new SelectionPath(null, null);

    private final Item item;
    private final SelectionPath previous;

    private SelectionPath(Item item, SelectionPath previous) {
        this.item = item;
        this.previous = previous;
    }

    static SelectionPath empty() {
        return EMPTY;
    }

    SelectionPath append(Item item) {
        return new SelectionPath(item, this);
    }

    List<Item> toItems() {
        List<Item> selectedItems = new ArrayList<>();
        SelectionPath current = this;

        while (!current.isEmpty()) {
            selectedItems.add(current.item);
            current = current.previous;
        }

        Collections.reverse(selectedItems);
        return List.copyOf(selectedItems);
    }

    private boolean isEmpty() {
        return this == EMPTY;
    }
}
