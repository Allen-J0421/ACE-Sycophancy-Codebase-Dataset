/**
 * A prefix tree (trie) over lowercase ASCII words ('a'–'z').
 *
 * <p>Supports inserting words, exact-word lookup, and prefix lookup, each in
 * O(L) time where L is the length of the key.
 *
 * <p>Declared package-private so the source can live in a standalone file whose
 * name need not match the class (a {@code public} top-level class would have to
 * be in {@code Trie.java}).
 */
class Trie {

    /** Number of distinct characters supported: 'a' through 'z'. */
    private static final int ALPHABET_SIZE = 26;

    /** First character of the supported range; used to map a char to an index. */
    private static final char FIRST_LETTER = 'a';

    private static final class TrieNode {
        final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
    }

    private final TrieNode root = new TrieNode();

    /**
     * Inserts a word into the trie, creating nodes as needed.
     *
     * @param word the word to insert; must be non-empty and contain only 'a'–'z'
     * @throws IllegalArgumentException if {@code word} is null, empty, or holds
     *                                  an unsupported character
     */
    public void insert(String word) {
        requireValidKey(word);
        TrieNode curr = root;
        for (int i = 0; i < word.length(); i++) {
            int index = indexOf(word.charAt(i));
            if (curr.children[index] == null) {
                curr.children[index] = new TrieNode();
            }
            curr = curr.children[index];
        }
        curr.isEndOfWord = true;
    }

    /**
     * Returns whether the exact word was previously inserted.
     *
     * @param word the word to look up
     * @return true if the word exists in the trie; false otherwise (including for
     *         null, empty, or out-of-range input)
     */
    public boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Returns whether any inserted word starts with the given prefix.
     *
     * @param prefix the prefix to look up
     * @return true if at least one word has this prefix; false otherwise
     *         (including for null, empty, or out-of-range input)
     */
    public boolean isPrefix(String prefix) {
        return traverse(prefix) != null;
    }

    /**
     * Walks the trie along {@code key} and returns the node reached, or null if
     * the path does not exist or {@code key} is null/empty/out-of-range. Shared
     * by {@link #search} and {@link #isPrefix}.
     */
    private TrieNode traverse(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!isSupported(c)) {
                return null;
            }
            curr = curr.children[indexOf(c)];
            if (curr == null) {
                return null;
            }
        }
        return curr;
    }

    private static void requireValidKey(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("word must be non-empty");
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!isSupported(c)) {
                throw new IllegalArgumentException(
                    "word must contain only 'a'-'z', found: '" + c + "'");
            }
        }
    }

    private static boolean isSupported(char c) {
        return c >= FIRST_LETTER && c < FIRST_LETTER + ALPHABET_SIZE;
    }

    private static int indexOf(char c) {
        return c - FIRST_LETTER;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        for (String word : new String[] {"and", "ant", "do", "dad"}) {
            trie.insert(word);
        }

        System.out.println("search:");
        for (String key : new String[] {"do", "gee", "bat"}) {
            System.out.println("  " + key + " -> " + trie.search(key));
        }

        System.out.println("isPrefix:");
        for (String key : new String[] {"ge", "ba", "do", "de"}) {
            System.out.println("  " + key + " -> " + trie.isPrefix(key));
        }
    }
}
