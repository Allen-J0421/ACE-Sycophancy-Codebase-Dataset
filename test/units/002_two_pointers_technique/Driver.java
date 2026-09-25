import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 002_two_pointers_technique.
 *
 * Baseline semantics (iteration 0, TwoPointers.twoSum):
 *   boolean f(int[] sortedAscending, int target)
 *   returns true iff two DISTINCT positions i < j exist with a[i] + a[j] == target.
 *   Supported domain: arrays sorted in ascending order (the two-pointer scan is
 *   only correct on sorted input). We keep all test values small enough that
 *   int-sum overflow can never occur, so the baseline's int arithmetic and any
 *   later long-widened variant agree on every tested case.
 *
 * Entry point located by reflection over every compiled class (robust to the
 * rename twoSum -> hasPairWithSum and any repackaging): a boolean-returning
 * method with parameters (int[], int). Name patterns are preferred; if none
 * match, a sole shape-matching candidate is accepted.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        // Collect all shape-matching candidates: (int[], int) -> boolean/Boolean.
        List<Method> candidates = new ArrayList<>();
        Map<Method, Class<?>> owner = new HashMap<>();
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                boolean shape = p.length == 2 && p[0] == int[].class && p[1] == int.class
                        && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class);
                if (!shape) continue;
                candidates.add(m);
                owner.put(m, c);
            }
        }
        // Prefer names that look like the two-sum entry point.
        Method entry = null;
        for (Method m : candidates) {
            String n = m.getName().toLowerCase();
            if (n.contains("sum") || n.contains("pair") || n.contains("twopointer")
                    || n.contains("target") || n.contains("find") || n.contains("has")) {
                entry = m;
                break;
            }
        }
        if (entry == null && candidates.size() == 1) entry = candidates.get(0);
        if (entry == null) {
            System.out.println("RESULT FAIL no two-sum entry point found; candidates=" + candidates
                    + " classes=" + classNames);
            return;
        }
        entry.setAccessible(true);
        Object receiver = null;
        if (!Modifier.isStatic(entry.getModifiers())) {
            try {
                var ctor = owner.get(entry).getDeclaredConstructor();
                ctor.setAccessible(true);
                receiver = ctor.newInstance();
            } catch (Throwable t) {
                System.out.println("RESULT FAIL entry point is non-static and not instantiable: " + entry);
                return;
            }
        }
        System.out.println("entry: " + entry);

        int checks = 0, failed = 0;

        // Fixed cases: baseline demo + edge cases within the sorted-int domain.
        List<Object[]> cases = new ArrayList<>();
        addCase(cases, new int[]{-3, -1, 0, 1, 2}, -2);          // baseline demo -> true
        addCase(cases, new int[]{-3, -1, 0, 1, 2}, 100);         // no pair
        addCase(cases, new int[]{}, 0);                          // empty
        addCase(cases, new int[]{}, 5);
        addCase(cases, new int[]{7}, 7);                         // single element: no pair
        addCase(cases, new int[]{7}, 14);                        // must not reuse same index
        addCase(cases, new int[]{3, 3}, 6);                      // duplicate values, distinct indices
        addCase(cases, new int[]{3, 3, 3}, 6);
        addCase(cases, new int[]{4}, 8);
        addCase(cases, new int[]{1, 2}, 3);
        addCase(cases, new int[]{1, 2}, 4);
        addCase(cases, new int[]{-8, -3, -1}, -4);               // negatives only
        addCase(cases, new int[]{-10, -10, -10}, -20);
        addCase(cases, new int[]{-5, -5, 0, 5, 5}, 0);
        addCase(cases, new int[]{2, 7, 11, 15}, 17);             // uses the two ends
        addCase(cases, new int[]{2, 7, 11, 15}, 9);
        addCase(cases, new int[]{2, 7, 11, 15}, 26);
        addCase(cases, new int[]{0, 0, 0, 0}, 0);
        addCase(cases, new int[]{1, 1, 2, 2, 3, 3}, 5);
        addCase(cases, new int[]{-1000, -500, 0, 500, 1000}, 0);
        addCase(cases, new int[]{-1000, -500, 0, 500, 1000}, -1500);
        addCase(cases, new int[]{-1000, -500, 0, 500, 1000}, 2000); // largest possible sum
        addCase(cases, new int[]{-1000, -500, 0, 500, 1000}, 2001); // just above it
        addCase(cases, new int[]{5, 5, 5, 5, 5}, 10);
        addCase(cases, new int[]{1, 3, 5, 7, 9, 11}, 2);          // below any pair sum

        // Randomized cases: sorted arrays, values bounded so int sums never overflow.
        Random rnd = new Random(20260925);
        for (int t = 0; t < 40; t++) {
            int n = rnd.nextInt(50);
            int[] a = rnd.ints(n, -1000, 1001).sorted().toArray();
            // Probes: mix of achievable pair sums and arbitrary targets.
            for (int q = 0; q < 5; q++) {
                int target;
                if (n >= 2 && rnd.nextBoolean()) {
                    int i = rnd.nextInt(n), j = rnd.nextInt(n);
                    while (j == i) j = rnd.nextInt(n);
                    target = a[i] + a[j];          // guaranteed achievable
                } else {
                    target = rnd.nextInt(4401) - 2200; // may or may not be achievable
                }
                addCase(cases, a, target);
            }
        }

        for (Object[] c : cases) {
            int[] a = (int[]) c[0];
            int target = (Integer) c[1];
            boolean expected = oracle(a, target);
            checks++;
            Object rObj;
            int[] copy = a.clone(); // protect shared arrays from subject mutation
            try {
                rObj = entry.invoke(receiver, copy, target);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL exception on arr=" + Arrays.toString(a)
                        + " target=" + target + ": " + e.getCause());
                continue;
            }
            boolean got = (Boolean) rObj;
            if (got != expected) {
                failed++;
                System.out.println("CHECK FAIL arr=" + Arrays.toString(a) + " target=" + target
                        + " expected=" + expected + " got=" + got);
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    static void addCase(List<Object[]> cases, int[] a, int target) {
        cases.add(new Object[]{a, target});
    }

    /** Independent oracle: exists i < j with a[i] + a[j] == target (no-overflow domain). */
    static boolean oracle(int[] a, int target) {
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] + a[j] == target) return true;
        return false;
    }
}
