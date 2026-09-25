import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 029_detect_cycle_directed_graph.
 *
 * Baseline semantics (iteration 0): static boolean isCyclic(ArrayList<ArrayList<Integer>> adj)
 * over a DIRECTED graph, vertices 0..V-1 (V = adj.size()), adj.get(u) = out-neighbours.
 * Parallel edges and self-loops are allowed (a self-loop is a cycle). Returns
 * true iff the graph contains a directed cycle (Kahn's algorithm in the baseline).
 *
 * API drift:
 *  iter 1     DirectedGraph(int) + addEdge(int,int); new CycleDetector().hasCycle(g)
 *  iter 2-3   + findCycle(g) -> Optional<List<Integer>> (closed walk, e.g. [0,1,2,0])
 *  iter 4     DirectedGraph.from(int, int[][]); findCycle -> Optional<Cycle> (record, vertices())
 *  iter 5+    CycleDetector becomes an interface; DfsCycleDetector / KahnCycleDetector
 *             strategies + CycleDetectionAlgorithm enum factory (create())
 *  iter 7+    LoggingCycleDetector(delegate, logger) decorator; NoOpLogger / ConsoleLogger
 *  iter 8+    DirectedGraph.Builder (from(...) delegates to it)
 *
 * Discovery (reflection): the graph type G is the single parameter of any
 * hasCycle/findCycle method. G is built via a static (int, int[][]) factory, or
 * an (int) constructor + addEdge(int,int). Every concrete class exposing
 * hasCycle(G)/findCycle(G) with a no-arg constructor is tested directly; every
 * enum constant with a create() returning a detector is tested; every decorator
 * (constructor taking a detector + other collaborators) is tested wrapping every
 * base detector with every available collaborator implementation.
 *
 * Oracle: independent iterative three-colour DFS cycle answer. Every returned
 * cycle must be a real simple directed cycle of the graph (each consecutive
 * pair, including the closing pair, is an edge; no repeated vertex).
 * Suite: baseline demo, fixed edge cases, long chains, 48 fixed-seed random graphs.
 */
public class Driver {

    static final PrintStream SINK = new PrintStream(OutputStream.nullOutputStream());

    static Method shapeA;          // static boolean isCyclic(ArrayList)
    static Class<?> graphType;     // G
    static Method graphFactory;    // static G f(int, int[][])
    static Constructor<?> graphCtor;
    static Method graphAddEdge;

    record Detector(String label, Object instance, Method hasCycle, Method findCycle) {}

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
        List<Class<?>> all = new ArrayList<>();
        for (String cn : classNames) {
            try { all.add(Class.forName(cn, false, cl)); } catch (Throwable t) { /* skip */ }
        }

        List<Detector> detectors = new ArrayList<>();
        String discErr = discover(all, detectors);
        if (discErr != null) {
            System.out.println("RESULT FAIL " + discErr + " among " + classNames);
            return;
        }
        if (shapeA != null) System.out.println("entry (shape A): " + shapeA);
        else {
            System.out.println("graph type: " + graphType.getName() + " built via "
                    + (graphFactory != null ? graphFactory.toString() : graphCtor + " + " + graphAddEdge));
            for (Detector d : detectors)
                System.out.println("detector: " + d.label + " hasCycle=" + (d.hasCycle != null)
                        + " findCycle=" + (d.findCycle != null));
        }

        List<TestCase> cases = buildCases();
        int failed = 0, checks = 0, cyclesValidated = 0;
        List<String> failedDetectors = new ArrayList<>();
        List<Detector> runList = shapeA != null
                ? List.of(new Detector("static isCyclic", null, shapeA, null)) : detectors;
        for (Detector d : runList) {
            int dFailed = 0;
            for (TestCase tc : cases) {
                checks++;
                boolean expected = referenceHasCycle(tc.v, tc.edges);
                String err;
                try {
                    err = runOne(d, tc, expected);
                    if (err == null && d.findCycle != null && expected) cyclesValidated++;
                } catch (Throwable e) {
                    Throwable c = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
                    err = "exception " + c;
                }
                if (err != null) {
                    dFailed++;
                    if (dFailed <= 5)
                        System.out.println("CHECK FAIL [" + d.label + "] " + tc.name + " V=" + tc.v
                                + " edges=" + tc.edgeString() + ": " + err);
                }
            }
            failed += dFailed;
            if (dFailed > 0) failedDetectors.add(d.label + " " + dFailed + "/" + cases.size());
        }
        System.out.println(runList.size() + " detector(s) x " + cases.size() + " graphs = " + checks
                + " checks, " + failed + " failed; " + cyclesValidated + " returned cycles validated");
        if (failed == 0)
            System.out.println("RESULT PASS " + runList.size() + " detector(s) x " + cases.size() + " graphs");
        else
            System.out.println("RESULT FAIL wrong answers from " + String.join(", ", failedDetectors));
    }

    // ---------------- discovery ----------------

    static boolean isDetectMethodName(String n) {
        n = n.toLowerCase();
        return n.equals("hascycle") || n.equals("findcycle") || n.equals("iscyclic");
    }

    static String discover(List<Class<?>> all, List<Detector> detectors) throws Exception {
        // Shape A: static boolean isCyclic(ArrayList/List)
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && p[0].isAssignableFrom(ArrayList.class)
                        && m.getReturnType() == boolean.class && isDetectMethodName(m.getName())) {
                    m.setAccessible(true);
                    shapeA = m;
                    return null;
                }
            }
        }

        // Graph type: parameter of any hasCycle/findCycle instance method.
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                if (!isDetectMethodName(m.getName()) || m.getParameterCount() != 1) continue;
                Class<?> p = m.getParameterTypes()[0];
                if (p.isPrimitive() || p.isArray() || p.getName().startsWith("java.")) continue;
                graphType = p;
                break;
            }
            if (graphType != null) break;
        }
        if (graphType == null) return "no hasCycle/findCycle(Graph) entry point found";

        // Graph construction.
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && p[0] == int.class && p[1] == int[][].class
                        && graphType.isAssignableFrom(m.getReturnType())) {
                    m.setAccessible(true);
                    graphFactory = m;
                    break;
                }
            }
            if (graphFactory != null) break;
        }
        if (graphFactory == null) {
            try {
                graphCtor = graphType.getDeclaredConstructor(int.class);
                graphCtor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                return "no way to build " + graphType.getName() + " (no static (int,int[][]) factory or (int) ctor)";
            }
            for (Method m : graphType.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && p[0] == int.class && p[1] == int.class
                        && m.getName().toLowerCase().contains("edge")) {
                    m.setAccessible(true);
                    graphAddEdge = m;
                    break;
                }
            }
            if (graphAddEdge == null) return "no addEdge(int,int) on " + graphType.getName();
        }

        // Base detectors: concrete classes with hasCycle/findCycle(G) and a no-arg ctor.
        List<Object[]> bases = new ArrayList<>();   // {label, instance}
        List<Class<?>> decorators = new ArrayList<>();
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || c.isEnum()) continue;
            if (detectMethod(c, "hasCycle") == null && detectMethod(c, "findCycle") == null) continue;
            Constructor<?> noArg = null;
            try { noArg = c.getDeclaredConstructor(); } catch (NoSuchMethodException ignored) { }
            if (noArg != null) {
                noArg.setAccessible(true);
                Object inst = noArg.newInstance();
                bases.add(new Object[]{c.getSimpleName(), inst});
            } else {
                decorators.add(c);
            }
        }
        for (Object[] b : bases) detectors.add(makeDetector((String) b[0], b[1]));

        // Enum factories: every constant's create() returning a detector.
        for (Class<?> c : all) {
            if (!c.isEnum()) continue;
            Method create = null;
            for (Method m : allMethods(c)) {
                if (m.getParameterCount() == 0 && !Modifier.isStatic(m.getModifiers())
                        && (detectMethod(m.getReturnType(), "hasCycle") != null
                        || detectMethod(m.getReturnType(), "findCycle") != null)) { create = m; break; }
            }
            if (create == null) continue;
            create.setAccessible(true);
            for (Object constant : c.getEnumConstants()) {
                Object inst = create.invoke(constant);
                detectors.add(makeDetector(c.getSimpleName() + "." + constant + "." + create.getName() + "()", inst));
            }
        }

        // Decorators: ctor taking a detector plus collaborators; wrap every base with every collaborator impl.
        for (Class<?> c : decorators) {
            for (Constructor<?> ctor : c.getDeclaredConstructors()) {
                Class<?>[] p = ctor.getParameterTypes();
                int detIdx = -1;
                for (int i = 0; i < p.length; i++)
                    if (!bases.isEmpty() && p[i].isInstance(bases.get(0)[1]) && p[i] != Object.class) detIdx = i;
                if (detIdx < 0) continue;
                // collaborator options per other parameter
                List<List<Object[]>> options = new ArrayList<>();
                boolean ok = true;
                for (int i = 0; i < p.length; i++) {
                    if (i == detIdx) { options.add(Collections.singletonList(new Object[]{"", null})); continue; }
                    List<Object[]> impls = implementationsOf(p[i], all);
                    if (impls.isEmpty()) { ok = false; break; }
                    options.add(impls);
                }
                if (!ok) continue;
                ctor.setAccessible(true);
                for (Object[] base : bases) {
                    for (List<Object[]> combo : cartesian(options)) {
                        Object[] argv = new Object[p.length];
                        StringBuilder lab = new StringBuilder(c.getSimpleName()).append("(");
                        for (int i = 0; i < p.length; i++) {
                            if (i > 0) lab.append(", ");
                            if (i == detIdx) { argv[i] = base[1]; lab.append(base[0]); }
                            else { argv[i] = combo.get(i)[1]; lab.append(combo.get(i)[0]); }
                        }
                        lab.append(")");
                        detectors.add(makeDetector(lab.toString(), ctor.newInstance(argv)));
                    }
                }
                break;
            }
        }
        if (detectors.isEmpty()) return "no instantiable cycle detector found";
        return null;
    }

    static List<List<Object[]>> cartesian(List<List<Object[]>> options) {
        List<List<Object[]>> out = new ArrayList<>();
        out.add(new ArrayList<>());
        for (List<Object[]> opts : options) {
            List<List<Object[]>> next = new ArrayList<>();
            for (List<Object[]> prefix : out)
                for (Object[] o : opts) {
                    List<Object[]> l = new ArrayList<>(prefix);
                    l.add(o);
                    next.add(l);
                }
            out = next;
        }
        return out;
    }

    /** Instances of every concrete subject implementation of {@code type} (enum constants, PrintStream ctor, no-arg ctor). */
    static List<Object[]> implementationsOf(Class<?> type, List<Class<?>> all) {
        List<Object[]> out = new ArrayList<>();
        for (Class<?> c : all) {
            if (!type.isAssignableFrom(c) || c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            try {
                if (c.isEnum()) {
                    for (Object k : c.getEnumConstants()) out.add(new Object[]{c.getSimpleName() + "." + k, k});
                    continue;
                }
                Constructor<?> ps = null, none = null;
                for (Constructor<?> k : c.getDeclaredConstructors()) {
                    if (k.getParameterCount() == 1 && k.getParameterTypes()[0] == PrintStream.class) ps = k;
                    if (k.getParameterCount() == 0) none = k;
                }
                if (ps != null) { ps.setAccessible(true); out.add(new Object[]{c.getSimpleName(), ps.newInstance(SINK)}); }
                else if (none != null) { none.setAccessible(true); out.add(new Object[]{c.getSimpleName(), none.newInstance()}); }
            } catch (Throwable ignored) { }
        }
        return out;
    }

    static Detector makeDetector(String label, Object inst) {
        Method h = detectMethod(inst.getClass(), "hasCycle");
        Method f = detectMethod(inst.getClass(), "findCycle");
        return new Detector(label, inst, h, f);
    }

    static Method detectMethod(Class<?> type, String name) {
        for (Method m : allMethods(type)) {
            if (!m.getName().equals(name) || m.getParameterCount() != 1) continue;
            if (graphType != null && !m.getParameterTypes()[0].isAssignableFrom(graphType)) continue;
            if (Modifier.isAbstract(m.getModifiers()) && !type.isInterface() && !Modifier.isAbstract(type.getModifiers()))
                continue; // abstract decl seen through an interface; concrete impl found elsewhere
            m.setAccessible(true);
            return m;
        }
        // fall back to abstract declarations (resolved virtually at invoke time)
        for (Method m : allMethods(type)) {
            if (!m.getName().equals(name) || m.getParameterCount() != 1) continue;
            if (graphType != null && !m.getParameterTypes()[0].isAssignableFrom(graphType)) continue;
            m.setAccessible(true);
            return m;
        }
        return null;
    }

    static List<Method> allMethods(Class<?> type) {
        List<Method> out = new ArrayList<>();
        Deque<Class<?>> todo = new ArrayDeque<>();
        Set<Class<?>> seen = new HashSet<>();
        todo.add(type);
        while (!todo.isEmpty()) {
            Class<?> c = todo.poll();
            if (c == Object.class || !seen.add(c)) continue;
            for (Method m : c.getDeclaredMethods())
                if (!m.isSynthetic() && !m.isBridge() && !Modifier.isStatic(m.getModifiers())) out.add(m);
            if (c.getSuperclass() != null) todo.add(c.getSuperclass());
            todo.addAll(Arrays.asList(c.getInterfaces()));
        }
        return out;
    }

    // ---------------- running ----------------

    static String runOne(Detector d, TestCase tc, boolean expected) throws Throwable {
        if (shapeA != null) {
            ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < tc.v; i++) adj.add(new ArrayList<>());
            for (int[] e : tc.edges) adj.get(e[0]).add(e[1]);
            boolean got = (Boolean) shapeA.invoke(null, adj);
            return got == expected ? null : "expected hasCycle=" + expected + " actual=" + got;
        }
        Object g = buildGraph(tc);
        if (d.hasCycle != null) {
            boolean got = (Boolean) d.hasCycle.invoke(d.instance, g);
            if (got != expected) return "hasCycle: expected " + expected + " actual " + got;
        }
        if (d.findCycle != null) {
            Object r = d.findCycle.invoke(d.instance, g);
            Object value;
            if (r instanceof Optional<?> o) value = o.orElse(null);
            else value = r;
            boolean got = value != null;
            if (got != expected) return "findCycle: expected present=" + expected + " actual " + value;
            if (value != null) {
                List<Integer> seq = cycleVertices(value);
                if (seq == null) return "findCycle: cannot read vertices from " + value.getClass().getName();
                String bad = validateCycle(tc, seq);
                if (bad != null) return "findCycle returned invalid cycle " + seq + ": " + bad;
            }
        }
        return null;
    }

    static List<Integer> cycleVertices(Object value) throws Exception {
        if (value instanceof Collection<?> c) return toInts(c);
        if (value instanceof int[] a) return Arrays.stream(a).boxed().collect(Collectors.toList());
        for (Method m : allMethods(value.getClass())) {
            if (m.getParameterCount() == 0 && Collection.class.isAssignableFrom(m.getReturnType())) {
                m.setAccessible(true);
                return toInts((Collection<?>) m.invoke(value));
            }
        }
        return null;
    }

    static List<Integer> toInts(Collection<?> c) {
        List<Integer> out = new ArrayList<>();
        for (Object o : c) out.add(((Number) o).intValue());
        return out;
    }

    /** Accepts a closed walk [a,...,a] or an open list [a,...] with implicit closing edge. */
    static String validateCycle(TestCase tc, List<Integer> seq) {
        List<Integer> cyc = new ArrayList<>(seq);
        if (cyc.size() >= 2 && cyc.get(0).equals(cyc.get(cyc.size() - 1))) cyc.remove(cyc.size() - 1);
        if (cyc.isEmpty()) return "empty";
        if (new HashSet<>(cyc).size() != cyc.size()) return "repeated vertex (not a simple cycle)";
        Set<Long> edgeSet = new HashSet<>();
        for (int[] e : tc.edges) edgeSet.add(((long) e[0] << 32) | e[1]);
        for (int i = 0; i < cyc.size(); i++) {
            int u = cyc.get(i), w = cyc.get((i + 1) % cyc.size());
            if (u < 0 || u >= tc.v) return "vertex " + u + " out of range";
            if (!edgeSet.contains(((long) u << 32) | w)) return "edge " + u + "->" + w + " not in graph";
        }
        return null;
    }

    static Object buildGraph(TestCase tc) throws Exception {
        if (graphFactory != null) {
            int[][] copy = new int[tc.edges.length][];
            for (int i = 0; i < copy.length; i++) copy[i] = tc.edges[i].clone();
            return graphFactory.invoke(null, tc.v, copy);
        }
        Object g = graphCtor.newInstance(tc.v);
        for (int[] e : tc.edges) graphAddEdge.invoke(g, e[0], e[1]);
        return g;
    }

    // ---------------- oracle ----------------

    /** Independent reference: iterative three-colour DFS (white/grey/black). */
    static boolean referenceHasCycle(int v, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < v; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        int[] color = new int[v];     // 0 white, 1 grey, 2 black
        int[] idx = new int[v];
        for (int s = 0; s < v; s++) {
            if (color[s] != 0) continue;
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(s);
            color[s] = 1;
            while (!stack.isEmpty()) {
                int u = stack.peek();
                if (idx[u] < adj.get(u).size()) {
                    int w = adj.get(u).get(idx[u]++);
                    if (color[w] == 1) return true;
                    if (color[w] == 0) { color[w] = 1; stack.push(w); }
                } else {
                    color[u] = 2;
                    stack.pop();
                }
            }
        }
        return false;
    }

    // ---------------- inputs ----------------

    record TestCase(String name, int v, int[][] edges) {
        String edgeString() {
            if (edges.length > 14) return edges.length + " edges";
            return Arrays.deepToString(edges);
        }
    }

    static int[][] e(int[]... es) { return es; }
    static int[] p(int a, int b) { return new int[]{a, b}; }

    static List<TestCase> buildCases() {
        List<TestCase> cases = new ArrayList<>();
        cases.add(new TestCase("baseline demo", 4, e(p(0, 1), p(1, 2), p(2, 0), p(2, 3))));
        cases.add(new TestCase("empty graph V=0", 0, e()));
        cases.add(new TestCase("single vertex", 1, e()));
        cases.add(new TestCase("self-loop", 1, e(p(0, 0))));
        cases.add(new TestCase("self-loop deep in DAG", 5, e(p(0, 1), p(1, 2), p(2, 3), p(3, 3), p(3, 4))));
        cases.add(new TestCase("2-cycle", 2, e(p(0, 1), p(1, 0))));
        cases.add(new TestCase("parallel edges DAG", 3, e(p(0, 1), p(0, 1), p(1, 2), p(0, 2))));
        cases.add(new TestCase("diamond DAG", 4, e(p(0, 1), p(0, 2), p(1, 3), p(2, 3))));
        cases.add(new TestCase("reverse chain DAG", 5, e(p(4, 3), p(3, 2), p(2, 1), p(1, 0))));
        cases.add(new TestCase("cycle unreachable from 0", 6, e(p(0, 1), p(3, 4), p(4, 5), p(5, 3))));
        cases.add(new TestCase("tail into cycle", 5, e(p(0, 1), p(1, 2), p(2, 3), p(3, 1), p(0, 4))));
        cases.add(new TestCase("cycle feeding a sink", 5, e(p(1, 2), p(2, 1), p(2, 3), p(3, 4))));
        cases.add(new TestCase("cross edge, no cycle", 5, e(p(0, 1), p(0, 2), p(2, 1), p(1, 3), p(2, 4), p(4, 3))));
        cases.add(new TestCase("two disjoint cycles", 6, e(p(0, 1), p(1, 0), p(2, 3), p(3, 4), p(4, 2))));
        cases.add(new TestCase("isolated vertices + DAG", 8, e(p(2, 5), p(5, 7))));
        // long structures (well inside the baseline's domain)
        int n = 1500;
        List<int[]> chain = new ArrayList<>();
        for (int i = 0; i + 1 < n; i++) chain.add(p(i, i + 1));
        cases.add(new TestCase("long chain DAG", n, chain.toArray(new int[0][])));
        List<int[]> ring = new ArrayList<>(chain);
        ring.add(p(n - 1, 0));
        cases.add(new TestCase("long ring", n, ring.toArray(new int[0][])));

        Random rnd = new Random(29029);
        for (int t = 0; t < 48; t++) cases.add(randomCase(rnd, t));
        return cases;
    }

    static TestCase randomCase(Random rnd, int t) {
        int v = 1 + rnd.nextInt(40);
        List<int[]> edges = new ArrayList<>();
        int[] order = IntStream.range(0, v).toArray();
        for (int i = v - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int x = order[i]; order[i] = order[j]; order[j] = x; }
        String mode;
        double d = new double[]{0.05, 0.1, 0.2, 0.4}[rnd.nextInt(4)];
        switch (t % 4) {
            case 0 -> { // random DAG over a random topological order
                mode = "DAG";
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (rnd.nextDouble() < d) edges.add(p(order[i], order[j]));
                if (!edges.isEmpty() && rnd.nextBoolean()) edges.add(edges.get(rnd.nextInt(edges.size())).clone());
            }
            case 1 -> { // DAG + one back edge (creates exactly the cycles through it)
                mode = "DAG+back edge";
                if (v < 2) v = 2 + rnd.nextInt(20);
                order = IntStream.range(0, v).toArray();
                for (int i = v - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int x = order[i]; order[i] = order[j]; order[j] = x; }
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (rnd.nextDouble() < d) edges.add(p(order[i], order[j]));
                int a = rnd.nextInt(v), b = rnd.nextInt(v);
                if (a > b) { int x = a; a = b; b = x; }
                // back edge order[b] -> order[a]; cycle exists iff order[a] reaches order[b] (or a == b)
                edges.add(p(order[b], order[a]));
            }
            case 2 -> { // unconstrained random digraph (may contain self-loops)
                mode = "random digraph";
                int m = rnd.nextInt(2 * v + 1);
                for (int i = 0; i < m; i++) {
                    int a = rnd.nextInt(v), b = rnd.nextInt(v);
                    if (a == b && rnd.nextInt(3) != 0) continue;
                    edges.add(p(a, b));
                }
            }
            default -> { // several components, one of which may carry a cycle
                mode = "multi-component";
                if (v < 6) v = 6 + rnd.nextInt(25);
                int comps = 2 + rnd.nextInt(3);
                int[] comp = new int[v];
                for (int i = 0; i < v; i++) comp[i] = rnd.nextInt(comps + 1);   // comps => isolated
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (comp[i] == comp[j] && comp[i] != comps && rnd.nextDouble() < 0.3) edges.add(p(i, j));
                if (rnd.nextBoolean()) {
                    // plant a cycle among the highest-numbered members of one component
                    int target = rnd.nextInt(comps);
                    List<Integer> mem = new ArrayList<>();
                    for (int i = v - 1; i >= 0; i--) if (comp[i] == target) mem.add(i);
                    if (mem.size() >= 2) edges.add(p(mem.get(0), mem.get(mem.size() - 1)));
                }
            }
        }
        Collections.shuffle(edges, rnd);
        return new TestCase("random#" + t + " (" + mode + ")", v, edges.toArray(new int[0][]));
    }
}
