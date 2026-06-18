import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Trie implements Iterable<String> {

    private static final int ALPHABET_SIZE = 26;

    private static class TrieNode {
        TrieNode[] children; // null until first child is added
        boolean isEndOfWord;

        TrieNode child(int idx) {
            return children == null ? null : children[idx];
        }

        // Creates the child at idx if absent and returns it.
        TrieNode addChild(int idx) {
            if (children == null) children = new TrieNode[ALPHABET_SIZE];
            if (children[idx] == null) children[idx] = new TrieNode();
            return children[idx];
        }

        // Caller guarantees children != null (child was reachable).
        void removeChild(int idx) {
            children[idx] = null;
        }

        boolean hasChildren() {
            if (children == null) return false;
            for (TrieNode c : children) {
                if (c != null) return true;
            }
            return false;
        }

        void clearChildren() {
            children = null;
        }
    }

    // -1 sentinel means "first visit: check isEndOfWord before scanning children"
    private static class Frame {
        final TrieNode node;
        int nextChild;
        Frame(TrieNode node) { this.node = node; this.nextChild = -1; }
    }

    private class TrieIterator implements Iterator<String> {
        private final Deque<Frame> stack = new ArrayDeque<>();
        private final StringBuilder prefix = new StringBuilder();
        private final int expectedModCount = modCount;
        private String next;

        TrieIterator() {
            this(root, "");
        }

        TrieIterator(TrieNode startNode, String initialPrefix) {
            prefix.append(initialPrefix);
            stack.push(new Frame(startNode));
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
                    if (frame.node.child(frame.nextChild) != null) {
                        found = frame.nextChild++;
                        break;
                    }
                    frame.nextChild++;
                }
                if (found >= 0) {
                    prefix.append((char) ('a' + found));
                    stack.push(new Frame(frame.node.child(found)));
                } else {
                    stack.pop();
                    if (prefix.length() > 0) prefix.deleteCharAt(prefix.length() - 1);
                }
            }
            return null;
        }

        @Override public boolean hasNext() { return next != null; }

        @Override public String next() {
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
            if (next == null) throw new NoSuchElementException();
            String result = next;
            next = findNext();
            return result;
        }
    }

    private enum PruneResult { NOT_FOUND, DELETED_KEEP, DELETED_PRUNE }

    private final TrieNode root = new TrieNode();
    private int wordCount;
    private int modCount;

    public void insert(String key) {
        validate(key);
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            curr = curr.addChild(key.charAt(i) - 'a');
        }
        if (!curr.isEndOfWord) {
            curr.isEndOfWord = true;
            wordCount++;
            modCount++;
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
        modCount++;
        return true;
    }

    /** Returns the longest word in the trie that is a prefix of {@code query}, or null if none. */
    public String longestPrefix(String query) {
        validate(query);
        TrieNode curr = root;
        int lastWordEnd = -1;
        for (int i = 0; i < query.length(); i++) {
            curr = curr.child(query.charAt(i) - 'a');
            if (curr == null) break;
            if (curr.isEndOfWord) lastWordEnd = i;
        }
        return lastWordEnd >= 0 ? query.substring(0, lastWordEnd + 1) : null;
    }

    public List<String> getAllWords() {
        List<String> words = new ArrayList<>(wordCount);
        for (String w : this) words.add(w);
        return words;
    }

    public List<String> getWordsWithPrefix(String prefix) {
        validate(prefix);
        List<String> words = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) {
            Iterator<String> it = new TrieIterator(node, prefix);
            while (it.hasNext()) words.add(it.next());
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
        root.clearChildren();
        wordCount = 0;
        modCount++;
    }

    @Override
    public Iterator<String> iterator() {
        return new TrieIterator();
    }

    private TrieNode findNode(String key) {
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            curr = curr.child(key.charAt(i) - 'a');
            if (curr == null) return null;
        }
        return curr;
    }

    private static PruneResult prune(TrieNode node, String key, int depth) {
        if (depth == key.length()) {
            if (!node.isEndOfWord) return PruneResult.NOT_FOUND;
            node.isEndOfWord = false;
            return node.hasChildren() ? PruneResult.DELETED_KEEP : PruneResult.DELETED_PRUNE;
        }
        int idx = key.charAt(depth) - 'a';
        TrieNode child = node.child(idx);
        if (child == null) return PruneResult.NOT_FOUND;
        PruneResult result = prune(child, key, depth + 1);
        if (result == PruneResult.NOT_FOUND) return PruneResult.NOT_FOUND;
        if (result == PruneResult.DELETED_PRUNE) {
            node.removeChild(idx);
            return (!node.isEndOfWord && !node.hasChildren()) ? PruneResult.DELETED_PRUNE : PruneResult.DELETED_KEEP;
        }
        return PruneResult.DELETED_KEEP;
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

        // Fail-fast: modifying the trie during iteration throws ConcurrentModificationException
        try {
            for (String word : trie) {
                trie.insert("zzz");
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException caught"); // expected
        }

        trie.clear();
        System.out.println("after clear — isEmpty: " + trie.isEmpty()); // true
    }
}
