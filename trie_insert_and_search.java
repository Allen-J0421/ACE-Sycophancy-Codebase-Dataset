class TrieNode {
    private static final int ALPHABET_SIZE = 26;
    TrieNode[] children;
    boolean isWord;

    TrieNode() {
        children = new TrieNode[ALPHABET_SIZE];
        isWord = false;
    }
}

class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String key) {
        TrieNode node = traverse(key, true);
        if (node != null) {
            node.isWord = true;
        }
    }

    public boolean search(String key) {
        TrieNode node = traverse(key, false);
        return node != null && node.isWord;
    }

    public boolean isPrefix(String prefix) {
        return traverse(prefix, false) != null;
    }

    private TrieNode traverse(String key, boolean createIfMissing) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            if (!Character.isLowerCase(c)) {
                return null;
            }

            int index = c - 'a';
            if (curr.children[index] == null) {
                if (!createIfMissing) {
                    return null;
                }
                curr.children[index] = new TrieNode();
            }
            curr = curr.children[index];
        }
        return curr;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        String[] words = {"and", "ant", "do", "dad"};
        for (String word : words) {
            trie.insert(word);
        }

        String[] searchKeys = {"do", "gee", "bat"};
        System.out.print("Search results: ");
        for (String key : searchKeys) {
            System.out.print(trie.search(key) ? "true " : "false ");
        }

        System.out.print("\nPrefix results: ");
        String[] prefixKeys = {"ge", "ba", "do", "de"};
        for (String prefix : prefixKeys) {
            System.out.print(trie.isPrefix(prefix) ? "true " : "false ");
        }
        System.out.println();
    }
}
