import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class EuclideanAlgorithmsTest {
    private EuclideanAlgorithmsTest() {
    }

    public static void main(String[] args) {
        assertEquals(5L, EuclideanAlgorithms.gcd(35, 15), "gcd uses the default sample values");
        assertEquals(6L, EuclideanAlgorithms.gcd(-24, 18), "gcd normalizes negative inputs");
        assertEquals(12L, EuclideanAlgorithms.gcd(0, 12), "gcd handles a zero operand");
        assertThrows(() -> EuclideanAlgorithms.gcd(0, 0), "gcd rejects two zero operands");

        assertRun(new String[0], 0, "5\n", "", "run uses default operands");
        assertRun(new String[] {"--help"}, 0, expectedUsage() + "\n", "", "run prints usage for help");
        assertRun(
            new String[] {"abc", "10"},
            1,
            "",
            "Invalid integer: abc\n" + expectedUsage() + "\n",
            "run reports invalid input"
        );
        assertRun(
            new String[] {"1"},
            1,
            "",
            "Expected either zero arguments or exactly two integers.\n" + expectedUsage() + "\n",
            "run rejects partial input"
        );
    }

    private static void assertRun(
        String[] args,
        int expectedExitCode,
        String expectedStdout,
        String expectedStderr,
        String scenario
    ) {
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
        int exitCode = EuclideanAlgorithms.run(
            args,
            new PrintStream(stdoutBuffer),
            new PrintStream(stderrBuffer)
        );

        assertEquals(expectedExitCode, exitCode, scenario + ": exit code");
        assertEquals(expectedStdout, stdoutBuffer.toString(), scenario + ": stdout");
        assertEquals(expectedStderr, stderrBuffer.toString(), scenario + ": stderr");
    }

    private static void assertThrows(Runnable operation, String scenario) {
        try {
            operation.run();
            throw new AssertionError("Expected IllegalArgumentException: " + scenario);
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertEquals(long expected, long actual, String scenario) {
        if (expected != actual) {
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

    private static String expectedUsage() {
        return "Usage: java EuclideanAlgorithms [first second]\n"
            + "Prints the greatest common divisor of two integers.\n"
            + "When no arguments are provided, defaults to 35 and 15.";
    }
}
