import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 030_strongly_connected_components.
 *
 * Baseline contract (iteration 0, strongly_connected_components.java):
 *   class StronglyConnectedComponents { int[][] kosaraju(int V, int[][] adj) }
 *   - instance method, directed graph, vertices 0..V-1
 *   - adj[u] lists out-neighbours of u, padded with -1 (rows of length V)
 *   - returns one row per SCC, each padded with -1; every vertex 0..V-1 appears
 *     in exactly one row (isolated vertices are singleton SCCs)
 *   - rows are emitted in Kosaraju order (decreasing finish time of pass 1), i.e.
 *     a topological order of the condensation DAG (source components first).
 *
 * Iterations 1..10 (default package, then com.algorithms.scc from iter 9):
 *   DirectedGraphBuilder<V>{addVertex(V), addEdge(V,V), build()} -> DirectedGraph<V>
 *   KosarajuStronglyConnectedComponentsFinder<V>.find(DirectedGraph) -> Result
 *   Result.components() -> List<StronglyConnectedComponent>; component.vertices() -> List<V>.
 *   Vertices only exist when registered, so the driver addVertex()es 0..V-1
 *   explicitly (the logical input is identical to the baseline's).
 *
 * Oracle: independent reference Tarjan SCC; partitions compared order-independently
 * (set of sets). Secondary baseline property: component order must be a topological
 * order of the condensation (every inter-component edge goes from an earlier to a
 * later component). Inputs: the baseline demo graph, edge cases, and 40 seeded
 * random graphs (no parallel edges beyond the baseline's per-row capacity).
 */
public class Driver {

    interface Adapter {
        List<List<Integer>> run(int n, int[][] edges) throws Exception;
    }

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
            System.out.println("RESULT FAIL no SCC entry point: " + e.getMessage());
            return;
        }

        List<Case> cases = buildCases();
        int checks = 0, failed = 0;
        for (Case tc : cases) {
            checks++;
            List<Set<Integer>> expected = referenceTarjan(tc.n, tc.edges);
            List<List<Integer>> actual;
            try {
                actual = adapter.run(tc.n, tc.edges);
            } catch (Throwable e) {
                failed++;
                Throwable c = e;
                while (c.getCause() != null && c instanceof java.lang.reflect.InvocationTargetException) c = c.getCause();
                System.out.println("CHECK FAIL exception " + tc + ": " + c);
                continue;
            }
            String err = compare(tc, expected, actual);
            if (err != null) {
                failed++;
                System.out.println("CHECK FAIL " + tc + ": " + err
                        + "\n  expected " + canon(expected) + "\n  actual   " + actual);
            }
        }
        System.out.println(checks + " graphs checked, " + failed + " failed");
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " graphs checked (partition + topological component order)"
                : "RESULT FAIL " + failed + "/" + checks + " graphs gave wrong SCCs");
    }

    // ------------------------------------------------------------------ discovery

    static Adapter discover(List<Class<?>> all) throws Exception {
        // Shape A: baseline int[][] kosaraju(int, int[][]) (instance or static).
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && p[0] == int.class && p[1] == int[][].class
                        && m.getReturnType() == int[][].class
                        && !m.getName().toLowerCase().contains("build")) {
                    m.setAccessible(true);
                    Object inst = null;
                    if (!Modifier.isStatic(m.getModifiers())) {
                        Constructor<?> k = c.getDeclaredConstructor();
                        k.setAccessible(true);
                        inst = k.newInstance();
                    }
                    final Object target = inst;
                    System.out.println("entry (shape A matrix): " + m);
                    return (n, edges) -> {
                        int[][] adj = new int[n][n];
                        for (int[] row : adj) Arrays.fill(row, -1);
                        int[] cnt = new int[n];
                        for (int[] e : edges) adj[e[0]][cnt[e[0]]++] = e[1];
                        int[][] res = (int[][]) m.invoke(target, n, adj);
                        List<List<Integer>> out = new ArrayList<>();
                        for (int[] row : res) {
                            List<Integer> comp = new ArrayList<>();
                            for (int x : row) { if (x == -1) break; comp.add(x); }
                            out.add(comp);
                        }
                        return out;
                    };
                }
            }
        }
        // Shape B: builder + finder objects.
        Class<?> builderCls = null;
        Method addVertex = null, addEdge = null, build = null;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            Method av = null, ae = null, b = null;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                String n = m.getName().toLowerCase();
                if (p.length == 1 && p[0] == Object.class && n.contains("vertex")) av = m;
                else if (p.length == 2 && p[0] == Object.class && p[1] == Object.class && n.contains("edge")) ae = m;
                else if (p.length == 0 && n.equals("build")) b = m;
            }
            if (ae != null && b != null && hasNoArgCtor(c)) {
                builderCls = c; addVertex = av; addEdge = ae; build = b; break;
            }
        }
        if (builderCls == null) throw new IllegalStateException("no graph builder among " + names(all));
        Class<?> graphType = build.getReturnType();
        Method find = null;
        Class<?> finderCls = null;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || !hasNoArgCtor(c)) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && p[0].isAssignableFrom(graphType)
                        && m.getReturnType() != void.class && !p[0].getName().startsWith("java.")) {
                    if (find == null || m.getName().toLowerCase().contains("find")) { find = m; finderCls = c; }
                }
            }
            if (find != null && find.getName().toLowerCase().contains("find")) break;
        }
        if (find == null) throw new IllegalStateException("no finder taking " + graphType.getName());
        System.out.println("entry (shape B objects): builder=" + builderCls.getName()
                + " finder=" + find);
        final Class<?> bc = builderCls;
        final Method fAddVertex = addVertex, fAddEdge = addEdge, fBuild = build, fFind = find;
        final Class<?> fc = finderCls;
        for (Method m : new Method[]{addVertex, addEdge, build, find}) if (m != null) m.setAccessible(true);
        return (n, edges) -> {
            Constructor<?> bk = bc.getDeclaredConstructor();
            bk.setAccessible(true);
            Object b = bk.newInstance();
            if (fAddVertex == null && n > 0) throw new IllegalStateException("no addVertex");
            for (int v = 0; v < n; v++) fAddVertex.invoke(b, Integer.valueOf(v));
            for (int[] e : edges) fAddEdge.invoke(b, Integer.valueOf(e[0]), Integer.valueOf(e[1]));
            Object g = fBuild.invoke(b);
            Constructor<?> fk = fc.getDeclaredConstructor();
            fk.setAccessible(true);
            Object res = fFind.invoke(fk.newInstance(), g);
            List<List<Integer>> out = new ArrayList<>();
            for (Object comp : elements(res, "components")) {
                List<Integer> members = new ArrayList<>();
                for (Object v : elements(comp, "vertices")) members.add((Integer) v);
                out.add(members);
            }
            return out;
        };
    }

    /** Elements of a collection-like result: prefer the named accessor, else any
     *  no-arg Collection accessor, else Iterable. */
    static Iterable<?> elements(Object o, String preferred) throws Exception {
        if (o instanceof Collection<?> c) return c;
        Method best = null;
        for (Method m : o.getClass().getMethods()) {
            if (m.getParameterCount() != 0 || m.isSynthetic() || m.isBridge()) continue;
            if (!Collection.class.isAssignableFrom(m.getReturnType())) continue;
            if (m.getName().equals(preferred)) { best = m; break; }
            if (best == null) best = m;
        }
        if (best != null) { best.setAccessible(true); return (Collection<?>) best.invoke(o); }
        if (o instanceof Iterable<?> it) return it;
        throw new IllegalStateException("cannot enumerate " + o.getClass().getName());
    }

    static boolean hasNoArgCtor(Class<?> c) {
        try { c.getDeclaredConstructor(); return true; } catch (NoSuchMethodException e) { return false; }
    }

    static String names(List<Class<?>> all) {
        return all.stream().map(Class::getName).collect(Collectors.joining(","));
    }

    // ------------------------------------------------------------------ oracle

    static String compare(Case tc, List<Set<Integer>> expected, List<List<Integer>> actual) {
        // Every vertex exactly once, in range.
        int[] compOf = new int[tc.n];
        Arrays.fill(compOf, -1);
        for (int i = 0; i < actual.size(); i++) {
            List<Integer> comp = actual.get(i);
            if (comp.isEmpty()) return "empty component at index " + i;
            for (int v : comp) {
                if (v < 0 || v >= tc.n) return "vertex out of range: " + v;
                if (compOf[v] != -1) return "vertex " + v + " appears in more than one component / twice";
                compOf[v] = i;
            }
        }
        for (int v = 0; v < tc.n; v++) if (compOf[v] == -1) return "vertex " + v + " missing";
        Set<Set<Integer>> exp = new HashSet<>(expected);
        Set<Set<Integer>> act = new HashSet<>();
        for (List<Integer> c : actual) act.add(new HashSet<>(c));
        if (!exp.equals(act)) return "partition differs";
        // Kosaraju order property of the baseline: condensation edges go forward.
        for (int[] e : tc.edges) {
            int a = compOf[e[0]], b = compOf[e[1]];
            if (a > b) return "component order not topological: edge " + e[0] + "->" + e[1]
                    + " goes from component #" + a + " back to #" + b;
        }
        return null;
    }

    static String canon(List<Set<Integer>> comps) {
        List<List<Integer>> l = new ArrayList<>();
        for (Set<Integer> s : comps) { List<Integer> x = new ArrayList<>(s); Collections.sort(x); l.add(x); }
        l.sort(Comparator.comparingInt(x -> x.get(0)));
        return l.toString();
    }

    /** Independent reference: recursive Tarjan. */
    static List<Set<Integer>> referenceTarjan(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        int[] idx = new int[n], low = new int[n];
        Arrays.fill(idx, -1);
        boolean[] on = new boolean[n];
        Deque<Integer> st = new ArrayDeque<>();
        List<Set<Integer>> out = new ArrayList<>();
        int[] counter = {0};
        for (int v = 0; v < n; v++) if (idx[v] == -1) tarjan(v, adj, idx, low, on, st, out, counter);
        return out;
    }

    static void tarjan(int v, List<List<Integer>> adj, int[] idx, int[] low, boolean[] on,
                       Deque<Integer> st, List<Set<Integer>> out, int[] counter) {
        idx[v] = low[v] = counter[0]++;
        st.push(v); on[v] = true;
        for (int w : adj.get(v)) {
            if (idx[w] == -1) { tarjan(w, adj, idx, low, on, st, out, counter); low[v] = Math.min(low[v], low[w]); }
            else if (on[w]) low[v] = Math.min(low[v], idx[w]);
        }
        if (low[v] == idx[v]) {
            Set<Integer> comp = new HashSet<>();
            int w;
            do { w = st.pop(); on[w] = false; comp.add(w); } while (w != v);
            out.add(comp);
        }
    }

    // ------------------------------------------------------------------ inputs

    record Case(String name, int n, int[][] edges) {
        @Override public String toString() {
            return name + " V=" + n + " E=" + (edges.length > 14 ? edges.length + " edges" : Arrays.deepToString(edges));
        }
    }

    static List<Case> buildCases() {
        List<Case> cs = new ArrayList<>();
        // Baseline demo: 1-indexed vertices, V+1 = 6 slots (phantom vertex 0).
        cs.add(new Case("demo", 6, new int[][]{{1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}}));
        cs.add(new Case("single", 1, new int[][]{}));
        cs.add(new Case("single-selfloop", 1, new int[][]{{0, 0}}));
        cs.add(new Case("isolated", 4, new int[][]{}));
        cs.add(new Case("two-cycle", 2, new int[][]{{0, 1}, {1, 0}}));
        cs.add(new Case("dag-chain", 5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}}));
        cs.add(new Case("rev-chain", 5, new int[][]{{4, 3}, {3, 2}, {2, 1}, {1, 0}}));
        cs.add(new Case("big-cycle", 6, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 0}}));
        cs.add(new Case("selfloops", 3, new int[][]{{0, 0}, {1, 1}, {0, 1}, {2, 2}}));
        cs.add(new Case("parallel", 3, new int[][]{{0, 1}, {0, 1}, {1, 0}, {1, 2}}));
        cs.add(new Case("two-sccs-linked", 6, new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 5}, {5, 3}}));
        cs.add(new Case("diamond", 4, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}}));
        cs.add(new Case("clrs", 8, new int[][]{{0, 1}, {1, 2}, {1, 4}, {1, 5}, {2, 3}, {2, 6}, {3, 2}, {3, 7},
                {4, 0}, {4, 5}, {5, 6}, {6, 5}, {6, 7}, {7, 7}}));
        {   // complete digraph
            List<int[]> e = new ArrayList<>();
            for (int i = 0; i < 7; i++) for (int j = 0; j < 7; j++) if (i != j) e.add(new int[]{i, j});
            cs.add(new Case("complete", 7, e.toArray(new int[0][])));
        }
        {   // long path (depth) then back edge closes it into one SCC
            int n = 300;
            List<int[]> e = new ArrayList<>();
            for (int i = 0; i + 1 < n; i++) e.add(new int[]{i, i + 1});
            cs.add(new Case("long-path", n, e.toArray(new int[0][])));
            List<int[]> e2 = new ArrayList<>(e);
            e2.add(new int[]{n - 1, 0});
            cs.add(new Case("long-cycle", n, e2.toArray(new int[0][])));
        }
        Random rnd = new Random(30030);
        for (int t = 0; t < 40; t++) {
            int n = 1 + rnd.nextInt(t < 20 ? 12 : 60);
            double p = switch (t % 5) { case 0 -> 0.02; case 1 -> 0.06; case 2 -> 0.12; case 3 -> 0.3; default -> 0.6; };
            List<int[]> e = new ArrayList<>();
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if ((i != j || t % 3 == 0) && rnd.nextDouble() < p) e.add(new int[]{i, j});
            Collections.shuffle(e, rnd);
            cs.add(new Case("random#" + t, n, e.toArray(new int[0][])));
        }
        return cs;
    }
}
