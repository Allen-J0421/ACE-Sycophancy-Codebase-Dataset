import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 007_dijkstras_shortest_path.
 *
 * Baseline semantics: weighted UNDIRECTED graph, vertices 0..V-1, non-negative
 * integer edge weights, single-source shortest distances to every vertex.
 * Unreachable vertices are reported as Integer.MAX_VALUE. Result is a
 * per-vertex distance sequence (ArrayList<Integer> in the baseline, int[] in
 * later iterations).
 *
 * Discovery (reflection, rename/repackage tolerant). Two API shapes exist:
 *  A) list-based:  static List dijkstra(List<List<int[]>> adj, int src)
 *     where adjacency entries are int[]{neighbour, weight} and the driver
 *     inserts both directions itself.
 *  B) graph-object: static int[] shortestDistances(Graph g, int src) where
 *     Graph has a (int vertexCount) constructor and an addEdge(int,int,int)
 *     that inserts the undirected edge in both directions.
 * The driver scans every compiled class for a 2-arg method (X, int) returning
 * int[]/long[]/List whose name suggests dijkstra/shortest/distance, then
 * adapts based on whether X is a JDK List type (shape A) or a subject class
 * (shape B).
 *
 * Oracle: independent Dijkstra over long distances, mapping "unreachable" to
 * Integer.MAX_VALUE. Fixed seed; demo graph + edge cases + 34 random graphs.
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
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Method entry = null;
        boolean listShape = false;
        // Two passes: first prefer methods whose name suggests the algorithm,
        // then accept any shape-matching method.
        outer:
        for (int pass = 0; pass < 2 && entry == null; pass++) {
            for (String cn : classNames) {
                Class<?> c;
                try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
                for (Method m : c.getDeclaredMethods()) {
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length != 2 || p[1] != int.class) continue;
                    Class<?> r = m.getReturnType();
                    boolean retOk = r == int[].class || r == long[].class
                            || List.class.isAssignableFrom(r) || Collection.class.isAssignableFrom(r);
                    if (!retOk) continue;
                    boolean listArg = p[0].isAssignableFrom(ArrayList.class);
                    boolean graphArg = !listArg && !p[0].isPrimitive() && !p[0].isArray()
                            && !p[0].getName().startsWith("java.");
                    if (!listArg && !graphArg) continue;
                    if (pass == 0) {
                        String n = m.getName().toLowerCase();
                        if (!(n.contains("dijkstra") || n.contains("shortest")
                                || n.contains("dist") || n.contains("solve"))) continue;
                    }
                    if (!Modifier.isStatic(m.getModifiers())) continue; // all iterations use static
                    m.setAccessible(true);
                    entry = m;
                    listShape = listArg;
                    break outer;
                }
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no dijkstra entry point found among " + classNames);
            return;
        }
        System.out.println("entry: " + entry + " (shape " + (listShape ? "A:list" : "B:graph") + ")");

        // Build the test suite: {V, source, edges[u,v,w]...}
        List<TestCase> cases = new ArrayList<>();
        // Baseline demo graph (expected 0 4 7 9 10 from source 0).
        cases.add(new TestCase(5, 0, new int[][]{
                {0, 1, 4}, {0, 2, 8}, {1, 4, 6}, {1, 2, 3}, {2, 3, 2}, {3, 4, 10}}));
        // Single vertex, no edges.
        cases.add(new TestCase(1, 0, new int[][]{}));
        // Two vertices, disconnected (unreachable).
        cases.add(new TestCase(2, 0, new int[][]{}));
        // Source-only reachable component in a larger disconnected graph.
        cases.add(new TestCase(6, 1, new int[][]{{0, 1, 5}, {3, 4, 2}, {4, 5, 7}}));
        // Zero-weight edge and a self-loop-free chain.
        cases.add(new TestCase(4, 0, new int[][]{{0, 1, 0}, {1, 2, 3}, {2, 3, 0}}));
        // Parallel edges with different weights.
        cases.add(new TestCase(3, 0, new int[][]{{0, 1, 9}, {0, 1, 2}, {1, 2, 4}}));
        // Randomized graphs: varied sizes, densities, weights, sources.
        Random rnd = new Random(4242);
        for (int t = 0; t < 34; t++) {
            int v = 1 + rnd.nextInt(40);
            int maxW = (t % 3 == 0) ? 10 : 1000;
            double density = switch (t % 4) {
                case 0 -> 0.05; case 1 -> 0.2; case 2 -> 0.5; default -> 0.9;
            };
            List<int[]> edges = new ArrayList<>();
            for (int i = 0; i < v; i++)
                for (int j = i + 1; j < v; j++)
                    if (rnd.nextDouble() < density)
                        edges.add(new int[]{i, j, rnd.nextInt(maxW + 1)});
            cases.add(new TestCase(v, rnd.nextInt(v), edges.toArray(new int[0][])));
        }

        int checks = 0, failed = 0;
        for (TestCase tc : cases) {
            checks++;
            int[] expected = referenceDijkstra(tc.v, tc.src, tc.edges);
            int[] actual;
            try {
                Object arg0 = listShape ? buildAdjacencyList(tc.v, tc.edges)
                                        : buildGraphObject(entry.getParameterTypes()[0], tc.v, tc.edges);
                Object res = entry.invoke(null, arg0, tc.src);
                actual = toIntArray(res);
            } catch (Throwable e) {
                failed++;
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                System.out.println("CHECK FAIL exception V=" + tc.v + " src=" + tc.src
                        + " edges=" + tc.edgeString() + ": " + cause);
                continue;
            }
            if (!Arrays.equals(expected, actual)) {
                failed++;
                System.out.println("CHECK FAIL V=" + tc.v + " src=" + tc.src
                        + " edges=" + tc.edgeString()
                        + "\n  expected " + Arrays.toString(expected)
                        + "\n  actual   " + Arrays.toString(actual));
            }
        }

        System.out.println(checks + " graphs checked, " + failed + " failed");
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " graphs checked"
                : "RESULT FAIL " + failed + "/" + checks + " graphs gave wrong distances");
    }

    record TestCase(int v, int src, int[][] edges) {
        String edgeString() {
            if (edges.length > 12) return edges.length + " edges";
            return Arrays.deepToString(edges);
        }
    }

    /** Independent reference: undirected Dijkstra, unreachable = Integer.MAX_VALUE. */
    static int[] referenceDijkstra(int v, int src, int[][] edges) {
        List<List<long[]>> adj = new ArrayList<>();
        for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new long[]{e[1], e[2]});
            adj.get(e[1]).add(new long[]{e[0], e[2]});
        }
        long[] dist = new long[v];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[src] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.offer(new long[]{0, src});
        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            int u = (int) top[1];
            if (top[0] > dist[u]) continue;
            for (long[] p : adj.get(u)) {
                int w = (int) p[0];
                long cand = dist[u] + p[1];
                if (cand < dist[w]) {
                    dist[w] = cand;
                    pq.offer(new long[]{cand, w});
                }
            }
        }
        int[] out = new int[v];
        for (int i = 0; i < v; i++)
            out[i] = dist[i] == Long.MAX_VALUE ? Integer.MAX_VALUE : (int) dist[i];
        return out;
    }

    /** Shape A input: ArrayList<ArrayList<int[]>> with both directions inserted. */
    static ArrayList<ArrayList<int[]>> buildAdjacencyList(int v, int[][] edges) {
        ArrayList<ArrayList<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        return adj;
    }

    /** Shape B input: instantiate the subject's graph type and add each edge once. */
    static Object buildGraphObject(Class<?> graphType, int v, int[][] edges) throws Exception {
        Constructor<?> ctor = null;
        for (Constructor<?> c : graphType.getDeclaredConstructors()) {
            Class<?>[] p = c.getParameterTypes();
            if (p.length == 1 && (p[0] == int.class || p[0] == Integer.class)) { ctor = c; break; }
        }
        if (ctor == null)
            throw new IllegalStateException("no (int) constructor on " + graphType.getName());
        ctor.setAccessible(true);
        Object g = ctor.newInstance(v);
        Method add = null;
        for (Method m : graphType.getDeclaredMethods()) {
            Class<?>[] p = m.getParameterTypes();
            if (p.length == 3 && p[0] == int.class && p[1] == int.class && p[2] == int.class) {
                String n = m.getName().toLowerCase();
                if (n.contains("add") || n.contains("edge") || n.contains("connect")) { add = m; break; }
                if (add == null) add = m; // fallback: any 3-int method
            }
        }
        if (add == null)
            throw new IllegalStateException("no addEdge(int,int,int) on " + graphType.getName());
        add.setAccessible(true);
        for (int[] e : edges) add.invoke(g, e[0], e[1], e[2]);
        return g;
    }

    static int[] toIntArray(Object res) {
        if (res instanceof int[] a) return a.clone();
        if (res instanceof long[] a) {
            int[] out = new int[a.length];
            for (int i = 0; i < a.length; i++)
                out[i] = a[i] >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) a[i];
            return out;
        }
        if (res instanceof Collection<?> col) {
            int[] out = new int[col.size()];
            int i = 0;
            for (Object o : col) out[i++] = ((Number) o).intValue();
            return out;
        }
        throw new IllegalStateException("unexpected result type: "
                + (res == null ? "null" : res.getClass().getName()));
    }
}
