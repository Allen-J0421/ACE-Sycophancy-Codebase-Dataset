import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 028_bipartite_graph.
 *
 * Baseline semantics (iteration 0): static boolean isBipartite(int V, int[][] edges)
 * over an UNDIRECTED graph with vertices 0..V-1, edges given as {u, v} pairs
 * (parallel edges allowed, self-loops allowed: a self-loop is an odd cycle so the
 * answer is false). Disconnected graphs / isolated vertices are handled by seeding
 * a BFS 2-coloring from every uncoloured vertex.
 *
 * Iterations 1+ move to package bipartite/: UndirectedGraph.of(int, int[][]) and
 * new BipartiteChecker().check(graph) -> BipartiteResult { isBipartite(),
 * partitionA()/partitionB() when bipartite, oddCycle() witness (iter 2+) when not }.
 * Iteration 5+ widens check(...) to a Graph interface; iterations 6-10 add
 * unrelated algorithms (DFS, topo sort, shortest path, SCC, MST) to the package,
 * which this driver deliberately ignores: only the bipartite contract is tested.
 *
 * Discovery (reflection):
 *  A) static (int, int[][]) -> boolean method (baseline shape), or
 *  B) a 1-arg method (GraphType) -> R where R has boolean isBipartite(), plus a
 *     static (int, int[][]) factory on an undirected graph class returning GraphType.
 *
 * Oracle: independent parity union-find 2-colouring. When the result exposes
 * partitions they must form a valid 2-colouring covering every vertex; when it
 * exposes an odd-cycle witness it must be a simple odd cycle of real edges.
 * Groups: baseline demo, fixed edge cases, 48 fixed-seed random graphs (odd
 * cycles, disconnected parts, isolated vertices, parallel edges), and a
 * self-loop group (baseline answers false for those).
 */
public class Driver {

    static Method shapeA;              // static boolean f(int, int[][])
    static Method checkM;              // R check(GraphType)
    static Object checkerInstance;     // null when checkM is static
    static Method graphFactory;        // static GraphType of(int, int[][])
    static int partitionsChecked, cyclesChecked;

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

        discover(all);
        if (shapeA == null && checkM == null) {
            System.out.println("RESULT FAIL no bipartite entry point found among " + classNames);
            return;
        }
        if (shapeA != null) System.out.println("entry (shape A): " + shapeA);
        else System.out.println("entry (shape B): " + checkM + " with graph factory " + graphFactory);

