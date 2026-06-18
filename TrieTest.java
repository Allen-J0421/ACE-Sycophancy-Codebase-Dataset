final class TrieTest {
    private TrieTest() {
    }

    public static void main(String[] args) {
        containsCompleteWords();
        detectsPrefixes();
        rejectsUnsupportedInput();
    }

    private static void containsCompleteWords() {
        Trie trie = sampleTrie();

        assertTrue(trie.contains("do"), "contains finds inserted words");
        assertTrue(trie.search("and"), "search remains a contains alias");
        assertFalse(trie.contains("gee"), "contains rejects missing words");
        assertFalse(trie.contains("an"), "contains requires a complete word");
    }

    private static void detectsPrefixes() {
        Trie trie = sampleTrie();

        assertTrue(trie.startsWith("an"), "startsWith finds inserted prefixes");
        assertTrue(trie.isPrefix("do"), "isPrefix remains a startsWith alias");
        assertFalse(trie.startsWith("de"), "startsWith rejects missing prefixes");
    }

    private static void rejectsUnsupportedInput() {
        Trie trie = new Trie();

        assertThrows(
            IllegalArgumentException.class,
            () -> trie.insert("Dad"),
            "insert rejects uppercase letters");
        assertThrows(
            NullPointerException.class,
            () -> trie.contains(null),
            "contains rejects null words");
    }

    private static Trie sampleTrie() {
        Trie trie = new Trie();
        trie.insertAll("and", "ant", "do", "dad");
        return trie;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertThrows(
        Class<? extends Throwable> expectedType,
        ThrowingRunnable action,
        String message) {

        try {
            action.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }
            throw new AssertionError(message + ": expected " + expectedType.getSimpleName()
                + " but got " + error.getClass().getSimpleName(), error);
        }

        throw new AssertionError(message + ": expected " + expectedType.getSimpleName());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
