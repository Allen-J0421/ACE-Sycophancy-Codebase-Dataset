import java.math.BigInteger;

public final class EuclideanAlgorithms {
    private EuclideanAlgorithms() {
        // Utility class.
    }

    public static BigInteger gcd(long first, long second) {
        return gcd(BigInteger.valueOf(first), BigInteger.valueOf(second));
    }

    public static BigInteger gcd(BigInteger first, BigInteger second) {
        return new Operands(first, second).gcd();
    }

    public static void main(String[] args) {
        int exitCode = EuclideanAlgorithmsCli.run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
