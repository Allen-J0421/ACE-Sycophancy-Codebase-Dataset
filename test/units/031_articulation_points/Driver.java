import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 031_articulation_points.
 *
 * Baseline contract (iteration 0, articulation_points.java):
 *   static ArrayList<Integer> ArticulationPoints.articulationPoints(int V, ArrayList<ArrayList<Integer>> adj)
 *   - UNDIRECTED graph, vertices 0..V-1, adj holds both directions of every edge
 *     (parallel edges and self-loops tolerated); graph may be disconnected
 *   - returns the articulation points in ascending order, or [-1] when there are none.
 *
 * Later iterations:
 *   1-4 : new ArticulationPointFinder().find(Graph) -> List<Integer> (empty when none);
 *         Graph(int n) + addEdge(int,int)
 *   5-7 : new GraphConnectivity().analyze(Graph) -> ConnectivityResult.articulationPoints()
 *   8-10: analyze(Graph interface); concrete UndirectedGraph(int) + addEdge(int,int)
 *         (DirectedGraph also implements Graph and must NOT be used)
 *   The "[-1] when none" sentinel moved to the demo's formatter at iteration 1, so the
 *   driver normalises [-1] and [] to the same "no articulation points" answer.
 *
 * Oracle: independent recursive low-link articulation-point reference, itself
 * cross-checked against brute force (v is a cut vertex iff removing it increases the
 * number of connected components). Exact ascending list compared.
 * Bridges are not part of the baseline's observable behaviour, so they are only
 * reported informationally (never affect the RESULT).
 */
public class Driver {

    interface Adapter {
        List<Integer> run(int n, int[][] edges) throws Exception;
    }

