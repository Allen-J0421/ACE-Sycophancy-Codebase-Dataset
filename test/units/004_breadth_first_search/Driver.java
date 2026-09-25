import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 004_breadth_first_search.
 *
 * Baseline semantics (iteration 0): an UNDIRECTED graph over vertices
 * 0..V-1 held as an adjacency list; addEdge(u,v) appends v to adj[u] and
 * u to adj[v] (insertion order preserved). bfs(adj) returns the whole-graph
 * BFS visitation order: outer loop scans start vertices 0,1,...,V-1,
 * starting a fresh BFS at each unvisited vertex; neighbors are explored in
 * adjacency-insertion order. Fully deterministic.
 *
 * API drift handled:
 *   mode LIST   (iters 0-1): static bfs(List-of-lists) + static addEdge(list,u,v)
 *   mode GRAPHN (iters 2-5): class Graph(int V) { addEdge(int,int) }, static bfs(Graph)
 *   mode GRAPHG (iters 6-10): generic Graph<V>() { addVertex(V), addEdge(V,V) },
 *                             static <V> List<V> bfs(Graph<V>). The driver pre-adds
 *                             vertices 0..V-1 (insertion order == numeric order),
 *                             which reproduces the baseline's start-vertex scan.
 *
 * Oracle: independent reference BFS over the same edge-insertion order.
 */
public class Driver {

    // ---- discovered subject API ----
    static Method bfsMethod;          // the whole-graph bfs entry (1 parameter)
    static Object bfsReceiver;        // null if static
    static String mode;               // LIST | GRAPHN | GRAPHG
    static Method listAddEdge;        // mode LIST: static addEdge(list,u,v), may be null
    static Class<?> graphClass;       // modes GRAPHN/GRAPHG
    static Constructor<?> graphCtor;  // Graph(int) or Graph()
    static Method graphAddEdge;       // instance addEdge(...)
    static Method graphAddVertex;     // mode GRAPHG, may be null

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

        List<Class<?>> loaded = new ArrayList<>();
        for (String cn : classNames) {
            try { loaded.add(Class.forName(cn, false, cl)); } catch (Throwable t) { /* skip */ }
        }
        // Prefer classes whose name suggests BFS, then the rest; skip obvious test suites.
        loaded.sort(Comparator.comparingInt(c -> {
            String n = c.getSimpleName().toLowerCase();
            if (n.contains("test")) return 2;
            return (n.contains("breadth") || n.contains("bfs")) ? 0 : 1;
        }));

        if (!discover(loaded)) {
            System.out.println("RESULT FAIL no BFS entry point found among " + classNames);
            return;
        }
        System.out.println("mode=" + mode + " entry=" + bfsMethod);

        // ---- test corpus: {V, edges...} ----
        List<int[][]> tests = new ArrayList<>();
        tests.add(graph(6, e(1, 2), e(2, 0), e(0, 3), e(4, 5)));   // baseline demo -> 0 2 3 1 4 5
        tests.add(graph(0));                                       // empty graph
        tests.add(graph(1));                                       // single isolated vertex
        tests.add(graph(3));                                       // all isolated
        tests.add(graph(2, e(0, 1)));
        tests.add(graph(1, e(0, 0)));                              // self-loop
        tests.add(graph(4, e(0, 1), e(0, 1), e(2, 3)));            // parallel edges + 2 components
        tests.add(graph(5, e(0, 4), e(0, 3), e(0, 2), e(0, 1)));   // neighbor-order sensitive star
        tests.add(graph(5, e(0, 1), e(1, 2), e(2, 3), e(3, 4)));   // path
        tests.add(graph(5, e(0, 1), e(1, 2), e(2, 3), e(3, 4), e(4, 0))); // cycle
        tests.add(graph(5, e(0, 1), e(0, 2), e(0, 3), e(0, 4), e(1, 2), e(1, 3),
                        e(1, 4), e(2, 3), e(2, 4), e(3, 4)));      // K5
        tests.add(graph(7, e(5, 6), e(3, 4), e(1, 2)));            // vertex 0 isolated, reverse comps

