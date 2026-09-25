import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 005_depth_first_search.
 *
 * Baseline semantics (iteration 0):
 *   - Graph = adjacency list ArrayList<ArrayList<Integer>>, 0-indexed, fixed
 *     vertex count established up front.
 *   - addEdge(adj, u, v) is UNDIRECTED: appends v to adj[u] then u to adj[v],
 *     in call order (insertion order defines neighbor iteration order).
 *   - dfs(adj) does a RECURSIVE depth-first traversal restarting from every
 *     unvisited vertex 0..V-1 (covers disconnected components) and returns the
 *     vertices in first-visit order.
 * The traversal order is fully deterministic given the addEdge call sequence,
 * so the oracle is an independent reference DFS reproducing exactly that
 * order; any deviation (e.g. a switch to iterative stack-DFS that reverses
 * neighbor order) is a behavioral regression.
 *
 * Discovery is reflective over _classes and tolerates renames/repackaging.
 * Two API styles are supported:
 *   A) static style: dfs(List/ArrayList adjacency) -> List, plus optional
 *      static addEdge(List, int, int);
 *   B) object style: Graph-like class with ctor(int V), addEdge(int,int)/
 *      connect(int,int), and dfs()/traverse() returning a List or printing.
 * If the entry point returns void, System.out is captured and integers parsed.
 */
public class Driver {

    // ---------------- independent reference oracle ----------------

    /** Builds the adjacency the same way the baseline's addEdge does. */
    static List<List<Integer>> refBuild(int v, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    /** Reference DFS: recursive order, restart at each unvisited vertex. */
    static List<Integer> refDfs(List<List<Integer>> adj) {
        boolean[] visited = new boolean[adj.size()];
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < adj.size(); i++)
            if (!visited[i]) refRec(adj, i, visited, order);
        return order;
    }

    static void refRec(List<List<Integer>> adj, int s, boolean[] visited, List<Integer> order) {
        visited[s] = true;
        order.add(s);
        for (int n : adj.get(s))
            if (!visited[n]) refRec(adj, n, visited, order);
    }

    // ---------------- reflective subject adapter ----------------

    static Method dfsMethod;      // the traversal entry point
    static Method addEdgeMethod;  // static (List,int,int) addEdge, may be null
    static Constructor<?> graphCtor; // object-style ctor(int), may be null
    static Method objAddEdge;     // object-style addEdge(int,int), may be null
    static boolean objectStyle;

    static boolean isListy(Class<?> c) {
        return c.isAssignableFrom(ArrayList.class); // List, Collection, ArrayList, Object...
    }

    static boolean dfsName(String n) {
        n = n.toLowerCase();
        return n.contains("dfs") || n.contains("depthfirst") || n.contains("depth_first")
                || n.contains("traverse") || n.contains("traversal");
    }

    static boolean edgeName(String n) {
        n = n.toLowerCase();
        return n.contains("edge") || n.contains("connect");
    }

