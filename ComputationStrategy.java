import java.util.List;

/**
 * Strategy interface for computing prefix sums.
 */
public interface ComputationStrategy {
    /**
     * Computes prefix sum from the input array.
     *
     * @param arr the input array
     * @return list of prefix sums
     */
    List<Long> compute(int[] arr);

    /**
     * Gets the name of this strategy.
     *
     * @return strategy name
     */
    String getName();
}
