import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Trie extends AbstractCollection<String> {

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

    private final TrieNode root = new TrieNode();
    private int wordCount;
    private int modCount;

    /** Inserts {@code key}. Returns {@code true} if the key was not already present. */
    @Override
    public boolean add(String key) {
        validate(key);
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            curr = curr.addChild(key.charAt(i) - 'a');
        }
        if (curr.isEndOfWord) return false;
        curr.isEndOfWord = true;
        wordCount++;
        modCount++;
        return true;
    }

    /** Returns {@code true} if {@code o} is a String present in the trie. */
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof String)) return false;
        String key = (String) o;
        if (!isValid(key)) return false;
        TrieNode node = findNode(key);
        return node != null && node.isEndOfWord;
    }

    /** Removes {@code o} from the trie. Returns {@code true} if it was present. */
    @Override
    public boolean remove(Object o) {
        if (!(o instanceof String)) return false;
        String key = (String) o;
        if (!isValid(key)) return false;

        // Forward pass: verify the word exists and record the path for pruning.
        TrieNode[] pathNodes = new TrieNode[key.length()];
        int[]      pathIdxs  = new int[key.length()];
        TrieNode curr = root;
        for (int i = 0; i < key.length(); i++) {
            int idx = key.charAt(i) - 'a';
            pathNodes[i] = curr;
            pathIdxs[i]  = idx;
            curr = curr.child(idx);
            if (curr == null) return false;
        }
        if (!curr.isEndOfWord) return false;

        curr.isEndOfWord = false;
        wordCount--;
        modCount++;

        // Backward pass: prune nodes that are no longer part of any word.
        for (int i = key.length() - 1; i >= 0; i--) {
            TrieNode child = pathNodes[i].child(pathIdxs[i]);
            if (child.hasChildren() || child.isEndOfWord) break;
            pathNodes[i].removeChild(pathIdxs[i]);
        }

        return true;
    }

    /** Returns {@code true} if {@code prefix} is a prefix of any stored word. */
    public boolean isPrefix(String prefix) {
        validate(prefix);
        return findNode(prefix) != null;
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

    /** Returns all stored words in sorted order. */
    public List<String> getAllWords() {
        List<String> words = new ArrayList<>(wordCount);
        for (String w : this) words.add(w);
        return words;
    }

    /** Returns all stored words that start with {@code prefix}, in sorted order. */
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

    @Override public int size()      { return wordCount; }
    @Override public boolean isEmpty() { return wordCount == 0; }

    @Override
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

    private static boolean isValid(String key) {
        if (key == null || key.isEmpty()) return false;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < 'a' || c > 'z') return false;
        }
        return true;
    }

    private static void validate(String key) {
        if (!isValid(key)) {
            throw new IllegalArgumentException(
                "Key must be non-empty and contain only lowercase letters: " + key);
        }
    }
}

class TrieDemo {
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.addAll(java.util.Arrays.asList("and", "ant", "do", "dad", "dance"));

        System.out.print("for-each: ");
        for (String word : trie) System.out.print(word + " ");
        System.out.println(); // and ant dad dance do

        System.out.println("contains 'do':  " + trie.contains("do"));   // true
        System.out.println("contains 'gee': " + trie.contains("gee"));  // false
        System.out.println("prefix 'da':    " + trie.getWordsWithPrefix("da")); // [dad, dance]

        System.out.println("longestPrefix 'dancer': " + trie.longestPrefix("dancer")); // dance
        System.out.println("longestPrefix 'ants':   " + trie.longestPrefix("ants"));   // ant
        System.out.println("longestPrefix 'xyz':    " + trie.longestPrefix("xyz"));    // null

        trie.remove("do");
        System.out.println("after remove 'do': " + trie); // [and, ant, dad, dance]

        System.out.println("containsAll: " +
            trie.containsAll(java.util.Arrays.asList("and", "dad"))); // true

        // Fail-fast: modifying the trie during iteration throws ConcurrentModificationException
        try {
            for (String word : trie) trie.add("zzz");
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException caught");
        }

        trie.clear();
        System.out.println("after clear — isEmpty: " + trie.isEmpty()); // true
    }
}
