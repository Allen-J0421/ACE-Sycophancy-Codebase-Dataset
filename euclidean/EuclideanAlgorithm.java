package euclidean;

public final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {}

    public static int gcd(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);
        if (x == 0) return toIntExact(y);
        if (y == 0) return toIntExact(x);
        while (y != 0) {
            long tmp = x % y;
            x = y;
            y = tmp;
        }
        return toIntExact(x);
    }

    private static int toIntExact(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("Result out of int range: " + value);
        }
        return (int) value;
    }
}
