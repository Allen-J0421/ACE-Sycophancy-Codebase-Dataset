import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class CapacityTable {
    private final int[] bestValues;
    private final SelectionTrace[] bestSelections;

    CapacityTable(int capacity) {
        this.bestValues = new int[capacity + 1];
        this.bestSelections = new SelectionTrace[capacity + 1];
    }

    private int bestValueAt(int capacity) {
        return bestValues[capacity];
    }

    void consider(Item item) {
        for (int capacity = bestValues.length - 1; capacity >= item.weight(); capacity--) {
            int candidateValue = bestValueAt(capacity - item.weight()) + item.value();
            if (candidateValue > bestValueAt(capacity)) {
                bestValues[capacity] = candidateValue;
                bestSelections[capacity] =
                        new SelectionTrace(item, bestSelections[capacity - item.weight()]);
            }
        }
    }

    KnapsackSolution solutionFor(Problem problem) {
        return new KnapsackSolution(
                problem,
                selectedItemsFor(problem.capacity()),
                bestValueAt(problem.capacity())
        );
    }

    private List<Item> selectedItemsFor(int capacity) {
        List<Item> selectedItems = new ArrayList<>();
        SelectionTrace selection = bestSelections[capacity];

        while (selection != null) {
            selectedItems.add(selection.item());
            selection = selection.previous();
        }

        Collections.reverse(selectedItems);
        return List.copyOf(selectedItems);
    }

    private record SelectionTrace(Item item, SelectionTrace previous) {
    }
}
