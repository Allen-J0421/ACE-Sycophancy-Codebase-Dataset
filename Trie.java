import java.util.Objects;

public final class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final char FIRST_LETTER = 'a';
    private static final char LAST_LETTER = 'z';

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        TrieNode current = root;

        for (char letter : requireLowercaseKey(key).toCharArray()) {
            int index = toIndex(letter);
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }

        current.isWord = true;
    }

    public boolean search(String key) {
        TrieNode node = findNode(key);
        return node != null && node.isWord;
    }

    public boolean isPrefix(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String key) {
        TrieNode current = root;

        for (char letter : requireLowercaseKey(key).toCharArray()) {
            current = current.children[toIndex(letter)];
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private static String requireLowercaseKey(String key) {
        Objects.requireNonNull(key, "key must not be null");

        for (char letter : key.toCharArray()) {
            if (letter < FIRST_LETTER || letter > LAST_LETTER) {
                throw new IllegalArgumentException(
                    "Trie keys may only contain lowercase letters a-z: " + key);
            }
        }

        return key;
    }

    private static int toIndex(char letter) {
        return letter - FIRST_LETTER;
    }

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isWord;
    }
}
