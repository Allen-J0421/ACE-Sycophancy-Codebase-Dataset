import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Correctness driver for 026_prims_mst.
 *
 * Baseline (iter 0): class MST with void primMST(int[][] adjacencyMatrix) that
 * prints "Edge \tWeight" and one "u - v\tw" line per MST edge. Weight 0 means
 * "no edge"; the tree is grown from vertex 0; the graph is assumed connected.
 *
 * Iterations 1..10: PrimsMST.computeMst(WeightedGraph) returning MstResult
 * (edges() + totalWeight()); WeightedGraph.fromAdjacencyMatrix(int[][]).
 *
 * Oracle: an independent Prim reference computes the MST total weight of each
 * (connected) test matrix. The subject's answer must have exactly V-1 edges
 * that exist in the graph with the printed/reported weights, must span all
 * vertices, and must total the reference MST weight.
 */
public class Driver {

    static List<Class<?>> classes = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Path classesDir = Paths.get(args[0]).resolve("_classes");
        loadClasses(classesDir);

        Entry entry = discover();
        if (entry == null) {
            System.out.println("RESULT FAIL no Prim/MST entry point found among " + classNames());
            return;
        }
        System.out.println("entry: " + entry.describe());

        List<int[][]> graphs = buildGraphs();
        int checks = 0, failed = 0;
        for (int gi = 0; gi < graphs.size(); gi++) {
            int[][] g = graphs.get(gi);
            long expect = refPrimWeight(g);
            checks++;
            try {
                Outcome out = entry.run(g);
                String err = validate(g, expect, out);
                if (err != null) {
                    failed++;
                    System.out.println("CHECK FAIL graph#" + gi + " V=" + g.length + ": " + err);
                }
            } catch (Throwable t) {
                failed++;
                Throwable c = t instanceof InvocationTargetException ? t.getCause() : t;
                System.out.println("CHECK FAIL graph#" + gi + " V=" + g.length + " exception: " + c);
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " MST instances verified"
                : "RESULT FAIL " + failed + "/" + checks + " MST instances wrong");
    }

    // ------------------------------------------------------------------ oracle

    /** Independent Prim (O(V^2)) over an adjacency matrix; 0 = no edge. */
    static long refPrimWeight(int[][] g) {
        int V = g.length;
        if (V == 0) return 0;
        boolean[] in = new boolean[V];
        long[] key = new long[V];
        Arrays.fill(key, Long.MAX_VALUE);
        key[0] = 0;
        long total = 0;
        for (int it = 0; it < V; it++) {
            int u = -1;
            for (int v = 0; v < V; v++)
                if (!in[v] && (u == -1 || key[v] < key[u])) u = v;
            in[u] = true;
            total += key[u];
            for (int v = 0; v < V; v++)
                if (!in[v] && g[u][v] != 0 && g[u][v] < key[v]) key[v] = g[u][v];
        }
        return total;
    }

    static String validate(int[][] g, long expect, Outcome out) {
        int V = g.length;
        if (out.edges != null) {
            if (out.edges.size() != Math.max(V - 1, 0))
                return "expected " + (V - 1) + " edges, got " + out.edges.size() + " " + fmtEdges(out.edges);
            long sum = 0;
            int[] uf = new int[V];
            for (int i = 0; i < V; i++) uf[i] = i;
            for (int[] e : out.edges) {
                int a = e[0], b = e[1], w = e[2];
                if (a < 0 || a >= V || b < 0 || b >= V) return "edge endpoint out of range: " + Arrays.toString(e);
                if (g[a][b] == 0) return "reported edge not in graph: " + Arrays.toString(e);
                if (w != g[a][b]) return "edge weight mismatch: " + Arrays.toString(e) + " matrix says " + g[a][b];
                sum += w;
                uf[find(uf, a)] = find(uf, b);
            }
            for (int i = 0; i < V; i++)
                if (find(uf, i) != find(uf, 0)) return "reported edges do not span vertex " + i;
            if (sum != expect) return "MST weight " + sum + " != reference " + expect;
            if (out.totalWeight != null && out.totalWeight != sum)
                return "totalWeight() " + out.totalWeight + " != sum of edges " + sum;
            return null;
        }
        if (out.totalWeight == null) return "no edges and no total weight reported";
        if (out.totalWeight != expect) return "MST weight " + out.totalWeight + " != reference " + expect;
        return null;
    }

