import java.io.File;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 027_connected_components.
 *
 * Baseline semantics (connected_components.java): static
 * ArrayList<ArrayList<Integer>> getComponents(ArrayList<ArrayList<Integer>> adj)
 * over an UNDIRECTED graph (addEdge inserts both directions), vertices 0..V-1.
 * Returns the connected components; every vertex appears in exactly one
 * component. Self-loops and parallel edges are tolerated.
 *
 * API across iterations:
 *  - iter 1: Graph(int) + addEdge(int,int); ConnectedComponentsFinder.find(Graph)
 *            -> List<List<Integer>>.
 *  - iter 2-6: find(Graph) -> Components {count, asList, componentOf, connected}.
 *  - iter 7-8: same, moved to package graph (src/main/java/graph/); iter 8 adds
 *            ConnectedComponentsFinder(TraversalStrategy) with BFS/DFS strategies.
 *  - iter 9: Graph built via GraphBuilder(int).addEdge(u,v).build().
 *  - iter 10: Components iterable over Component {id, size, contains, vertices}.
 * Everything is discovered by reflection. Every concrete implementation of the
 * finder's strategy-parameter type (plus the no-arg default) is exercised.
 *
 * Oracle: independent union-find. Components are compared as an
 * order-independent partition (set of vertex sets); the driver also checks each
 * vertex appears exactly once, and cross-checks count()/componentOf()/connected()
 * and per-Component size()/contains() when those query APIs exist.
 */
public class Driver {
    static List<Class<?>> loaded = new ArrayList<>();
    static Method listEntry;                 // shape L (baseline)
    static Class<?> finderClass;             // shape G
    static Method finderFind;
    static Class<?> graphClass;

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
        for (String cn : classNames) {
            try { loaded.add(Class.forName(cn, false, cl)); } catch (Throwable t) { }
        }

