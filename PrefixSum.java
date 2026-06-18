import java.util.ArrayList;
import java.util.List;

public class PrefixSum {

    public static List<Long> computePrefixSum(int[] arr) {
        if (arr == null || arr.length == 0) {
            return new ArrayList<>();
        }

        List<Long> result = new ArrayList<>(arr.length);
        long sum = 0;

        for (int value : arr) {
            sum += value;
            result.add(sum);
        }

        return result;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        List<Long> result = computePrefixSum(arr);
        System.out.println("Prefix sum array: " + result);
    }
}
