package hashmap;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Lightweight, dependency-free test runner for {@link OpenAddressingHashMap}.
 *
 * <p>Each {@code check} records a pass/fail; {@code main} exits non-zero if any
 * assertion fails, so it can be wired into a build or run by hand.
 */
public final class OpenAddressingHashMapTest {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        basicPutGet();
        replaceValue();
        removeAndAbsent();
        nullValuesAreDistinctFromAbsent();
        negativeKeys();
        tombstoneDoesNotCreateDuplicates();
        probeSurvivesTombstone();
        resizePreservesEntries();
        nullKeyRejected();
        invalidConstructorArgs();
        getOrDefaultDistinguishesNull();
        iterationVisitsEveryEntry();
        keysAndValuesViews();
        failFastIteration();
        valueBasedEqualsAndHashCode();
        mapInteroperability();
        viewsAreLive();
        iteratorRemove();
        entrySetSetValueWritesThrough();
        mapDefaultMethods();
        randomizedAgainstJdkMap();

        System.out.printf("%n%d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void basicPutGet() {
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>();
        check("put returns null for new key", m.put(1, 10) == null);
        check("get returns value", m.get(1) == 10);
        check("size is 1", m.size() == 1);
        check("absent key is null", m.get(99) == null);
        check("containsKey true", m.containsKey(1));
        check("containsKey false", !m.containsKey(99));
    }

    private static void replaceValue() {
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>();
        m.put(2, 2);
        check("put returns previous value", m.put(2, 3) == 2);
        check("value replaced", m.get(2) == 3);
        check("replace does not grow size", m.size() == 1);
    }

    private static void removeAndAbsent() {
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>();
        m.put(2, 2);
        check("remove returns value", m.remove(2) == 2);
        check("size after remove", m.size() == 0);
        check("isEmpty after remove", m.isEmpty());
        check("remove absent returns null", m.remove(2) == null);
        check("get after remove is null", m.get(2) == null);
    }

    private static void nullValuesAreDistinctFromAbsent() {
        OpenAddressingHashMap<String, String> m = new OpenAddressingHashMap<>();
        m.put("k", null);
        check("null value stored", m.get("k") == null);
        check("containsKey distinguishes null value", m.containsKey("k"));
        check("absent key not contained", !m.containsKey("missing"));
    }

    private static void negativeKeys() {
        // The original key % capacity would have thrown for negative keys.
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>();
        m.put(-1, 100);
        m.put(-12345, 200);
        check("negative key -1", m.get(-1) == 100);
        check("negative key -12345", m.get(-12345) == 200);
    }

    private static void tombstoneDoesNotCreateDuplicates() {
        // Force collisions in a tiny table, then delete and reinsert.
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>(8, 0.9);
        m.put(1, 1);
        m.put(9, 9);  // collides with 1 (same low bits in an 8-slot table)
        m.put(17, 17);
        m.remove(9);
        m.put(9, 99); // must update the single existing slot, not duplicate it
        check("reinsert after delete keeps one entry", m.get(9) == 99);
        check("no duplicate created", m.size() == 3);
    }

    private static void probeSurvivesTombstone() {
        // Deleting an early element in a probe chain must not hide later ones.
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>(8, 0.9);
        m.put(1, 1);
        m.put(9, 9);
        m.remove(1);
        check("later chain element still found", m.get(9) == 9);
    }

    private static void resizePreservesEntries() {
        // The original fixed-capacity map would loop forever once full.
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>(2);
        for (int i = 0; i < 1000; i++) {
            m.put(i, i * 2);
        }
        check("size after many inserts", m.size() == 1000);
        boolean allPresent = true;
        for (int i = 0; i < 1000; i++) {
            if (m.get(i) == null || m.get(i) != i * 2) {
                allPresent = false;
                break;
            }
        }
        check("all entries survive resize", allPresent);
    }

    private static void nullKeyRejected() {
        OpenAddressingHashMap<String, String> m = new OpenAddressingHashMap<>();
        check("put(null) throws NPE", throwsException(() -> m.put(null, "x"), NullPointerException.class));
        check("get(null) is null", m.get(null) == null);
        check("remove(null) is null", m.remove(null) == null);
        check("containsKey(null) is false", !m.containsKey(null));
    }

