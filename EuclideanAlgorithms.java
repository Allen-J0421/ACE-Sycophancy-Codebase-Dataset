import java.io.PrintStream;
import java.math.BigInteger;

public final class EuclideanAlgorithms {
    private EuclideanAlgorithms() {
        // Utility class.
    }

    public static BigInteger gcd(long first, long second) {
        return gcd(BigInteger.valueOf(first), BigInteger.valueOf(second));
    }

    public static BigInteger gcd(BigInteger first, BigInteger second) {
        return Operands.of(first, second).gcd();
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        return EuclideanAlgorithmsCli.run(args, out, err);
    }

    static String usageText() {
        return EuclideanAlgorithmsCli.usageText();
    }
}
