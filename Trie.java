public class Trie {

    private static final int ALPHABET_SIZE = 26;

    private static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
    }

    private final TrieNode root = new TrieNode();
    private int wordCount;

    public void insert(String key) {
        validate(key);
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            int idx = key.charAt(i) - 'a';
            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }
            curr = curr.children[idx];
        }
        if (!curr.isEndOfWord) {
            curr.isEndOfWord = true;
            wordCount++;
        }
    }

    public boolean search(String key) {
        validate(key);
        TrieNode node = findNode(key);
        return node != null && node.isEndOfWord;
    }

    public boolean isPrefix(String prefix) {
        validate(prefix);
        return findNode(prefix) != null;
    }

    public boolean delete(String key) {
        validate(key);
        TrieNode endNode = findNode(key);
        if (endNode == null || !endNode.isEndOfWord) {
            return false;
        }
        prune(root, key, 0);
        wordCount--;
        return true;
    }

    public int size() {
        return wordCount;
    }

    private TrieNode findNode(String key) {
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            int idx = key.charAt(i) - 'a';
            if (curr.children[idx] == null) {
                return null;
            }
            curr = curr.children[idx];
        }
        return curr;
    }

    // Returns true if the node can be pruned (leaf with no other word terminating here).
    private boolean prune(TrieNode node, String key, int depth) {
        if (depth == key.length()) {
            node.isEndOfWord = false;
            return !hasChildren(node);
        }
        int idx = key.charAt(depth) - 'a';
        if (prune(node.children[idx], key, depth + 1)) {
            node.children[idx] = null;
            return !node.isEndOfWord && !hasChildren(node);
        }
        return false;
    }

    private static boolean hasChildren(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return true;
        }
        return false;
    }

    private static void validate(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must be non-null and non-empty");
        }
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
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
        System.out.println("size: " + trie.size()); // 4

        for (String s : new String[]{"do", "gee", "bat"}) {
            System.out.print(trie.search(s) + " ");  // true false false
        }
        System.out.println();

        for (String s : new String[]{"ge", "ba", "do", "de"}) {
            System.out.print(trie.isPrefix(s) + " "); // false false true false
        }
        System.out.println();

        trie.delete("do");
        System.out.println("size after delete: " + trie.size()); // 3
        System.out.println("search 'do': " + trie.search("do")); // false
        System.out.println("prefix 'do': " + trie.isPrefix("do")); // false — node pruned
        System.out.println("prefix 'da': " + trie.isPrefix("da")); // true — 'dad' still present
    }
}