    private static void invalidConstructorArgs() {
        check("zero capacity rejected",
            throwsException(() -> new OpenAddressingHashMap<>(0), IllegalArgumentException.class));
        check("load factor >= 1 rejected",
            throwsException(() -> new OpenAddressingHashMap<>(16, 1.0), IllegalArgumentException.class));
        check("load factor <= 0 rejected",
            throwsException(() -> new OpenAddressingHashMap<>(16, 0.0), IllegalArgumentException.class));
    }

    private static void getOrDefaultDistinguishesNull() {
        OpenAddressingHashMap<String, String> m = new OpenAddressingHashMap<>();
        m.put("k", null);
        check("getOrDefault returns stored null", m.getOrDefault("k", "fallback") == null);
        check("getOrDefault returns fallback when absent", "fallback".equals(m.getOrDefault("x", "fallback")));
    }

    private static void iterationVisitsEveryEntry() {
        OpenAddressingHashMap<Integer, Integer> m = new OpenAddressingHashMap<>(4);
        for (int i = 0; i < 50; i++) {
            m.put(i, i * 10);
        }
        Set<Integer> seen = new HashSet<>();
        boolean valuesMatch = true;
        for (Map.Entry<Integer, Integer> e : m.entrySet()) {
            seen.add(e.getKey());
            if (e.getValue() != e.getKey() * 10) {
                valuesMatch = false;
            }
        }
        check("iterator visits every entry exactly once", seen.size() == 50);
        check("iterator entry values are correct", valuesMatch);
    }

    private static void keysAndValuesViews() {
        Map<Integer, Integer> m = new OpenAddressingHashMap<>();
        m.put(1, 100);
        m.put(2, 200);
        m.put(3, 300);
        check("keySet() view", new HashSet<>(m.keySet()).equals(Set.of(1, 2, 3)));
        check("values() view", new HashSet<>(m.values()).equals(Set.of(100, 200, 300)));
        check("keySet contains is O(1) accurate", m.keySet().contains(2) && !m.keySet().contains(9));
        check("keySet size", m.keySet().size() == 3);
    }

    private static void failFastIteration() {
        Map<Integer, Integer> m = new OpenAddressingHashMap<>();
        m.put(1, 1);
        m.put(2, 2);
        boolean threw = false;
        try {
            for (Map.Entry<Integer, Integer> e : m.entrySet()) {
                m.put(99, 99); // structural modification mid-iteration
            }
        } catch (ConcurrentModificationException expected) {
            threw = true;
        }
        check("iteration is fail-fast on structural modification", threw);
    }

    private static void mapInteroperability() {
        Map<Integer, String> mine = new OpenAddressingHashMap<>();
        Map<Integer, String> jdk = new TreeMap<>();
        for (int i = 0; i < 20; i++) {
            mine.put(i, "v" + i);
            jdk.put(i, "v" + i);
        }
        check("equals a TreeMap with same mappings", mine.equals(jdk));
        check("TreeMap equals us (symmetric)", jdk.equals(mine));
        check("hashCode matches the Map contract", mine.hashCode() == jdk.hashCode());
        check("is a java.util.Map", mine instanceof Map);
    }

    private static void viewsAreLive() {
        Map<Integer, Integer> m = new OpenAddressingHashMap<>();
        m.put(1, 1);
        m.put(2, 2);
        m.put(3, 3);
        // Removing through the keySet view must remove from the map.
        m.keySet().remove(2);
        check("keySet().remove writes through", !m.containsKey(2) && m.size() == 2);
        // Removing through the values view removes a matching entry.
        m.values().remove(3);
        check("values().remove writes through", !m.containsValue(3) && m.size() == 1);
        check("containsValue true", m.containsValue(1));
        check("containsValue false", !m.containsValue(999));
    }