        // ---------------- test suite ----------------
        List<TestCase> core = new ArrayList<>();
        core.add(new TestCase("baseline demo", 4, new int[][]{{0, 1}, {0, 2}, {1, 2}, {2, 3}}));
        core.add(new TestCase("empty graph V=0", 0, new int[][]{}));
        core.add(new TestCase("single vertex", 1, new int[][]{}));
        core.add(new TestCase("all isolated", 5, new int[][]{}));
        core.add(new TestCase("single edge", 2, new int[][]{{0, 1}}));
        core.add(new TestCase("triangle", 3, new int[][]{{0, 1}, {1, 2}, {2, 0}}));
        core.add(new TestCase("square", 4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}}));
        core.add(new TestCase("pentagon", 5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0}}));
        core.add(new TestCase("parallel edges", 3, new int[][]{{0, 1}, {1, 0}, {0, 1}, {1, 2}}));
        core.add(new TestCase("bipartite comp + triangle in 2nd comp", 7,
                new int[][]{{0, 1}, {1, 2}, {2, 3}, {4, 5}, {5, 6}, {6, 4}}));
        core.add(new TestCase("triangle far from vertex 0, isolated between", 9,
                new int[][]{{0, 1}, {6, 7}, {7, 8}, {8, 6}}));
        core.add(new TestCase("K3,3", 6, new int[][]{{0, 3}, {0, 4}, {0, 5}, {1, 3}, {1, 4}, {1, 5},
                {2, 3}, {2, 4}, {2, 5}}));
        core.add(new TestCase("K4", 4, new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}}));
        core.add(new TestCase("odd cycle 9 with chords-free tail", 12,
                new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 0},
                        {8, 9}, {9, 10}}));
        core.add(new TestCase("even cycle 10 + isolated", 12,
                new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 9}, {9, 0}}));
        core.add(new TestCase("reverse-labelled path", 6,
                new int[][]{{5, 4}, {4, 3}, {3, 2}, {2, 1}, {1, 0}}));

        Random rnd = new Random(28028);
        for (int t = 0; t < 48; t++) core.add(randomCase(rnd, t));

        List<TestCase> selfLoops = new ArrayList<>();
        selfLoops.add(new TestCase("self-loop only", 1, new int[][]{{0, 0}}));
        selfLoops.add(new TestCase("self-loop on otherwise bipartite path", 4,
                new int[][]{{0, 1}, {1, 2}, {2, 3}, {2, 2}}));
        selfLoops.add(new TestCase("self-loop on isolated vertex of 2nd comp", 5,
                new int[][]{{0, 1}, {1, 2}, {4, 4}}));

        int coreFailed = runGroup("core", core);
        int loopFailed = runGroup("self-loop", selfLoops);

        int total = core.size() + selfLoops.size();
        System.out.println("witnesses validated: " + partitionsChecked + " partitions, "
                + cyclesChecked + " odd cycles");
        System.out.println(total + " graphs checked: core " + (core.size() - coreFailed) + "/" + core.size()
                + " ok, self-loop " + (selfLoops.size() - loopFailed) + "/" + selfLoops.size() + " ok");
        if (coreFailed == 0 && loopFailed == 0) {
            System.out.println("RESULT PASS " + total + " graphs (" + core.size() + " core incl. 48 random, "
                    + selfLoops.size() + " self-loop)");
        } else {
            StringBuilder why = new StringBuilder();
            if (coreFailed > 0) why.append(coreFailed).append("/").append(core.size())
                    .append(" core graphs wrong");
            if (loopFailed > 0) {
                if (why.length() > 0) why.append("; ");
                why.append(loopFailed).append("/").append(selfLoops.size())
                        .append(" self-loop graphs wrong (baseline answers false)");
            }
            if (coreFailed == 0) why.append("; core ").append(core.size()).append("/").append(core.size()).append(" ok");
            System.out.println("RESULT FAIL " + why);
        }
    }

    // ---------------- discovery ----------------

    static void discover(List<Class<?>> all) {
        // Shape A: static boolean f(int, int[][])
        for (int pass = 0; pass < 1 && shapeA == null; pass++) {   // name must mention "bipartite"
            for (Class<?> c : all) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge()) continue;
                    if (!Modifier.isStatic(m.getModifiers())) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length != 2 || p[0] != int.class || p[1] != int[][].class) continue;
                    if (m.getReturnType() != boolean.class && m.getReturnType() != Boolean.class) continue;
                    if (pass == 0 && !m.getName().toLowerCase().contains("bipartite")) continue;
                    m.setAccessible(true);
                    shapeA = m;
                    return;
                }
            }
        }

        // Shape B: checker method (X) -> R with R.isBipartite()
        Method best = null;
        int bestScore = -1;
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || p[0].isPrimitive() || p[0].isArray()
                        || p[0].getName().startsWith("java.")) continue;
                if (findNoArg(m.getReturnType(), "isBipartite", boolean.class) == null) continue;
                int score = 0;
                String n = m.getName().toLowerCase();
                if (n.equals("check")) score += 4;
                if (n.contains("bipartite") || n.contains("check") || n.contains("color")) score += 2;
                if (c.getSimpleName().toLowerCase().contains("bipartite")) score += 1;
                if (score > bestScore) { best = m; bestScore = score; }
            }
        }
        if (best == null) return;
        best.setAccessible(true);
        checkM = best;
        Class<?> graphParam = best.getParameterTypes()[0];

        // Graph factory: static (int, int[][]) returning something assignable to graphParam.
        Method bestF = null;
        int bestFScore = Integer.MIN_VALUE;
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                if (!Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 2 || p[0] != int.class || p[1] != int[][].class) continue;
                if (!graphParam.isAssignableFrom(m.getReturnType())) continue;
                String cn = m.getReturnType().getSimpleName().toLowerCase();
                int score = 0;
                if (cn.contains("undirected")) score += 4;
                else if (cn.contains("directed")) score -= 10;   // a directed graph is not the contract
                if (cn.contains("weighted")) score -= 2;
                if (score > bestFScore) { bestF = m; bestFScore = score; }
            }
        }
        if (bestF != null) { bestF.setAccessible(true); graphFactory = bestF; }

        if (!Modifier.isStatic(best.getModifiers())) {
            try {
                Constructor<?> ctor = best.getDeclaringClass().getDeclaredConstructor();
                ctor.setAccessible(true);
                checkerInstance = ctor.newInstance();
            } catch (Exception e) {
                checkerInstance = null;
            }
        }
    }

    static Method findNoArg(Class<?> type, String name, Class<?> ret) {
        for (Method m : allMethods(type)) {
            if (m.getParameterCount() == 0 && m.getName().equals(name)
                    && (m.getReturnType() == ret || (ret == boolean.class && m.getReturnType() == Boolean.class))) {
                m.setAccessible(true);
                return m;
            }
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
            if (c == null || c == Object.class || !seen.add(c)) continue;
            for (Method m : c.getDeclaredMethods())
                if (!m.isSynthetic() && !m.isBridge() && !Modifier.isStatic(m.getModifiers())) out.add(m);
            if (c.getSuperclass() != null) todo.add(c.getSuperclass());
            todo.addAll(Arrays.asList(c.getInterfaces()));
        }
        return out;
    }

    // ---------------- running ----------------

    static int runGroup(String group, List<TestCase> cases) {
        int failed = 0;
        for (TestCase tc : cases) {
            String err = runOne(tc);
            if (err != null) {
                failed++;
                System.out.println("CHECK FAIL [" + group + "] " + tc.name + " V=" + tc.v + " edges="
                        + tc.edgeString() + ": " + err);
            }
        }
        return failed;
    }

    /** @return null on success, else a failure description */
    static String runOne(TestCase tc) {
        boolean expected = referenceBipartite(tc.v, tc.edges);
        Object result;
        boolean actual;
        try {
            if (shapeA != null) {
                result = shapeA.invoke(null, tc.v, deepCopy(tc.edges));
                actual = (Boolean) result;
            } else {
                if (graphFactory == null) return "no (int, int[][]) undirected graph factory found";
                if (!Modifier.isStatic(checkM.getModifiers()) && checkerInstance == null)
                    return "could not instantiate checker " + checkM.getDeclaringClass().getName();
                Object graph = graphFactory.invoke(null, tc.v, deepCopy(tc.edges));
                result = checkM.invoke(checkerInstance, graph);
                if (result == null) return "check returned null";
                Method isB = findNoArg(result.getClass(), "isBipartite", boolean.class);
                if (isB == null) return "result " + result.getClass().getName() + " has no isBipartite()";
                actual = (Boolean) isB.invoke(result);
            }
        } catch (Throwable e) {
            Throwable cause = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
            return "exception " + cause;
        }
        if (actual != expected) return "expected isBipartite=" + expected + " actual=" + actual;
        if (shapeA != null) return null;

        // Witness checks (only when the result exposes them).
        try {
            if (actual) {
                List<Method> parts = new ArrayList<>();
                for (Method m : allMethods(result.getClass())) {
                    if (m.getParameterCount() == 0 && m.getName().toLowerCase().contains("partition")
                            && Collection.class.isAssignableFrom(m.getReturnType())) parts.add(m);
                }
                parts.sort(Comparator.comparing(Method::getName));
                // de-duplicate overridden methods with the same name
                LinkedHashMap<String, Method> byName = new LinkedHashMap<>();
                for (Method m : parts) byName.putIfAbsent(m.getName(), m);
                if (byName.size() == 2) {
                    Iterator<Method> it = byName.values().iterator();
                    Method ma = it.next(), mb = it.next();
                    ma.setAccessible(true);
                    mb.setAccessible(true);
                    Collection<?> a = (Collection<?>) ma.invoke(result);
                    Collection<?> b = (Collection<?>) mb.invoke(result);
                    partitionsChecked++;
                    String bad = validatePartition(tc, a, b);
                    if (bad != null) return "invalid partition A=" + a + " B=" + b + ": " + bad;
                }
            } else {
                Method cyc = null;
                for (Method m : allMethods(result.getClass())) {
                    if (m.getParameterCount() == 0 && m.getName().toLowerCase().contains("cycle")
                            && Collection.class.isAssignableFrom(m.getReturnType())) { cyc = m; break; }
                }
                if (cyc != null) {
                    cyc.setAccessible(true);
                    Collection<?> c = (Collection<?>) cyc.invoke(result);
                    cyclesChecked++;
                    String bad = validateOddCycle(tc, c);
                    if (bad != null) return "invalid odd-cycle witness " + c + ": " + bad;
                }
            }
        } catch (Throwable e) {
            Throwable cause = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
            return "exception reading witness: " + cause;
        }
        return null;
    }

    static String validatePartition(TestCase tc, Collection<?> a, Collection<?> b) {
        int[] side = new int[tc.v];
        Arrays.fill(side, -1);
        int count = 0;
        for (int s = 0; s < 2; s++) {
            for (Object o : (s == 0 ? a : b)) {
                int x = ((Number) o).intValue();
                if (x < 0 || x >= tc.v) return "vertex " + x + " out of range";
                if (side[x] != -1) return "vertex " + x + " appears twice";
                side[x] = s;
                count++;
            }
        }
        if (count != tc.v) return "partitions cover " + count + " of " + tc.v + " vertices";
        for (int[] e : tc.edges)
            if (side[e[0]] == side[e[1]]) return "edge " + e[0] + "-" + e[1] + " inside one side";
        return null;
    }

    static String validateOddCycle(TestCase tc, Collection<?> c) {
        List<Integer> cyc = new ArrayList<>();
        for (Object o : c) cyc.add(((Number) o).intValue());
        if (cyc.size() >= 2 && cyc.get(0).equals(cyc.get(cyc.size() - 1))) cyc.remove(cyc.size() - 1);
        if (cyc.isEmpty()) return "empty";
        if (cyc.size() % 2 == 0) return "even length " + cyc.size();
        Set<Long> edgeSet = new HashSet<>();
        for (int[] e : tc.edges) {
            edgeSet.add(key(e[0], e[1]));
            edgeSet.add(key(e[1], e[0]));
        }
        if (new HashSet<>(cyc).size() != cyc.size()) return "repeated vertex";
        for (int i = 0; i < cyc.size(); i++) {
            int u = cyc.get(i), w = cyc.get((i + 1) % cyc.size());
            if (u < 0 || u >= tc.v) return "vertex " + u + " out of range";
            if (!edgeSet.contains(key(u, w))) return "edge " + u + "-" + w + " not in graph";
        }
        return null;
    }

    static long key(int u, int v) { return ((long) u << 32) | (v & 0xffffffffL); }

    // ---------------- oracle ----------------

    /** Independent reference: union-find with parity (no BFS). */
    static boolean referenceBipartite(int v, int[][] edges) {
        int[] parent = new int[v];
        int[] parity = new int[v];   // parity to parent
        for (int i = 0; i < v; i++) parent[i] = i;
        for (int[] e : edges) {
            int a = e[0], b = e[1];
            int[] ra = find(parent, parity, a), rb = find(parent, parity, b);
            if (ra[0] == rb[0]) {
                if (ra[1] == rb[1]) return false;   // same colour, adjacent (covers self-loops)
            } else {
                parent[ra[0]] = rb[0];
                parity[ra[0]] = ra[1] ^ rb[1] ^ 1;
            }
        }
        return true;
    }

    /** @return {root, parity of x relative to root} */
    static int[] find(int[] parent, int[] parity, int x) {
        int p = 0, cur = x;
        List<Integer> path = new ArrayList<>();
        while (parent[cur] != cur) { path.add(cur); p ^= parity[cur]; cur = parent[cur]; }
        int root = cur;
        // path compression
        int acc = p;
        for (int node : path) {
            int old = parity[node];
            parent[node] = root;
            parity[node] = acc;
            acc ^= old;
        }
        return new int[]{root, p};
    }

    // ---------------- inputs ----------------

    record TestCase(String name, int v, int[][] edges) {
        String edgeString() {
            if (edges.length > 14) return edges.length + " edges";
            return Arrays.deepToString(edges);
        }
    }

    static int[][] deepCopy(int[][] e) {
        int[][] out = new int[e.length][];
        for (int i = 0; i < e.length; i++) out[i] = e[i].clone();
        return out;
    }

    /**
     * Random undirected graphs without self-loops. Modes rotate between:
     * bipartite-by-construction, bipartite + one planted odd cycle, random
     * density, and multi-component with isolated vertices.
     */
    static TestCase randomCase(Random rnd, int t) {
        int v = 1 + rnd.nextInt(30);
        List<int[]> edges = new ArrayList<>();
        String mode;
        switch (t % 4) {
            case 0 -> { // bipartite by construction
                mode = "random bipartite";
                int[] side = new int[v];
                for (int i = 0; i < v; i++) side[i] = rnd.nextInt(2);
                double d = 0.1 + rnd.nextDouble() * 0.4;
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (side[i] != side[j] && rnd.nextDouble() < d) edges.add(pair(rnd, i, j));
            }
            case 1 -> { // bipartite + planted odd cycle
                mode = "bipartite + odd cycle";
                if (v < 3) v = 3 + rnd.nextInt(10);
                int[] side = new int[v];
                for (int i = 0; i < v; i++) side[i] = rnd.nextInt(2);
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (side[i] != side[j] && rnd.nextDouble() < 0.2) edges.add(pair(rnd, i, j));
                int len = 3 + 2 * rnd.nextInt(Math.max(1, (v - 1) / 2));
                if (len > v) len = (v % 2 == 1) ? v : v - 1;
                List<Integer> perm = IntStream.range(0, v).boxed().collect(Collectors.toList());
                Collections.shuffle(perm, rnd);
                for (int i = 0; i < len; i++) edges.add(pair(rnd, perm.get(i), perm.get((i + 1) % len)));
            }
            case 2 -> { // random density
                mode = "random density";
                double d = new double[]{0.03, 0.08, 0.15, 0.4}[rnd.nextInt(4)];
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (rnd.nextDouble() < d) edges.add(pair(rnd, i, j));
            }
            default -> { // several components, some isolated vertices, maybe parallel edges
                mode = "multi-component";
                if (v < 6) v = 6 + rnd.nextInt(20);
                int comps = 2 + rnd.nextInt(3);
                int[] comp = new int[v];
                for (int i = 0; i < v; i++) comp[i] = rnd.nextInt(comps + 1); // comp==comps -> isolated
                for (int i = 0; i < v; i++)
                    for (int j = i + 1; j < v; j++)
                        if (comp[i] == comp[j] && comp[i] != comps && rnd.nextDouble() < 0.3)
                            edges.add(pair(rnd, i, j));
                if (!edges.isEmpty() && rnd.nextBoolean()) edges.add(edges.get(rnd.nextInt(edges.size())).clone());
            }
        }
        Collections.shuffle(edges, rnd);
        return new TestCase("random#" + t + " (" + mode + ")", v, edges.toArray(new int[0][]));
    }

    static int[] pair(Random rnd, int a, int b) {
        return rnd.nextBoolean() ? new int[]{a, b} : new int[]{b, a};
    }
}