        Random rnd = new Random(20260925);
        for (int t = 0; t < 34; t++) {
            int v = 1 + rnd.nextInt(14);
            int m = rnd.nextInt(2 * v + 1);
            int[][] g = new int[m + 1][];
            g[0] = new int[]{v};
            for (int i = 1; i <= m; i++) {
                int a = rnd.nextInt(v), b = rnd.nextInt(v);
                if (a == b && v > 1 && rnd.nextInt(4) != 0) b = (a + 1) % v; // some self-loops kept
                g[i] = new int[]{a, b};
            }
            tests.add(g);
        }

        int checks = 0, failed = 0;
        for (int[][] t : tests) {
            checks++;
            int v = t[0][0];
            int[][] edges = Arrays.copyOfRange(t, 1, t.length);
            List<Integer> expected = refBfs(v, edges);
            List<Integer> actual;
            try {
                actual = runSubject(v, edges);
            } catch (Throwable ex) {
                failed++;
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                System.out.println("CHECK FAIL V=" + v + " edges=" + Arrays.deepToString(edges)
                        + " threw " + cause);
                continue;
            }
            if (!expected.equals(actual)) {
                failed++;
                System.out.println("CHECK FAIL V=" + v + " edges=" + Arrays.deepToString(edges)
                        + " expected=" + expected + " actual=" + actual);
            }
        }

