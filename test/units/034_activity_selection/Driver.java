import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 034_activity_selection.
 *
 * Baseline (iter 0) and all iterations expose
 * activitySelection(int[] start, int[] finish) -> int, the size of a maximum
 * subset of mutually compatible activities. The compatibility rule of the
 * baseline is STRICT: the next activity must start strictly after the previous
 * one finishes (next.start > prev.finish); touching activities conflict.
 *
 * Oracle: independent earliest-finish greedy (optimal for this compatibility
 * relation) computing the optimum count. The specific selected set may vary
 * between equally-optimal answers, so only the count is part of the contract.
 * The baseline assumes n >= 1 (it returns 1 for an empty input), so all test
 * instances have n >= 1 and start <= finish (iteration 1+ validates that).
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
                if (!(p.length == 2 && p[0] == int[].class && p[1] == int[].class)) continue;
                if (!(m.getReturnType() == int.class || m.getReturnType() == long.class
                        || m.getReturnType() == Integer.class)) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("activ") || n.contains("select"))) continue;
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
            System.out.println("RESULT FAIL no activity-selection entry point found among "
                    + cs.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }
        System.out.println("entry: " + entry);

        List<int[][]> cases = new ArrayList<>();
        cases.add(new int[][]{{1, 3, 0, 5, 8, 5}, {2, 4, 6, 7, 9, 9}}); // demo -> 4
        cases.add(new int[][]{{3}, {7}});                               // single -> 1
        cases.add(new int[][]{{1, 1, 1}, {5, 5, 5}});                   // identical -> 1
        cases.add(new int[][]{{0, 1, 2}, {1, 2, 3}});                   // touching chains (strict >) -> 1
        cases.add(new int[][]{{0, 2, 4}, {1, 3, 5}});                   // disjoint -> 3
        cases.add(new int[][]{{4, 4, 4, 4}, {4, 4, 4, 4}});             // zero-length, same instant -> 1
        Random rnd = new Random(99);
        for (int t = 0; t < 40; t++) {
            int n = 1 + rnd.nextInt(25);
            int[] s = new int[n], f = new int[n];
            for (int i = 0; i < n; i++) {
                s[i] = rnd.nextInt(51);
                f[i] = s[i] + rnd.nextInt(21);
            }
            cases.add(new int[][]{s, f});
        }
        // Larger instances, negative times, wide ranges, many duplicate finishes.
        for (int t = 0; t < 20; t++) {
            int n = 30 + rnd.nextInt(300);
            int span = (t % 2 == 0) ? 40 : 1_000_000;
            int[] s = new int[n], f = new int[n];
            for (int i = 0; i < n; i++) {
                s[i] = rnd.nextInt(span) - span / 2;
                f[i] = s[i] + rnd.nextInt(Math.max(2, span / 10));
            }
            cases.add(new int[][]{s, f});
        }

        int checks = 0, failed = 0;
        for (int[][] c : cases) {
            int expected = refCount(c[0], c[1]);
            checks++;
            Object r;
            int[] sIn = c[0].clone(), fIn = c[1].clone();
            try {
                r = entry.invoke(receiver, sIn, fIn);
            } catch (Throwable e) {
                failed++;
                Throwable cause = e;
                while (cause.getCause() != null) cause = cause.getCause();
                System.out.println("CHECK FAIL start=" + Arrays.toString(c[0])
                        + " finish=" + Arrays.toString(c[1]) + " threw " + cause);
                continue;
            }
            long actual = ((Number) r).longValue();
            if (actual != expected) {
                failed++;
                System.out.println("CHECK FAIL start=" + Arrays.toString(c[0])
                        + " finish=" + Arrays.toString(c[1])
                        + " expected " + expected + " got " + actual);
            } else if (!Arrays.equals(sIn, c[0]) || !Arrays.equals(fIn, c[1])) {
                failed++;   // baseline sorts a private copy; caller's arrays are untouched
                System.out.println("CHECK FAIL input arrays were modified for start=" + Arrays.toString(c[0]));
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " selection instances"
                : "RESULT FAIL " + failed + "/" + checks + " selection counts wrong");
    }

    /** Earliest-finish greedy with the baseline's strict start &gt; finish rule. */
    static int refCount(int[] start, int[] finish) {
        int n = start.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingInt(i -> finish[i]));
        int count = 0;
        long lastFinish = Long.MIN_VALUE;
        for (int i : idx) {
            if (count == 0 || start[i] > lastFinish) {
                count++;
                lastFinish = finish[i];
            }
        }
        return count;
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
