public final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {
        // Utility class.
    }

    public static int gcd(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);

        if (x == 0) {
            return toIntExact(y);
        }
        if (y == 0) {
            return toIntExact(x);
        }

        while (y != 0) {
            long remainder = x % y;
            x = y;
            y = remainder;
        }

        return toIntExact(x);
    }

    @Deprecated
    public static int findGCD(int a, int b) {
        return gcd(a, b);
    }

    private static int toIntExact(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("GCD does not fit in an int: " + value);
        }
        return (int) value;
    }
}
