package euclidean;

public final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {}

    public static GcdProvider iterative() {
        return EuclideanAlgorithm::iterativeGcd;
    }

    public static GcdProvider recursive() {
        return EuclideanAlgorithm::recursiveGcd;
    }

    private static Result<Integer, GcdError> iterativeGcd(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);
        while (y != 0) {
            long tmp = x % y;
            x = y;
            y = tmp;
        }
        return toResult(x);
    }

    private static Result<Integer, GcdError> recursiveGcd(int a, int b) {
        return recursiveGcd(Math.abs((long) a), Math.abs((long) b));
    }

    private static Result<Integer, GcdError> recursiveGcd(long x, long y) {
        return y == 0 ? toResult(x) : recursiveGcd(y, x % y);
    }

    private static Result<Integer, GcdError> toResult(long value) {
        if (value > Integer.MAX_VALUE) {
            return Result.failure(new GcdError("Result out of int range: " + value));
        }
        return Result.success((int) value);
    }
}
