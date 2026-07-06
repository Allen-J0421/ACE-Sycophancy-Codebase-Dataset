class Demo {
    public static void main(String[] args) {
        HashMapOperations<String, Integer> h = new HashMap<>();

        // Insert enough entries to trigger at least one resize
        // (capacity starts at 20; resize fires when size > 20 * 0.75 = 15)
        String[] keys = {
            "alpha", "beta", "gamma", "delta", "epsilon",
            "zeta", "eta", "theta", "iota", "kappa",
            "lambda", "mu", "nu", "xi", "omicron",
            "pi", "rho", "sigma", "tau", "upsilon"
        };
        for (int i = 0; i < keys.length; i++)
            h.insertNode(keys[i], i + 1);

        System.out.println("Size after 20 inserts: " + h.getSize());

        // Update an existing key
        h.insertNode("alpha", 100);
        System.out.println("Size after updating 'alpha': " + h.getSize());
        System.out.println("Get 'alpha': " + h.get("alpha"));

        // Delete and confirm miss
        System.out.println("Delete 'beta': " + h.deleteNode("beta"));
        System.out.println("Size after delete: " + h.getSize());
        System.out.println("Get 'beta': " + h.get("beta"));

        System.out.println("Is empty: " + h.isEmpty());
    }
}