        // Shape G: an instance method X find(Graph) on a finder class.
        for (int pass = 0; pass < 2 && finderFind == null && listEntry == null; pass++) {
            for (Class<?> c : loaded) {
                if (c.getName().toLowerCase().contains("test")) continue;
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge()) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length != 1 || m.getReturnType() == void.class) continue;
                    String n = m.getName().toLowerCase();
                    boolean named = n.contains("find") || n.contains("component");
                    if (pass == 0 && !named) continue;
                    if (Modifier.isStatic(m.getModifiers()) && p[0].isAssignableFrom(ArrayList.class)
                            && Collection.class.isAssignableFrom(m.getReturnType())) {
                        if (listEntry == null) listEntry = m;
                    } else if (!Modifier.isStatic(m.getModifiers()) && !p[0].isPrimitive() && !p[0].isArray()
                            && !p[0].getName().startsWith("java.") && !p[0].isInterface()
                            && p[0].getSimpleName().toLowerCase().contains("graph")) {
                        if (finderFind == null) { finderFind = m; finderClass = c; graphClass = p[0]; }
                    }
                }
            }
        }
        if (listEntry == null && finderFind == null) {
            System.out.println("RESULT FAIL no connected-components entry point found among " + classNames);
            return;
        }

        // Finder variants: default ctor + one per concrete strategy implementation.
        List<String> variantNames = new ArrayList<>();
        List<Object> finders = new ArrayList<>();
        if (listEntry != null) {
            listEntry.setAccessible(true);
            variantNames.add("static " + listEntry.getName());
            finders.add(null);
            System.out.println("entry (list shape): " + listEntry);
        } else {
            finderFind.setAccessible(true);
            System.out.println("entry: " + finderFind);
            for (Constructor<?> k : finderClass.getDeclaredConstructors()) {
                if (k.isSynthetic()) continue;
                k.setAccessible(true);
                Class<?>[] kp = k.getParameterTypes();
                if (kp.length == 0) { finders.add(k.newInstance()); variantNames.add("default"); }
                else if (kp.length == 1 && !kp[0].isPrimitive() && !kp[0].getName().startsWith("java.")) {
                    for (Class<?> impl : loaded) {
                        if (impl.isInterface() || Modifier.isAbstract(impl.getModifiers())
                                || !kp[0].isAssignableFrom(impl) || impl.getName().toLowerCase().contains("test")) continue;
                        try {
                            Constructor<?> ic = impl.getDeclaredConstructor();
                            ic.setAccessible(true);
                            finders.add(k.newInstance(ic.newInstance()));
                            variantNames.add(impl.getSimpleName());
                        } catch (NoSuchMethodException e) { /* not no-arg constructible */ }
                    }
                }
            }
            if (finders.isEmpty()) {
                System.out.println("RESULT FAIL could not construct " + finderClass.getName());
                return;
            }
        }
        System.out.println("variants: " + variantNames);

        List<Case> cases = new ArrayList<>();
        cases.add(new Case(6, new int[][]{{1, 2}, {0, 3}, {2, 0}, {5, 4}}));   // baseline demo
        cases.add(new Case(0, new int[][]{}));
        cases.add(new Case(1, new int[][]{}));
        cases.add(new Case(1, new int[][]{{0, 0}}));                          // self-loop
        cases.add(new Case(5, new int[][]{}));                                // all isolated
        cases.add(new Case(4, new int[][]{{0, 1}, {0, 1}, {1, 0}, {2, 2}}));  // parallel + self-loop
        cases.add(new Case(7, new int[][]{{6, 5}, {5, 4}, {3, 2}, {0, 6}}));  // components not led by min index
        {   // long path (deep traversal)
            int n = 5000; int[][] e = new int[n - 1][];
            for (int i = 0; i + 1 < n; i++) e[i] = new int[]{n - 1 - i, n - 2 - i};
            cases.add(new Case(n, e));
        }
        {   // star + isolated tail
            int n = 300; List<int[]> e = new ArrayList<>();
            for (int i = 1; i < 200; i++) e.add(new int[]{0, i});
            cases.add(new Case(n, e.toArray(new int[0][])));
        }
        Random rnd = new Random(27027);
        for (int t = 0; t < 40; t++) {
            int v = 1 + rnd.nextInt(t < 20 ? 15 : 200);
            // Edge counts around the connectivity threshold give varied partitions.
            int m = switch (t % 4) { case 0 -> v / 3; case 1 -> v / 2; case 2 -> v; default -> 2 * v; };
            int[][] e = new int[m][];
            for (int i = 0; i < m; i++) e[i] = new int[]{rnd.nextInt(v), rnd.nextInt(v)};
            cases.add(new Case(v, e));
        }

        int failed = 0, runs = 0;
        for (Case c : cases) {
            Set<Set<Integer>> expected = reference(c.v, c.edges);
            for (int f = 0; f < finders.size(); f++) {
                runs++;
                String err;
                try { err = runCase(finders.get(f), c, expected); }
                catch (Throwable e) {
                    Throwable x = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
                    err = "exception " + x;
                }
                if (err != null) {
                    failed++;
                    System.out.println("CHECK FAIL [" + variantNames.get(f) + "] V=" + c.v + " edges="
                            + (c.edges.length <= 10 ? Arrays.deepToString(c.edges) : c.edges.length + " edges")
                            + ": " + err);
                }
            }
        }
        System.out.println(runs + " runs (" + cases.size() + " graphs x " + finders.size() + " variants), " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + cases.size() + " graphs x " + variantNames
                : "RESULT FAIL " + failed + "/" + runs + " runs gave wrong components");
    }

    record Case(int v, int[][] edges) {}

    static String runCase(Object finder, Case c, Set<Set<Integer>> expected) throws Exception {
        Object res;
        if (listEntry != null) {
            ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < c.v; i++) adj.add(new ArrayList<>());
            for (int[] e : c.edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
            res = listEntry.invoke(null, adj);
        } else {
            res = finderFind.invoke(finder, buildGraph(c.v, c.edges));
        }
        List<List<Integer>> comps = readComponents(res);
        // Partition checks.
        int[] seen = new int[c.v];
        Set<Set<Integer>> actual = new HashSet<>();
        for (List<Integer> comp : comps) {
            if (comp.isEmpty()) return "empty component in result";
            for (int x : comp) {
                if (x < 0 || x >= c.v) return "vertex out of range: " + x;
                if (seen[x]++ > 0) return "vertex " + x + " appears more than once";
            }
            actual.add(new HashSet<>(comp));
        }
        for (int i = 0; i < c.v; i++) if (seen[i] == 0) return "vertex " + i + " missing from every component";
        if (!actual.equals(expected))
            return "partition differs:\n  expected " + fmt(expected) + "\n  actual   " + fmt(actual);
        if (res instanceof Collection<?>) return null;
        return checkQueries(res, comps, c.v);
    }

    /** Cross-checks count()/componentOf()/connected()/Component.size()/contains() on a result object. */
    static String checkQueries(Object res, List<List<Integer>> comps, int v) throws Exception {
        Class<?> rc = res.getClass();
        Method count = find(rc, "count");
        if (count != null && ((Number) count.invoke(res)).intValue() != comps.size())
            return "count() " + count.invoke(res) + " != " + comps.size();
        int[] owner = new int[v];
        for (int id = 0; id < comps.size(); id++) for (int x : comps.get(id)) owner[x] = id;
        Method compOf = find(rc, "componentOf", int.class);
        if (compOf != null)
            for (int x = 0; x < v; x++) {
                int id = ((Number) compOf.invoke(res, x)).intValue();
                if (id != owner[x]) return "componentOf(" + x + ")=" + id + " but vertex is in component #" + owner[x];
            }
        Method conn = find(rc, "connected", int.class, int.class);
        if (conn != null) {
            Random r = new Random(v * 31L + 7);
            int probes = Math.min(400, v * v);
            for (int k = 0; k < probes; k++) {
                int a = r.nextInt(v), b = r.nextInt(v);
                boolean exp = owner[a] == owner[b];
                if ((Boolean) conn.invoke(res, a, b) != exp) return "connected(" + a + "," + b + ") != " + exp;
            }
        }
        if (res instanceof Iterable<?> it) {
            for (Object comp : it) {
                if (comp instanceof Collection<?>) break;
                Method size = find(comp.getClass(), "size");
                Method contains = find(comp.getClass(), "contains", int.class);
                List<Integer> verts = toIntList(comp);
                if (size != null && ((Number) size.invoke(comp)).intValue() != verts.size())
                    return "Component.size() disagrees with its vertices " + verts;
                if (contains != null && v <= 400)
                    for (int x = 0; x < v; x++)
                        if ((Boolean) contains.invoke(comp, x) != verts.contains(x))
                            return "Component.contains(" + x + ") wrong for " + verts;
            }
        }
        return null;
    }

    /** Extracts components as lists of vertex ints from any of the result shapes. */
    static List<List<Integer>> readComponents(Object res) throws Exception {
        if (res == null) throw new IllegalStateException("null result");
        if (!(res instanceof Collection<?>)) {
            Method asList = find(res.getClass(), "asList");
            if (asList != null && List.class.isAssignableFrom(asList.getReturnType()))
                return readComponents(asList.invoke(res));
        }
        if (res instanceof Iterable<?> it) {
            List<List<Integer>> out = new ArrayList<>();
            for (Object comp : it) out.add(toIntList(comp));
            return out;
        }
        throw new IllegalStateException("cannot read components from " + res.getClass().getName());
    }

    static List<Integer> toIntList(Object comp) throws Exception {
        List<Integer> out = new ArrayList<>();
        if (comp instanceof Iterable<?> it) { for (Object o : it) out.add(((Number) o).intValue()); return out; }
        Method verts = find(comp.getClass(), "vertices");
        if (verts != null) return toIntList(verts.invoke(comp));
        throw new IllegalStateException("cannot read vertices from " + comp.getClass().getName());
    }

    /** Builds the subject's Graph: Graph(int)+addEdge, or a builder (int ctor, addEdge, build() -> Graph). */
    static Object buildGraph(int v, int[][] edges) throws Exception {
        Constructor<?> gc = intCtor(graphClass);
        Method add = find(graphClass, "addEdge", int.class, int.class);
        if (gc != null && add != null) {
            Object g = gc.newInstance(v);
            for (int[] e : edges) add.invoke(g, e[0], e[1]);
            return g;
        }
        for (Class<?> b : loaded) {
            if (b.getName().toLowerCase().contains("test")) continue;
            Constructor<?> bc = intCtor(b);
            Method badd = find(b, "addEdge", int.class, int.class);
            Method build = null;
            for (Method m : b.getDeclaredMethods())
                if (!m.isSynthetic() && m.getParameterCount() == 0 && m.getReturnType() == graphClass
                        && !Modifier.isStatic(m.getModifiers())) { build = m; build.setAccessible(true); }
            if (bc == null || badd == null || build == null) continue;
            Object builder = bc.newInstance(v);
            for (int[] e : edges) badd.invoke(builder, e[0], e[1]);
            return build.invoke(builder);
        }
        throw new IllegalStateException("no way to construct " + graphClass.getName());
    }

    static Constructor<?> intCtor(Class<?> c) {
        for (Constructor<?> k : c.getDeclaredConstructors()) {
            Class<?>[] p = k.getParameterTypes();
            if (p.length == 1 && p[0] == int.class) { k.setAccessible(true); return k; }
        }
        return null;
    }

    static Method find(Class<?> c, String name, Class<?>... params) {
        for (Class<?> k = c; k != null; k = k.getSuperclass())
            for (Method m : k.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || !m.getName().equals(name)) continue;
                if (Arrays.equals(m.getParameterTypes(), params)) { m.setAccessible(true); return m; }
            }
        return null;
    }

    /** Independent oracle: union-find partition. */
    static Set<Set<Integer>> reference(int v, int[][] edges) {
        int[] p = new int[v];
        for (int i = 0; i < v; i++) p[i] = i;
        for (int[] e : edges) {
            int a = root(p, e[0]), b = root(p, e[1]);
            if (a != b) p[a] = b;
        }
        Map<Integer, Set<Integer>> groups = new HashMap<>();
        for (int i = 0; i < v; i++) groups.computeIfAbsent(root(p, i), k -> new HashSet<>()).add(i);
        return new HashSet<>(groups.values());
    }

    static int root(int[] p, int x) { while (p[x] != x) x = p[x] = p[p[x]]; return x; }

    static String fmt(Set<Set<Integer>> s) {
        if (s.size() > 12) return s.size() + " components";
        return s.stream().map(TreeSet::new).map(Object::toString).sorted().collect(Collectors.joining(" "));
    }
}
