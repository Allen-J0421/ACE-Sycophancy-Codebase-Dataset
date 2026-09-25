import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 032_ford_fulkerson_max_flow.
 *
 * Baseline (iter 0): class MaxFlow with instance method fordFulkerson(int[][], int, int)
 * over a FIXED 6-vertex capacity matrix (static final V = 6), so every test instance
 * here is a 6x6 matrix.
 *
 * Iterations 1-9: FordFulkersonSolver.solve(FlowNetwork, int, int) -> MaxFlowResult
 * where FlowNetwork has static fromMatrix(int[][]). value() returns int (early) or a
 * Capacity value object with units() (later).
 *
 * Iteration 10: FordFulkersonSolver.solve(MaxFlowProblem) where MaxFlowProblem is a
 * record (FlowNetwork, int source, int sink).
 *
 * Oracle: independent Edmonds-Karp reference over the baseline demo network plus
 * 34 seeded random 6x6 capacity graphs (including varied source/sink pairs and an
 * all-zero graph).
 */
public class Driver {

    interface MaxFlowFn {
        long compute(int[][] cap, int s, int t) throws Exception;
    }

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> cs = loadClasses(classes);

        List<Map.Entry<String, MaxFlowFn>> fns = discover(cs);
        if (fns.isEmpty()) {
            System.out.println("RESULT FAIL no max-flow entry point found among "
                    + cs.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }

        List<int[][]> graphs = new ArrayList<>();
        List<int[]> pairs = new ArrayList<>();

        int[][] demo = {
                { 0, 16, 13, 0, 0, 0 }, { 0, 0, 10, 12, 0, 0 },
                { 0, 4, 0, 0, 14, 0 },  { 0, 0, 9, 0, 0, 20 },
                { 0, 0, 0, 7, 0, 4 },   { 0, 0, 0, 0, 0, 0 }
        };
        graphs.add(demo);            pairs.add(new int[]{0, 5});   // classic answer: 23
        graphs.add(new int[6][6]);   pairs.add(new int[]{0, 5});   // no edges -> 0

        Random rnd = new Random(12345);
        for (int t = 0; t < 34; t++) {
            int[][] g = new int[6][6];
            double density = 0.25 + 0.5 * rnd.nextDouble();
            for (int u = 0; u < 6; u++)
                for (int v = 0; v < 6; v++)
                    if (u != v && rnd.nextDouble() < density)
                        g[u][v] = rnd.nextInt(21);
            int s = 0, k = 5;
            if (t % 5 == 4) {                      // some non-default source/sink pairs
                s = rnd.nextInt(6);
                do { k = rnd.nextInt(6); } while (k == s);
            }
            graphs.add(g);
            pairs.add(new int[]{s, k});
        }
        // Residual back-edge cancellation required (a DFS/greedy path s-1-2-5
        // must be undone): s=0, t=5, answer 2.
        int[][] zig = new int[6][6];
        zig[0][1] = 1; zig[0][2] = 1; zig[1][2] = 1; zig[1][5] = 1; zig[2][5] = 1;
        graphs.add(zig); pairs.add(new int[]{0, 5});
        // BFS (index-order) finds the blocking shortest path 0-1-3-5 first; the
        // true max flow 2 (0-1-4-5 + 0-2-3-5) needs the 1->3 flow cancelled.
        int[][] block = new int[6][6];
        block[0][1] = 1; block[1][4] = 1; block[4][5] = 1;
        block[0][2] = 1; block[2][3] = 1; block[3][5] = 1; block[1][3] = 1;
        graphs.add(block); pairs.add(new int[]{0, 5});
        // Chain through every vertex with one bottleneck, reversed labels.
        int[][] chain = new int[6][6];
        chain[3][1] = 9; chain[1][4] = 2; chain[4][0] = 7; chain[0][2] = 8; chain[2][5] = 6;
        graphs.add(chain); pairs.add(new int[]{3, 5});
        // More random networks: arbitrary source/sink, larger capacities, dense
        // anti-parallel edges.
        for (int t = 0; t < 30; t++) {
            int[][] g = new int[6][6];
            double density = 0.3 + 0.7 * rnd.nextDouble();
            int maxCap = (t % 2 == 0) ? 1000 : 4;
            for (int u = 0; u < 6; u++)
                for (int v = 0; v < 6; v++)
                    if (u != v && rnd.nextDouble() < density)
                        g[u][v] = rnd.nextInt(maxCap + 1);
            int s = rnd.nextInt(6), k;
            do { k = rnd.nextInt(6); } while (k == s);
            graphs.add(g);
            pairs.add(new int[]{s, k});
        }

        int checks = 0, failed = 0;
        for (Map.Entry<String, MaxFlowFn> named : fns) {
          MaxFlowFn fn = named.getValue();
          System.out.println("testing " + named.getKey());
          for (int i = 0; i < graphs.size(); i++) {
            int[][] g = graphs.get(i);
            int s = pairs.get(i)[0], t = pairs.get(i)[1];
            long expected = edmondsKarp(deepCopy(g), s, t);
            checks++;
            long actual;
            int[][] input = deepCopy(g);
            try {
                actual = fn.compute(input, s, t);
            } catch (Throwable e) {
                failed++;
                Throwable cause = e;
                while (cause.getCause() != null) cause = cause.getCause();
                System.out.println("CHECK FAIL [" + named.getKey() + "] case " + i + " s=" + s + " t=" + t
                        + " threw " + cause);
                continue;
            }
            if (actual != expected) {
                failed++;
                System.out.println("CHECK FAIL [" + named.getKey() + "] case " + i + " s=" + s + " t=" + t
                        + " expected maxflow " + expected + " got " + actual
                        + " graph=" + Arrays.deepToString(g));
            } else if (!Arrays.deepEquals(input, g)) {
                failed++;   // baseline works on a residual copy; caller's matrix is untouched
                System.out.println("CHECK FAIL [" + named.getKey() + "] case " + i + " mutated the input capacity matrix");
            }
          }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " max-flow instances"
                : "RESULT FAIL " + failed + "/" + checks + " max-flow values wrong");
    }

