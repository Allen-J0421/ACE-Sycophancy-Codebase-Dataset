import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public final class CompatibilityTest {
    private static final String DEMO_OUTPUT = "0 9 12" + System.lineSeparator();

    private CompatibilityTest() {
    }

    public static void main(String[] args) {
        assertFacadeSearch();
        assertMainOutput("KMPSearch main", () -> KMPSearch.main(new String[0]));
        assertMainOutput("legacy launcher", () -> kmp_pattern_searching.main(new String[0]));
    }

    private static void assertFacadeSearch() {
        List<Integer> actual = KMPSearch.search("aba", "ababa");
        List<Integer> expected = Arrays.asList(0, 2);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected facade matches " + expected + ", but got " + actual + ".");
        }
    }

    private static void assertMainOutput(String scenario, Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        String actual = output.toString();
        if (!DEMO_OUTPUT.equals(actual)) {
            throw new AssertionError("Expected " + scenario + " to print '" + DEMO_OUTPUT + "', but got '"
                    + actual + "'.");
        }
    }
}
