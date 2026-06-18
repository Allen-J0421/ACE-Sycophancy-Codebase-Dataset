import java.util.ArrayList;
import java.util.List;

public class PrefixSum {

    public static List<Long> computePrefixSum(int[] arr) {
        if (arr == null || arr.length == 0) {
            return new ArrayList<>();
        }

        List<Long> prefixSum = new ArrayList<>();
        prefixSum.add((long) arr[0]);

        for (int i = 1; i < arr.length; i++) {
            prefixSum.add(prefixSum.get(i - 1) + arr[i]);
        }

        return prefixSum;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        List<Long> prefixSum = computePrefixSum(arr);
        System.out.println("Prefix sum array: " + prefixSum);
    }
}
