import java.util.ArrayList;
import java.util.List;

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
        if (prune(root, key, 0) < 0) return false;
        wordCount--;
        return true;
    }

    public int size() {
        return wordCount;
    }

    public boolean isEmpty() {
        return wordCount == 0;
    }

    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        collectWords(root, new StringBuilder(), words);
        return words;
    }

    private static TrieNode findNode(TrieNode root, String key) {
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            int idx = key.charAt(i) - 'a';
            if (curr.children[idx] == null) return null;
            curr = curr.children[idx];
        }
        return curr;
    }

    private TrieNode findNode(String key) {
        return findNode(root, key);
    }

    // Returns <0 if key not found, 0 if deleted (node kept), >0 if deleted (node prunable).
    private static int prune(TrieNode node, String key, int depth) {
        if (depth == key.length()) {
            if (!node.isEndOfWord) return -1;
            node.isEndOfWord = false;
            return hasChildren(node) ? 0 : 1;
        }
        int idx = key.charAt(depth) - 'a';
        TrieNode child = node.children[idx];
        if (child == null) return -1;
        int result = prune(child, key, depth + 1);
        if (result < 0) return -1;
        if (result > 0) {
            node.children[idx] = null;
            return (!node.isEndOfWord && !hasChildren(node)) ? 1 : 0;
        }
        return 0;
    }

    private static boolean hasChildren(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return true;
        }
        return false;
    }

    private static void collectWords(TrieNode node, StringBuilder prefix, List<String> words) {
        if (node.isEndOfWord) {
            words.add(prefix.toString());
        }
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (node.children[i] != null) {
                prefix.append((char) ('a' + i));
                collectWords(node.children[i], prefix, words);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
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
}

class TrieDemo {
    public static void main(String[] args) {
        Trie trie = new Trie();
        for (String s : new String[]{"and", "ant", "do", "dad"}) {
            trie.insert(s);
        }
        System.out.println("words: " + trie.getAllWords()); // [and, ant, dad, do]
        System.out.println("size: " + trie.size());         // 4

        System.out.println("search 'do': "    + trie.search("do"));    // true
        System.out.println("search 'gee': "   + trie.search("gee"));   // false
        System.out.println("isPrefix 'an': "  + trie.isPrefix("an"));  // true
        System.out.println("isPrefix 'ba': "  + trie.isPrefix("ba"));  // false

        trie.delete("do");
        System.out.println("after delete 'do': " + trie.getAllWords()); // [and, ant, dad]
        System.out.println("size: "              + trie.size());        // 3
        System.out.println("isPrefix 'do': "     + trie.isPrefix("do")); // false — node pruned
        System.out.println("isPrefix 'da': "     + trie.isPrefix("da")); // true  — 'dad' intact
    }
}
