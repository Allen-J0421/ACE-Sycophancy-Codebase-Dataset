import java.util.function.Predicate;

final class TrieDemo {
    private static final String[] WORDS = {"and", "ant", "do", "dad"};
    private static final String[] SEARCH_KEYS = {"do", "gee", "bat"};
    private static final String[] PREFIX_KEYS = {"ge", "ba", "do", "de"};

    private TrieDemo() {
    }

    public static void main(String[] args) {
        Trie trie = buildTrie(WORDS);

        printResults(SEARCH_KEYS, trie::contains);
        System.out.println();
        printResults(PREFIX_KEYS, trie::startsWith);
    }

    private static Trie buildTrie(String[] words) {
        Trie trie = new Trie();
        trie.insertAll(words);
        return trie;
    }

    private static void printResults(String[] keys, Predicate<String> lookup) {
        for (String key : keys) {
            System.out.print(lookup.test(key) + " ");
        }
    }
}
