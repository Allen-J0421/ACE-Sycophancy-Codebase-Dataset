import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Minimal test runner that snapshots registered tests before execution.
 *
 * <p>Registration is synchronized, and each run uses its own immutable execution
 * snapshot and local counters. This keeps the runner predictable if a suite is
 * executed more than once or from multiple threads after registration.</p>
 */
final class TestRunner {
    private final String suiteName;
    private final List<TestCase> tests = new ArrayList<>();

    TestRunner(String suiteName) {
        this.suiteName = Objects.requireNonNull(suiteName, "suiteName must not be null");
    }

    synchronized TestRunner add(String name, TestAssertions.ThrowingRunnable test) {
        tests.add(new TestCase(
                Objects.requireNonNull(name, "name must not be null"),
                Objects.requireNonNull(test, "test must not be null")));
        return this;
    }

    void run() {
        List<TestCase> testsToRun = snapshotTests();
        long startedAt = System.nanoTime();
        int passedTests = 0;

        for (TestCase test : testsToRun) {
            try {
                test.run();
                passedTests++;
            } catch (AssertionError error) {
                throw new AssertionError(
                        "Test failed: " + test.name() + System.lineSeparator() + error.getMessage(),
                        error);
            } catch (Throwable error) {
                throw new AssertionError("Test errored: " + test.name(), error);
            }
        }

        double elapsedMilliseconds = (System.nanoTime() - startedAt) / 1_000_000.0;
        System.out.printf("%d %s tests passed in %.2f ms.%n", passedTests, suiteName, elapsedMilliseconds);
    }

    private synchronized List<TestCase> snapshotTests() {
        return new ArrayList<>(tests);
    }

    private static final class TestCase {
        private final String name;
        private final TestAssertions.ThrowingRunnable test;

        private TestCase(String name, TestAssertions.ThrowingRunnable test) {
            this.name = name;
            this.test = test;
        }

        private String name() {
            return name;
        }

        private void run() throws Throwable {
            test.run();
        }
    }
}
