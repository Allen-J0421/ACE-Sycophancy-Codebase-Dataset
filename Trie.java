public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final String LOWERCASE_ONLY_MESSAGE =
        "Trie only supports lowercase letters a-z: '";

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = walk(word, true);
        node.isWord = true;
    }

    public boolean search(String word) {
        TrieNode node = walk(word, false);
        return node != null && node.isWord;
    }

    public boolean isPrefix(String prefix) {
        return startsWith(prefix);
    }

    public boolean startsWith(String prefix) {
        return walk(prefix, false) != null;
    }

    private TrieNode walk(String text, boolean createMissing) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            int index = toIndex(text.charAt(i));
            TrieNode next = node.children[index];
            if (next == null) {
                if (!createMissing) {
                    return null;
                }
                next = new TrieNode();
                node.children[index] = next;
            }
            node = next;
        }
        return node;
    }

    private static int toIndex(char c) {
        if (c < 'a' || c > 'z') {
            throw new IllegalArgumentException(LOWERCASE_ONLY_MESSAGE + c + "'");
        }
        return c - 'a';
    }

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isWord;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.loadWords("and", "ant", "do", "dad");
        trie.printLookups("do", "gee", "bat");
        trie.printPrefixes("ge", "ba", "do", "de");
    }

    private void loadWords(String... words) {
        for (String word : words) {
            insert(word);
        }
    }

    private void printLookups(String... keys) {
        printResults(keys, this::search);
    }

    private void printPrefixes(String... prefixes) {
        printResults(prefixes, this::startsWith);
    }

    private void printResults(String[] values, Lookup lookup) {
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
