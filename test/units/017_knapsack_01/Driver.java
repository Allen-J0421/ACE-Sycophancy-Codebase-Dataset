import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 017_knapsack_01.
 *
 * Baseline semantics (iteration 0): 0/1 knapsack maximum value.
 *   static int knapsack(int W, int[] val, int[] wt)
 * Iterations 1+: static int maxValue(int capacity, List<Item> items) with a
 * nested record Item(int value, int weight).
 *
 * Oracle: independent 2-D DP over 40 random instances (fixed seed) plus the
 * baseline demo instance and edge cases (empty items, zero capacity).
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes, true);

        // Shape A: (int, int[], int[]) -> number
        Method arrEntry = null;
        // Shape B: (int, List) -> number, plus an item type
        Method listEntry = null;
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (!isNumeric(m.getReturnType())) continue;
                if (p.length == 3 && p[0] == int.class && p[1] == int[].class && p[2] == int[].class)
                    arrEntry = m;
                if (p.length == 2 && p[0] == int.class && List.class.isAssignableFrom(p[1]))
                    listEntry = m;
            }
        }

        ItemFactory items = null;
        Method entry;
        Object receiver = null;
        if (arrEntry != null) {
            entry = arrEntry;
        } else if (listEntry != null) {
            entry = listEntry;
            items = findItemFactory(all);
            if (items == null) {
                System.out.println("RESULT FAIL found list-based entry " + listEntry
                        + " but no Item(value, weight) type");
                return;
            }
        } else {
            System.out.println("RESULT FAIL no knapsack entry point found");
            return;
        }
        entry.setAccessible(true);
        if (!java.lang.reflect.Modifier.isStatic(entry.getModifiers())) {
            var ct = entry.getDeclaringClass().getDeclaredConstructor();
            ct.setAccessible(true);
            receiver = ct.newInstance();
        }
        System.out.println("entry: " + entry + (items == null ? "" : " with " + items));

        List<int[][]> instances = new ArrayList<>();          // {vals, wts, {W}}
        instances.add(new int[][]{{1, 2, 3}, {4, 5, 1}, {4}});  // baseline demo -> 3
        instances.add(new int[][]{{}, {}, {10}});               // no items -> 0
        instances.add(new int[][]{{5, 9}, {3, 4}, {0}});        // zero capacity -> 0
        instances.add(new int[][]{{7}, {0}, {5}});              // zero-weight item -> 7
        instances.add(new int[][]{{60, 100, 120}, {10, 20, 30}, {50}}); // classic -> 220
        Random rnd = new Random(1717);
        for (int t = 0; t < 40; t++) {
            int n = rnd.nextInt(13);
            int[] val = new int[n], wt = new int[n];
            for (int i = 0; i < n; i++) { val[i] = rnd.nextInt(31); wt[i] = rnd.nextInt(16); }
            instances.add(new int[][]{val, wt, {rnd.nextInt(61)}});
        }

        int checks = 0, failed = 0;
        for (int[][] inst : instances) {
            int[] val = inst[0], wt = inst[1];
            int W = inst[2][0];
            long expected = refKnapsack(W, val, wt);
            checks++;
            Object got;
            try {
                if (items == null) {
                    got = entry.invoke(receiver, W, val, wt);
                } else {
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < val.length; i++) list.add(items.make(val[i], wt[i]));
                    got = entry.invoke(receiver, W, list);
                }
            } catch (Throwable t) {
                failed++;
                System.out.println("CHECK FAIL W=" + W + " val=" + Arrays.toString(val)
                        + " wt=" + Arrays.toString(wt) + " threw " + deep(t));
                continue;
            }
            long g = ((Number) got).longValue();
            if (g != expected) {
                failed++;
                System.out.println("CHECK FAIL W=" + W + " val=" + Arrays.toString(val)
                        + " wt=" + Arrays.toString(wt) + " -> " + g + ", expected " + expected);
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " instances"
                : "RESULT FAIL " + failed + "/" + checks + " knapsack values wrong");
    }

    /** Independent reference: classic O(n*W) 0/1 knapsack, 1-D descending. */
    static long refKnapsack(int W, int[] val, int[] wt) {
        long[] dp = new long[W + 1];
        for (int i = 0; i < val.length; i++) {
            for (int j = W; j >= wt[i]; j--) {
                dp[j] = Math.max(dp[j], dp[j - wt[i]] + val[i]);
            }
        }
        return dp[W];
    }

    // -------------------------------------------------------------- item type

    record ItemFactory(Constructor<?> ctor, boolean valueFirst) {
        Object make(int value, int weight) throws Exception {
            return valueFirst ? ctor.newInstance(value, weight) : ctor.newInstance(weight, value);
        }
    }

    static ItemFactory findItemFactory(List<Class<?>> all) throws Exception {
        for (Class<?> c : all) {
            Constructor<?> ct;
            try { ct = c.getDeclaredConstructor(int.class, int.class); }
            catch (NoSuchMethodException e) { continue; }
            Method valAcc = accessor(c, "value"), wtAcc = accessor(c, "weight");
            if (valAcc == null || wtAcc == null) continue;
            ct.setAccessible(true); valAcc.setAccessible(true); wtAcc.setAccessible(true);
            // Determine constructor argument order by probing the accessors.
            Object probe = ct.newInstance(7, 3);
            int v = ((Number) valAcc.invoke(probe)).intValue();
            int w = ((Number) wtAcc.invoke(probe)).intValue();
            if (v == 7 && w == 3) return new ItemFactory(ct, true);
            if (v == 3 && w == 7) return new ItemFactory(ct, false);
        }
        return null;
    }

    static Method accessor(Class<?> c, String base) {
        for (Method m : c.getDeclaredMethods()) {
            if (m.getParameterCount() != 0 || !isNumeric(m.getReturnType())) continue;
            String n = m.getName().toLowerCase();
            if (n.equals(base) || n.equals("get" + base)) return m;
        }
        return null;
    }

    static boolean isNumeric(Class<?> t) {
        return t == int.class || t == long.class || t == Integer.class || t == Long.class;
    }

    static String deep(Throwable t) {
        Throwable c = t.getCause() != null ? t.getCause() : t;
        return c.toString();
    }

    static List<Class<?>> scan(Path classes, boolean includeNested) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> includeNested || !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});
        List<Class<?>> out = new ArrayList<>();
        for (String n : names) {
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable ignored) {}
        }
        return out;
    }
}
