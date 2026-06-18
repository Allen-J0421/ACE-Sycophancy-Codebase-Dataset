import java.util.Arrays;

public class PrefixSum {

    public static int[] computePrefixSum(int[] values) {
        if (values.length == 0) return new int[0];
        int[] result = new int[values.length];
        result[0] = values[0];
        for (int i = 1; i < values.length; i++) {
            result[i] = result[i - 1] + values[i];
        }
        return result;
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        System.out.println(Arrays.toString(computePrefixSum(values)));
    }
}
