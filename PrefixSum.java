import java.util.Arrays;

public class PrefixSum implements RangeSumQuery {
    private final int[] prefix;

    public static PrefixSum of(int[] arr) {
        return new PrefixSum(arr);
    }

    private PrefixSum(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("Input array must not be null");
        if (arr.length == 0) throw new IllegalArgumentException("Input array must not be empty");
        prefix = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            prefix[i] = arr[i] + (i > 0 ? prefix[i - 1] : 0);
        }
    }

    @Override
    public int rangeSum(int l, int r) {
        if (l < 0 || r >= prefix.length) throw new IndexOutOfBoundsException(
                "Range [" + l + ", " + r + "] out of bounds for length " + prefix.length);
        if (l > r) throw new IllegalArgumentException("l (" + l + ") must not exceed r (" + r + ")");
        return prefix[r] - (l > 0 ? prefix[l - 1] : 0);
    }

    @Override
    public int[] toArray() {
        return Arrays.copyOf(prefix, prefix.length);
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        RangeSumQuery ps = PrefixSum.of(arr);
        System.out.println(Arrays.toString(ps.toArray()));
        System.out.println(ps.rangeSum(1, 3)); // 35: arr[1]+arr[2]+arr[3]
    }
}
