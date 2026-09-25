import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 019_matrix_chain_multiplication.
 *
 * Baseline semantics (iteration 0): minimum scalar multiplications to evaluate
 * a matrix chain described by a dimension array (matrix i is arr[i] x arr[i+1]):
 *   static int matrixMultiplication(int[] arr)
 * Iterations 1+: matrixchain package — MatrixChainSolver.solve(MatrixDimensions)
 * returning MatrixChainResult with long minimumCost() (dims must be >= 2 entries,
 * all positive).
 *
 * Oracle: independent O(n^3) interval DP over the demo instance, known-answer
 * cases, and 30 random positive-dimension chains with a fixed seed.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        Solver solver = findArrayEntry(all);
        if (solver == null) solver = findSolverEntry(all);
        if (solver == null) {
            System.out.println("RESULT FAIL no matrix-chain entry point found");
            return;
        }

        List<int[]> instances = new ArrayList<>();
        instances.add(new int[]{2, 1, 3, 4});          // baseline demo
        instances.add(new int[]{10, 20, 30});          // single product -> 6000
        instances.add(new int[]{5, 7});                // one matrix -> 0
        instances.add(new int[]{1, 2, 3, 4, 3});
        instances.add(new int[]{40, 20, 30, 10, 30});  // CLRS-style -> 26000
        Random rnd = new Random(1919);
        for (int t = 0; t < 30; t++) {
            int n = 2 + rnd.nextInt(11);
            int[] dims = new int[n];
            for (int i = 0; i < n; i++) dims[i] = 1 + rnd.nextInt(15);
            instances.add(dims);
        }
        // wider values / longer chains (still within the baseline's int range:
        // worst case < 25 * 100^3)
        for (int t = 0; t < 15; t++) {
            int n = 10 + rnd.nextInt(16);
            int[] dims = new int[n];
            for (int i = 0; i < n; i++) dims[i] = 1 + rnd.nextInt(100);
            instances.add(dims);
        }
        instances.add(new int[]{1, 1, 1, 1, 1, 1});    // all-ones: cost = matrices - 1
        instances.add(new int[]{100, 1, 100, 1, 100}); // order matters a lot

        int checks = 0, failed = 0;
        for (int[] dims : instances) {
            long expected = refMcm(dims);
            checks++;
            long got;
            try {
                got = solver.minCost(dims);
            } catch (Throwable t) {
                failed++;
                System.out.println("CHECK FAIL dims=" + Arrays.toString(dims) + " threw " + deep(t));
                continue;
            }
            if (got != expected) {
                failed++;
                System.out.println("CHECK FAIL dims=" + Arrays.toString(dims) + " -> " + got
                        + ", expected " + expected);
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " instances"
                : "RESULT FAIL " + failed + "/" + checks + " minimum costs wrong");
    }

    /** Independent reference: interval DP, cost of (a x b)(b x c) = a*b*c. */
    static long refMcm(int[] d) {
        int n = d.length - 1;                 // number of matrices
        long[][] dp = new long[n + 1][n + 1]; // 1-based inclusive intervals
        for (int len = 2; len <= n; len++) {
            for (int i = 1; i + len - 1 <= n; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long c = dp[i][k] + dp[k + 1][j] + (long) d[i - 1] * d[k] * d[j];
                    if (c < dp[i][j]) dp[i][j] = c;
                }
            }
        }
        return n == 0 ? 0 : dp[1][n];
    }

    // ------------------------------------------------------------- discovery

    interface Solver { long minCost(int[] dims) throws Exception; }

    /** Shape A: a numeric method taking a single int[] of dimensions. */
    static Solver findArrayEntry(List<Class<?>> all) {
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || p[0] != int[].class || !isNumeric(m.getReturnType())) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("matrix") || n.contains("chain") || n.contains("mult")
                        || n.contains("mcm") || n.contains("mincost") || n.contains("solve"))) continue;
                m.setAccessible(true);
                Object receiver = null;
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        Constructor<?> ct = c.getDeclaredConstructor();
                        ct.setAccessible(true);
                        receiver = ct.newInstance();
                    } catch (Throwable t) { continue; }
                }
                System.out.println("entry (array): " + m);
                final Object recv = receiver;
                final Method mm = m;
                return dims -> ((Number) mm.invoke(recv, (Object) dims)).longValue();
            }
        }
        return null;
    }

    /** Shape B: solver.solve(Dimensions) -> result with a numeric *cost* accessor. */
    static Solver findSolverEntry(List<Class<?>> all) {
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().toLowerCase().contains("solve") || m.getParameterCount() != 1) continue;
                Class<?> dimType = m.getParameterTypes()[0];
                Class<?> resType = m.getReturnType();
                if (dimType.isPrimitive() || dimType.getName().startsWith("java.")) continue;
                if (resType == void.class) continue;

                // Build the dimensions argument: static factory of(int[]) or ctor(int[]).
                Method of = null;
                Constructor<?> dimCtor = null;
                for (Method f : dimType.getDeclaredMethods()) {
                    if (Modifier.isStatic(f.getModifiers()) && f.getParameterCount() == 1
                            && f.getParameterTypes()[0] == int[].class
                            && dimType.isAssignableFrom(f.getReturnType())) { of = f; break; }
                }
                if (of == null) {
                    try { dimCtor = dimType.getDeclaredConstructor(int[].class); }
                    catch (NoSuchMethodException ignored) {}
                }
                if (of == null && dimCtor == null) continue;

                // Cost accessor on the result: no-arg numeric method mentioning "cost".
                Method costAcc = null;
                for (Method r : resType.getDeclaredMethods()) {
                    if (r.getParameterCount() == 0 && isNumeric(r.getReturnType())
                            && r.getName().toLowerCase().contains("cost")) { costAcc = r; break; }
                }
                if (costAcc == null) continue;

                Object receiver = null;
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        Constructor<?> ct = c.getDeclaredConstructor();
                        ct.setAccessible(true);
                        receiver = ct.newInstance();
                    } catch (Throwable t) { continue; }
                }
                m.setAccessible(true); costAcc.setAccessible(true);
                if (of != null) of.setAccessible(true);
                if (dimCtor != null) dimCtor.setAccessible(true);
                System.out.println("entry (solver): " + m + " via " + (of != null ? of : dimCtor)
                        + ", cost=" + costAcc);
                final Object recv = receiver;
                final Method solve = m, factory = of, cost = costAcc;
                final Constructor<?> dc = dimCtor;
                return dims -> {
                    Object dimObj = factory != null ? factory.invoke(null, (Object) dims)
                                                    : dc.newInstance((Object) dims);
                    Object res = solve.invoke(recv, dimObj);
                    return ((Number) cost.invoke(res)).longValue();
                };
            }
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

    static List<Class<?>> scan(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
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
