import java.util.Arrays;

public class PrefixSum {

    public static int[] prefSum(int[] arr) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i] + (i > 0 ? result[i - 1] : 0);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        System.out.println(Arrays.toString(prefSum(arr)));
    }
}
