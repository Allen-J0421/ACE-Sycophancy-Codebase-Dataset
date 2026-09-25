import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 040_bubble_sort.
 *
 * Baseline (iteration 0): package-private class BubbleSort with
 *   static void bubbleSort(int[] arr, int n)
 * sorting arr[0..n) in place, ascending, adjacent-swap with early exit.
 * Iterations 1+ move to package `sorting` with
 *   static void BubbleSort.sort(int[])            (whole array, ascending)
 *   instance <T> void sort(T[], Comparator)        (Sorter strategy)
 * and later add InsertionSort / MergeSort / QuickSort sharing the Sorter shape,
 * plus (iteration 7+) SortObserver / SortStats instrumentation via
 *   <T> void sort(T[], Comparator, SortObserver).
 *
 * Every iteration is driven with the same logical input: sort the whole array
 * (n = arr.length for the baseline 2-arg form).
 *
 * Oracles (independent of the subject):
 *  - primitive path: result must equal java.util.Arrays.sort of a copy;
 *  - generic path (every Sorter-shaped class): result equals Arrays.sort with
 *    the same comparator (natural and reverse); stability is required for
 *    bubble/insertion/merge (bubble sort's baseline algorithm only swaps on a
 *    strict '>' so it is intrinsically stable);
 *  - SortStats (if present): counts never negative; comparisons() equals the
 *    number of comparator invocations the driver itself counted; for bubble
 *    sort swaps() must equal the inversion count (a mathematical invariant of
 *    any adjacent-swap sort) and comparisons lie in [n-1, n(n-1)/2]; reset()
 *    returns both counts to zero.
 */
public class Driver {
    static int checks = 0, failed = 0;
    static List<String> failures = new ArrayList<>();