        System.out.println(checks + " graphs checked, " + failed + " failed");
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " graphs, BFS order matches baseline semantics"
                : "RESULT FAIL " + failed + "/" + checks + " graphs gave wrong BFS order");
    }

    // ---------------- discovery ----------------

    static boolean discover(List<Class<?>> loaded) {
        for (Class<?> c : loaded) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic()) continue;
                if (m.getParameterCount() != 1) continue;
                if (!Collection.class.isAssignableFrom(m.getReturnType())) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("bfs") || n.contains("breadth") || n.equals("traverse")
                        || n.equals("traversal") || n.equals("search"))) continue;
                Class<?> p = m.getParameterTypes()[0];
                if (Collection.class.isAssignableFrom(p)) {
                    if (setupList(c, m)) return true;
                } else if (!p.isPrimitive() && !p.getName().startsWith("java.")) {
                    if (setupGraph(c, m, p)) return true;
                }
            }
        }
        return false;
    }

    static boolean bindReceiver(Class<?> declaring, Method m) {
        m.setAccessible(true);
        bfsReceiver = null;
        if (!Modifier.isStatic(m.getModifiers())) {
            try {
                Constructor<?> k = declaring.getDeclaredConstructor();
                k.setAccessible(true);
                bfsReceiver = k.newInstance();
            } catch (Throwable t) {
                return false;
            }
        }
        return true;
    }

    static boolean setupList(Class<?> declaring, Method m) {
        if (!bindReceiver(declaring, m)) return false;
        bfsMethod = m;
        mode = "LIST";
        listAddEdge = null;
        for (Class<?> c : new Class<?>[]{declaring}) {
            for (Method a : c.getDeclaredMethods()) {
                Class<?>[] pp = a.getParameterTypes();
                if (pp.length == 3 && Collection.class.isAssignableFrom(pp[0])
                        && pp[1] == int.class && pp[2] == int.class
                        && a.getName().toLowerCase().contains("edge")) {
                    a.setAccessible(true);
                    listAddEdge = a;
                }
            }
        }
        return true;
    }

    static boolean setupGraph(Class<?> declaring, Method m, Class<?> gClass) {
        Method edge2int = null, edge2obj = null, addV = null;
        for (Method a : gClass.getDeclaredMethods()) {
            if (a.isSynthetic()) continue;   // e.g. lambda$addVertex$0
            String an = a.getName().toLowerCase();
            Class<?>[] pp = a.getParameterTypes();
            if (an.contains("edge") && (an.contains("add") || an.contains("connect")) && pp.length == 2) {
                if (pp[0] == int.class && pp[1] == int.class) edge2int = a;
                else if (!pp[0].isPrimitive()) edge2obj = a;
            }
            if ((an.contains("vertex") || an.contains("node")) && an.contains("add") && pp.length == 1) {
                addV = a;
            }
        }
        Constructor<?> ctorInt = null, ctorNone = null;
        for (Constructor<?> k : gClass.getDeclaredConstructors()) {
            Class<?>[] pp = k.getParameterTypes();
            if (pp.length == 1 && (pp[0] == int.class || pp[0] == Integer.class)) ctorInt = k;
            if (pp.length == 0) ctorNone = k;
        }
        if (edge2int != null && ctorInt != null) {
            if (!bindReceiver(declaring, m)) return false;
            bfsMethod = m; mode = "GRAPHN";
            graphClass = gClass; graphCtor = ctorInt; graphAddEdge = edge2int; graphAddVertex = null;
        } else if (edge2obj != null && ctorNone != null) {
            if (!bindReceiver(declaring, m)) return false;
            bfsMethod = m; mode = "GRAPHG";
            graphClass = gClass; graphCtor = ctorNone; graphAddEdge = edge2obj; graphAddVertex = addV;
        } else {
            return false;
        }
        graphCtor.setAccessible(true);
        graphAddEdge.setAccessible(true);
        if (graphAddVertex != null) graphAddVertex.setAccessible(true);
        return true;
    }

    // ---------------- subject invocation ----------------

    static List<Integer> runSubject(int v, int[][] edges) throws Exception {
        Object arg;
        switch (mode) {
            case "LIST": {
                ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
                for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
                for (int[] ed : edges) {
                    if (listAddEdge != null) {
                        listAddEdge.invoke(null, adj, ed[0], ed[1]);
                    } else {
                        adj.get(ed[0]).add(ed[1]);
                        adj.get(ed[1]).add(ed[0]);
                    }
                }
                arg = adj;
                break;
            }
            case "GRAPHN": {
                Object g = graphCtor.newInstance(v);
                for (int[] ed : edges) graphAddEdge.invoke(g, ed[0], ed[1]);
                arg = g;
                break;
            }
            default: { // GRAPHG
                Object g = graphCtor.newInstance();
                if (graphAddVertex != null) {
                    for (int i = 0; i < v; i++) graphAddVertex.invoke(g, Integer.valueOf(i));
                }
                for (int[] ed : edges)
                    graphAddEdge.invoke(g, Integer.valueOf(ed[0]), Integer.valueOf(ed[1]));
                arg = g;
            }
        }
        Object out = bfsMethod.invoke(bfsReceiver, arg);
        List<Integer> res = new ArrayList<>();
        for (Object o : (Collection<?>) out) res.add(((Number) o).intValue());
        return res;
    }

    // ---------------- independent oracle ----------------

    /** Reference BFS matching the baseline exactly: undirected adjacency lists in
     *  edge-insertion order; outer scan of start vertices 0..V-1. */
    static List<Integer> refBfs(int v, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
        for (int[] ed : edges) {
            adj.get(ed[0]).add(ed[1]);
            adj.get(ed[1]).add(ed[0]);
        }
        boolean[] visited = new boolean[v];
        List<Integer> order = new ArrayList<>();
        for (int s = 0; s < v; s++) {
            if (visited[s]) continue;
            ArrayDeque<Integer> q = new ArrayDeque<>();
            visited[s] = true;
            q.add(s);
            while (!q.isEmpty()) {
                int cur = q.poll();
                order.add(cur);
                for (int nb : adj.get(cur)) {
                    if (!visited[nb]) {
                        visited[nb] = true;
                        q.add(nb);
                    }
                }
            }
        }
        return order;
    }

    // ---------------- helpers ----------------

    static int[] e(int a, int b) { return new int[]{a, b}; }

    static int[][] graph(int v, int[]... edges) {
        int[][] g = new int[edges.length + 1][];
        g[0] = new int[]{v};
        System.arraycopy(edges, 0, g, 1, edges.length);
        return g;
    }
}
