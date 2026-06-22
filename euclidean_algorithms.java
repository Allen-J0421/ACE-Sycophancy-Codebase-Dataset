/**
 * Euclidean algorithm utilities for computing the greatest common divisor (GCD)
 * and least common multiple (LCM) of two integers.
 *
 * <p>This is a stateless utility class; all methods are static and it cannot be
 * instantiated. GCD is computed with the classic Euclidean algorithm, defined
 * for negative inputs via their absolute values. By convention {@code gcd(0, 0)}
 * returns {@code 0}.
 */
final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {
        // Utility class: prevent instantiation.
    }

    /**
     * Returns the absolute value of {@code value}, throwing rather than silently
     * returning a negative result for {@link Long#MIN_VALUE} (whose magnitude,
     * 2<sup>63</sup>, is not representable as a positive {@code long}).
     */
    private static long absExact(long value) {
        if (value == Long.MIN_VALUE) {
            throw new ArithmeticException("absolute value of " + value + " overflows long");
        }
        return Math.abs(value);
    }

    /**
     * Returns the greatest common divisor of {@code a} and {@code b} using the
     * iterative Euclidean algorithm. The result is always non-negative.
     *
     * <p>The iterative form is preferred over recursion to avoid growing the call
     * stack for inputs that require many division steps.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the non-negative GCD; {@code 0} when both arguments are {@code 0}
     * @throws ArithmeticException if the GCD is 2<sup>63</sup> (i.e. when both
     *     arguments are {@link Long#MIN_VALUE}, or one is and the other is
     *     {@code 0}), which is not representable as a positive {@code long}
     */
    static long gcd(long a, long b) {
        // Java's % keeps the sign of the dividend, so the algorithm converges to
        // ±gcd without abs-ing the inputs (which would overflow on MIN_VALUE).
        while (b != 0) {
            long remainder = a % b;
            a = b;
            b = remainder;
        }
        return absExact(a);
    }

    /**
     * Returns the greatest common divisor using a recursive formulation, provided
     * as an equivalent alternative to {@link #gcd(long, long)}.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the non-negative GCD; {@code 0} when both arguments are {@code 0}
     * @throws ArithmeticException if the GCD is 2<sup>63</sup>, which is not
     *     representable as a positive {@code long}
     */
    static long gcdRecursive(long a, long b) {
        if (b == 0) {
            return absExact(a);
        }
        return gcdRecursive(b, a % b);
    }

    /**
     * Returns the least common multiple of {@code a} and {@code b}. The result is
     * always non-negative and is {@code 0} when either argument is {@code 0}.
     *
     * <p>The division is performed before the multiplication ({@code a / gcd * b})
     * to reduce the chance of intermediate overflow.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the non-negative LCM
     * @throws ArithmeticException if the result overflows a {@code long}
     */
    static long lcm(long a, long b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        long divisor = gcd(a, b);
        return absExact(Math.multiplyExact(a / divisor, b));
    }

    /**
     * Computes the GCD of two integers supplied as command-line arguments,
     * defaulting to {@code 35} and {@code 15} when none are given.
     *
     * @param args optionally two integers whose GCD and LCM are printed
     */
    public static void main(String[] args) {
        long a = 35;
        long b = 15;
        if (args.length == 2) {
            a = Long.parseLong(args[0]);
            b = Long.parseLong(args[1]);
        } else if (args.length != 0) {
            System.err.println("Usage: java EuclideanAlgorithm [<a> <b>]");
            return;
        }

        System.out.println("gcd(" + a + ", " + b + ") = " + gcd(a, b));
        System.out.println("lcm(" + a + ", " + b + ") = " + lcm(a, b));
    }
}