    static int find(int[] uf, int x) {
        while (uf[x] != x) { uf[x] = uf[uf[x]]; x = uf[x]; }
        return x;
    }

    static String fmtEdges(List<int[]> es) {
        StringBuilder sb = new StringBuilder("[");
        for (int[] e : es) sb.append(Arrays.toString(e));
        return sb.append("]").toString();
    }

    // ------------------------------------------------------------- test data

    static List<int[][]> buildGraphs() {
        List<int[][]> graphs = new ArrayList<>();
        graphs.add(new int[][] { // classic baseline example, MST weight 16
                { 0, 2, 0, 6, 0 },
                { 2, 0, 3, 8, 5 },
                { 0, 3, 0, 0, 7 },
                { 6, 8, 0, 0, 9 },
                { 0, 5, 7, 9, 0 } });
        graphs.add(new int[][] { { 0 } });                       // single vertex
        graphs.add(new int[][] { { 0, 5 }, { 5, 0 } });          // one edge
        graphs.add(new int[][] { { 0, 1, 4 }, { 1, 0, 2 }, { 4, 2, 0 } }); // triangle
        Random rnd = new Random(20260926);
        for (int t = 0; t < 30; t++) {
            int V = 2 + rnd.nextInt(24);
            int[][] g = new int[V][V];
            for (int v = 1; v < V; v++) {           // random spanning tree
                int u = rnd.nextInt(v);
                int w = 1 + rnd.nextInt(50);
                g[u][v] = g[v][u] = w;
            }
            int extra = rnd.nextInt(V * 2);
            for (int e = 0; e < extra; e++) {
                int u = rnd.nextInt(V), v = rnd.nextInt(V);
                if (u != v && g[u][v] == 0) {
                    int w = 1 + rnd.nextInt(50);
                    g[u][v] = g[v][u] = w;
                }
            }
            graphs.add(g);
        }
        // Tie-heavy dense graphs (weights 1..3), complete graphs, and graphs with
        // negative weights (baseline only treats 0 as "no edge"; Prim is
        // correct for negative weights too).
        for (int t = 0; t < 12; t++) {
            int V = 3 + rnd.nextInt(28);
            int kind = t % 3;
            int[][] g = new int[V][V];
            for (int v = 1; v < V; v++) {
                int u = rnd.nextInt(v);
                g[u][v] = g[v][u] = weightFor(rnd, kind);
            }
            double density = kind == 1 ? 1.0 : 0.4;
            for (int u = 0; u < V; u++)
                for (int v = u + 1; v < V; v++)
                    if (g[u][v] == 0 && rnd.nextDouble() < density) g[u][v] = g[v][u] = weightFor(rnd, kind);
            graphs.add(g);
        }
        graphs.add(new int[][] { { 0, 7, 7 }, { 7, 0, 7 }, { 7, 7, 0 } }); // all ties
        return graphs;
    }

    static int weightFor(Random rnd, int kind) {
        if (kind == 0) return 1 + rnd.nextInt(3);                  // many ties
        if (kind == 1) return 1 + rnd.nextInt(1000);               // complete graph
        int w = rnd.nextInt(41) - 20;                              // negatives allowed
        return w == 0 ? 21 : w;
    }

    // ------------------------------------------------------------- discovery

    static class Outcome {
        Long totalWeight;
        List<int[]> edges; // {u, v, w}
    }

    abstract static class Entry {
        abstract Outcome run(int[][] g) throws Exception;
        abstract String describe();
    }

