class Demo {
    public static void main(String[] args) {
        runDemo("Linear Probing",              new HashMap<>(ProbingStrategies.linear()));
        runDemo("Quadratic Probing",           new HashMap<>(ProbingStrategies.quadratic()));
        runDemo("Double Hashing",              new HashMap<>(ProbingStrategies.doubleHashing()));
        runDemo("Linear Probing (capacity 4)", new HashMap<>(4, ProbingStrategies.linear()));

        // Demonstrate Iterable and keySet on a small, readable map
        System.out.println("=== Iterator & keySet ===");
        HashMapOperations<String, Integer> small = new HashMap<>(ProbingStrategies.linear());
        small.insertNode("apple",  1);
        small.insertNode("banana", 2);
        small.insertNode("cherry", 3);
        System.out.print("for-each keys: ");
        for (String key : small)
            System.out.print(key + " ");
        System.out.println();
        System.out.println("keySet():      " + small.keySet());
    }

    static void runDemo(String label, HashMapOperations<String, Integer> h) {
        System.out.println("=== " + label + " ===");

        // Insert 20 entries, triggering a resize at size 16 (load factor > 0.75)
        for (int i = 1; i <= 20; i++)
            h.insertNode("key" + i, i * 10);

        System.out.println("Size after 20 inserts: " + h.getSize());

        // Verify iterator visits every live key
        int itCount = 0;
        for (String ignored : h) itCount++;
        System.out.println("Keys via iterator:     " + itCount);
        System.out.println("keySet size:           " + h.keySet().size());

        // Update an existing key
        h.insertNode("key5", 999);
        System.out.println("Get key5 (updated):    " + h.get("key5"));

        // Delete and confirm miss
        System.out.println("Delete key10:          " + h.deleteNode("key10"));
        System.out.println("Get key10 (deleted):   " + h.get("key10"));
        System.out.println("Size after delete:     " + h.getSize());

        // Clear and verify empty state
        h.clear();
        System.out.println("Size after clear:      " + h.getSize());
        System.out.println("Is empty after clear:  " + h.isEmpty());
        System.out.println();
    }
}
