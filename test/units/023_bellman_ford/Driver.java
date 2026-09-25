import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 023_bellman_ford.
 *
 * Baseline contract (iter 0): static int[] bellmanFord(int V, int[][] edges, int src)
 * over directed edges {u, v, w}; unreachable vertices hold (int)1e8 and a negative
 * cycle reachable from the source yields the sentinel array {-1}.
 *
 * Iter 1+ contract: BellmanFord.shortestPaths(WeightedGraph, int) where
 * WeightedGraph.from(int, int[][]) builds the graph. The result is either a
 * "negative cycle" value (iter 1: hasNegativeCycle(); iter 2+: a NegativeCycle
 * type, from iter 6 carrying the cycle's vertices) or a distances value
 * (UNREACHABLE = Integer.MAX_VALUE, distanceTo(int)/all(); from iter 4 also
 * pathTo(int) returning a path whose vertices and total weight are checked here).
 *
 * Oracle: an independent reference Bellman-Ford (V-1 relaxation passes plus one
 * detection pass, negative cycles reachable from the source only), evaluated on
 * the baseline demo plus fixed edge cases and 40 seeded random graphs with
 * negative edges and negative cycles, worst-case chains that need all V-1
 * passes, and 20 larger graphs with parallel edges / self-loops (path and cycle
 * weights are validated with the minimum weight per vertex pair).
 */
public class Driver {

    static final long RINF = Long.MAX_VALUE / 4;
    static final int BASELINE_INF = (int) 1e8;

    record Case(String name, int V, int[][] edges, int src) {}

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> loaded = loadClasses(classes);

        // ---- adapter A: baseline static int[] f(int, int[][], int) ----
        Method arrEntry = null;
        for (Class<?> c : loaded) {
            if (c.getName().toLowerCase().contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 3 && p[0] == int.class && p[1] == int[][].class
                        && p[2] == int.class && m.getReturnType() == int[].class
                        && Modifier.isStatic(m.getModifiers())) {
                    String n = m.getName().toLowerCase();
                    if (n.contains("bellman") || n.contains("shortest") || n.contains("dist")) {
                        m.setAccessible(true);
                        arrEntry = m;
                    }
                }
            }
        }

        // ---- adapter B: static R f(Graph, int) with Graph.from(int, int[][]) ----
        Method objEntry = null, factory = null;
        for (Class<?> c : loaded) {
            if (c.getName().toLowerCase().contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (!Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 2 || p[1] != int.class) continue;
                Class<?> g = p[0];
                if (g.isPrimitive() || g.isArray() || g.getClassLoader() == null) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("shortest") || n.contains("bellman") || n.contains("dist"))) continue;
                Method f = findFactory(g);
                if (f != null) {
                    m.setAccessible(true);
                    objEntry = m;
                    factory = f;
                }
            }
        }

        if (arrEntry == null && objEntry == null) {
            System.out.println("RESULT FAIL no Bellman-Ford entry point found among "
                    + loaded.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }
        System.out.println("array entry: " + arrEntry);
        System.out.println("object entry: " + objEntry + " (factory " + factory + ")");

        List<Case> cases = buildCases();

        int checks = 0, failed = 0;
        for (Case cs : cases) {
            long[] ref = refBellmanFord(cs.V(), cs.edges(), cs.src());

            if (arrEntry != null) {
                checks++;
                try {
                    int[] got = (int[]) arrEntry.invoke(null, cs.V(), cs.edges(), cs.src());
                    String err = checkArray(cs, ref, got);
                    if (err != null) {
                        failed++;
                        System.out.println("CHECK FAIL [" + cs.name() + "] " + err);
                    }
                } catch (Throwable e) {
                    failed++;
                    System.out.println("CHECK FAIL [" + cs.name() + "] array entry threw: " + cause(e));
                }
            }

            if (objEntry != null) {
                checks++;
                try {
                    Object graph = factory.invoke(null, cs.V(), cs.edges());
                    Object res = objEntry.invoke(null, graph, cs.src());
                    String err = checkObject(cs, ref, res);
                    if (err != null) {
                        failed++;
                        System.out.println("CHECK FAIL [" + cs.name() + "] " + err);
                    }
                } catch (Throwable e) {
                    failed++;
                    System.out.println("CHECK FAIL [" + cs.name() + "] object entry threw: " + cause(e));
                }
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    // ---------- result validation ----------

    static String checkArray(Case cs, long[] ref, int[] got) {
        if (ref == null) {
            if (got.length == 1 && got[0] == -1) return null;
            return "expected negative-cycle sentinel {-1}, got " + Arrays.toString(got);
        }
        if (got.length != cs.V())
            return "expected " + cs.V() + " distances, got " + Arrays.toString(got);
        for (int v = 0; v < cs.V(); v++) {
            int expected = ref[v] >= RINF ? BASELINE_INF : (int) ref[v];
            if (got[v] != expected)
                return "dist[" + v + "] = " + got[v] + ", expected " + expected
                        + " (all: " + Arrays.toString(got) + ")";
        }
        return null;
    }

    static String checkObject(Case cs, long[] ref, Object res) throws Exception {
        boolean gotNeg = isNegativeCycleResult(res);
        if (ref == null) {
            if (!gotNeg) return "expected a negative-cycle result, got " + res;
            return validateCycleIfExposed(cs, res);
        }
        if (gotNeg) return "unexpected negative-cycle result " + res + ", expected distances "
                + Arrays.toString(ref);

        long sentinel = sentinelOf(res.getClass());
        Method distTo = findMethod(res.getClass(), new String[]{"distanceto", "distance"}, int.class);
        if (distTo == null) return "cannot read distances from " + res.getClass().getName();
        for (int v = 0; v < cs.V(); v++) {
            long got = ((Number) distTo.invoke(res, v)).longValue();
            boolean refUnreach = ref[v] >= RINF;
            if (refUnreach ? got != sentinel : got != ref[v])
                return "distanceTo(" + v + ") = " + got + ", expected "
                        + (refUnreach ? "UNREACHABLE(" + sentinel + ")" : String.valueOf(ref[v]));
        }
        return validatePathsIfExposed(cs, ref, res, sentinel);
    }

    /** From iter 6 NegativeCycle carries vertices(); verify it is a genuine negative cycle. */
    static String validateCycleIfExposed(Case cs, Object res) throws Exception {
        Method verts = findMethod(res.getClass(), new String[]{"vertices", "cycle"});
        if (verts == null) return null; // earlier iterations expose no cycle detail
        Object vo = verts.invoke(res);
        if (!(vo instanceof List<?> cyc)) return null;
        if (cyc.isEmpty()) return "reported negative cycle has no vertices";
        Map<Long, Integer> w = weightMap(cs.edges());
        long sum = 0;
        for (int i = 0; i < cyc.size(); i++) {
            int a = ((Number) cyc.get(i)).intValue();
            int b = ((Number) cyc.get((i + 1) % cyc.size())).intValue();
            Integer weight = w.get(key(a, b));
            if (weight == null) return "reported cycle " + cyc + " uses nonexistent edge " + a + "->" + b;
            sum += weight;
        }
        if (sum >= 0) return "reported cycle " + cyc + " has non-negative total weight " + sum;
        return null;
    }

    /** From iter 4/6 Distances exposes pathTo(int); verify vertices and total weight. */
    static String validatePathsIfExposed(Case cs, long[] ref, Object res, long sentinel) throws Exception {
        Method pathTo = findMethod(res.getClass(), new String[]{"pathto", "path"}, int.class);
        if (pathTo == null) return null;
        Map<Long, Integer> w = weightMap(cs.edges());
        for (int v = 0; v < cs.V(); v++) {
            Object p = pathTo.invoke(res, v);
            List<?> verts;
            Long tw = null;
            if (p instanceof List<?> l) {
                verts = l;
            } else {
                Method vm = findMethod(p.getClass(), new String[]{"vertices"});
                if (vm == null) return null; // unrecognized path shape: distances already checked
                verts = (List<?>) vm.invoke(p);
                Method twm = findMethod(p.getClass(), new String[]{"totalweight", "weight"});
                if (twm != null) tw = ((Number) twm.invoke(p)).longValue();
            }
            boolean reachable = ref[v] < RINF;
            if (!reachable) {
                if (!verts.isEmpty()) return "pathTo(" + v + ") = " + verts + " but " + v + " is unreachable";
                continue;
            }
            if (verts.isEmpty()) return "pathTo(" + v + ") empty but " + v + " is reachable";
            int first = ((Number) verts.get(0)).intValue();
            int last = ((Number) verts.get(verts.size() - 1)).intValue();
            if (first != cs.src() || last != v)
                return "pathTo(" + v + ") = " + verts + " does not run from " + cs.src() + " to " + v;
            long sum = 0;
            for (int i = 0; i + 1 < verts.size(); i++) {
                int a = ((Number) verts.get(i)).intValue();
                int b = ((Number) verts.get(i + 1)).intValue();
                Integer weight = w.get(key(a, b));
                if (weight == null) return "pathTo(" + v + ") = " + verts + " uses nonexistent edge " + a + "->" + b;
                sum += weight;
            }
            if (sum != ref[v])
                return "pathTo(" + v + ") = " + verts + " has weight " + sum + ", expected " + ref[v];
            if (tw != null && tw != ref[v])
                return "pathTo(" + v + ").totalWeight() = " + tw + ", expected " + ref[v];
        }
        return null;
    }

    static boolean isNegativeCycleResult(Object res) throws Exception {
        Method m = findMethod(res.getClass(), new String[]{"hasnegativecycle"});
        if (m != null && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class))
            return (Boolean) m.invoke(res);
        return res.getClass().getSimpleName().toLowerCase().contains("negativecycle");
    }

    static long sentinelOf(Class<?> c) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Field f : k.getDeclaredFields()) {
                String n = f.getName().toLowerCase();
                if (Modifier.isStatic(f.getModifiers())
                        && (n.contains("unreachable") || n.equals("inf") || n.contains("infinity"))) {
                    try {
                        f.setAccessible(true);
                        return ((Number) f.get(null)).longValue();
                    } catch (Throwable ignored) {}
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    // ---------- independent oracle ----------

    /** Returns shortest distances from src, or null if a negative cycle is reachable from src. */
    static long[] refBellmanFord(int V, int[][] edges, int src) {
        long[] d = new long[V];
        Arrays.fill(d, RINF);
        d[src] = 0;
        for (int pass = 0; pass < V - 1; pass++) {
            boolean improved = false;
            for (int[] e : edges) {
                if (d[e[0]] < RINF && d[e[0]] + e[2] < d[e[1]]) {
                    d[e[1]] = d[e[0]] + e[2];
                    improved = true;
                }
            }
            if (!improved) break;
        }
        for (int[] e : edges) {
            if (d[e[0]] < RINF && d[e[0]] + e[2] < d[e[1]]) return null;
        }
        return d;
    }

    // ---------- cases ----------

    static List<Case> buildCases() {
        List<Case> cases = new ArrayList<>();
        cases.add(new Case("baseline demo", 5, new int[][]{
                {1, 3, 2}, {4, 3, -1}, {2, 4, 1}, {1, 2, 1}, {0, 1, 5}}, 0)); // -> 0 5 6 6 7
        cases.add(new Case("single vertex", 1, new int[][]{}, 0));
        cases.add(new Case("no edges", 3, new int[][]{}, 0));
        cases.add(new Case("unreachable", 3, new int[][]{{1, 2, 5}}, 0));
        cases.add(new Case("negative edge chain", 4,
                new int[][]{{0, 1, 4}, {1, 2, -6}, {2, 3, 2}, {0, 3, 5}}, 0));
        cases.add(new Case("reachable negative cycle", 3,
                new int[][]{{0, 1, 1}, {1, 2, -5}, {2, 1, 2}}, 0));
        cases.add(new Case("unreachable negative cycle", 4,
                new int[][]{{2, 3, -5}, {3, 2, 2}, {0, 1, 1}}, 0));
        cases.add(new Case("zero-weight cycle", 3,
                new int[][]{{0, 1, 2}, {1, 2, 3}, {2, 1, -3}}, 0));
        cases.add(new Case("nonzero source", 4,
                new int[][]{{2, 0, 3}, {0, 1, -2}, {1, 3, 7}}, 2));

        Random rnd = new Random(99);
        for (int t = 0; t < 40; t++) {
            int V = 2 + rnd.nextInt(7);
            List<long[]> pairs = new ArrayList<>();
            for (int u = 0; u < V; u++)
                for (int v = 0; v < V; v++)
                    if (u != v) pairs.add(new long[]{u, v});
            Collections.shuffle(pairs, rnd);
            int m = rnd.nextInt(pairs.size() + 1);
            boolean positiveOnly = rnd.nextInt(3) == 0;
            int[][] edges = new int[m][3];
            for (int i = 0; i < m; i++) {
                edges[i][0] = (int) pairs.get(i)[0];
                edges[i][1] = (int) pairs.get(i)[1];
                edges[i][2] = positiveOnly ? 1 + rnd.nextInt(15) : rnd.nextInt(22) - 7;
            }
            cases.add(new Case("random#" + t, V, edges, rnd.nextInt(V)));
        }

        // Worst-case chains: path 0 -> V-1 -> V-2 -> ... -> 1 with the edges listed
        // last hop first, so neither an edge-list-order nor a vertex-order
        // relaxation sweep can shortcut it: all V-1 passes are needed, and a
        // negative cycle at the far end is only discovered by the final check.
        for (int V : new int[]{6, 12, 20}) {
            int[] p = new int[V];
            for (int k = 1; k < V; k++) p[k] = V - k;
            int[][] chain = new int[V - 1][];
            for (int k = V - 1, i = 0; k >= 1; k--, i++) chain[i] = new int[]{p[k - 1], p[k], -1 - (k % 3)};
            cases.add(new Case("worst-case chain V=" + V, V, chain, 0));
            int[][] cyc = Arrays.copyOf(chain, V);
            cyc[V - 1] = new int[]{p[V - 1], p[V - 3], 1};  // negative cycle at the far end
            cases.add(new Case("worst-case chain + far neg cycle V=" + V, V, cyc, 0));
        }
        // Larger graphs, allowing parallel edges and self-loops (baseline
        // accepts any {u, v, w} list); path/cycle checks use the min weight per pair.
        for (int t = 0; t < 20; t++) {
            int V = 8 + rnd.nextInt(18);
            int m = rnd.nextInt(V * 4);
            int[][] edges = new int[m][];
            boolean positiveOnly = t % 4 == 0;
            for (int i = 0; i < m; i++) {
                int u = rnd.nextInt(V), v = rnd.nextInt(V);
                int w;
                if (u == v) w = positiveOnly ? rnd.nextInt(5) : rnd.nextInt(9) - 1; // mostly harmless self-loops
                else w = positiveOnly ? 1 + rnd.nextInt(30) : rnd.nextInt(40) - 6;
                edges[i] = new int[]{u, v, w};
            }
            cases.add(new Case("large#" + t, V, edges, rnd.nextInt(V)));
        }
        return cases;
    }

    // ---------- helpers ----------

    static long key(int u, int v) {
        return (long) u * 1_000_000L + v;
    }

    static Map<Long, Integer> weightMap(int[][] edges) {
        Map<Long, Integer> w = new HashMap<>();
        for (int[] e : edges) w.merge(key(e[0], e[1]), e[2], Math::min);
        return w;
    }

    static Method findFactory(Class<?> g) {
        for (Method m : g.getDeclaredMethods()) {
            Class<?>[] p = m.getParameterTypes();
            if (Modifier.isStatic(m.getModifiers()) && p.length == 2
                    && p[0] == int.class && p[1] == int[][].class
                    && g.isAssignableFrom(m.getReturnType())) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    static Method findMethod(Class<?> c, String[] names, Class<?>... params) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Method m : k.getDeclaredMethods()) {
                if (!Arrays.equals(m.getParameterTypes(), params)) continue;
                String n = m.getName().toLowerCase();
                for (String want : names) {
                    if (n.equals(want)) {
                        m.setAccessible(true);
                        return m;
                    }
                }
            }
        }
        return null;
    }

    static String cause(Throwable e) {
        Throwable t = e.getCause() != null ? e.getCause() : e;
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }

    static List<Class<?>> loadClasses(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
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
