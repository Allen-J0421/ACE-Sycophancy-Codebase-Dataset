import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;

public final class EuclideanAlgorithmsTest {
    private static final String USAGE = """
        Usage: java EuclideanAlgorithms [first second]
        Prints the greatest common divisor of two integers.
        When no arguments are provided, defaults to 35 and 15.
        """.stripTrailing();

    private EuclideanAlgorithmsTest() {
    }

    public static void main(String[] args) {
        verifyAlgorithmCases();
        verifyRunCases();
    }

    private static void verifyAlgorithmCases() {
        AlgorithmCase[] cases = {
            new AlgorithmCase(BigInteger.valueOf(35), BigInteger.valueOf(15), BigInteger.valueOf(5), "gcd uses the default sample values"),
            new AlgorithmCase(BigInteger.valueOf(-24), BigInteger.valueOf(18), BigInteger.valueOf(6), "gcd normalizes negative inputs"),
            new AlgorithmCase(BigInteger.ZERO, BigInteger.valueOf(12), BigInteger.valueOf(12), "gcd handles a zero operand"),
            new AlgorithmCase(
                BigInteger.valueOf(Long.MIN_VALUE),
                BigInteger.ZERO,
                new BigInteger("9223372036854775808"),
                "gcd handles the absolute value overflow case"
            ),
            new AlgorithmCase(
                new BigInteger("9223372036854775808"),
                BigInteger.TWO,
                BigInteger.valueOf(2),
                "gcd supports integers larger than long"
            )
        };

        for (AlgorithmCase testCase : cases) {
            assertEquals(
                testCase.expected(),
                EuclideanAlgorithms.gcd(testCase.first(), testCase.second()),
                testCase.scenario()
            );
        }

        assertThrows(() -> EuclideanAlgorithms.gcd(0, 0), "gcd rejects two zero operands");
        assertThrows(() -> EuclideanAlgorithms.gcd((BigInteger) null, BigInteger.ONE), "gcd rejects a null first operand");
        assertThrows(() -> EuclideanAlgorithms.gcd(BigInteger.ONE, (BigInteger) null), "gcd rejects a null second operand");
    }

    private static void verifyRunCases() {
        RunCase[] cases = {
            new RunCase(new String[0], 0, "5\n", "", "run uses default operands"),
            new RunCase(new String[] {"--help"}, 0, USAGE + "\n", "", "run prints usage for help"),
            new RunCase(
                new String[] {"abc", "10"},
                1,
                "",
                "Invalid integer: abc\n" + USAGE + "\n",
                "run reports invalid input"
            ),
            new RunCase(
                new String[] {"1"},
                1,
                "",
                "Expected either zero arguments or exactly two integers.\n" + USAGE + "\n",
                "run rejects partial input"
            ),
            new RunCase(
                new String[] {"9223372036854775808", "2"},
                0,
                "2\n",
                "",
                "run supports integers larger than long"
            )
        };

        for (RunCase testCase : cases) {
            assertRun(testCase);
        }
    }

    private static void assertRun(RunCase testCase) {
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
        int exitCode = EuclideanAlgorithms.run(
            testCase.args(),
            new PrintStream(stdoutBuffer),
            new PrintStream(stderrBuffer)
        );

        assertEquals(testCase.expectedExitCode(), exitCode, testCase.scenario() + ": exit code");
        assertEquals(testCase.expectedStdout(), stdoutBuffer.toString(), testCase.scenario() + ": stdout");
        assertEquals(testCase.expectedStderr(), stderrBuffer.toString(), testCase.scenario() + ": stderr");
    }

    private static void assertThrows(Runnable operation, String scenario) {
        try {
            operation.run();
            throw new AssertionError("Expected IllegalArgumentException: " + scenario);
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertEquals(BigInteger expected, BigInteger actual, String scenario) {
        if (!expected.equals(actual)) {
            throw new AssertionError(scenario + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String scenario) {
        if (expected != actual) {
            throw new AssertionError(scenario + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String scenario) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                scenario + ": expected [" + expected + "] but was [" + actual + "]"
            );
        }
    }

    private record AlgorithmCase(
        BigInteger first,
        BigInteger second,
        BigInteger expected,
        String scenario
    ) {
    }

    private record RunCase(
        String[] args,
        int expectedExitCode,
        String expectedStdout,
        String expectedStderr,
        String scenario
    ) {
    }
}
