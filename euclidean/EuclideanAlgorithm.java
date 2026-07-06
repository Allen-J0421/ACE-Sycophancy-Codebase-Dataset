package euclidean;

public final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {}

    public static GcdProvider iterative() {
        return EuclideanAlgorithm::iterativeGcd;
    }

    public static GcdProvider recursive() {
        return EuclideanAlgorithm::recursiveGcd;
    }

    private static int iterativeGcd(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);
        while (y != 0) {
            long tmp = x % y;
            x = y;
            y = tmp;
        }
        return toIntExact(x);
    }

    private static int recursiveGcd(int a, int b) {
        return recursiveGcd(Math.abs((long) a), Math.abs((long) b));
    }

    private static int recursiveGcd(long x, long y) {
        return y == 0 ? toIntExact(x) : recursiveGcd(y, x % y);
    }

    private static int toIntExact(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("Result out of int range: " + value);
        }
        return (int) value;
    }
}