    static void fail(String msg) {
        failed++;
        if (failures.size() < 12) { failures.add(msg); System.out.println("CHECK FAIL " + msg); }
    }

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        // ---------- primitive bubble-sort entry point ----------
        Method prim = null; boolean withN = false;
        for (int pass = 0; pass < 2 && prim == null; pass++) {
            for (Class<?> c : all) {
                String cn = c.getSimpleName().toLowerCase();
                if (cn.contains("test") || cn.contains("demo")) continue;
                if (pass == 0 && !cn.contains("bubble")) continue;
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                    if (m.getReturnType() != void.class) continue;
                    String n = m.getName().toLowerCase();
                    if (!(n.contains("sort"))) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length == 1 && p[0] == int[].class) { prim = m; withN = false; }
                    else if (p.length == 2 && p[0] == int[].class && p[1] == int.class && prim == null) { prim = m; withN = true; }
                }
                if (prim != null) break;
            }
        }
        if (prim == null) {
            System.out.println("RESULT FAIL no primitive bubble sort entry point (static void sort(int[][, int])) found");
            return;
        }
        prim.setAccessible(true);
        System.out.println("primitive entry: " + prim);

        List<int[]> inputs = new ArrayList<>();
        inputs.add(new int[]{64, 34, 25, 12, 22, 11, 90}); // baseline demo
        inputs.add(new int[]{});
        inputs.add(new int[]{7});
        inputs.add(new int[]{2, 1});
        inputs.add(new int[]{1, 2});
        inputs.add(new int[]{5, 5, 5, 5});
        inputs.add(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        inputs.add(new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
        inputs.add(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE});
        inputs.add(new int[]{3, -3, 3, -3, 0, 0});
        Random rnd = new Random(4040);
        for (int t = 0; t < 60; t++) {
            int n = rnd.nextInt(t < 40 ? 25 : 120);
            int range = (t % 3 == 0) ? 5 : (t % 3 == 1 ? 1000 : Integer.MAX_VALUE);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = (range == Integer.MAX_VALUE) ? rnd.nextInt() : rnd.nextInt(2 * range + 1) - range;
            inputs.add(a);
        }
        for (int[] in : inputs) {
            checks++;
            int[] got = in.clone();
            int[] exp = in.clone();
            Arrays.sort(exp);
            try {
                if (withN) prim.invoke(null, got, got.length); else prim.invoke(null, (Object) got);
                if (!Arrays.equals(got, exp))
                    fail("primitive sort(" + abbrev(in) + ") = " + abbrev(got) + ", expected " + abbrev(exp));
            } catch (Throwable t) {
                fail("primitive sort(" + abbrev(in) + ") threw " + deep(t));
            }
        }
        System.out.println("primitive: " + inputs.size() + " arrays checked");

        // ---------- generic Sorter-shaped classes ----------
        Class<?> statsClass = null;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            if (!c.getSimpleName().toLowerCase().contains("stat")) continue;
            try {
                c.getDeclaredConstructor();
                if (findNoArg(c, "comparisons") != null && findNoArg(c, "swaps") != null) { statsClass = c; break; }
            } catch (NoSuchMethodException ignored) {}
        }
        int sorters = 0;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            String sn = c.getSimpleName().toLowerCase();
            if (sn.contains("test") || sn.contains("demo")) continue;
            Method gen;
            try { gen = c.getMethod("sort", Object[].class, Comparator.class); } catch (NoSuchMethodException e) { continue; }
            if (Modifier.isStatic(gen.getModifiers())) continue;
            Constructor<?> ctor;
            try { ctor = c.getDeclaredConstructor(); } catch (NoSuchMethodException e) { continue; }
            ctor.setAccessible(true); gen.setAccessible(true);
            Object sorter = ctor.newInstance();
            Method obs = null;
            for (Method m : c.getMethods()) {
                if (m.getName().equals("sort") && m.getParameterCount() == 3 && !m.isSynthetic() && !m.isBridge()
                        && m.getParameterTypes()[0] == Object[].class && m.getParameterTypes()[1] == Comparator.class) obs = m;
            }
            sorters++;
            boolean bubble = sn.contains("bubble");
            boolean stable = bubble || sn.contains("insertion") || sn.contains("merge");
            System.out.println("sorter: " + c.getName() + (stable ? " (stable)" : "") + (obs != null ? " [observable]" : ""));
            checkGeneric(sorter, gen, obs, statsClass, c.getSimpleName(), stable, bubble, inputs);
            if (failed >= 12) break;
        }
        if (sorters == 0 && !withN) fail("no Sorter-shaped class (instance sort(Object[], Comparator)) found after baseline");
        System.out.println("sorters checked: " + sorters + (statsClass != null ? ", stats class " + statsClass.getName() : ""));

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " checks: " + failures.get(0));
    }

    /** Element with a key (compared) and an original index (tracked for stability). */
    static final class Item {
        final int key, idx;
        Item(int key, int idx) { this.key = key; this.idx = idx; }
        public String toString() { return key + "#" + idx; }
    }

    static void checkGeneric(Object sorter, Method gen, Method obs, Class<?> statsClass, String name,
                             boolean stable, boolean bubble, List<int[]> inputs) throws Exception {
        long[] counter = new long[1];
        Comparator<Item> byKey = (a, b) -> { counter[0]++; return Integer.compare(a.key, b.key); };
        Comparator<Item> byKeyDesc = (a, b) -> { counter[0]++; return Integer.compare(b.key, a.key); };
        for (int[] in : inputs) {
            for (int dir = 0; dir < 2; dir++) {
                Comparator<Item> cmp = dir == 0 ? byKey : byKeyDesc;
                Comparator<Item> pure = dir == 0 ? (a, b) -> Integer.compare(a.key, b.key)
                                                 : (a, b) -> Integer.compare(b.key, a.key);
                Item[] arr = new Item[in.length];
                for (int i = 0; i < in.length; i++) arr[i] = new Item(in[i], i);
                Item[] exp = arr.clone();
                Arrays.sort(exp, pure); // TimSort: stable
                // plain two-arg path
                checks++;
                Item[] got = arr.clone();
                try {
                    gen.invoke(sorter, got, cmp);
                    verify(name + (dir == 0 ? " asc" : " desc"), in, got, exp, stable, pure);
                } catch (Throwable t) {
                    fail(name + " sort(" + abbrev(in) + ") threw " + deep(t));
                }
                // instrumented path
                if (obs != null && statsClass != null) {
                    checks++;
                    got = arr.clone();
                    try {
                        Constructor<?> sc = statsClass.getDeclaredConstructor();
                        sc.setAccessible(true);
                        Object stats = sc.newInstance();
                        counter[0] = 0;
                        obs.invoke(sorter, got, cmp, stats);
                        long invoked = counter[0];
                        verify(name + " observed" + (dir == 0 ? " asc" : " desc"), in, got, exp, stable, pure);
                        long comps = ((Number) findNoArg(statsClass, "comparisons").invoke(stats)).longValue();
                        long swaps = ((Number) findNoArg(statsClass, "swaps").invoke(stats)).longValue();
                        int n = in.length;
                        if (comps < 0 || swaps < 0)
                            fail(name + " SortStats negative (comparisons=" + comps + ", swaps=" + swaps + ") for " + abbrev(in));
                        if (comps != invoked)
                            fail(name + " SortStats.comparisons()=" + comps + " but comparator was invoked " + invoked
                                    + " times for " + abbrev(in));
                        if (bubble) {
                            long inv = inversions(in, dir == 1);
                            if (swaps != inv)
                                fail(name + " SortStats.swaps()=" + swaps + " but inversion count is " + inv + " for " + abbrev(in));
                            long lo = n < 2 ? 0 : n - 1, hi = (long) n * (n - 1) / 2;
                            if (comps < lo || comps > hi)
                                fail(name + " SortStats.comparisons()=" + comps + " outside [" + lo + "," + hi + "] for n=" + n);
                        }
                        Method reset = findNoArg(statsClass, "reset");
                        if (reset != null) {
                            reset.invoke(stats);
                            long c2 = ((Number) findNoArg(statsClass, "comparisons").invoke(stats)).longValue();
                            long s2 = ((Number) findNoArg(statsClass, "swaps").invoke(stats)).longValue();
                            if (c2 != 0 || s2 != 0) fail(name + " SortStats.reset() left " + c2 + "/" + s2);
                        }
                    } catch (Throwable t) {
                        fail(name + " observed sort(" + abbrev(in) + ") threw " + deep(t));
                    }
                }
                if (failed >= 12) return;
            }
        }
    }

    static void verify(String what, int[] in, Item[] got, Item[] exp, boolean stable, Comparator<Item> pure) {
        for (int i = 0; i < exp.length; i++) {
            if (got[i] == null || got[i].key != exp[i].key) {
                fail(what + " sort(" + abbrev(in) + ") keys = " + Arrays.toString(Arrays.stream(got).map(x -> x == null ? "null" : "" + x.key).toArray())
                        + ", expected " + Arrays.toString(Arrays.stream(exp).mapToInt(x -> x.key).toArray()));
                return;
            }
        }
        // permutation check (same Item objects)
        Set<Item> a = Collections.newSetFromMap(new IdentityHashMap<>());
        a.addAll(Arrays.asList(got));
        if (a.size() != exp.length) { fail(what + " result is not a permutation of the input for " + abbrev(in)); return; }
        if (stable) {
            for (int i = 0; i < exp.length; i++) {
                if (got[i] != exp[i]) {
                    fail(what + " not stable for " + abbrev(in) + ": got " + Arrays.toString(got) + ", expected " + Arrays.toString(exp));
                    return;
                }
            }
        }
    }

    static long inversions(int[] a, boolean desc) {
        long c = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (desc ? a[i] < a[j] : a[i] > a[j]) c++;
        return c;
    }

    static Method findNoArg(Class<?> c, String name) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass())
            for (Method m : k.getDeclaredMethods())
                if (m.getName().equals(name) && m.getParameterCount() == 0 && !m.isSynthetic() && !m.isBridge()) {
                    m.setAccessible(true);
                    return m;
                }
        return null;
    }

    static String abbrev(int[] a) {
        if (a.length <= 16) return Arrays.toString(a);
        return Arrays.toString(Arrays.copyOf(a, 16)).replace("]", ", ...](n=" + a.length + ")");
    }

    static String deep(Throwable t) {
        Throwable c = t;
        while (c instanceof java.lang.reflect.InvocationTargetException && c.getCause() != null) c = c.getCause();
        return c.toString();
    }

    static List<Class<?>> scan(Path classes) throws Exception {
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
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable ignored) {}
        }
        return out;
    }
}
