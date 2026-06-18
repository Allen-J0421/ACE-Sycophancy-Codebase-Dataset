import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Trie implements Iterable<String> {

    private static final int ALPHABET_SIZE = 26;

    private static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
    }

    // -1 sentinel means "first visit: check isEndOfWord before scanning children"
    private static class Frame {
        final TrieNode node;
        int nextChild;
        Frame(TrieNode node) { this.node = node; this.nextChild = -1; }
    }

    private static class TrieIterator implements Iterator<String> {
        private final Deque<Frame> stack = new ArrayDeque<>();
        private final StringBuilder prefix = new StringBuilder();
        private String next;

        TrieIterator(TrieNode root) {
            stack.push(new Frame(root));
            next = findNext();
        }

        private String findNext() {
            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                if (frame.nextChild == -1) {
                    frame.nextChild = 0;
                    if (frame.node.isEndOfWord) return prefix.toString();
                }
                int found = -1;
                while (frame.nextChild < ALPHABET_SIZE) {
                    if (frame.node.children[frame.nextChild] != null) {
                        found = frame.nextChild++;
                        break;
                    }
                    frame.nextChild++;
                }
                if (found >= 0) {
                    prefix.append((char) ('a' + found));
                    stack.push(new Frame(frame.node.children[found]));
                } else {
                    stack.pop();
                    if (prefix.length() > 0) prefix.deleteCharAt(prefix.length() - 1);
                }
            }
            return null;
        }

        @Override public boolean hasNext() { return next != null; }

        @Override public String next() {
            if (next == null) throw new NoSuchElementException();
            String result = next;
            next = findNext();
            return result;
        }
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

    /** Returns the longest word in the trie that is a prefix of {@code query}, or null if none. */
    public String longestPrefix(String query) {
        validate(query);
        TrieNode curr = root;
        int lastWordEnd = -1;
        for (int i = 0; i < query.length(); i++) {
            int idx = query.charAt(i) - 'a';
            if (curr.children[idx] == null) break;
            curr = curr.children[idx];
            if (curr.isEndOfWord) lastWordEnd = i;
        }
        return lastWordEnd >= 0 ? query.substring(0, lastWordEnd + 1) : null;
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

    @Override
    public Iterator<String> iterator() {
        return new TrieIterator(root);
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

        System.out.print("for-each: ");
        for (String word : trie) System.out.print(word + " ");
        System.out.println(); // and ant dad dance do

        System.out.println("prefix 'da': " + trie.getWordsWithPrefix("da")); // [dad, dance]

        System.out.println("longestPrefix 'dancer': " + trie.longestPrefix("dancer")); // dance
        System.out.println("longestPrefix 'ants':   " + trie.longestPrefix("ants"));   // ant
        System.out.println("longestPrefix 'xyz':    " + trie.longestPrefix("xyz"));    // null

        trie.delete("do");
        System.out.println("after delete 'do': " + trie.getAllWords()); // [and, ant, dad, dance]

        trie.clear();
        System.out.println("after clear — isEmpty: " + trie.isEmpty()); // true
    }
}
