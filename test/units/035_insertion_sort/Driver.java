import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 035_insertion_sort.
 *
 * Baseline (iter 0): instance method InsertionSort.sort(int[]) sorting in place.
 * Iterations 1-5: static InsertionSort.sort(int[]).
 * Iterations 6-10: Sorter interface with InsertionSorter implementation (plus
 * BubbleSorter, SortingStrategy enum and SorterFacade later).
 *
 * Discovery: any in-place void sort(int[]) (static or instance), preferring
 * classes whose name mentions "insertion" and skipping *Test classes; falls
 * back to a generic T[]-sorting method invoked with Integer[].
 * Oracle: java.util.Arrays.sort over edge cases + seeded random arrays.
 */
public class Driver {

    interface SortFn { int[] sorted(int[] input) throws Exception; }

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> cs = loadClasses(classes);

        List<Map.Entry<String, SortFn>> fns = discover(cs, "insertion");
        if (fns.isEmpty()) {
            System.out.println("RESULT FAIL no sort entry point found among "
                    + cs.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }

        int checks = 0, failed = 0;
        List<int[]> arrays = new ArrayList<>(List.of(
                new int[]{},
                new int[]{7},
                new int[]{12, 11, 13, 5, 6},
                new int[]{1, 2, 3, 4, 5},
                new int[]{5, 4, 3, 2, 1},
                new int[]{3, 3, 3, 3},
                new int[]{-5, 0, -2, 9, -5, 0},
                new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, Integer.MAX_VALUE}));
        Random rnd = new Random(4242);
        for (int t = 0; t < 40; t++) {
            int n = rnd.nextInt(201);
            arrays.add(rnd.ints(n, -1000, 1001).toArray());
        }
        // larger arrays, heavy duplicates, full int range, presorted/reversed runs
        for (int t = 0; t < 8; t++) {
            int n = 500 + rnd.nextInt(1500);
            int[] a = switch (t % 4) {
                case 0 -> rnd.ints(n, 0, 4).toArray();
                case 1 -> rnd.ints(n).toArray();
                case 2 -> { int[] b = rnd.ints(n, -50, 50).toArray(); Arrays.sort(b); yield b; }
                default -> { int[] b = rnd.ints(n, -1_000_000, 1_000_000).toArray(); Arrays.sort(b);
                             for (int i = 0; i < n / 2; i++) { int x = b[i]; b[i] = b[n - 1 - i]; b[n - 1 - i] = x; } yield b; }
            };
            arrays.add(a);
        }

        for (Map.Entry<String, SortFn> named : fns) {
          SortFn fn = named.getValue();
          System.out.println("testing " + named.getKey());
          for (int[] a : arrays) {
            int[] expected = a.clone();
            Arrays.sort(expected);
            checks++;
            int[] actual;
            try {
                actual = fn.sorted(a.clone());
            } catch (Throwable e) {
                failed++;
                Throwable cause = e;
                while (cause.getCause() != null) cause = cause.getCause();
                System.out.println("CHECK FAIL [" + named.getKey() + "] input=" + abbrev(a) + " threw " + cause);
                continue;
            }
            if (!Arrays.equals(actual, expected)) {
                failed++;
                System.out.println("CHECK FAIL [" + named.getKey() + "] input=" + abbrev(a)
                        + " got " + abbrev(actual)
                        + " expected " + abbrev(expected));
            }
          }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " arrays sorted correctly"
                : "RESULT FAIL " + failed + "/" + checks + " arrays sorted wrong");
    }

    // ------------------------------------------------------------------ discovery

    /**
     * All in-place sort entry points on classes whose name mentions the
     * algorithm (int[], generic T[] via Integer[], and T[] + Comparator with
     * natural order); if there are none, the single best-scoring entry anywhere.
     */
    static List<Map.Entry<String, SortFn>> discover(List<Class<?>> cs, String preferred) {
        List<Map.Entry<String, SortFn>> preferredFns = new ArrayList<>();
        SortFn best = null;
        String bestName = null;
        int bestScore = -1;
        for (Class<?> c : cs) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || c.isEnum()) continue;
            String cn = c.getSimpleName().toLowerCase();
            if (cn.contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                if (!m.getName().toLowerCase().contains("sort")) continue;
                if (Modifier.isPrivate(m.getModifiers())) continue;
                if (m.getReturnType() != void.class) continue;
                Class<?>[] p = m.getParameterTypes();
                SortFn fn = null;
                int score = 0;
                if (p.length == 1 && p[0] == int[].class) {
                    fn = adapt(c, m, 0);
                    score += 4;                       // native int[] signature preferred
                } else if (p.length == 1 && boxedArray(p[0])) {
                    fn = adapt(c, m, 1);              // generic T[] signature, box/unbox
                } else if (p.length == 2 && boxedArray(p[0]) && p[1] == Comparator.class) {
                    fn = adapt(c, m, 2);              // T[] + Comparator.naturalOrder()
                }
                if (fn == null) continue;
                if (cn.contains(preferred)) {
                    score += 10;
                    preferredFns.add(Map.entry(m.toString(), fn));
                }
                if (score > bestScore) {
                    bestScore = score;
                    best = fn;
                    bestName = m.toString();
                }
            }
        }
        if (!preferredFns.isEmpty()) return preferredFns;
        List<Map.Entry<String, SortFn>> out = new ArrayList<>();
        if (best != null) out.add(Map.entry(bestName, best));
        return out;
    }

    static boolean boxedArray(Class<?> t) {
        return t.isArray() && !t.getComponentType().isPrimitive()
                && t.getComponentType().isAssignableFrom(Integer.class);
    }

    static String abbrev(int[] a) {
        if (a.length <= 40) return Arrays.toString(a);
        return Arrays.toString(Arrays.copyOf(a, 40)).replace("]", ", ... (" + a.length + " elements)]");
    }

    static SortFn adapt(Class<?> c, Method m, int mode) {
        final Object receiver;
        if (Modifier.isStatic(m.getModifiers())) {
            receiver = null;
        } else {
            try {
                Constructor<?> k = c.getDeclaredConstructor();
                k.setAccessible(true);
                receiver = k.newInstance();
            } catch (Throwable t) {
                return null;
            }
        }
        m.setAccessible(true);
        if (mode == 0) {
            return in -> { m.invoke(receiver, (Object) in); return in; };
        }
        return in -> {
            Integer[] boxed = Arrays.stream(in).boxed().toArray(Integer[]::new);
            if (mode == 1) m.invoke(receiver, (Object) boxed);
            else m.invoke(receiver, boxed, Comparator.naturalOrder());
            int[] out = new int[boxed.length];
            for (int i = 0; i < out.length; i++) out[i] = boxed[i];
            return out;
        };
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
