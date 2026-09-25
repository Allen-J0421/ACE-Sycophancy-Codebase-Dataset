import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 038_radix_sort.
 *
 * Baseline (iteration 0): Radix.radixsort(int[] arr, int n) sorts ascending,
 * in place, via LSD base-10 counting sort. Its input domain is NON-EMPTY
 * arrays of NON-NEGATIVE ints (getMax reads arr[0] unconditionally and the
 * digit extraction uses integer division, which mis-handles negatives), so
 * the oracle stays inside that domain. Iterations 1-10 replace it with
 * RadixSort.sort(int[]) / sorted(int[]) (byte-wise LSD, handles negatives)
 * plus generic key-extractor overloads; the int[] entry point remains the
 * common denominator and is what this driver exercises.
 *
 * Discovery: reflection over compiled classes (test/benchmark/demo classes
 * skipped, radix-named classes preferred) for a method named *sort* taking
 * (int[]) or (int[], int) and returning void or int[]. A void method is
 * treated as in-place; an int[] return value is treated as the sorted result
 * (covers copy-returning "sorted" variants).
 *
 * Oracle: java.util.Arrays.sort on an independent copy.
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

        // Prefer classes whose simple name mentions "radix".
        List<String> ordered = new ArrayList<>(classNames);
        ordered.sort(Comparator
                .comparingInt((String n) -> simple(n).toLowerCase().contains("radix") ? 0 : 1)
                .thenComparing(n -> n));

        // Every matching int[] entry on the first class that has one (e.g. both
        // in-place sort(int[]) and copy-returning sorted(int[])) is exercised.
        List<Method> entries = new ArrayList<>();
        List<Object> receivers = new ArrayList<>();
        Method entry = null;
        Object receiver = null;
        int arity = 0; // 1 = (int[]), 2 = (int[], int)
        outer:
        for (int shape = 1; shape <= 2 && entry == null; shape++) {
            for (String cn : ordered) {
                String sn = simple(cn).toLowerCase();
                if (sn.contains("test") || sn.contains("bench") || sn.contains("demo")
                        || sn.contains("driver") || sn.contains("placeholder")) continue;
                Class<?> c;
                try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
                for (Method m : c.getDeclaredMethods()) {
                    Class<?>[] p = m.getParameterTypes();
                    boolean shapeOk = shape == 1
                            ? (p.length == 1 && p[0] == int[].class)
                            : (p.length == 2 && p[0] == int[].class && p[1] == int.class);
                    if (!shapeOk) continue;
                    if (m.getReturnType() != void.class && m.getReturnType() != int[].class) continue;
                    if (!m.getName().toLowerCase().contains("sort")) continue;
                    m.setAccessible(true);
                    Object rec = null;
                    if (!Modifier.isStatic(m.getModifiers())) {
                        try {
                            var ctor = c.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            rec = ctor.newInstance();
                        } catch (Throwable t) { continue; }
                    }
                    if (m.isSynthetic() || m.isBridge()) continue;
                    entry = m;
                    receiver = rec;
                    arity = shape;
                    entries.add(m);
                    receivers.add(rec);
                }
                if (entry != null) break outer;
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no radix-sort entry point found among " + classNames);
            return;
        }
        for (Method m : entries) System.out.println("entry: " + m + " (arity mode " + arity + ")");

        // ---- test cases: non-empty, non-negative (the baseline's domain) ----
        List<int[]> cases = new ArrayList<>(List.of(
                new int[]{170, 45, 75, 90, 802, 24, 2, 66},   // baseline demo
                new int[]{5},
                new int[]{0},
                new int[]{7, 7, 7, 7},
                new int[]{1, 2, 3, 4, 5, 6},
                new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
                new int[]{0, 0, 0, 1, 0},
                new int[]{1_000_000_000, 999_999_999, 0, 1, 2},
                new int[]{123_456_789, 987_654_321, 555, 0, 42, 42}));
        Random rnd = new Random(20260925);
        for (int t = 0; t < 36; t++) {
            int n = 1 + rnd.nextInt(300);           // >64 exercises the radix path, <=64 insertion path
            int bound = (t % 3 == 0) ? 100 : 1_000_000_000; // narrow range => many duplicates
            int[] a = rnd.ints(n, 0, bound).toArray();
            cases.add(a);
        }
        for (int t = 0; t < 6; t++) {               // larger inputs, all 10 decimal digits
            int n = 1000 + rnd.nextInt(4000);
            cases.add(rnd.ints(n, 0, (t % 2 == 0) ? 1_000_000_000 : 1000).toArray());
        }
        cases.add(new int[]{1_000_000_000, 0, 999_999_999, 100_000_000, 10, 1, 1_000_000_000});

        int checks = 0, failed = 0;
        for (int ei = 0; ei < entries.size(); ei++) {
          entry = entries.get(ei);
          receiver = receivers.get(ei);
          for (int[] a : cases) {
            checks++;
            int[] expect = a.clone();
            Arrays.sort(expect);
            int[] in = a.clone();
            Object out;
            try {
                out = arity == 1 ? entry.invoke(receiver, in)
                                 : entry.invoke(receiver, in, in.length);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL [" + entry.getName() + "] exception on " + preview(a) + ": " + cause(e));
                continue;
            }
            int[] got = (out instanceof int[] r) ? r : in;
            if (!Arrays.equals(got, expect)) {
                failed++;
                System.out.println("CHECK FAIL [" + entry.getName() + "] input=" + preview(a)
                        + " expected=" + preview(expect) + " got=" + preview(got));
            }
          }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    static String simple(String cn) {
        int i = cn.lastIndexOf('.');
        return i < 0 ? cn : cn.substring(i + 1);
    }

    static String preview(int[] a) {
        if (a.length <= 16) return Arrays.toString(a);
        return Arrays.toString(Arrays.copyOf(a, 16)).replace("]", ", ...len=" + a.length + "]");
    }

    static String cause(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null) t = t.getCause();
        return String.valueOf(t);
    }
}