    static Entry discover() {
        // Pass 1: method with a structured (non-void) result.
        for (Class<?> c : classes) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            for (Method m : declaredMethods(c)) {
                if (m.getParameterCount() != 1) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("mst") || n.contains("prim") || n.contains("spanningtree"))) continue;
                if (m.getReturnType() == void.class) continue;
                GraphMaker gm = graphMaker(m.getParameterTypes()[0]);
                if (gm == null) continue;
                Object recv = Modifier.isStatic(m.getModifiers()) ? null : instantiate(c);
                if (!Modifier.isStatic(m.getModifiers()) && recv == null) continue;
                m.setAccessible(true);
                return structuredEntry(m, recv, gm);
            }
        }
        // Pass 2: void method printing the MST to stdout (baseline shape).
        for (Class<?> c : classes) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            for (Method m : declaredMethods(c)) {
                if (m.getParameterCount() != 1) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("mst") || n.contains("prim") || n.contains("spanningtree"))) continue;
                if (m.getReturnType() != void.class) continue;
                if (m.getParameterTypes()[0] != int[][].class) continue;
                Object recv = Modifier.isStatic(m.getModifiers()) ? null : instantiate(c);
                if (!Modifier.isStatic(m.getModifiers()) && recv == null) continue;
                m.setAccessible(true);
                return stdoutEntry(m, recv);
            }
        }
        return null;
    }

    static Entry structuredEntry(Method m, Object recv, GraphMaker gm) {
        return new Entry() {
            Outcome run(int[][] g) throws Exception {
                Object result = m.invoke(recv, gm.make(g));
                if (result == null) throw new IllegalStateException("entry returned null");
                Outcome out = new Outcome();
                if (result instanceof Number num) {
                    out.totalWeight = num.longValue();
                    return out;
                }
                out.totalWeight = extractWeight(result);
                out.edges = extractEdges(result);
                if (out.edges == null && hasEdgeAccessor(result))
                    throw new IllegalStateException("result exposes an edge collection whose edges could not be read: " + result);
                return out;
            }
            String describe() { return m + " [structured]"; }
        };
    }

    static Entry stdoutEntry(Method m, Object recv) {
        return new Entry() {
            Outcome run(int[][] g) throws Exception {
                PrintStream old = System.out;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    System.setOut(new PrintStream(bos, true));
                    m.invoke(recv, (Object) g);
                } finally {
                    System.setOut(old);
                }
                Outcome out = new Outcome();
                out.edges = new ArrayList<>();
                Pattern p = Pattern.compile("^\\s*(\\d+)\\s*-\\s*(\\d+)\\s+(-?\\d+)\\s*$");
                for (String line : bos.toString().split("\\R")) {
                    Matcher mm = p.matcher(line);
                    if (mm.matches()) {
                        out.edges.add(new int[] { Integer.parseInt(mm.group(1)),
                                Integer.parseInt(mm.group(2)), Integer.parseInt(mm.group(3)) });
                    }
                }
                return out;
            }
            String describe() { return m + " [stdout]"; }
        };
    }

    /** Extracts a total-weight value from a result object, if it exposes one. */
    static Long extractWeight(Object result) {
        Method best = null;
        for (Method m : result.getClass().getMethods()) {
            if (m.getParameterCount() != 0) continue;
            Class<?> rt = m.getReturnType();
            if (rt != int.class && rt != long.class && rt != Integer.class && rt != Long.class) continue;
            String n = m.getName().toLowerCase();
            if (n.contains("weight") || n.contains("cost")) {
                if (best == null || (n.contains("total") && !best.getName().toLowerCase().contains("total")))
                    best = m;
            }
        }
        if (best == null) return null;
        try {
            best.setAccessible(true);
            return ((Number) best.invoke(result)).longValue();
        } catch (Throwable t) {
            return null;
        }
    }

    static boolean hasEdgeAccessor(Object result) {
        for (Method m : result.getClass().getMethods())
            if (m.getParameterCount() == 0 && Collection.class.isAssignableFrom(m.getReturnType())
                    && m.getName().toLowerCase().contains("edge")) return true;
        return false;
    }

    /** Extracts MST edges as {u,v,w} triples from a result object, if possible. */
    static List<int[]> extractEdges(Object result) {
        Method edgesM = null;
        for (Method m : result.getClass().getMethods()) {
            if (m.getParameterCount() == 0 && Collection.class.isAssignableFrom(m.getReturnType())
                    && m.getName().toLowerCase().contains("edge")) {
                edgesM = m;
                break;
            }
        }
        if (edgesM == null) return null;
        try {
            edgesM.setAccessible(true);
            Collection<?> col = (Collection<?>) edgesM.invoke(result);
            if (col == null) return null;
            List<int[]> out = new ArrayList<>();
            for (Object e : col) {
                Method a = intGetter(e, "source", "from", "u", "first", "start");
                Method b = intGetter(e, "destination", "dest", "to", "v", "second", "end");
                Method w = intGetter(e, "weight", "cost", "w");
                if (a == null || b == null || w == null) return null;
                out.add(new int[] { intOf(a, e), intOf(b, e), intOf(w, e) });
            }
            return out;
        } catch (Throwable t) {
            return null;
        }
    }

    static Method intGetter(Object o, String... names) {
        for (String want : names) {
            for (Method m : o.getClass().getMethods()) {
                if (m.getParameterCount() != 0) continue;
                Class<?> rt = m.getReturnType();
                if (rt != int.class && rt != Integer.class) continue;
                String n = m.getName().toLowerCase();
                if (n.equals(want) || n.equals("get" + want)) return m;
            }
        }
        return null;
    }

    static int intOf(Method m, Object o) throws Exception {
        m.setAccessible(true);
        return ((Number) m.invoke(o)).intValue();
    }

    // -------------------------------------------------------- graph building

    interface GraphMaker {
        Object make(int[][] matrix) throws Exception;
    }

    /** Builds an object of type {@code pt} from an adjacency matrix. */
    static GraphMaker graphMaker(Class<?> pt) {
        if (pt == int[][].class) return m -> m;
        if (pt.isPrimitive() || pt.isArray()) return null;
        // static factory on the param type itself
        for (Method f : declaredMethods(pt)) {
            if (Modifier.isStatic(f.getModifiers()) && f.getParameterCount() == 1
                    && f.getParameterTypes()[0] == int[][].class
                    && pt.isAssignableFrom(f.getReturnType())) {
                f.setAccessible(true);
                return m -> f.invoke(null, (Object) m);
            }
        }
        // constructor taking the matrix
        try {
            Constructor<?> k = pt.getDeclaredConstructor(int[][].class);
            k.setAccessible(true);
            return m -> k.newInstance((Object) m);
        } catch (NoSuchMethodException ignored) { }
        // static factory anywhere else
        for (Class<?> c : classes) {
            for (Method f : declaredMethods(c)) {
                if (Modifier.isStatic(f.getModifiers()) && f.getParameterCount() == 1
                        && f.getParameterTypes()[0] == int[][].class
                        && pt.isAssignableFrom(f.getReturnType())) {
                    f.setAccessible(true);
                    return m -> f.invoke(null, (Object) m);
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------- plumbing

    static void loadClasses(Path classesDir) throws Exception {
        List<String> names = new ArrayList<>();
        try (Stream<Path> s = Files.walk(classesDir)) {
            s.filter(p -> p.toString().endsWith(".class"))
             .forEach(p -> names.add(classesDir.relativize(p).toString()
                     .replace(".class", "").replace(File.separatorChar, '.')));
        }
        names.sort(String::compareTo);
        URLClassLoader cl = new URLClassLoader(new URL[] { classesDir.toUri().toURL() });
        for (String n : names) {
            if (n.contains("$")) continue;
            try { classes.add(Class.forName(n, false, cl)); } catch (Throwable ignored) { }
        }
    }

    static Method[] declaredMethods(Class<?> c) {
        try { return c.getDeclaredMethods(); } catch (Throwable t) { return new Method[0]; }
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

    static List<String> classNames() {
        List<String> out = new ArrayList<>();
        for (Class<?> c : classes) out.add(c.getName());
        return out;
    }
}
