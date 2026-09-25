import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 024_floyd_warshall.
 *
 * Baseline semantics (floyd_warshall.java): static void floydWarshall(int[][] dist)
 * computes all-pairs shortest paths IN PLACE on a square DIRECTED distance
 * matrix; dist[i][j] is the edge weight i->j, "no edge"/unreachable is the
 * sentinel INF = (int) 1e8 (exactly), the diagonal is 0. Negative edges are
 * allowed; the baseline defines no negative-cycle handling (it silently leaves
 * garbage), so negative cycles are outside the tested domain.
 *
 * API shapes across iterations:
 *  A) iter 0: static void X(int[][]) mutating its argument.
 *  B) iter 1-4: static Graph shortestPaths(Graph), Graph.of(int[][]) factory,
 *     result read via toMatrix() / weight(i,j).
 *  C) iter 5-10: static ShortestPaths shortestPaths(Graph); result exposes
 *     distance(i,j), distances() -> Graph, and path(i,j) -> List<Integer>.
 * Discovery is by reflection over every compiled class.
 *
 * Oracle: independent long-based Floyd-Warshall, unreachable mapped to 1e8.
 * Random graphs get negative edges without negative cycles via potential
 * reweighting (w' = w + p(u) - p(v), w >= 0). When the result exposes
 * path(i,j) (a feature added at iter 5), each reconstructed path is also
 * property-checked (endpoints, every hop is an input edge, weight = distance).
 */
public class Driver {
    static final int INF = (int) 1e8;

    static Method inPlace;          // shape A
    static Method algo;             // shape B/C
    static Method factory;          // Graph.of(int[][])

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
        List<Class<?>> loaded = new ArrayList<>();
        for (String cn : classNames) {
            try { loaded.add(Class.forName(cn, false, cl)); } catch (Throwable t) { }
        }

        for (int pass = 0; pass < 2 && inPlace == null && algo == null; pass++) {
            for (Class<?> c : loaded) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                    if (c.getName().toLowerCase().contains("test")) continue;
                    String n = m.getName().toLowerCase();
                    boolean named = n.contains("floyd") || n.contains("short") || n.contains("allpairs")
                            || n.contains("solve") || n.contains("compute");
                    if (pass == 0 && !named) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length != 1) continue;
                    if (p[0] == int[][].class && m.getReturnType() == void.class && inPlace == null) {
                        inPlace = m;
                    } else if (!p[0].isPrimitive() && !p[0].isArray() && !p[0].getName().startsWith("java.")
                            && m.getReturnType() != void.class && algo == null) {
                        Method f = findFactory(p[0]);
                        if (f != null) { algo = m; factory = f; }
                    }
                }
            }
        }
        if (inPlace == null && algo == null) {
            System.out.println("RESULT FAIL no Floyd-Warshall entry point found among " + classNames);
            return;
        }
        if (inPlace != null) { inPlace.setAccessible(true); System.out.println("entry (in-place): " + inPlace); }
        else { algo.setAccessible(true); factory.setAccessible(true);
               System.out.println("entry: " + algo + " via " + factory); }

        List<int[][]> cases = new ArrayList<>();
        // Baseline demo input.
        cases.add(new int[][]{{0, 4, INF, 5, INF},
                              {INF, 0, 1, INF, 6},
                              {2, INF, 0, 3, INF},
                              {INF, INF, 1, 0, 2},
                              {1, INF, INF, 4, 0}});
        // Single vertex.
        cases.add(new int[][]{{0}});
        // Two vertices, no edges.
        cases.add(new int[][]{{0, INF}, {INF, 0}});
        // One-way edge (directedness).
        cases.add(new int[][]{{0, 7}, {INF, 0}});
        // Negative edge, no negative cycle; indirect path beats direct.
        cases.add(new int[][]{{0, 5, INF}, {INF, 0, -3}, {1, INF, 0}});
        // Zero-weight cycle.
        cases.add(new int[][]{{0, 0, INF, INF}, {INF, 0, 0, INF}, {0, INF, 0, 9}, {INF, INF, INF, 0}});
        // Disconnected pieces.
        cases.add(new int[][]{{0, 2, INF, INF}, {3, 0, INF, INF}, {INF, INF, 0, -1}, {INF, INF, 4, 0}});

        Random rnd = new Random(24024);
        for (int t = 0; t < 40; t++) {
            int v = 1 + rnd.nextInt(t < 20 ? 12 : 40);
            double density = switch (t % 4) { case 0 -> 0.1; case 1 -> 0.35; case 2 -> 0.7; default -> 1.0; };
            boolean negative = t % 3 != 0;
            int[] pot = new int[v];
            for (int i = 0; i < v; i++) pot[i] = negative ? rnd.nextInt(201) : 0;
            int[][] m = new int[v][v];
            for (int i = 0; i < v; i++)
                for (int j = 0; j < v; j++) {
                    if (i == j) { m[i][j] = 0; continue; }
                    if (rnd.nextDouble() < density) {
                        int w = rnd.nextInt(t % 5 == 0 ? 5 : 1000);
                        m[i][j] = w + pot[i] - pot[j];
                    } else m[i][j] = INF;
                }
            cases.add(m);
        }

        int failed = 0, checks = 0, pathChecks = 0;
        for (int[][] input : cases) {
            checks++;
            int[][] expected = reference(input);
            String err;
            try {
                err = runCase(input, expected);
                if (err == null) pathChecks += lastPathChecks;
            } catch (Throwable e) {
                Throwable c = e.getCause() != null ? e.getCause() : e;
                err = "exception " + c;
            }
            if (err != null) {
                failed++;
                System.out.println("CHECK FAIL V=" + input.length + " " + describe(input) + ": " + err);
            }
        }
        System.out.println(checks + " graphs checked (" + pathChecks + " path checks), " + failed + " failed");
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " graphs checked, " + pathChecks + " paths verified"
                : "RESULT FAIL " + failed + "/" + checks + " graphs gave wrong results");
    }

    static int lastPathChecks;

    static String runCase(int[][] input, int[][] expected) throws Exception {
        lastPathChecks = 0;
        int n = input.length;
        int[][] copy = deepCopy(input);
        int[][] actual;
        Object result = null;
        if (inPlace != null) {
            inPlace.invoke(null, (Object) copy);
            actual = copy;
        } else {
            Object g = factory.invoke(null, (Object) copy);
            result = algo.invoke(null, g);
            actual = extractMatrix(result, n);
        }
        if (actual == null) return "could not read result matrix from " + result.getClass();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (actual[i][j] != expected[i][j])
                    return "dist[" + i + "][" + j + "] expected " + expected[i][j] + " got " + actual[i][j]
                            + (n <= 6 ? "\n  expected " + Arrays.deepToString(expected)
                                      + "\n  actual   " + Arrays.deepToString(actual) : "");
        if (result != null) {
            Method path = find(result.getClass(), "path", int.class, int.class);
            if (path != null && List.class.isAssignableFrom(path.getReturnType())) {
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++) {
                        List<?> p = (List<?>) path.invoke(result, i, j);
                        String e = checkPath(input, expected, i, j, p);
                        if (e != null) return e;
                        lastPathChecks++;
                    }
            }
        }
        return null;
    }

    static String checkPath(int[][] in, int[][] exp, int i, int j, List<?> p) {
        if (exp[i][j] == INF) {
            return p.isEmpty() ? null : "path(" + i + "," + j + ") should be empty (unreachable), got " + p;
        }
        if (p.isEmpty()) return "path(" + i + "," + j + ") empty but distance " + exp[i][j];
        if (((Number) p.get(0)).intValue() != i || ((Number) p.get(p.size() - 1)).intValue() != j)
            return "path(" + i + "," + j + ") wrong endpoints: " + p;
        if (i == j && p.size() != 1) return "path(" + i + "," + i + ") should be [" + i + "], got " + p;
        long sum = 0;
        for (int k = 0; k + 1 < p.size(); k++) {
            int a = ((Number) p.get(k)).intValue(), b = ((Number) p.get(k + 1)).intValue();
            if (a == b || in[a][b] == INF) return "path(" + i + "," + j + ") uses non-edge " + a + "->" + b + ": " + p;
            sum += in[a][b];
        }
        if (sum != exp[i][j]) return "path(" + i + "," + j + ") weight " + sum + " != distance " + exp[i][j] + ": " + p;
        return null;
    }

    /** Reads an n x n distance matrix from a Graph/ShortestPaths-like result object. */
    static int[][] extractMatrix(Object r, int n) throws Exception {
        if (r instanceof int[][] a) return a;
        Class<?> c = r.getClass();
        Method dist2 = find(c, "distance", int.class, int.class);
        if (dist2 == null) dist2 = find(c, "dist", int.class, int.class);
        if (dist2 == null) dist2 = find(c, "weight", int.class, int.class);
        if (dist2 != null && dist2.getReturnType() == int.class) {
            int[][] out = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    out[i][j] = (int) dist2.invoke(r, i, j);
            // Also cross-check the matrix view(s), if exposed.
            Method toM = find(c, "toMatrix");
            if (toM != null && toM.getReturnType() == int[][].class
                    && !Arrays.deepEquals(out, (int[][]) toM.invoke(r)))
                throw new IllegalStateException("toMatrix() disagrees with " + dist2.getName() + "(i,j)");
            Method dv = find(c, "distances");
            if (dv != null && !dv.getReturnType().isPrimitive()) {
                int[][] other = extractMatrix(dv.invoke(r), n);
                if (!Arrays.deepEquals(out, other))
                    throw new IllegalStateException("distances() view disagrees with distance(i,j)");
            }
            return out;
        }
        Method toM = find(c, "toMatrix");
        if (toM != null && toM.getReturnType() == int[][].class) return (int[][]) toM.invoke(r);
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

    static Method findFactory(Class<?> t) {
        for (Method m : t.getDeclaredMethods()) {
            if (m.isSynthetic() || !Modifier.isStatic(m.getModifiers())) continue;
            Class<?>[] p = m.getParameterTypes();
            if (p.length == 1 && p[0] == int[][].class && t.isAssignableFrom(m.getReturnType())) return m;
        }
        return null;
    }

    /** Independent Floyd-Warshall in long arithmetic; unreachable -> INF (1e8). */
    static int[][] reference(int[][] in) {
        int n = in.length;
        long U = Long.MAX_VALUE / 4;
        long[][] d = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                d[i][j] = in[i][j] == INF ? U : in[i][j];
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++) {
                if (d[i][k] == U) continue;
                for (int j = 0; j < n; j++)
                    if (d[k][j] != U && d[i][k] + d[k][j] < d[i][j]) d[i][j] = d[i][k] + d[k][j];
            }
        int[][] out = new int[n][n];
        for (int i = 0; i < n; i++) {
            if (d[i][i] < 0) throw new IllegalStateException("test generator produced a negative cycle");
            for (int j = 0; j < n; j++) out[i][j] = d[i][j] == U ? INF : (int) d[i][j];
        }
        return out;
    }

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        return c;
    }

    static String describe(int[][] m) {
        return m.length <= 5 ? Arrays.deepToString(m) : "(" + m.length + "x" + m.length + ")";
    }
}
