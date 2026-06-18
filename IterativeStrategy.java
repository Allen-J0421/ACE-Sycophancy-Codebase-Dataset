import java.util.ArrayList;
import java.util.List;

/**
 * Iterative implementation of prefix sum computation.
 */
public class IterativeStrategy implements ComputationStrategy {

    @Override
    public List<Long> compute(int[] arr) {
        List<Long> result = new ArrayList<>(arr.length);
        long sum = 0;

        for (int value : arr) {
            sum += value;
            result.add(sum);
        }

        return result;
    }

    @Override
    public String getName() {
        return "Iterative";
    }
}
