final class TrieDemo {
    private TrieDemo() {
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        loadWords(trie, "and", "ant", "do", "dad");
        printLookups(trie, "do", "gee", "bat");
        printPrefixes(trie, "ge", "ba", "do", "de");
    }

    private static void loadWords(Trie trie, String... words) {
        for (String word : words) {
            trie.insert(word);
        }
    }

    private static void printLookups(Trie trie, String... keys) {
        printResults(keys, trie::search);
    }

    private static void printPrefixes(Trie trie, String... prefixes) {
        printResults(prefixes, trie::startsWith);
    }

    private static void printResults(String[] values, Lookup lookup) {
        for (String value : values) {
            System.out.print(lookup.test(value) + " ");
        }
        System.out.println();
    }

    @FunctionalInterface
    private interface Lookup {
        boolean test(String value);
    }
}
