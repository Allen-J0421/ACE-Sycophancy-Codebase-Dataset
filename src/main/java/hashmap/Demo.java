package hashmap;

import java.util.Map;
import java.util.TreeMap;

/**
 * Small command-line demonstration of {@link OpenAddressingHashMap}.
 *
 * <p>Mirrors the scenario from the original single-file program (insert, replace,
 * display, size, delete, lookup) and additionally shows behaviour the original
 * could not handle, plus the standard {@link Map} API it now implements.
 */
public final class Demo {

    private Demo() {
    }

    public static void main(String[] args) {
        Map<Integer, Integer> map = new OpenAddressingHashMap<>();

        map.put(1, 1);
        map.put(2, 2);
        map.put(2, 3); // replaces the value for key 2

        System.out.println("Contents:   " + map);
        System.out.println("Size:       " + map.size());
        System.out.println("Remove 2:   " + map.remove(2));
        System.out.println("Size:       " + map.size());
        System.out.println("Is empty:   " + map.isEmpty());
        System.out.println("Get 2:      " + map.get(2)); // null: key was removed

        // Capabilities the original int-only, fixed-size version lacked.
        System.out.println();
        Map<String, String> capitals = new OpenAddressingHashMap<>();
        capitals.put("France", "Paris");
        capitals.put("Japan", "Tokyo");
        System.out.println("String keys: " + capitals.get("Japan"));

        Map<Integer, Integer> stress = new OpenAddressingHashMap<>(4);
        for (int i = -5; i <= 100; i++) {
            stress.put(i, i * i);
        }
        System.out.println("Negative key -5 -> " + stress.get(-5));
        System.out.println("Resized size: " + stress.size());

        // Standard Map views and iteration.
        System.out.println();
        System.out.println("Iterating capitals via entrySet():");
        for (Map.Entry<String, String> e : capitals.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
        System.out.println("keySet():   " + capitals.keySet());
        System.out.println("values():   " + capitals.values());

        // Interoperability: equals against any other Map implementation.
        Map<String, String> other = new TreeMap<>();
        other.put("Japan", "Tokyo");
        other.put("France", "Paris");
        System.out.println("Equals a TreeMap with same mappings: " + capitals.equals(other));

        // Constructed directly from another Map, and Map default methods work.
        Map<String, String> copy = new OpenAddressingHashMap<>(other);
        copy.putIfAbsent("Germany", "Berlin");
        copy.merge("Germany", "!", (a, b) -> a + b);
        System.out.println("Copy with default methods: " + new TreeMap<>(copy));
    }
}
