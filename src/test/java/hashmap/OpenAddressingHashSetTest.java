package hashmap;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Lightweight, dependency-free test runner for {@link OpenAddressingHashSet}.
 *
 * <p>Each {@code check} records a pass/fail; {@code main} exits non-zero if any
 * assertion fails, so it can be wired into a build or run by hand.
 */
public final class OpenAddressingHashSetTest {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        addContainsRemove();
        duplicatesIgnored();
        nullRejected();
        iteration();
        iteratorRemove();
        failFastIteration();
        bulkOperations();
        setEqualityAndHashCode();
        constructFromCollection();
        clearAndEmpty();
        randomizedAgainstJdkSet();

        System.out.printf("%n%d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void addContainsRemove() {
        Set<Integer> s = new OpenAddressingHashSet<>();
        check("add new returns true", s.add(1));
        check("contains added", s.contains(1));
        check("size is 1", s.size() == 1);
        check("remove present returns true", s.remove(1));
        check("remove absent returns false", !s.remove(1));
        check("does not contain removed", !s.contains(1));
        check("empty after removal", s.isEmpty());
    }

    private static void duplicatesIgnored() {
        Set<String> s = new OpenAddressingHashSet<>();
        check("first add returns true", s.add("x"));
        check("duplicate add returns false", !s.add("x"));
        check("size stays 1", s.size() == 1);
    }

    private static void nullRejected() {
        Set<String> s = new OpenAddressingHashSet<>();
        boolean threw = false;
        try {
            s.add(null);
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("add(null) throws NPE", threw);
        check("contains(null) is false", !s.contains(null));
        check("remove(null) is false", !s.remove(null));
    }

    private static void iteration() {
        Set<Integer> s = new OpenAddressingHashSet<>(4);
        for (int i = 0; i < 100; i++) {
            s.add(i);
        }
        Set<Integer> seen = new HashSet<>();
        for (int e : s) {
            seen.add(e);
        }
        check("iterator visits every element once", seen.size() == 100);
        boolean all = true;
        for (int i = 0; i < 100; i++) {
            if (!seen.contains(i)) {
                all = false;
            }
        }
        check("iterator visits exactly the inserted elements", all);
    }

    private static void iteratorRemove() {
        Set<Integer> s = new OpenAddressingHashSet<>();
        for (int i = 0; i < 100; i++) {
            s.add(i);
        }
        Iterator<Integer> it = s.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) {
                it.remove();
            }
        }
        check("iterator.remove() drops matching elements", s.size() == 50);
        check("odd elements remain", s.contains(7) && !s.contains(8));
    }

    private static void failFastIteration() {
        Set<Integer> s = new OpenAddressingHashSet<>();
        s.add(1);
        s.add(2);
        boolean threw = false;
        try {
            for (int e : s) {
                s.add(99); // structural modification mid-iteration
            }
        } catch (ConcurrentModificationException expected) {
            threw = true;
        }
        check("iteration is fail-fast", threw);
    }

    private static void bulkOperations() {
        Set<Integer> s = new OpenAddressingHashSet<>();
        check("addAll reports change", s.addAll(List.of(1, 2, 3, 4, 5)));
        check("addAll populated set", s.size() == 5);
        check("containsAll", s.containsAll(List.of(2, 4)));
        check("retainAll reports change", s.retainAll(List.of(2, 4, 6)));
        check("retainAll kept intersection", s.equals(Set.of(2, 4)));
        check("removeAll reports change", s.removeAll(List.of(2)));
        check("removeAll result", s.equals(Set.of(4)));
    }

    private static void setEqualityAndHashCode() {
        Set<Integer> mine = new OpenAddressingHashSet<>();
        Set<Integer> jdk = new TreeSet<>();
        for (int i = 0; i < 50; i++) {
            mine.add(i);
            jdk.add(49 - i); // same elements, different order & implementation
        }
        check("equals a TreeSet with same elements", mine.equals(jdk));
        check("TreeSet equals us (symmetric)", jdk.equals(mine));
        check("hashCode matches the Set contract", mine.hashCode() == jdk.hashCode());
        check("is a java.util.Set", mine instanceof Set);
    }

    private static void constructFromCollection() {
        Set<Integer> s = new OpenAddressingHashSet<>(List.of(1, 2, 2, 3, 3, 3));
        check("constructor deduplicates", s.size() == 3);
        check("constructor copies elements", s.equals(Set.of(1, 2, 3)));
    }

    private static void clearAndEmpty() {
        Set<Integer> s = new OpenAddressingHashSet<>();
        s.addAll(List.of(1, 2, 3));
        s.clear();
        check("clear empties the set", s.isEmpty() && s.size() == 0);
        check("usable after clear", s.add(9) && s.contains(9));
    }

    private static void randomizedAgainstJdkSet() {
        Set<Integer> mine = new OpenAddressingHashSet<>();
        Set<Integer> ref = new HashSet<>();
        long seed = 7L;
        boolean ok = true;
        for (int i = 0; i < 20000; i++) {
            seed = seed * 6364136223846793005L + 1442695040888963407L; // LCG
            int key = (int) (seed >>> 40) % 256;
            int op = (int) (seed >>> 33) & 3;
            if (op == 0) {
                if (mine.add(key) != ref.add(key)) {
                    ok = false;
                    break;
                }
            } else if (op == 1) {
                if (mine.remove(key) != ref.remove(key)) {
                    ok = false;
                    break;
                }
            } else if (op == 2) {
                if (mine.contains(key) != ref.contains(key)) {
                    ok = false;
                    break;
                }
            } else if (mine.size() != ref.size()) {
                ok = false;
                break;
            }
        }
        check("matches java.util.HashSet over 20k random ops", ok);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name);
        }
    }
}
