import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 037_counting_sort.
 *
 * Baseline (iter 0) and all iterations: static CountingSort.countSort(int[])
 * returns a NEW sorted array and leaves the input unmodified. The baseline
 * indexes a count array directly by value, so its supported input domain is
 * NON-NEGATIVE ints (zero allowed); all test instances stay in that domain so
 * iteration 0 passes. (Later iterations additionally support negatives via a
 * min-offset; that widening is not part of the shared contract and is not
 * tested.)
 *
 * Oracle: java.util.Arrays.sort over edge cases (empty, single, all-zero,
 * heavy duplicates, sorted/reverse, wide value range) + seeded random arrays.
 * Also verifies the pure-function contract: the input array is unmodified.
 */
public class Driver {

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> cs = loadClasses(classes);

        Method entry = null;
        Object receiver = null;
        for (Class<?> c : cs) {
            if (c.getSimpleName().toLowerCase().contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (!(p.length == 1 && p[0] == int[].class && m.getReturnType() == int[].class)) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("sort") || n.contains("count"))) continue;
                m.setAccessible(true);
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        Constructor<?> k = c.getDeclaredConstructor();
                        k.setAccessible(true);
                        receiver = k.newInstance();
                    } catch (Throwable t) { continue; }
                }
                entry = m;
                break;
            }
            if (entry != null) break;
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no counting-sort entry point found among "
                    + cs.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }
        System.out.println("entry: " + entry);

        List<int[]> arrays = new ArrayList<>(List.of(
                new int[]{},
                new int[]{0},
                new int[]{5},
                new int[]{0, 0, 0},
                new int[]{2, 5, 3, 0, 2, 3, 0, 3},        // baseline demo
                new int[]{1, 2, 3, 4, 5},
                new int[]{5, 4, 3, 2, 1},
                new int[]{7, 7, 7, 7},
                new int[]{1000, 0, 999, 1000, 1}));       // wide range
        Random rnd = new Random(31337);
        for (int t = 0; t < 40; t++) {
            int n = rnd.nextInt(151);
            int bound = (t % 4 == 0) ? 6 : 301;           // some heavy-duplicate cases
            arrays.add(rnd.ints(n, 0, bound).toArray());
        }
        // larger arrays and value ranges (still non-negative), a lone max value
        for (int t = 0; t < 8; t++) {
            int n = 500 + rnd.nextInt(3000);
            int bound = (t % 2 == 0) ? 3 : 100_000;
            arrays.add(rnd.ints(n, 0, bound).toArray());
        }
        arrays.add(new int[]{0, 65536, 0, 1, 65535, 65536});

        int checks = 0, failed = 0;
        for (int[] a : arrays) {
            int[] input = a.clone();
            int[] expected = a.clone();
            Arrays.sort(expected);
            checks++;
            Object r;
            try {
                r = entry.invoke(receiver, input);
            } catch (Throwable e) {
                failed++;
                Throwable cause = e;
                while (cause.getCause() != null) cause = cause.getCause();
                System.out.println("CHECK FAIL input=" + Arrays.toString(a) + " threw " + cause);
                continue;
            }
            int[] actual = (int[]) r;
            if (actual == null || !Arrays.equals(actual, expected)) {
                failed++;
                System.out.println("CHECK FAIL input=" + abbrev(a)
                        + " got " + (actual == null ? "null" : abbrev(actual))
                        + " expected " + abbrev(expected));
            } else if (!Arrays.equals(input, a)) {
                failed++;
                System.out.println("CHECK FAIL input array was modified: was "
                        + Arrays.toString(a) + " now " + Arrays.toString(input));
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " arrays sorted correctly"
                : "RESULT FAIL " + failed + "/" + checks + " counting-sort results wrong");
    }

    static String abbrev(int[] a) {
        if (a.length <= 40) return Arrays.toString(a);
        return Arrays.toString(Arrays.copyOf(a, 40)).replace("]", ", ... (" + a.length + " elements)]");
    }

    static List<Class<?>> loadClasses(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted()
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});
        List<Class<?>> out = new ArrayList<>();
        for (String n : names) {
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable ignore) {}
        }
        return out;
    }
}
