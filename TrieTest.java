final class TrieTest {
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
        trie.insert("and");
        trie.insert("ant");
        trie.insert("do");

        expectTrue(trie.search("and"), "Expected 'and' to be found");
        expectTrue(trie.search("ant"), "Expected 'ant' to be found");
        expectTrue(trie.search("do"), "Expected 'do' to be found");
        expectFalse(trie.search("an"), "Expected 'an' not to be found");
        expectFalse(trie.search("dog"), "Expected 'dog' not to be found");
    }

    private static void testPrefixChecks() {
        Trie trie = new Trie();
        trie.insert("dad");
        trie.insert("dart");

        expectTrue(trie.startsWith("da"), "Expected 'da' to be a prefix");
        expectFalse(trie.startsWith("de"), "Expected 'de' not to be a prefix");
        expectFalse(trie.startsWith("dads"), "Expected 'dads' not to be a prefix");
    }

    private static void testMissingAndInvalidInputs() {
        Trie trie = new Trie();

        expectFalse(trie.search("missing"), "Expected missing word to return false");
        expectFalse(trie.startsWith("missing"), "Expected missing prefix to return false");

        expectThrows(
            IllegalArgumentException.class,
            () -> trie.insert("Do"),
            "Expected uppercase input to be rejected");
        expectThrows(
            IllegalArgumentException.class,
            () -> trie.search(null),
            "Expected null input to be rejected");
        expectThrows(
            IllegalArgumentException.class,
            () -> trie.startsWith("do-"),
            "Expected punctuation to be rejected");
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
}
