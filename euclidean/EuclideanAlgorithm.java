package euclidean;

public final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {}

    public static GcdProvider iterative() {
        return EuclideanAlgorithm::iterativeGcd;
    }

    public static GcdProvider recursive() {
        return EuclideanAlgorithm::recursiveGcd;
    }

    private static GcdResult iterativeGcd(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);
        while (y != 0) {
            long tmp = x % y;
            x = y;
            y = tmp;
        }
        return toResult(x);
    }

    private static GcdResult recursiveGcd(int a, int b) {
        return recursiveGcd(Math.abs((long) a), Math.abs((long) b));
    }

    private static GcdResult recursiveGcd(long x, long y) {
        return y == 0 ? toResult(x) : recursiveGcd(y, x % y);
    }

    private static GcdResult toResult(long value) {
        if (value > Integer.MAX_VALUE) {
            return new GcdResult.Failure("Result out of int range: " + value);
        }
        return new GcdResult.Success((int) value);
    }
}