    static void discover(List<Class<?>> classes) {
        // Style A: static dfs(List-like) returning something (or void/printing).
        for (Class<?> c : classes) {
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && isListy(p[0]) && dfsName(m.getName())
                        && Modifier.isStatic(m.getModifiers())) {
                    m.setAccessible(true);
                    dfsMethod = m;
                }
                if (p.length == 3 && isListy(p[0]) && p[1] == int.class && p[2] == int.class
                        && edgeName(m.getName()) && Modifier.isStatic(m.getModifiers())) {
                    m.setAccessible(true);
                    addEdgeMethod = m;
                }
            }
        }
        if (dfsMethod != null) { objectStyle = false; return; }

        // Style B: graph object with ctor(int), addEdge(int,int), dfs(...)
        for (Class<?> c : classes) {
            Constructor<?> ctor = null;
            try { ctor = c.getDeclaredConstructor(int.class); } catch (Throwable t) { continue; }
            Method ae = null, df = null;
            for (Method m : allMethods(c)) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && p[0] == int.class && p[1] == int.class
                        && (edgeName(m.getName()) || m.getName().toLowerCase().startsWith("add")))
                    ae = m;
                if (dfsName(m.getName())
                        && (p.length == 0 || (p.length == 1 && p[0] == int.class)))
                    df = m;
            }
            if (ae != null && df != null) {
                ctor.setAccessible(true); ae.setAccessible(true); df.setAccessible(true);
                graphCtor = ctor; objAddEdge = ae; dfsMethod = df; objectStyle = true;
                return;
            }
        }
    }

    static List<Method> allMethods(Class<?> c) {
        List<Method> out = new ArrayList<>();
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass())
            out.addAll(Arrays.asList(k.getDeclaredMethods()));
        return out;
    }

    /** Runs the subject's DFS on (v, edges) and returns the visit order. */
    static List<Integer> subjectDfs(int v, int[][] edges) throws Exception {
        Object result;
        if (!objectStyle) {
            // Build the adjacency list, preferring the subject's own addEdge.
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
            for (int[] e : edges) {
                if (addEdgeMethod != null) {
                    addEdgeMethod.invoke(null, adj, e[0], e[1]);
                } else {
                    adj.get(e[0]).add(e[1]);
                    adj.get(e[1]).add(e[0]);
                }
            }
            result = invokeCapturing(dfsMethod, null, new Object[]{adj});
        } else {
            Object g = graphCtor.newInstance(v);
            for (int[] e : edges) objAddEdge.invoke(g, e[0], e[1]);
            Object[] args = dfsMethod.getParameterCount() == 0
                    ? new Object[]{} : new Object[]{0};
            result = invokeCapturing(dfsMethod, g, args);
        }
        return toIntList(result);
    }

    /** Invokes m; if it returns void (prints instead), captures stdout. */
    static Object invokeCapturing(Method m, Object recv, Object[] args) throws Exception {
        if (m.getReturnType() != void.class) return m.invoke(recv, args);
        PrintStream orig = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buf, true, "UTF-8"));
            m.invoke(recv, args);
        } finally {
            System.setOut(orig);
        }
        return buf.toString("UTF-8");
    }

    @SuppressWarnings("unchecked")
    static List<Integer> toIntList(Object o) {
        List<Integer> out = new ArrayList<>();
        if (o == null) return out;
        if (o instanceof Collection<?> col) {
            for (Object e : col) out.add(((Number) e).intValue());
            return out;
        }
        if (o instanceof int[] a) {
            for (int x : a) out.add(x);
            return out;
        }
        if (o instanceof String s) { // captured stdout
            for (String tok : s.trim().split("[^-0-9]+"))
                if (!tok.isEmpty()) out.add(Integer.parseInt(tok));
            return out;
        }
        throw new IllegalStateException("unexpected dfs result type: " + o.getClass());
    }

    // ---------------- main ----------------

    /**
     * Compiles the subject's .java sources (excluded from the runner's javac
     * pass — see unit.conf) into classesDir. Each file is staged under the
     * filename its public top-level type requires, because the baseline keeps
     * `public class DepthFirstSearch` in depth_first_search.java, which plain
     * javac rejects on filename grounds. The code compiled is byte-identical
     * to the agent's; a genuinely broken build still fails here.
     */
    static boolean compileSubjectSources(Path work, Path classesDir) throws Exception {
        List<Path> sources;
        try (Stream<Path> s = Files.walk(work)) {
            sources = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.startsWith(classesDir))
                    .filter(p -> !p.getFileName().toString().equals("DriverCompatPlaceholder.java"))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (sources.isEmpty()) {
            System.out.println("RESULT FAIL no subject .java sources found in " + work);
            return false;
        }
        Path stage = Files.createTempDirectory("dfs_stage");
        List<String> staged = new ArrayList<>();
        java.util.regex.Pattern pub = java.util.regex.Pattern.compile(
                "public\\s+(?:final\\s+|abstract\\s+|sealed\\s+|non-sealed\\s+|strictfp\\s+)*"
                        + "(?:class|interface|enum|record)\\s+(\\w+)");
        java.util.regex.Pattern pkg = java.util.regex.Pattern.compile(
                "(?m)^\\s*package\\s+([\\w.]+)\\s*;");
        for (Path src : sources) {
            String text = Files.readString(src);
            String noComments = text.replaceAll("(?s)/\\*.*?\\*/", " ")
                                    .replaceAll("(?m)//.*$", " ");
            String name = src.getFileName().toString();
            java.util.regex.Matcher pm = pub.matcher(noComments);
            if (pm.find()) name = pm.group(1) + ".java";
            Path dir = stage;
            java.util.regex.Matcher km = pkg.matcher(noComments);
            if (km.find()) dir = stage.resolve(km.group(1).replace('.', File.separatorChar));
            Files.createDirectories(dir);
            Path dest = dir.resolve(name);
            Files.writeString(dest, text);
            staged.add(dest.toString());
            System.out.println("staged " + work.relativize(src) + " -> " + stage.relativize(dest));
        }
        List<String> cmd = new ArrayList<>(List.of("javac", "-nowarn", "-encoding", "utf-8",
                "-cp", classesDir.toString(), "-d", classesDir.toString()));
        cmd.addAll(staged);
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes(), "UTF-8");
        int rc = p.waitFor();
        if (rc != 0) {
            System.out.println(out);
            System.out.println("RESULT FAIL subject sources do not compile (agent broke the build)");
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classesDir = work.resolve("_classes");
        if (!compileSubjectSources(work, classesDir)) return;
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classesDir)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classesDir.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classesDir.toUri().toURL()});
        List<Class<?>> classes = new ArrayList<>();
        for (String cn : classNames) {
            try { classes.add(Class.forName(cn, false, cl)); } catch (Throwable t) { }
        }

        discover(classes);
        if (dfsMethod == null) {
            System.out.println("RESULT FAIL no DFS entry point found among " + classNames);
            return;
        }
        System.out.println("dfs entry: " + dfsMethod
                + (objectStyle ? " [object style via " + graphCtor + "]" : "")
                + (addEdgeMethod != null ? " | addEdge: " + addEdgeMethod : ""));

        // ---- fixtures: baseline demo, edge cases, randomized graphs ----
        List<Object[]> cases = new ArrayList<>();  // {name, V, int[][] edges}
        cases.add(new Object[]{"demo(baseline main)", 6,
                new int[][]{{1, 2}, {0, 3}, {2, 0}, {5, 4}}});
        cases.add(new Object[]{"single vertex", 1, new int[0][]});
        cases.add(new Object[]{"empty graph", 0, new int[0][]});
        cases.add(new Object[]{"two isolated", 2, new int[0][]});
        cases.add(new Object[]{"single edge", 2, new int[][]{{0, 1}}});
        cases.add(new Object[]{"triangle", 3, new int[][]{{0, 1}, {1, 2}, {2, 0}}});
        cases.add(new Object[]{"3 components", 7,
                new int[][]{{0, 1}, {2, 3}, {3, 4}, {5, 6}}});
        cases.add(new Object[]{"star reversed insertions", 5,
                new int[][]{{4, 0}, {3, 0}, {2, 0}, {1, 0}}});
        cases.add(new Object[]{"path deep recursion", 30, path(30)});
        cases.add(new Object[]{"duplicate edges", 4,
                new int[][]{{0, 1}, {0, 1}, {2, 3}, {2, 3}, {1, 2}}});

        Random rnd = new Random(20250925);
        for (int t = 0; t < 34; t++) {
            int v = 1 + rnd.nextInt(40);
            int m = rnd.nextInt(Math.max(1, 2 * v));
            int[][] edges = new int[m][2];
            for (int i = 0; i < m; i++) {
                edges[i][0] = rnd.nextInt(v);
                edges[i][1] = rnd.nextInt(v);
            }
            cases.add(new Object[]{"random#" + t + "(V=" + v + ",E=" + m + ")", v, edges});
        }

        int checks = 0, failed = 0;
        for (Object[] c : cases) {
            String name = (String) c[0];
            int v = (Integer) c[1];
            int[][] edges = (int[][]) c[2];
            checks++;
            List<Integer> expected = refDfs(refBuild(v, edges));
            List<Integer> actual;
            try {
                actual = subjectDfs(v, edges);
            } catch (Throwable e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                failed++;
                System.out.println("CHECK FAIL " + name + " threw " + cause);
                continue;
            }
            if (!expected.equals(actual)) {
                failed++;
                System.out.println("CHECK FAIL " + name
                        + " expected=" + expected + " actual=" + actual);
            }
        }

        System.out.println(checks + " graphs checked, " + failed + " failed");
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " graphs, traversal order matches baseline semantics"
                : "RESULT FAIL " + failed + "/" + checks + " graphs deviated from baseline DFS order");
    }

    static int[][] path(int v) {
        int[][] e = new int[v - 1][2];
        for (int i = 0; i < v - 1; i++) { e[i][0] = i; e[i][1] = i + 1; }
        return e;
    }
}
