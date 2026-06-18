import java.util.*;

public class TrieDemo {
    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateAdvancedOperations();
        demonstrateListenerPattern();
        demonstrateStateManagement();
        demonstrateCachingAndMetrics();
        demonstrateBuilder();
        demonstrateThreadSafety();
        demonstrateTrieNodeStates();
    }

    private static void demonstrateBasicOperations() {
        System.out.println("=== Basic Operations ===");
        Trie trie = new Trie();
        String[] words = {"and", "ant", "do", "dad"};
        Arrays.stream(words).forEach(w -> trie.insert(w));
        System.out.println("Inserted words: " + Arrays.toString(words));

        String[] searchKeys = {"do", "gee", "bat"};
        System.out.print("Search results: ");
        Arrays.stream(searchKeys)
            .forEach(key -> System.out.print(trie.search(key) ? "true " : "false "));

        System.out.print("\nPrefix results: ");
        String[] prefixKeys = {"ge", "ba", "do", "de"};
        Arrays.stream(prefixKeys)
            .forEach(prefix -> System.out.print(trie.isPrefix(prefix) ? "true " : "false "));
        System.out.println();
    }

    private static void demonstrateAdvancedOperations() {
        System.out.println("\n=== Advanced Operations ===");
        Trie trie = new Trie();
        String[] words = {"apple", "app", "apricot", "application"};
        Arrays.stream(words).forEach(w -> trie.insert(w));

        System.out.println("All words: " + trie.getAllWords());
        System.out.println("Words with prefix 'app': " + trie.findWordsWithPrefix("app"));
        System.out.println("Trie size: " + trie.getSize());
        System.out.println("Statistics: " + trie.getStatistics());

        trie.delete("app");
        System.out.println("After deleting 'app': " + trie.getAllWords());
        System.out.println("Trie size: " + trie.getSize());
    }

    private static void demonstrateListenerPattern() {
        System.out.println("\n=== Listener Pattern Demo ===");
        Trie trie = new Trie();

        TrieOperationListener logger = new TrieOperationListener() {
            @Override
            public void onInsert(String word) {
                System.out.println("[LOG] Inserted: " + word);
            }

            @Override
            public void onDelete(String word) {
                System.out.println("[LOG] Deleted: " + word);
            }

            @Override
            public void onSearch(String word, boolean found) {
                System.out.println("[LOG] Searched: " + word + " -> " + (found ? "Found" : "Not Found"));
            }
        };

        trie.addListener(logger);
        OperationResult<Void> result = trie.insert("hello");
        System.out.println("Insert result: " + result.getMessage());
        trie.search("hello");
        OperationResult<Boolean> deleteResult = trie.delete("hello");
        System.out.println("Delete result: " + deleteResult.getMessage());
    }

    private static void demonstrateStateManagement() {
        System.out.println("\n=== State Management Demo ===");
        Trie trie = new Trie();

        System.out.println("Initial state: " + trie.getState().getDescription());
        trie.insert("initial");

        trie.setReadOnly(true);
        System.out.println("After setReadOnly: " + trie.getState().getDescription());
        OperationResult<Void> insertResult = trie.insert("readonly");
        System.out.println("Insert result: " + insertResult.getMessage());

        trie.freeze();
        System.out.println("After freeze: " + trie.getState().getDescription());
        OperationResult<Void> clearResult;
        try {
            trie.clear();
            clearResult = OperationResult.success(null);
        } catch (TrieException e) {
            clearResult = OperationResult.failure(e.getMessage());
        }
        System.out.println("Clear result: " + clearResult.getMessage());
    }

    private static void demonstrateCachingAndMetrics() {
        System.out.println("\n=== Caching & Metrics Demo ===");
        Trie trie = new TrieBuilder()
            .withCharacterSet(new LowercaseCharacterSet())
            .withCaching(true)
            .withCacheSize(256)
            .withMetrics(true)
            .build();

        String[] words = {"performance", "pattern", "practice", "precise"};
        Arrays.stream(words).forEach(w -> trie.insert(w));

        for (int i = 0; i < 5; i++) {
            trie.search("performance");
        }

        trie.search("missing");
        System.out.println("Metrics: " + trie.getMetrics());
    }

    private static void demonstrateBuilder() {
        System.out.println("\n=== Builder Pattern Demo ===");
        Trie customTrie = new TrieBuilder()
            .withMaxWordLength(10)
            .withCaching(true)
            .withCacheSize(512)
            .withMetrics(true)
            .build();

        customTrie.insert("hello");
        customTrie.insert("world");
        customTrie.search("hello");
        customTrie.search("hello");
        customTrie.search("missing");

        System.out.println("Custom trie size: " + customTrie.getSize());
        System.out.println("Custom trie metrics: " + customTrie.getMetrics());
    }

    private static void demonstrateThreadSafety() {
        System.out.println("\n=== Thread Safety Demo ===");
        Trie trie = new Trie();

        Thread writer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                trie.insert("word" + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread reader = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Size: " + trie.getSize());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        writer.start();
        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final size: " + trie.getSize());
        System.out.println("Final metrics: " + trie.getMetrics());
    }

    private static void demonstrateTrieNodeStates() {
        System.out.println("\n=== TrieNode State Management Demo ===");
        Trie trie = new Trie();

        trie.insert("test");
        trie.insert("testing");

        System.out.println("Words in trie: " + trie.getAllWords());
        trie.delete("test");
        System.out.println("After deleting 'test': " + trie.getAllWords());
        System.out.println("'test' still searchable: " + trie.search("test"));
        System.out.println("'testing' still searchable: " + trie.search("testing"));

        System.out.println("\nTrieNode states:");
        System.out.println("  TERMINAL: " + TrieNodeState.TERMINAL.getDescription());
        System.out.println("  INTERMEDIATE: " + TrieNodeState.INTERMEDIATE.getDescription());
        System.out.println("  DEPRECATED: " + TrieNodeState.DEPRECATED.getDescription());
    }
}