    private static void iteratorRemove() {
        Map<Integer, Integer> m = new OpenAddressingHashMap<>();
        for (int i = 0; i < 100; i++) {
            m.put(i, i);
        }
        Iterator<Map.Entry<Integer, Integer>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getKey() % 2 == 0) {
                it.remove();
            }
        }
        boolean onlyOdd = true;
        for (int k : m.keySet()) {
            if (k % 2 == 0) {
                onlyOdd = false;
            }
        }
        check("iterator.remove() drops matching entries", m.size() == 50 && onlyOdd);
        check("iterator.remove() leaves odd keys reachable", m.get(7) == 7 && m.get(8) == null);
    }

    private static void entrySetSetValueWritesThrough() {
        Map<String, Integer> m = new OpenAddressingHashMap<>();
        m.put("a", 1);
        for (Map.Entry<String, Integer> e : m.entrySet()) {
            e.setValue(e.getValue() + 100);
        }
        check("Map.Entry.setValue writes through", m.get("a") == 101);
    }

    private static void mapDefaultMethods() {
        Map<String, Integer> m = new OpenAddressingHashMap<>();
        check("putIfAbsent inserts", m.putIfAbsent("a", 1) == null && m.get("a") == 1);
        check("putIfAbsent keeps existing", m.putIfAbsent("a", 2) == 1 && m.get("a") == 1);
        m.merge("a", 10, Integer::sum);
        check("merge combines", m.get("a") == 11);
        m.compute("a", (k, v) -> v * 2);
        check("compute updates", m.get("a") == 22);
        m.computeIfAbsent("b", k -> 5);
        check("computeIfAbsent inserts", m.get("b") == 5);
        Map<String, Integer> source = Map.of("x", 1, "y", 2);
        m.putAll(source);
        check("putAll copies mappings", m.get("x") == 1 && m.get("y") == 2);
    }

    private static void valueBasedEqualsAndHashCode() {
        OpenAddressingHashMap<Integer, String> a = new OpenAddressingHashMap<>(2);
        OpenAddressingHashMap<Integer, String> b = new OpenAddressingHashMap<>(64);
        for (int i = 0; i < 20; i++) {
            a.put(i, "v" + i);
            b.put(19 - i, "v" + (19 - i)); // same mappings, different insertion order & capacity
        }
        check("equal contents are equal regardless of capacity/order", a.equals(b));
        check("equal maps have equal hashCode", a.hashCode() == b.hashCode());
        check("reflexive equals", a.equals(a));
        check("not equal to non-map", !a.equals("string"));
        b.remove(0);
        check("differing contents are not equal", !a.equals(b));
    }

    private static void randomizedAgainstJdkMap() {
        // Differential test: behave identically to java.util.HashMap over random ops.
        OpenAddressingHashMap<Integer, Integer> mine = new OpenAddressingHashMap<>();
        Map<Integer, Integer> ref = new HashMap<>();
        long seed = 42L; // deterministic
        boolean ok = true;
        for (int i = 0; i < 20000; i++) {
            seed = seed * 6364136223846793005L + 1442695040888963407L; // LCG
            int key = (int) (seed >>> 40) % 256;
            int op = (int) (seed >>> 33) & 3;
            if (op == 0) {
                int val = (int) (seed >>> 17) & 0xFFFF;
                if (!java.util.Objects.equals(mine.put(key, val), ref.put(key, val))) {
                    ok = false;
                    break;
                }
            } else if (op == 1) {
                if (!java.util.Objects.equals(mine.remove(key), ref.remove(key))) {
                    ok = false;
                    break;
                }
            } else if (op == 2) {
                if (!java.util.Objects.equals(mine.get(key), ref.get(key))) {
                    ok = false;
                    break;
                }
            } else {
                if (mine.containsKey(key) != ref.containsKey(key) || mine.size() != ref.size()) {
                    ok = false;
                    break;
                }
            }
        }
        check("matches java.util.HashMap over 20k random ops", ok);
    }

    // ----------------------------------------------------------------------

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name);
        }
    }

    private static boolean throwsException(Runnable r, Class<? extends Throwable> expected) {
        try {
            r.run();
            return false;
        } catch (Throwable t) {
            return expected.isInstance(t);
        }
    }
}
