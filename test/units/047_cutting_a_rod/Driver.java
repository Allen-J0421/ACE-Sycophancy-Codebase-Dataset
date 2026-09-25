import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 047_cutting_a_rod.
 *
 * Baseline contract (iteration 0): {@code static int cutRod(int[] price)} where
 * price[k] is the price of a piece of length k, the rod length is
 * price.length - 1, and the result is the maximum obtainable revenue (bottom-up
 * DP). Domain exercised here: price[0] == 0 (the demo's convention) and
 * non-negative prices, price.length >= 1.
 * Iterations 1+ rename it to {@code maxRevenue(int[])} and add
 * {@code solve(int[])} returning a Solution(maxRevenue, cuts).
 *
 * Oracle: an independent top-down memoised recursion
 *   best(len) = max over first piece p in [1, len] of price[p] + best(len - p), best(0) = 0,
 * checked on the baseline demo (22), edge cases and 45 fixed-seed random tables.
 * If a Solution-like result exposes the cuts, their consistency is reported as
 * WARN only (not part of the baseline contract).
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        Method revenue = null;   // int[] -> int
        Method solve = null;     // int[] -> object carrying revenue (+ cuts)
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                if (!Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || p[0] != int[].class) continue;
                Class<?> r = m.getReturnType();
                String n = m.getName().toLowerCase();
                if (r == int.class || r == long.class || r == Integer.class || r == Long.class) {
                    if (revenue == null || n.contains("revenue") || n.contains("cutrod")) revenue = m;
                } else if (!r.isPrimitive() && r != void.class && revenueOf(r) != null) {
                    if (solve == null || n.contains("solve")) solve = m;
                }
            }
        }
        if (revenue == null && solve == null) {
            System.out.println("RESULT FAIL no rod-cutting entry point (static int f(int[])) found");
            return;
        }
        if (revenue != null) revenue.setAccessible(true);
        if (solve != null) solve.setAccessible(true);
        System.out.println("revenue entry: " + revenue);
        System.out.println("solve entry:   " + solve);

        List<int[]> cases = new ArrayList<>();
        cases.add(new int[]{0, 1, 5, 8, 9, 10, 17, 17, 20});        // baseline demo -> 22
        cases.add(new int[]{0});                                   // length-0 rod
        cases.add(new int[]{0, 7});
        cases.add(new int[]{0, 0, 0, 0});                          // all zero
        cases.add(new int[]{0, 3, 5, 8, 9, 10, 17, 17, 20});        // textbook variant -> 24
        cases.add(new int[]{0, 1, 1, 1, 1, 100});                  // single long piece best
        cases.add(new int[]{0, 10, 1, 1, 1, 1, 1});                // all unit pieces best
        Random rnd = new Random(4747);
        for (int t = 0; t < 45; t++) {
            int n = 1 + rnd.nextInt(t < 30 ? 15 : 60);
            int[] price = new int[n + 1];
            int mode = t % 3;
            for (int k = 1; k <= n; k++) {
                if (mode == 0) price[k] = rnd.nextInt(51);                         // arbitrary
                else if (mode == 1) price[k] = price[k - 1] + rnd.nextInt(8);      // non-decreasing
                else price[k] = rnd.nextInt(4) == 0 ? 0 : k * (1 + rnd.nextInt(5)) + rnd.nextInt(7);
            }
            cases.add(price);
        }
        int[] big = new int[201];
        for (int k = 1; k <= 200; k++) big[k] = rnd.nextInt(1000);
        cases.add(big);

        int checks = 0, failed = 0, warns = 0;
        for (int ci = 0; ci < cases.size(); ci++) {
            int[] price = cases.get(ci);
            long exp = reference(price);
            if (ci == 0 && exp != 22) throw new IllegalStateException("oracle broken: demo != 22");
            if (revenue != null) {
                checks++;
                String err = null;
                try {
                    long got = ((Number) revenue.invoke(null, (Object) price.clone())).longValue();
                    if (got != exp) err = "got " + got + ", expected " + exp;
                } catch (Throwable t) { err = "threw " + deep(t); }
                if (err != null) {
                    failed++;
                    System.out.println("CHECK FAIL " + revenue.getName() + " case#" + ci + " "
                            + Arrays.toString(price) + ": " + err);
                }
            }
            if (solve != null) {
                checks++;
                String err = null;
                try {
                    Object sol = solve.invoke(null, (Object) price.clone());
                    long got = revenueValue(sol);
                    if (got != exp) err = "revenue " + got + ", expected " + exp;
                    else {
                        String w = cutsProblem(sol, price, exp);
                        if (w != null) {
                            warns++;
                            System.out.println("WARN " + solve.getName() + " case#" + ci + " cuts: " + w);
                        }
                    }
                } catch (Throwable t) { err = "threw " + deep(t); }
                if (err != null) {
                    failed++;
                    System.out.println("CHECK FAIL " + solve.getName() + " case#" + ci + " "
                            + Arrays.toString(price) + ": " + err);
                }
            }
            if (failed >= 12) break;
        }

        // Informational only: inputs outside the exercised domain (baseline ignores
        // price[0]; negative prices are not a meaningful rod-cutting instance).
        if (revenue != null) {
            info(revenue, new int[]{5, 1, 5, 8}, "price[0]!=0 (baseline ignores price[0])");
            info(revenue, new int[]{0, -3, -1}, "negative prices (baseline floors at 0)");
        }

        System.out.println(checks + " checks, " + failed + " failed, " + warns + " warnings");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " revenues diverged from reference DP");
    }

    /** Independent oracle: top-down memoised recursion over the first piece. */
    static long reference(int[] price) {
        long[] memo = new long[price.length];
        Arrays.fill(memo, Long.MIN_VALUE);
        return best(price.length - 1, price, memo);
    }

    static long best(int len, int[] price, long[] memo) {
        if (len == 0) return 0;
        if (memo[len] != Long.MIN_VALUE) return memo[len];
        long b = 0; // baseline dp starts at 0 (no-cut floor); prices here are non-negative anyway
        for (int p = 1; p <= len; p++) b = Math.max(b, price[p] + best(len - p, price, memo));
        return memo[len] = b;
    }

    static void info(Method m, int[] price, String label) {
        String out;
        try { out = String.valueOf(m.invoke(null, (Object) price.clone())); }
        catch (Throwable t) { out = "threw " + deep(t); }
        System.out.println("INFO (out of domain, not scored) " + label + " " + Arrays.toString(price) + " -> " + out);
    }

    /** Accessor method or field carrying the revenue on a Solution-like result. */
    static Object revenueOf(Class<?> r) {
        Method m = revenueAccessor(r);
        if (m != null) return m;
        for (java.lang.reflect.Field f : r.getDeclaredFields()) {
            Class<?> t = f.getType();
            if ((t == int.class || t == long.class) && f.getName().toLowerCase().contains("revenue")) {
                f.setAccessible(true);
                return f;
            }
        }
        return null;
    }

    static long revenueValue(Object sol) throws Exception {
        Object a = revenueOf(sol.getClass());
        Object v = a instanceof Method ? ((Method) a).invoke(sol) : ((java.lang.reflect.Field) a).get(sol);
        return ((Number) v).longValue();
    }

    static Method revenueAccessor(Class<?> r) {
        for (Method m : r.getDeclaredMethods()) {
            if (m.isSynthetic() || m.getParameterCount() != 0) continue;
            Class<?> t = m.getReturnType();
            if ((t == int.class || t == long.class) && m.getName().toLowerCase().contains("revenue")) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    /** Returns a description of what's wrong with the reported cuts, or null. */
    static String cutsProblem(Object sol, int[] price, long exp) throws Exception {
        Object cuts = null;
        for (Method m : sol.getClass().getDeclaredMethods()) {
            if (m.getParameterCount() == 0 && m.getName().toLowerCase().contains("cut")
                    && List.class.isAssignableFrom(m.getReturnType())) {
                m.setAccessible(true);
                cuts = m.invoke(sol);
            }
        }
        if (cuts == null) {
            try {
                java.lang.reflect.Field f = sol.getClass().getDeclaredField("cuts");
                f.setAccessible(true);
                cuts = f.get(sol);
            } catch (NoSuchFieldException e) { return null; }
        }
        long len = 0, rev = 0;
        for (Object o : (List<?>) cuts) {
            int p = ((Number) o).intValue();
            if (p < 1 || p >= price.length) return "piece " + p + " out of range in " + cuts;
            len += p;
            rev += price[p];
        }
        if (len != price.length - 1) return "pieces " + cuts + " sum to " + len + ", rod length " + (price.length - 1);
        if (rev != exp) return "pieces " + cuts + " earn " + rev + ", optimum " + exp;
        return null;
    }

    static String deep(Throwable t) {
        Throwable c = t.getCause() != null ? t.getCause() : t;
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