    // ------------------------------------------------------------------ discovery

    /**
     * Picks the best-scoring entry method, then returns it bound to the default
     * receiver plus, if the solver class takes a pluggable strategy through a
     * one-arg constructor (e.g. AugmentingPathFinder), one receiver per concrete
     * no-arg implementation of that strategy type.
     */
    static List<Map.Entry<String, MaxFlowFn>> discover(List<Class<?>> cs) {
        Class<?> bestC = null;
        Method bestM = null;
        int bestScore = -1;
        for (Class<?> c : cs) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || c.isEnum()) continue;
            String cn = c.getSimpleName().toLowerCase();
            if (cn.contains("test") || cn.contains("demo") || cn.contains("comparison")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                String mn = m.getName().toLowerCase();
                if (!(mn.contains("solve") || mn.contains("fulkerson")
                        || mn.contains("maxflow") || mn.contains("flow"))) continue;
                if (Modifier.isPrivate(m.getModifiers())) continue;
                Object recv = Modifier.isStatic(m.getModifiers()) ? null : instantiate(c);
                if (!Modifier.isStatic(m.getModifiers()) && recv == null) continue;
                MaxFlowFn fn = adapt(m, recv);
                if (fn == null) continue;
                int score = 0;
                if (cn.contains("fulkerson")) score += 10;
                if (cn.contains("solver")) score += 5;
                if (mn.contains("fulkerson") || mn.equals("solve")) score += 2;
                if (score > bestScore) {
                    bestScore = score;
                    bestC = c;
                    bestM = m;
                    System.out.println("candidate entry: " + m + " (score " + score + ")");
                }
            }
        }
        List<Map.Entry<String, MaxFlowFn>> out = new ArrayList<>();
        if (bestM == null) return out;
        Object recv = Modifier.isStatic(bestM.getModifiers()) ? null : instantiate(bestC);
        out.add(Map.entry("default " + bestC.getSimpleName(), adapt(bestM, recv)));
        if (Modifier.isStatic(bestM.getModifiers())) return out;
        for (Constructor<?> k : bestC.getDeclaredConstructors()) {
            if (k.getParameterCount() != 1) continue;
            Class<?> strat = k.getParameterTypes()[0];
            if (!strat.isInterface() || strat.getClassLoader() == null) continue;
            for (Class<?> impl : cs) {
                if (!strat.isAssignableFrom(impl) || impl.isInterface()
                        || Modifier.isAbstract(impl.getModifiers())) continue;
                Object si = instantiate(impl);
                if (si == null) continue;
                try {
                    k.setAccessible(true);
                    Object r = k.newInstance(si);
                    out.add(Map.entry(bestC.getSimpleName() + "(" + impl.getSimpleName() + ")", adapt(bestM, r)));
                } catch (Throwable ignore) {
                }
            }
        }
        return out;
    }

    static MaxFlowFn adapt(Method m, Object receiver) {
        Class<?>[] p = m.getParameterTypes();
        try {
            m.setAccessible(true);

            // Shape A: (int[][], int, int) -> number   [baseline]
            if (p.length == 3 && p[0] == int[][].class && p[1] == int.class && p[2] == int.class) {
                return (g, s, t) -> unwrap(m.invoke(receiver, g, s, t), 3);
            }
            // Shape B: (Network, int, int) -> result, Network buildable from int[][]
            if (p.length == 3 && p[1] == int.class && p[2] == int.class
                    && !p[0].isPrimitive() && !p[0].isArray()) {
                Method fm = fromMatrix(p[0]);
                if (fm == null) return null;
                return (g, s, t) -> unwrap(m.invoke(receiver, fm.invoke(null, (Object) g), s, t), 3);
            }
            // Shape C: (Problem) -> result, Problem has ctor (Network, int, int)
            if (p.length == 1 && !p[0].isPrimitive() && !p[0].isArray()) {
                for (Constructor<?> k : p[0].getConstructors()) {
                    Class<?>[] kp = k.getParameterTypes();
                    if (kp.length == 3 && kp[1] == int.class && kp[2] == int.class
                            && !kp[0].isPrimitive() && !kp[0].isArray()) {
                        Method fm = fromMatrix(kp[0]);
                        if (fm == null) continue;
                        k.setAccessible(true);
                        return (g, s, t) -> unwrap(
                                m.invoke(receiver, k.newInstance(fm.invoke(null, (Object) g), s, t)), 3);
                    }
                }
            }
        } catch (Throwable ignore) {
        }
        return null;
    }

    static Object instantiate(Class<?> c) {
        try {
            Constructor<?> k = c.getDeclaredConstructor();
            k.setAccessible(true);
            return k.newInstance();
        } catch (Throwable t) {
            return null;
        }
    }

    /** Finds a static factory on {@code type} that builds it from an int[][] matrix. */
    static Method fromMatrix(Class<?> type) {
        for (Method m : type.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers())
                    && m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == int[][].class
                    && type.isAssignableFrom(m.getReturnType())) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    /** Extracts a numeric flow value from an int, MaxFlowResult, Capacity, ... */
    static long unwrap(Object r, int depth) throws Exception {
        if (r instanceof Number n) return n.longValue();
        if (r == null || depth <= 0) throw new RuntimeException("cannot unwrap flow value: " + r);
        for (String name : new String[]{"value", "units", "getValue", "getFlow", "flowValue", "intValue"}) {
            try {
                Method m = r.getClass().getMethod(name);
                m.setAccessible(true);
                return unwrap(m.invoke(r), depth - 1);
            } catch (NoSuchMethodException ignore) {
            }
        }
        throw new RuntimeException("cannot unwrap flow value from " + r.getClass());
    }

    // ------------------------------------------------------------------ oracle

    /** Independent Edmonds-Karp reference. */
    static long edmondsKarp(int[][] cap, int s, int t) {
        int n = cap.length;
        long flow = 0;
        int[] parent = new int[n];
        while (true) {
            Arrays.fill(parent, -1);
            parent[s] = s;
            ArrayDeque<Integer> q = new ArrayDeque<>();
            q.add(s);
            while (!q.isEmpty() && parent[t] == -1) {
                int u = q.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && cap[u][v] > 0) {
                        parent[v] = u;
                        q.add(v);
                    }
                }
            }
            if (parent[t] == -1) break;
            int bottleneck = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parent[v]) bottleneck = Math.min(bottleneck, cap[parent[v]][v]);
            for (int v = t; v != s; v = parent[v]) {
                cap[parent[v]][v] -= bottleneck;
                cap[v][parent[v]] += bottleneck;
            }
            flow += bottleneck;
        }
        return flow;
    }

    static int[][] deepCopy(int[][] g) {
        int[][] c = new int[g.length][];
        for (int i = 0; i < g.length; i++) c[i] = Arrays.copyOf(g[i], g[i].length);
        return c;
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
