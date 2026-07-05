import java.util.Arrays;

public class PrefixSum {
    private final int[] prefix;

    public PrefixSum(int[] arr) {
        prefix = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            prefix[i] = arr[i] + (i > 0 ? prefix[i - 1] : 0);
        }
    }

    public int rangeSum(int l, int r) {
        return prefix[r] - (l > 0 ? prefix[l - 1] : 0);
    }

    public int[] toArray() {
        return Arrays.copyOf(prefix, prefix.length);
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        PrefixSum ps = new PrefixSum(arr);
        System.out.println(Arrays.toString(ps.toArray()));
        System.out.println(ps.rangeSum(1, 3)); // 35: arr[1]+arr[2]+arr[3]
    }
}
