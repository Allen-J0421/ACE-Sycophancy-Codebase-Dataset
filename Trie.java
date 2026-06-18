import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trie {

    private static final int ALPHABET_SIZE = 26;

    private static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
    }

    private enum PruneResult { NOT_FOUND, DELETED_KEEP, DELETED_PRUNE }

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
        if (prune(root, key, 0) == PruneResult.NOT_FOUND) return false;
        wordCount--;
        return true;
    }

    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        collectWords(root, new StringBuilder(), words);
        return words;
    }

    public List<String> getWordsWithPrefix(String prefix) {
        validate(prefix);
        List<String> words = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) {
            collectWords(node, new StringBuilder(prefix), words);
        }
        return words;
    }

    public int size() {
        return wordCount;
    }

    public boolean isEmpty() {
        return wordCount == 0;
    }

    public void clear() {
        Arrays.fill(root.children, null);
        wordCount = 0;
    }

    private TrieNode findNode(String key) {
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            int idx = key.charAt(i) - 'a';
            if (curr.children[idx] == null) return null;
            curr = curr.children[idx];
        }
        return curr;
    }

    private static PruneResult prune(TrieNode node, String key, int depth) {
        if (depth == key.length()) {
            if (!node.isEndOfWord) return PruneResult.NOT_FOUND;
            node.isEndOfWord = false;
            return hasChildren(node) ? PruneResult.DELETED_KEEP : PruneResult.DELETED_PRUNE;
        }
        int idx = key.charAt(depth) - 'a';
        TrieNode child = node.children[idx];
        if (child == null) return PruneResult.NOT_FOUND;
        PruneResult result = prune(child, key, depth + 1);
        if (result == PruneResult.NOT_FOUND) return PruneResult.NOT_FOUND;
        if (result == PruneResult.DELETED_PRUNE) {
            node.children[idx] = null;
            return (!node.isEndOfWord && !hasChildren(node)) ? PruneResult.DELETED_PRUNE : PruneResult.DELETED_KEEP;
        }
        return PruneResult.DELETED_KEEP;
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
        for (String s : new String[]{"and", "ant", "do", "dad", "dance"}) {
            trie.insert(s);
        }
        System.out.println("words: " + trie.getAllWords()); // [and, ant, dad, dance, do]
        System.out.println("size: " + trie.size());         // 5

        System.out.println("prefix 'da': " + trie.getWordsWithPrefix("da")); // [dad, dance]
        System.out.println("prefix 'an': " + trie.getWordsWithPrefix("an")); // [and, ant]
        System.out.println("prefix 'xyz': " + trie.getWordsWithPrefix("xyz")); // []

        trie.delete("do");
        System.out.println("after delete 'do': " + trie.getAllWords()); // [and, ant, dad, dance]

        trie.clear();
        System.out.println("after clear — size: " + trie.size() + ", isEmpty: " + trie.isEmpty()); // 0, true
    }
}
