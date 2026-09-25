import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 041_quickselect.
 *
 * Baseline (iteration 0): class QuickSelect with
 *   static int kthSmallest(int[] arr, int low, int high, int k)
 * called as kthSmallest(arr, 0, n-1, k). k is ONE-based and selects the k-th
 * SMALLEST element (k=1 -> minimum, k=n -> maximum). The baseline partitions
 * the caller's array in place (so it is permuted, not preserved).
 * Iterations 1+ expose static int kthSmallest(int[] array, int k) with the same
 * one-based k-th-smallest convention (operating on a defensive copy).
 *
 * Domain: non-empty arrays, 1 <= k <= n (the baseline's own main guards k > n
 * before calling; out-of-range k is outside its contract, so not tested).
 *
 * Oracle: sorted copy; answer must equal sorted[k-1]. After every call the
 * caller's array must still be a permutation of the original multiset.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        Method four = null, two = null;
        // pass 0: public + kth/smallest name; pass 1: public + select-ish name; pass 2: any shape match
        for (int pass = 0; pass < 3; pass++) {
            for (Class<?> c : all) {
                String cn = c.getSimpleName().toLowerCase();
                if (cn.contains("test") || cn.contains("demo")) continue;
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                    if (m.getReturnType() != int.class && m.getReturnType() != Integer.class) continue;
                    String n = m.getName().toLowerCase();
                    boolean pub = Modifier.isPublic(m.getModifiers());
                    boolean strong = n.contains("kth") || n.contains("smallest");
                    boolean named = strong || n.contains("select");
                    if (pass == 0 && !(pub && strong)) continue;
                    if (pass == 1 && !(pub && named)) continue;
                    if (n.contains("partition")) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length == 2 && p[0] == int[].class && p[1] == int.class && two == null) two = m;
                    if (p.length == 4 && p[0] == int[].class && p[1] == int.class && p[2] == int.class
                            && p[3] == int.class && four == null) four = m;
                }
            }
            if (two != null || four != null) break;
        }
        Method entry = two != null ? two : four;
        if (entry == null) {
            System.out.println("RESULT FAIL no quickselect entry point (int kthSmallest(int[], int) or (int[],int,int,int)) found");
            return;
        }
        entry.setAccessible(true);
        final boolean fourArg = entry == four;
        System.out.println("entry: " + entry);

        List<int[]> arrays = new ArrayList<>();
        arrays.add(new int[]{10, 4, 5, 8, 6, 11, 26}); // baseline demo (k=3 -> 6)
        arrays.add(new int[]{42});
        arrays.add(new int[]{2, 1});
        arrays.add(new int[]{7, 7, 7, 7, 7});
        arrays.add(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        arrays.add(new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1});
        arrays.add(new int[]{3, 1, 3, 1, 3, 1, 2, 2});
        arrays.add(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE});
        arrays.add(new int[]{-5, -10, 0, -5, 5, 10, 0});
        Random rnd = new Random(4141);
        for (int t = 0; t < 60; t++) {
            int n = 1 + rnd.nextInt(t < 40 ? 20 : 200);
            int range = (t % 3 == 0) ? 3 : (t % 3 == 1 ? 50 : 1_000_000);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(2 * range + 1) - range;
            arrays.add(a);
        }

        int checks = 0, failed = 0;
        String first = null;
        // Demo known answer.
        {
            int[] demo = {10, 4, 5, 8, 6, 11, 26};
            checks++;
            try {
                int got = call(entry, fourArg, demo, 3);
                if (got != 6) { failed++; first = "demo kthSmallest(k=3) = " + got + ", expected 6"; System.out.println("CHECK FAIL " + first); }
            } catch (Throwable t) { failed++; first = "demo threw " + deep(t); System.out.println("CHECK FAIL " + first); }
        }
        outer:
        for (int[] orig : arrays) {
            int[] sorted = orig.clone();
            Arrays.sort(sorted);
            int n = orig.length;
            // every k for small arrays, a spread for large ones
            List<Integer> ks = new ArrayList<>();
            if (n <= 25) for (int k = 1; k <= n; k++) ks.add(k);
            else { ks.add(1); ks.add(n); ks.add(n / 2); ks.add((n + 1) / 2); for (int i = 0; i < 12; i++) ks.add(1 + rnd.nextInt(n)); }
            for (int k : ks) {
                checks++;
                int[] arr = orig.clone();
                String msg = null;
                try {
                    int got = call(entry, fourArg, arr, k);
                    if (got != sorted[k - 1])
                        msg = "kthSmallest(" + abbrev(orig) + ", k=" + k + ") = " + got + ", expected " + sorted[k - 1];
                    else {
                        int[] after = arr.clone();
                        Arrays.sort(after);
                        if (!Arrays.equals(after, sorted))
                            msg = "input array no longer a permutation after k=" + k + ": " + abbrev(orig) + " -> " + abbrev(arr);
                    }
                } catch (Throwable t) {
                    msg = "kthSmallest(" + abbrev(orig) + ", k=" + k + ") threw " + deep(t);
                }
                if (msg != null) {
                    failed++;
                    if (first == null) first = msg;
                    if (failed <= 10) System.out.println("CHECK FAIL " + msg);
                    if (failed >= 25) break outer;
                }
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " checks: " + first);
    }

    static int call(Method m, boolean fourArg, int[] arr, int k) throws Exception {
        Object r = fourArg ? m.invoke(null, arr, 0, arr.length - 1, k) : m.invoke(null, arr, k);
        return ((Number) r).intValue();
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
