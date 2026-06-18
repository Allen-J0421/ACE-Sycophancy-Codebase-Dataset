public class Trie {

    private static final int ALPHABET_SIZE = 26;

    private static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        validate(key);
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }
            curr = curr.children[idx];
        }
        curr.isEndOfWord = true;
    }

    public boolean search(String key) {
        TrieNode node = findNode(key);
        return node != null && node.isEndOfWord;
    }

    public boolean isPrefix(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String key) {
        validate(key);
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) {
                return null;
            }
            curr = curr.children[idx];
        }
        return curr;
    }

    private static void validate(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must be non-null and non-empty");
        }
        for (char c : key.toCharArray()) {
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException("Key must contain only lowercase letters: " + key);
            }
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        for (String s : new String[]{"and", "ant", "do", "dad"}) {
            trie.insert(s);
        }

        for (String s : new String[]{"do", "gee", "bat"}) {
            System.out.print(trie.search(s) + " ");
        }
        System.out.println();

        for (String s : new String[]{"ge", "ba", "do", "de"}) {
            System.out.print(trie.isPrefix(s) + " ");
        }
        System.out.println();
    }
}
