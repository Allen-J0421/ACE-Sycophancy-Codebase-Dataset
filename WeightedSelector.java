import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A weighted random selector over a fixed, ordered set of items.
 *
 * Items are added with an individual probability. Selection mirrors the
 * simulation's original probability handling exactly: the items are tested in
 * insertion order and each test draws its own fresh random number, returning
 * the first item whose cumulative probability threshold is met. If no item is
 * selected - which happens when the probabilities sum to less than one and
 * every draw falls above its threshold - null is returned.
 *
 * @version (27/06/2026)
 */
public class WeightedSelector<T>
{
    // The items available for selection, in insertion order.
    private final List<T> items = new ArrayList<>();
    // The cumulative probability threshold for the item at the same index.
    private final List<Double> thresholds = new ArrayList<>();
    // The running total of all probabilities added so far.
    private double total = 0.0;

    /**
     * Add an item with its individual probability. The item's selection
     * threshold becomes the running total of all probabilities added so far,
     * matching the cumulative scheme the simulation originally used.
     *
     * @param item        The item that can be selected.
     * @param probability The item's individual probability.
     * @return This selector, so that calls can be chained.
     */
    public WeightedSelector<T> add(T item, double probability)
    {
        total += probability;
        items.add(item);
        thresholds.add(total);
        return this;
    }

    /**
     * Select an item using an independent draw per candidate, in insertion
     * order, returning the first whose cumulative threshold is met.
     *
     * @param rand The random number generator to draw from.
     * @return The selected item, or null if none was selected.
     */
    public T select(Random rand)
    {
        for (int i = 0; i < items.size(); i++) {
            if (rand.nextDouble() <= thresholds.get(i)) {
                return items.get(i);
            }
        }
        return null;
    }

    /**
     * Return the sum of all probabilities added to this selector. Useful for
     * sanity-checking that a probability distribution is well formed.
     *
     * @return The total probability across all items.
     */
    public double totalProbability()
    {
        return total;
    }
}
