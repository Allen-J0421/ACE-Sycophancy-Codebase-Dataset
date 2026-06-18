final class TrieTest {
    private static final QueryCase[] SEARCH_CASES = {
        new QueryCase("and", true),
        new QueryCase("ant", true),
        new QueryCase("do", true),
        new QueryCase("an", false),
        new QueryCase("dog", false),
    };

    private static final QueryCase[] PREFIX_CASES = {
        new QueryCase("da", true),
        new QueryCase("de", false),
        new QueryCase("dads", false),
    };

    private static final ThrowCase[] INVALID_INPUT_CASES = {
        new ThrowCase(() -> new Trie().insert("Do"), "Expected uppercase input to be rejected"),
        new ThrowCase(() -> new Trie().search(null), "Expected null input to be rejected"),
        new ThrowCase(() -> new Trie().startsWith("do-"), "Expected punctuation to be rejected"),
    };

    private TrieTest() {
    }

    public static void main(String[] args) {
        testInsertAndSearch();
        testPrefixChecks();
        testMissingAndInvalidInputs();
        System.out.println("All Trie tests passed.");
    }

    private static void testInsertAndSearch() {
        Trie trie = new Trie();
        insertAll(trie, "and", "ant", "do");
        assertQueries(trie::search, SEARCH_CASES);
    }

    private static void testPrefixChecks() {
        Trie trie = new Trie();
        insertAll(trie, "dad", "dart");
        assertQueries(trie::startsWith, PREFIX_CASES);
    }

    private static void testMissingAndInvalidInputs() {
        Trie trie = new Trie();

        expectFalse(trie.search("missing"), "Expected missing word to return false");
        expectFalse(trie.startsWith("missing"), "Expected missing prefix to return false");

        for (ThrowCase testCase : INVALID_INPUT_CASES) {
            expectThrows(IllegalArgumentException.class, testCase.action, testCase.message);
        }
    }

    private static void insertAll(Trie trie, String... words) {
        for (String word : words) {
            trie.insert(word);
        }
    }

    private static void assertQueries(QueryFunction query, QueryCase[] cases) {
        for (QueryCase testCase : cases) {
            boolean actual = query.test(testCase.value);
            if (actual != testCase.expected) {
                throw new AssertionError(
                    "Expected '" + testCase.value + "' to be " + testCase.expected);
            }
        }
    }

    private static void expectTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static void expectFalse(boolean value, String message) {
        if (value) {
            throw new AssertionError(message);
        }
    }

    private static void expectThrows(
        Class<? extends Throwable> expectedType,
        CheckedRunnable action,
        String message) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(
                message + " but got " + thrown.getClass().getSimpleName(),
                thrown);
        }

        throw new AssertionError(message);
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run();
    }

    @FunctionalInterface
    private interface QueryFunction {
        boolean test(String value);
    }

    private static final class QueryCase {
        private final String value;
        private final boolean expected;

        private QueryCase(String value, boolean expected) {
            this.value = value;
            this.expected = expected;
        }
    }

    private static final class ThrowCase {
        private final CheckedRunnable action;
        private final String message;

        private ThrowCase(CheckedRunnable action, String message) {
            this.action = action;
            this.message = message;
        }
    }
}
