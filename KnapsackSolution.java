import java.util.List;
import java.util.Objects;

record KnapsackSolution(Problem problem, List<Item> selectedItems, int optimalValue) {
    KnapsackSolution {
        Objects.requireNonNull(problem, "problem must not be null");
        selectedItems = Validation.copyNonNullElements(selectedItems, "selectedItems");
        Validation.requireNonNegative(optimalValue, "optimalValue");
    }

    int totalWeight() {
        int weight = 0;

        for (Item item : selectedItems) {
            weight += item.weight();
        }

        return weight;
    }
}
