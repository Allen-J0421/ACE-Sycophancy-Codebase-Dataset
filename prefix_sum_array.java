import java.util.Arrays;

public class PrefixSum {

    public static int[] computePrefixSum(int[] arr) {
        int[] result = new int[arr.length];
        result[0] = arr[0];
        for (int i = 1; i < arr.length; i++) {
            result[i] = result[i - 1] + arr[i];
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        System.out.println(Arrays.toString(computePrefixSum(arr)));
    }
}
