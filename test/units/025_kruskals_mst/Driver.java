import java.io.File;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 025_kruskals_mst.
 *
 * Baseline semantics (kruskals_mst.java): static int kruskalsMST(int V, int[][] edges)
 * over an UNDIRECTED graph, vertices 0..V-1, rows {u, v, w}. Returns the total
 * weight of the minimum spanning tree. On a DISCONNECTED graph the baseline
 * simply exhausts the edges and returns the minimum spanning FOREST weight
 * (sum of per-component MST weights) — no exception. Self-loops are skipped,
 * parallel edges allowed, negative weights allowed (plain int).
 *
 * API across iterations: iter 1-10 keep a backward-compatible static
 * int mstWeight(int, int[][]) and add static MstResult findMst(int, List<Edge>)
 * whose result exposes edges(), totalWeight(), spanning(). The driver drives the
 * (int, int[][]) weight entry in every iteration; where a (int, List) entry
 * exists it additionally checks its result: total weight, the chosen edge set
 * (compared exactly on graphs with distinct weights, where the MST is unique),
 * edge count and the spanning flag.
 *
 * Oracle: independent Prim-style reference (per component) for the weight, and
 * an independent union-find Kruskal for the unique edge set.
 */
public class Driver {
    static Method weightEntry, richEntry;
    static Constructor<?> edgeCtor;

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .sorted().collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});
        List<Class<?>> loaded = new ArrayList<>();
        for (String cn : classNames) {
            try { loaded.add(Class.forName(cn, false, cl)); } catch (Throwable t) { }
        }
        for (int pass = 0; pass < 2 && weightEntry == null; pass++) {
            for (Class<?> c : loaded) {
                if (c.getName().toLowerCase().contains("test")) continue;
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length != 2 || p[0] != int.class) continue;
                    String n = m.getName().toLowerCase();
                    boolean named = n.contains("mst") || n.contains("kruskal") || n.contains("span");
                    if (pass == 0 && !named) continue;
                    Class<?> r = m.getReturnType();
                    if (p[1] == int[][].class && (r == int.class || r == long.class) && weightEntry == null)
                        weightEntry = m;
                    else if (List.class.isAssignableFrom(p[1]) && r != void.class && !r.isPrimitive()
                            && richEntry == null) {
                        Type g = m.getGenericParameterTypes()[1];
                        if (g instanceof ParameterizedType pt && pt.getActualTypeArguments()[0] instanceof Class<?> ec) {
                            for (Constructor<?> k : ec.getDeclaredConstructors()) {
                                Class<?>[] kp = k.getParameterTypes();
                                if (kp.length == 3 && kp[0] == int.class && kp[1] == int.class && kp[2] == int.class) {
                                    edgeCtor = k; richEntry = m;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (weightEntry == null) {
            System.out.println("RESULT FAIL no MST weight entry (int, int[][]) found among " + classNames);
            return;
        }
        weightEntry.setAccessible(true);
        System.out.println("weight entry: " + weightEntry);
        if (richEntry != null) {
            richEntry.setAccessible(true); edgeCtor.setAccessible(true);
            System.out.println("rich entry: " + richEntry + " edge ctor " + edgeCtor);
        }

        List<Case> cases = new ArrayList<>();
        cases.add(new Case(4, new int[][]{{0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}}, true)); // demo -> 19
        cases.add(new Case(1, new int[][]{}, true));
        cases.add(new Case(2, new int[][]{{0, 1, 7}}, true));
        cases.add(new Case(3, new int[][]{{0, 0, 1}, {0, 1, 5}, {1, 1, -3}, {1, 2, 2}}, true));   // self-loops
        cases.add(new Case(3, new int[][]{{0, 1, 9}, {0, 1, 3}, {1, 2, 4}, {2, 1, 1}}, false));   // parallel edges
        cases.add(new Case(4, new int[][]{{0, 1, -5}, {1, 2, -2}, {2, 3, 7}, {3, 0, -1}}, true)); // negative weights
        cases.add(new Case(5, new int[][]{{0, 1, 3}, {3, 4, 8}}, true));                       // disconnected forest
        cases.add(new Case(4, new int[][]{}, true));                                           // no edges
        cases.add(new Case(4, new int[][]{{0, 1, 2}, {1, 2, 2}, {2, 3, 2}, {3, 0, 2}, {0, 2, 2}}, false)); // ties
        Random rnd = new Random(25025);
        for (int t = 0; t < 40; t++) {
            int v = 1 + rnd.nextInt(t < 20 ? 10 : 60);
            double density = switch (t % 4) { case 0 -> 0.08; case 1 -> 0.25; case 2 -> 0.6; default -> 1.0; };
            boolean distinct = t % 2 == 0;
            boolean neg = t % 5 == 1;
            List<int[]> es = new ArrayList<>();
            Set<Integer> used = new HashSet<>();
            for (int i = 0; i < v; i++)
                for (int j = i + 1; j < v; j++)
                    if (rnd.nextDouble() < density) {
                        int w;
                        if (distinct) { do { w = rnd.nextInt(100000) - (neg ? 50000 : 0); } while (!used.add(w)); }
                        else w = rnd.nextInt(10) - (neg ? 5 : 0);
                        es.add(rnd.nextBoolean() ? new int[]{i, j, w} : new int[]{j, i, w});
                    }
            Collections.shuffle(es, rnd);
            cases.add(new Case(v, es.toArray(new int[0][]), distinct));
        }

        int failed = 0;
        for (Case c : cases) {
            String err;
            try { err = check(c); }
            catch (Throwable e) {
                Throwable x = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
                err = "exception " + x;
            }
            if (err != null) {
                failed++;
                System.out.println("CHECK FAIL V=" + c.v + " edges=" + (c.edges.length <= 10
                        ? Arrays.deepToString(c.edges) : c.edges.length + " edges") + ": " + err);
            }
        }
        System.out.println(cases.size() + " graphs checked, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + cases.size() + " graphs checked"
                + (richEntry != null ? " (weight + rich result)" : "")
                : "RESULT FAIL " + failed + "/" + cases.size() + " graphs gave wrong MST results");
    }

    record Case(int v, int[][] edges, boolean distinct) {}

    static String check(Case c) throws Exception {
        long expW = referenceWeight(c.v, c.edges);
        boolean expSpan = components(c.v, c.edges) <= 1;
        Object r = weightEntry.invoke(null, c.v, deepCopy(c.edges));
        long got = ((Number) r).longValue();
        if (got != expW) return "weight expected " + expW + " got " + got;
        if (richEntry == null) return null;

        List<Object> el = new ArrayList<>();
        for (int[] e : c.edges) el.add(edgeCtor.newInstance(e[0], e[1], e[2]));
        Object res = richEntry.invoke(null, c.v, el);
        Long tw = null; Boolean span = null; List<?> chosen = null;
        for (Method m : res.getClass().getDeclaredMethods()) {
            if (m.isSynthetic() || m.getParameterCount() != 0 || Modifier.isStatic(m.getModifiers())) continue;
            String n = m.getName().toLowerCase();
            m.setAccessible(true);
            if ((n.contains("weight") || n.contains("cost")) && (m.getReturnType() == long.class || m.getReturnType() == int.class))
                tw = ((Number) m.invoke(res)).longValue();
            else if (n.contains("span") || n.contains("connected")) {
                if (m.getReturnType() == boolean.class) span = (Boolean) m.invoke(res);
            } else if (n.equals("edges") && List.class.isAssignableFrom(m.getReturnType()))
                chosen = (List<?>) m.invoke(res);
        }
        if (tw != null && tw != expW) return "rich totalWeight expected " + expW + " got " + tw;
        if (span != null && span != expSpan) return "spanning flag expected " + expSpan + " got " + span;
        if (chosen != null) {
            int comps = components(c.v, c.edges);
            int expCount = c.v - comps;
            if (chosen.size() != expCount) return "chosen edge count expected " + expCount + " got " + chosen.size();
            long sum = 0;
            int[] par = new int[c.v];
            for (int i = 0; i < c.v; i++) par[i] = i;
            List<int[]> got3 = new ArrayList<>();
            for (Object e : chosen) {
                int[] t = edgeInts(e);
                if (!containsEdge(c.edges, t)) return "chosen edge " + Arrays.toString(t) + " not in input";
                int a = find(par, t[0]), b = find(par, t[1]);
                if (a == b) return "chosen edges contain a cycle at " + Arrays.toString(t);
                par[a] = b;
                sum += t[2];
                got3.add(t);
            }
            if (sum != expW) return "chosen edges weigh " + sum + " but MST weight is " + expW;
            if (c.distinct) {
                Set<String> exp = new TreeSet<>(), act = new TreeSet<>();
                for (int[] e : referenceKruskalEdges(c.v, c.edges)) exp.add(key(e));
                for (int[] e : got3) act.add(key(e));
                if (!exp.equals(act)) return "unique MST edge set differs: expected " + exp + " got " + act;
            }
        }
        return null;
    }

    static int[] edgeInts(Object e) throws Exception {
        if (e instanceof int[] a) return a;
        RecordComponent[] rc = e.getClass().getRecordComponents();
        if (rc != null && rc.length == 3) {
            int[] out = new int[3];
            for (int i = 0; i < 3; i++) { Method a = rc[i].getAccessor(); a.setAccessible(true); out[i] = (int) a.invoke(e); }
            return out;
        }
        throw new IllegalStateException("cannot read edge " + e.getClass());
    }

    static boolean containsEdge(int[][] edges, int[] t) {
        for (int[] e : edges)
            if (e[2] == t[2] && ((e[0] == t[0] && e[1] == t[1]) || (e[0] == t[1] && e[1] == t[0]))) return true;
        return false;
    }

    static String key(int[] e) { return Math.min(e[0], e[1]) + "-" + Math.max(e[0], e[1]) + ":" + e[2]; }

    /** Independent reference: Prim's algorithm (O(V^2)) run from every unvisited vertex -> forest weight. */
    static long referenceWeight(int v, int[][] edges) {
        long INFL = Long.MAX_VALUE;
        long[][] w = new long[v][v];
        for (long[] row : w) Arrays.fill(row, INFL);
        for (int[] e : edges) {
            if (e[0] == e[1]) continue;
            if (e[2] < w[e[0]][e[1]]) { w[e[0]][e[1]] = e[2]; w[e[1]][e[0]] = e[2]; }
        }
        boolean[] in = new boolean[v];
        long total = 0;
        for (int root = 0; root < v; root++) {
            if (in[root]) continue;
            long[] best = new long[v];
            Arrays.fill(best, INFL);
            best[root] = 0;
            while (true) {
                int u = -1;
                for (int i = 0; i < v; i++) if (!in[i] && best[i] != INFL && (u < 0 || best[i] < best[u])) u = i;
                if (u < 0) break;
                in[u] = true;
                total += best[u];
                for (int x = 0; x < v; x++) if (!in[x] && w[u][x] < best[x]) best[x] = w[u][x];
            }
        }
        return total;
    }

    static List<int[]> referenceKruskalEdges(int v, int[][] edges) {
        int[][] s = deepCopy(edges);
        Arrays.sort(s, Comparator.comparingInt(e -> e[2]));
        int[] par = new int[v];
        for (int i = 0; i < v; i++) par[i] = i;
        List<int[]> out = new ArrayList<>();
        for (int[] e : s) {
            int a = find(par, e[0]), b = find(par, e[1]);
            if (a != b) { par[a] = b; out.add(e); }
        }
        return out;
    }

    static int components(int v, int[][] edges) {
        int[] par = new int[v];
        for (int i = 0; i < v; i++) par[i] = i;
        int c = v;
        for (int[] e : edges) {
            int a = find(par, e[0]), b = find(par, e[1]);
            if (a != b) { par[a] = b; c--; }
        }
        return c;
    }

    static int find(int[] p, int x) { while (p[x] != x) x = p[x] = p[p[x]]; return x; }

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        return c;
    }
}
