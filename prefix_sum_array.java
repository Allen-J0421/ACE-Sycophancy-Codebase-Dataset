import java.util.Arrays;

public class PrefixSum {

    private PrefixSum() {}

    public static int[] computePrefixSum(int[] values) {
        int[] result = new int[values.length];
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
            result[i] = sum;
        }
        return result;
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        System.out.println(Arrays.toString(computePrefixSum(values)));
    }
}
