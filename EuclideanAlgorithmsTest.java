import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;

public final class EuclideanAlgorithmsTest {
    private EuclideanAlgorithmsTest() {
    }

    public static void main(String[] args) {
        verifyAlgorithmCases();
        verifyRunCases();
    }

    private static void verifyAlgorithmCases() {
        AlgorithmCase[] cases = {
            algorithmCase(35, 15, 5, "gcd uses the default sample values"),
            algorithmCase(-24, 18, 6, "gcd normalizes negative inputs"),
            new AlgorithmCase(BigInteger.ZERO, BigInteger.valueOf(12), BigInteger.valueOf(12), "gcd handles a zero operand"),
            new AlgorithmCase(
                BigInteger.valueOf(Long.MIN_VALUE),
                BigInteger.ZERO,
                big("9223372036854775808"),
                "gcd handles the absolute value overflow case"
            ),
            new AlgorithmCase(
                big("9223372036854775808"),
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
        String usage = EuclideanAlgorithms.usageText();
        String usageOutput = usage + "\n";
        RunCase[] cases = {
            runCase(new String[0], 0, "5\n", "", "run uses default operands"),
            runCase(new String[] {"--help"}, 0, usageOutput, "", "run prints usage for --help"),
            runCase(new String[] {"-h"}, 0, usageOutput, "", "run prints usage for -h"),
            runCase(
                new String[] {"abc", "10"},
                1,
                "",
                "Invalid integer: abc\n" + usageOutput,
                "run reports invalid input"
            ),
            runCase(
                new String[] {"1"},
                1,
                "",
                "Expected either zero arguments or exactly two integers.\n" + usageOutput,
                "run rejects partial input"
            ),
            runCase(
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

    private static AlgorithmCase algorithmCase(long first, long second, long expected, String scenario) {
        return new AlgorithmCase(
            BigInteger.valueOf(first),
            BigInteger.valueOf(second),
            BigInteger.valueOf(expected),
            scenario
        );
    }

    private static BigInteger big(String value) {
        return new BigInteger(value);
    }

    private static RunCase runCase(
        String[] args,
        int expectedExitCode,
        String expectedStdout,
        String expectedStderr,
        String scenario
    ) {
        return new RunCase(args, expectedExitCode, expectedStdout, expectedStderr, scenario);
    }

    private static void assertRun(RunCase testCase) {
        RunResult result = captureRun(testCase.args());

        assertEquals(testCase.expectedExitCode(), result.exitCode(), testCase.scenario() + ": exit code");
        assertEquals(testCase.expectedStdout(), result.stdout(), testCase.scenario() + ": stdout");
        assertEquals(testCase.expectedStderr(), result.stderr(), testCase.scenario() + ": stderr");
    }

    private static RunResult captureRun(String[] args) {
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
        int exitCode = EuclideanAlgorithms.run(
            args,
            new PrintStream(stdoutBuffer),
            new PrintStream(stderrBuffer)
        );

        return new RunResult(exitCode, stdoutBuffer.toString(), stderrBuffer.toString());
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

    private record RunResult(int exitCode, String stdout, String stderr) {
    }
}
