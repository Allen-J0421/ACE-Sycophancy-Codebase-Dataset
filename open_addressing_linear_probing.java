class Demo {
    public static void main(String[] args) {
        runDemo("Linear Probing",    new HashMap<>(new LinearProbingStrategy()));
        runDemo("Quadratic Probing", new HashMap<>(new QuadraticProbingStrategy()));
        runDemo("Double Hashing",    new HashMap<>(new DoubleHashingStrategy()));
    }

    static void runDemo(String label, HashMapOperations<String, Integer> h) {
        System.out.println("=== " + label + " ===");

        // Insert 20 entries, triggering a resize at size 16 (load factor > 0.75)
        for (int i = 1; i <= 20; i++)
            h.insertNode("key" + i, i * 10);

        System.out.println("Size after 20 inserts: " + h.getSize());

        // Update an existing key
        h.insertNode("key5", 999);
        System.out.println("Get key5 (updated):    " + h.get("key5"));

        // Delete and confirm miss
        System.out.println("Delete key10:          " + h.deleteNode("key10"));
        System.out.println("Get key10 (deleted):   " + h.get("key10"));
        System.out.println("Size after delete:     " + h.getSize());
        System.out.println();
    }
}