    static Method bridgesAccessor; // informational only
    static Object lastResult;

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .sorted()
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});
        List<Class<?>> all = new ArrayList<>();
        for (String cn : classNames) {
            try { all.add(Class.forName(cn, false, cl)); } catch (Throwable t) { /* skip */ }
        }

        Adapter adapter;
        try {
            adapter = discover(all);
        } catch (Exception e) {
            System.out.println("RESULT FAIL no articulation-point entry point: " + e.getMessage());
            return;
        }

        List<Case> cases = buildCases();
        int checks = 0, failed = 0, bridgeInfoMismatch = 0, bridgeInfoChecked = 0;
        for (Case tc : cases) {
            checks++;
            List<Integer> expected = referenceAP(tc.n, tc.edges);
            List<Integer> brute = bruteForceAP(tc.n, tc.edges);
            if (!expected.equals(brute)) {
                System.out.println("RESULT FAIL driver oracle self-check failed on " + tc);
                return;
            }
            List<Integer> actual;
            lastResult = null;
            try {
                actual = adapter.run(tc.n, tc.edges);
            } catch (Throwable e) {
                failed++;
                Throwable c = e;
                while (c instanceof java.lang.reflect.InvocationTargetException && c.getCause() != null) c = c.getCause();
                System.out.println("CHECK FAIL exception " + tc + ": " + c);
                continue;
            }
            if (!expected.equals(actual)) {
                failed++;
                System.out.println("CHECK FAIL " + tc + "\n  expected " + expected + "\n  actual   " + actual);
            }
            if (bridgesAccessor != null && lastResult != null) {
                bridgeInfoChecked++;
                try {
                    Set<String> act = new TreeSet<>();
                    for (Object e : (Collection<?>) bridgesAccessor.invoke(lastResult)) act.add(edgeKey(e));
                    Set<String> exp = new TreeSet<>(bruteForceBridges(tc.n, tc.edges));
                    if (!exp.equals(act)) {
                        bridgeInfoMismatch++;
                        System.out.println("INFO bridge mismatch " + tc + " expected " + exp + " actual " + act);
                    }
                } catch (Throwable t) {
                    bridgeInfoMismatch++;
                    System.out.println("INFO bridge read failed: " + t);
                }
            }
        }
        String info = bridgeInfoChecked > 0
                ? " (info: bridges " + (bridgeInfoChecked - bridgeInfoMismatch) + "/" + bridgeInfoChecked + " match)"
                : "";
        System.out.println(checks + " graphs checked, " + failed + " failed" + info);
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " graphs checked" + info
                : "RESULT FAIL " + failed + "/" + checks + " graphs gave wrong articulation points");
    }

    // ------------------------------------------------------------------ discovery

    static Adapter discover(List<Class<?>> all) throws Exception {
        // Shape A: baseline static List articulationPoints(int, ArrayList<ArrayList<Integer>>).
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && p[0] == int.class && p[1].isAssignableFrom(ArrayList.class)
                        && List.class.isAssignableFrom(m.getReturnType())
                        && m.getName().toLowerCase().contains("articulation")) {
                    m.setAccessible(true);
                    System.out.println("entry (shape A adjacency list): " + m);
                    return (n, edges) -> {
                        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
                        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
                        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
                        return normalise((List<?>) m.invoke(null, n, adj));
                    };
                }
            }
        }
        // Shape B: instance method on a no-arg class taking an undirected graph object.
        Method entry = null;
        int bestScore = -1;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || !hasNoArgCtor(c)) continue;
            String cn = c.getSimpleName().toLowerCase();
            if (cn.contains("strongly") || cn.contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || p[0].isPrimitive() || p[0].isArray() || p[0].getName().startsWith("java.")) continue;
                if (p[0].getSimpleName().startsWith("Directed")) continue;
                Class<?> r = m.getReturnType();
                int score;
                if (List.class.isAssignableFrom(r)) score = 1;
                else if (apAccessor(r) != null) score = 2;
                else continue;
                String n = m.getName().toLowerCase();
                if (n.contains("find") || n.contains("analy") || n.contains("articulation")) score += 2;
                if (cn.contains("articulation") || cn.contains("connectivity")) score += 2;
                if (score > bestScore) { bestScore = score; entry = m; }
            }
        }
        if (entry == null) throw new IllegalStateException("no finder among " + names(all));
        entry.setAccessible(true);
        Class<?> graphParam = entry.getParameterTypes()[0];
        // Concrete undirected graph class assignable to the parameter.
        Class<?> graphCls = null;
        Method addEdge = null;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || !graphParam.isAssignableFrom(c)) continue;
            if (c.getSimpleName().startsWith("Directed")) continue;
            Method ae = null;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && p[0] == int.class && p[1] == int.class
                        && m.getName().toLowerCase().contains("edge")) ae = m;
            }
            if (ae != null && intCtor(c) != null) { graphCls = c; addEdge = ae; break; }
        }
        if (graphCls == null) throw new IllegalStateException("no undirected graph class for " + graphParam.getName());
        addEdge.setAccessible(true);
        final Constructor<?> gk = intCtor(graphCls);
        gk.setAccessible(true);
        final Constructor<?> fk = entry.getDeclaringClass().getDeclaredConstructor();
        fk.setAccessible(true);
        final Method fEntry = entry, fAddEdge = addEdge;
        final Method acc = List.class.isAssignableFrom(entry.getReturnType()) ? null : apAccessor(entry.getReturnType());
        if (acc != null) {
            acc.setAccessible(true);
            for (Method m : entry.getReturnType().getMethods())
                if (m.getParameterCount() == 0 && m.getName().toLowerCase().contains("bridge")
                        && Collection.class.isAssignableFrom(m.getReturnType())) { bridgesAccessor = m; m.setAccessible(true); }
        }
        System.out.println("entry (shape B objects): " + entry + " graph=" + graphCls.getName()
                + (acc != null ? " accessor=" + acc.getName() : ""));
        return (n, edges) -> {
            Object g = gk.newInstance(n);
            for (int[] e : edges) fAddEdge.invoke(g, e[0], e[1]);
            Object res = fEntry.invoke(fk.newInstance(), g);
            lastResult = res;
            return normalise((List<?>) (acc == null ? res : acc.invoke(res)));
        };
    }

    static Method apAccessor(Class<?> r) {
        if (r.getName().startsWith("java.")) return null;
        for (Method m : r.getMethods())
            if (m.getParameterCount() == 0 && List.class.isAssignableFrom(m.getReturnType())
                    && m.getName().toLowerCase().contains("articulation")) return m;
        return null;
    }

    static Constructor<?> intCtor(Class<?> c) {
        for (Constructor<?> k : c.getDeclaredConstructors()) {
            Class<?>[] p = k.getParameterTypes();
            if (p.length == 1 && p[0] == int.class) return k;
        }
        return null;
    }

    /** [-1] (baseline sentinel) and [] both mean "no articulation points". */
    static List<Integer> normalise(List<?> raw) {
        List<Integer> out = new ArrayList<>();
        for (Object o : raw) out.add(((Number) o).intValue());
        if (out.size() == 1 && out.get(0) == -1) out.clear();
        return out;
    }

    static String edgeKey(Object edge) throws Exception {
        int u = (int) edge.getClass().getMethod("u").invoke(edge);
        int v = (int) edge.getClass().getMethod("v").invoke(edge);
        return Math.min(u, v) + "-" + Math.max(u, v);
    }

    static boolean hasNoArgCtor(Class<?> c) {
        try { c.getDeclaredConstructor(); return true; } catch (NoSuchMethodException e) { return false; }
    }

    static String names(List<Class<?>> all) {
        return all.stream().map(Class::getName).collect(Collectors.joining(","));
    }

    // ------------------------------------------------------------------ oracle

    /** Independent reference: recursive low-link (Hopcroft-Tarjan), parent skipped by edge id. */
    static List<Integer> referenceAP(int n, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int id = 0; id < edges.length; id++) {
            int[] e = edges[id];
            if (e[0] == e[1]) continue;
            adj.get(e[0]).add(new int[]{e[1], id});
            adj.get(e[1]).add(new int[]{e[0], id});
        }
        int[] disc = new int[n], low = new int[n];
        Arrays.fill(disc, -1);
        boolean[] ap = new boolean[n];
        int[] timer = {0};
        for (int r = 0; r < n; r++) {
            if (disc[r] != -1) continue;
            int kids = apDfs(r, -1, adj, disc, low, ap, timer);
            ap[r] = kids > 1;
        }
        List<Integer> out = new ArrayList<>();
        for (int v = 0; v < n; v++) if (ap[v]) out.add(v);
        return out;
    }

    static int apDfs(int u, int parentEdge, List<List<int[]>> adj, int[] disc, int[] low, boolean[] ap, int[] timer) {
        disc[u] = low[u] = timer[0]++;
        int kids = 0;
        for (int[] pe : adj.get(u)) {
            int v = pe[0], id = pe[1];
            if (id == parentEdge) continue;
            if (disc[v] == -1) {
                kids++;
                apDfs(v, id, adj, disc, low, ap, timer);
                low[u] = Math.min(low[u], low[v]);
                if (parentEdge != -1 && low[v] >= disc[u]) ap[u] = true;
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        return kids;
    }

    static int components(int n, int[][] edges, int removedVertex, int removedEdge) {
        int[] par = new int[n];
        for (int i = 0; i < n; i++) par[i] = i;
        for (int id = 0; id < edges.length; id++) {
            if (id == removedEdge) continue;
            int a = edges[id][0], b = edges[id][1];
            if (a == removedVertex || b == removedVertex) continue;
            par[find(par, a)] = find(par, b);
        }
        int c = 0;
        for (int i = 0; i < n; i++) if (i != removedVertex && find(par, i) == i) c++;
        return c;
    }

    static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }

    static List<Integer> bruteForceAP(int n, int[][] edges) {
        int base = components(n, edges, -1, -1);
        List<Integer> out = new ArrayList<>();
        for (int v = 0; v < n; v++) if (components(n, edges, v, -1) > base) out.add(v);
        return out;
    }

    static List<String> bruteForceBridges(int n, int[][] edges) {
        int base = components(n, edges, -1, -1);
        List<String> out = new ArrayList<>();
        for (int id = 0; id < edges.length; id++)
            if (components(n, edges, -1, id) > base)
                out.add(Math.min(edges[id][0], edges[id][1]) + "-" + Math.max(edges[id][0], edges[id][1]));
        return out;
    }

    // ------------------------------------------------------------------ inputs

    record Case(String name, int n, int[][] edges) {
        @Override public String toString() {
            return name + " V=" + n + " E=" + (edges.length > 14 ? edges.length + " edges" : Arrays.deepToString(edges));
        }
    }

    static List<Case> buildCases() {
        List<Case> cs = new ArrayList<>();
        cs.add(new Case("demo", 5, new int[][]{{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}}));
        cs.add(new Case("single", 1, new int[][]{}));
        cs.add(new Case("isolated", 4, new int[][]{}));
        cs.add(new Case("one-edge", 2, new int[][]{{0, 1}}));
        cs.add(new Case("path3", 3, new int[][]{{0, 1}, {1, 2}}));
        cs.add(new Case("triangle", 3, new int[][]{{0, 1}, {1, 2}, {2, 0}}));
        cs.add(new Case("star", 5, new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}}));
        cs.add(new Case("star-rooted-at-leaf", 5, new int[][]{{4, 0}, {4, 1}, {4, 2}, {4, 3}}));
        cs.add(new Case("bowtie", 5, new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 2}}));
        cs.add(new Case("self-loops", 4, new int[][]{{0, 0}, {0, 1}, {1, 1}, {1, 2}, {3, 3}}));
        cs.add(new Case("parallel", 4, new int[][]{{0, 1}, {0, 1}, {1, 2}, {2, 3}, {2, 3}}));
        cs.add(new Case("disconnected", 8, new int[][]{{0, 1}, {1, 2}, {3, 4}, {4, 5}, {5, 3}, {6, 7}}));
        cs.add(new Case("two-cycles-bridge", 6, new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 5}, {5, 3}}));
        {
            int n = 800;
            List<int[]> e = new ArrayList<>();
            for (int i = 0; i + 1 < n; i++) e.add(new int[]{i, i + 1});
            cs.add(new Case("long-path", n, e.toArray(new int[0][])));
            List<int[]> e2 = new ArrayList<>(e);
            e2.add(new int[]{n - 1, 0});
            cs.add(new Case("long-cycle", n, e2.toArray(new int[0][])));
        }
        Random rnd = new Random(31031);
        for (int t = 0; t < 45; t++) {
            int n = 1 + rnd.nextInt(t < 20 ? 10 : 45);
            double p = switch (t % 5) { case 0 -> 0.03; case 1 -> 0.08; case 2 -> 0.15; case 3 -> 0.3; default -> 0.55; };
            List<int[]> e = new ArrayList<>();
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (rnd.nextDouble() < p) e.add(rnd.nextBoolean() ? new int[]{i, j} : new int[]{j, i});
            if (t % 4 == 0 && n > 1) { // sprinkle parallels and self-loops
                for (int k = 0; k < 3; k++) {
                    int a = rnd.nextInt(n);
                    e.add(new int[]{a, a});
                    if (!e.isEmpty()) { int[] d = e.get(rnd.nextInt(e.size())); e.add(new int[]{d[1], d[0]}); }
                }
            }
            if (t % 7 == 3) { // tree-like: random spanning tree plus few extras
                e.clear();
                for (int v = 1; v < n; v++) e.add(new int[]{rnd.nextInt(v), v});
                for (int k = 0; k < n / 6; k++) e.add(new int[]{rnd.nextInt(n), rnd.nextInt(n)});
            }
            Collections.shuffle(e, rnd);
            cs.add(new Case("random#" + t, n, e.toArray(new int[0][])));
        }
        return cs;
    }
}
