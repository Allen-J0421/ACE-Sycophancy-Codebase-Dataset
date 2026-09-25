import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 006_topological_sorting_bfs.
 *
 * Baseline semantics (iteration 0): Kahn's algorithm (BFS over in-degrees).
 * Entry point: static ArrayList<Integer> topoSort(ArrayList<ArrayList<Integer>> adj)
 * where adj.get(u) lists the successors of u (directed edge u -> v).
 * On a DAG it returns a topological order of all n vertices. On a cyclic
 * graph the baseline silently returns only the "peelable" vertices (those
 * repeatedly removable at in-degree 0); later iterations instead throw to
 * reject cycles. Both are accepted for cyclic input; DAG behavior is checked
 * with the standard property oracle: the result is a permutation of 0..n-1
 * and every edge u->v has u before v.
 *
 * The entry point is located by reflection (name contains topo/kahn/sort,
 * one List-like parameter, List-like or int[] return) so renames and
 * repackaging across iterations are tolerated.
 */
public class Driver {

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Method entry = null;
        Object receiver = null;
        int bestScore = -1;
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1) continue;
                if (!p[0].isAssignableFrom(ArrayList.class)) continue; // accepts ArrayList/List/Collection/Iterable/Object
                Class<?> r = m.getReturnType();
                boolean listReturn = Collection.class.isAssignableFrom(r) || r == int[].class;
                if (!listReturn) continue;
                String n = m.getName().toLowerCase();
                int score = -1;
                if (n.contains("topo") || n.contains("kahn")) score = 2;
                else if (n.contains("sort") || n.contains("order")) score = 1;
                if (score <= bestScore) continue;
                Object recv = null;
                if (!Modifier.isStatic(m.getModifiers())) {
                    try { recv = c.getDeclaredConstructor().newInstance(); }
                    catch (Throwable t) { continue; }
                }
                m.setAccessible(true);
                entry = m;
                receiver = recv;
                bestScore = score;
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no topological-sort entry point found among " + classNames);
            return;
        }
        System.out.println("entry: " + entry);

        List<int[][]> dags = new ArrayList<>();   // each: {n}, then edges {u,v}
        // Baseline demo graph.
        dags.add(g(6, e(0,1), e(1,2), e(2,3), e(4,5), e(5,1), e(5,2)));
        // Edge cases.
        dags.add(g(0));                                        // empty graph
        dags.add(g(1));                                        // single vertex, no edges
        dags.add(g(2, e(0,1)));
        dags.add(g(2, e(1,0)));
        dags.add(g(5));                                        // 5 isolated vertices
        dags.add(g(4, e(3,2), e(2,1), e(1,0)));                // reversed chain
        dags.add(g(6, e(0,1), e(0,2), e(1,3), e(2,3), e(4,5))); // disconnected diamond + pair
        dags.add(g(5, e(0,1), e(0,1), e(1,2), e(3,4)));        // duplicate edge
        dags.add(g(7, e(6,0), e(5,0), e(4,1), e(3,1), e(2,0), e(2,1))); // many sinks/sources
        // 32 randomized DAGs of varied size/density (edges only forward along a random permutation).
        Random rnd = new Random(20260925);
        for (int t = 0; t < 32; t++) {
            int n = rnd.nextInt(40) + (t % 4 == 0 ? 0 : 1); // sizes 0..40
            List<int[]> edges = new ArrayList<>();
            Integer[] perm = new Integer[n];
            for (int i = 0; i < n; i++) perm[i] = i;
            Collections.shuffle(Arrays.asList(perm), rnd);
            double p = rnd.nextDouble() * 0.5;
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (rnd.nextDouble() < p) edges.add(new int[]{perm[i], perm[j]});
            int[][] gr = new int[edges.size() + 1][];
            gr[0] = new int[]{n};
            for (int i = 0; i < edges.size(); i++) gr[i + 1] = edges.get(i);
            dags.add(gr);
        }

        List<int[][]> cyclics = new ArrayList<>();
        cyclics.add(g(3, e(0,1), e(1,2), e(2,0)));                    // pure 3-cycle
        cyclics.add(g(1, e(0,0)));                                    // self-loop
        cyclics.add(g(2, e(0,1), e(1,0)));                            // 2-cycle
        cyclics.add(g(5, e(0,1), e(1,2), e(2,3), e(3,1), e(0,4)));    // tail + cycle (peelable prefix 0,4)
        cyclics.add(g(6, e(0,1), e(1,2), e(2,0), e(3,4), e(4,5)));    // cycle + separate chain
        cyclics.add(g(4, e(0,1), e(1,0), e(2,3), e(3,2)));            // two disjoint cycles
        for (int t = 0; t < 6; t++) {                                 // random DAG + back edge
            int n = rnd.nextInt(15) + 3;
            List<int[]> edges = new ArrayList<>();
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (rnd.nextDouble() < 0.3) edges.add(new int[]{i, j});
            int a = rnd.nextInt(n - 1) + 1, b = rnd.nextInt(a);
            edges.add(new int[]{b, a});
            edges.add(new int[]{a, b});                               // close a cycle b->a->b
            int[][] gr = new int[edges.size() + 1][];
            gr[0] = new int[]{n};
            for (int i = 0; i < edges.size(); i++) gr[i + 1] = edges.get(i);
            cyclics.add(gr);
        }

        int checks = 0, failed = 0;
        for (int[][] spec : dags) {
            checks++;
            if (!checkDag(entry, receiver, spec)) failed++;
        }
        for (int[][] spec : cyclics) {
            checks++;
            if (!checkCyclic(entry, receiver, spec)) failed++;
        }

        System.out.println(checks + " graphs checked, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " graphs"
                                       : "RESULT FAIL " + failed + "/" + checks + " graphs failed");
    }

    // ---- checks -----------------------------------------------------------

    static boolean checkDag(Method entry, Object receiver, int[][] spec) {
        int n = spec[0][0];
        Object result;
        try {
            result = entry.invoke(receiver, buildAdj(spec));
        } catch (Throwable t) {
            Throwable cause = t.getCause() != null ? t.getCause() : t;
            System.out.println("CHECK FAIL " + describe(spec) + " -> exception on DAG: " + cause);
            return false;
        }
        List<Integer> order = toIntList(result);
        if (order == null) {
            System.out.println("CHECK FAIL " + describe(spec) + " -> unusable return value: " + result);
            return false;
        }
        String err = validateFullOrder(n, spec, order);
        if (err != null) {
            System.out.println("CHECK FAIL " + describe(spec) + " -> " + order + " : " + err);
            return false;
        }
        return true;
    }

    static boolean checkCyclic(Method entry, Object receiver, int[][] spec) {
        int n = spec[0][0];
        Object result;
        try {
            result = entry.invoke(receiver, buildAdj(spec));
        } catch (Throwable t) {
            // Rejecting a cyclic graph (no topological order exists) is acceptable.
            return true;
        }
        List<Integer> order = toIntList(result);
        if (order == null) {
            System.out.println("CHECK FAIL cyclic " + describe(spec) + " -> unusable return value: " + result);
            return false;
        }
        // Baseline semantics: returns exactly the peelable vertices, edges among
        // them respected, and no vertex appears before one of its predecessors.
        Set<Integer> peelable = peelableSet(spec);
        Set<Integer> got = new HashSet<>(order);
        if (order.size() != got.size()) {
            System.out.println("CHECK FAIL cyclic " + describe(spec) + " -> duplicates in " + order);
            return false;
        }
        if (!got.equals(peelable)) {
            System.out.println("CHECK FAIL cyclic " + describe(spec) + " -> returned " + order
                    + " but peelable set is " + peelable);
            return false;
        }
        int[] pos = new int[n];
        Arrays.fill(pos, -1);
        for (int i = 0; i < order.size(); i++) pos[order.get(i)] = i;
        for (int i = 1; i < spec.length; i++) {
            int u = spec[i][0], v = spec[i][1];
            if (pos[u] >= 0 && pos[v] >= 0 && pos[u] >= pos[v]) {
                System.out.println("CHECK FAIL cyclic " + describe(spec) + " -> edge " + u + "->" + v
                        + " violated in " + order);
                return false;
            }
        }
        return true;
    }

    /** null if valid; else an error message. Order must be a permutation of 0..n-1 with all edges forward. */
    static String validateFullOrder(int n, int[][] spec, List<Integer> order) {
        if (order.size() != n) return "size " + order.size() + " != " + n;
        int[] pos = new int[n];
        Arrays.fill(pos, -1);
        for (int i = 0; i < n; i++) {
            int v = order.get(i);
            if (v < 0 || v >= n) return "vertex " + v + " out of range";
            if (pos[v] != -1) return "duplicate vertex " + v;
            pos[v] = i;
        }
        for (int i = 1; i < spec.length; i++) {
            int u = spec[i][0], v = spec[i][1];
            if (pos[u] >= pos[v]) return "edge " + u + "->" + v + " violated";
        }
        return null;
    }

    /** Independent reference: the unique maximal set removable by repeated in-degree-0 peeling. */
    static Set<Integer> peelableSet(int[][] spec) {
        int n = spec[0][0];
        int[] indeg = new int[n];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 1; i < spec.length; i++) {
            adj.get(spec[i][0]).add(spec[i][1]);
            indeg[spec[i][1]]++;
        }
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);
        Set<Integer> out = new HashSet<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            out.add(u);
            for (int v : adj.get(u)) if (--indeg[v] == 0) q.add(v);
        }
        return out;
    }

    // ---- plumbing ---------------------------------------------------------

    static ArrayList<ArrayList<Integer>> buildAdj(int[][] spec) {
        int n = spec[0][0];
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 1; i < spec.length; i++) adj.get(spec[i][0]).add(spec[i][1]);
        return adj;
    }

    static List<Integer> toIntList(Object result) {
        if (result instanceof int[] a)
            return Arrays.stream(a).boxed().collect(Collectors.toList());
        if (result instanceof Collection<?> c) {
            List<Integer> out = new ArrayList<>();
            for (Object o : c) {
                if (!(o instanceof Number num)) return null;
                out.add(num.intValue());
            }
            return out;
        }
        return null;
    }

    static String describe(int[][] spec) {
        StringBuilder sb = new StringBuilder("n=" + spec[0][0] + " edges=[");
        for (int i = 1; i < spec.length; i++)
            sb.append(spec[i][0]).append("->").append(spec[i][1]).append(i + 1 < spec.length ? "," : "");
        return sb.append(']').toString();
    }

    static int[] e(int u, int v) { return new int[]{u, v}; }

    static int[][] g(int n, int[]... edges) {
        int[][] spec = new int[edges.length + 1][];
        spec[0] = new int[]{n};
        System.arraycopy(edges, 0, spec, 1, edges.length);
        return spec;
    }
}
