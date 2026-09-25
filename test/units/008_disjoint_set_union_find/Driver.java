import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 008_disjoint_set_union_find.
 *
 * Baseline semantics: UnionFind(int size) creates singleton sets 0..size-1;
 * find(i) returns the representative of i's set; union(i, j) merges the two
 * sets. Representative identity is implementation-defined, so the oracle only
 * checks CONNECTIVITY: find(a) == find(b) must match a reference DSU after
 * every scripted union, plus find(x) must always be a member of x's set
 * (i.e. find is consistent: same component -> same root, different -> different).
 *
 * Entry point discovery: any class with a method find(int)->int and a method
 * union(int,int), constructible from a single int.
 *
 * The subject sources are compiled here rather than by the runner (see
 * unit.conf): the iteration-0 baseline keeps `public class UnionFind` in
 * disjoint_set_union_find.java, which plain javac rejects on filename
 * grounds, so each file is staged under the name its public type requires.
 * The compiled code is byte-identical to the agent's; a genuinely broken
 * build still fails here with the javac error.
 */
public class Driver {

    /** Compiles the subject's .java sources into classesDir with filename staging. */
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
        Path stage = Files.createTempDirectory("dsu_stage");
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
        Path classes = work.resolve("_classes");
        if (!compileSubjectSources(work, classes)) return;
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Class<?> dsu = null;
        Method find = null, union = null;
        Constructor<?> ctor = null;
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            if (c.getSimpleName().toLowerCase().contains("test")) continue;
            Method f = null, u = null;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                String n = m.getName().toLowerCase();
                if (p.length == 1 && p[0] == int.class
                        && (m.getReturnType() == int.class || m.getReturnType() == Integer.class)
                        && (n.equals("find") || n.contains("find") || n.contains("root")
                            || n.contains("representative"))) {
                    if (f == null || m.getName().equalsIgnoreCase("find")) f = m;
                }
                if (p.length == 2 && p[0] == int.class && p[1] == int.class
                        && (n.equals("union") || n.contains("union") || n.contains("merge"))) {
                    if (u == null || m.getName().equalsIgnoreCase("union")) u = m;
                }
            }
            if (f == null || u == null) continue;
            Constructor<?> k = null;
            for (Constructor<?> cc : c.getDeclaredConstructors()) {
                if (cc.getParameterCount() == 1 && cc.getParameterTypes()[0] == int.class) { k = cc; break; }
            }
            if (k == null) continue;
            dsu = c; find = f; union = u; ctor = k;
            break;
        }
        if (dsu == null) {
            System.out.println("RESULT FAIL no union-find entry point (class with find(int) + union(int,int) + (int) ctor) among " + classNames);
            return;
        }
        find.setAccessible(true); union.setAccessible(true); ctor.setAccessible(true);
        System.out.println("class: " + dsu.getName() + " find: " + find + " union: " + union);

        int checks = 0, failed = 0;
        Random rnd = new Random(20260925L);
        int[] sizes = {1, 2, 5, 10, 30, 50, 80};
        for (int trial = 0; trial < sizes.length; trial++) {
            int n = sizes[trial];
            Object subject = ctor.newInstance(n);
            RefDSU ref = new RefDSU(n);
            int unions = Math.max(1, n * 2);
            for (int step = 0; step < unions; step++) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                try {
                    union.invoke(subject, a, b);
                } catch (Throwable e) {
                    failed++; checks++;
                    System.out.println("CHECK FAIL n=" + n + " union(" + a + "," + b + ") threw " + cause(e));
                    continue;
                }
                ref.union(a, b);
                // verify a sample of connectivity queries after every union
                int probes = Math.min(n * n, 40);
                for (int q = 0; q < probes; q++) {
                    int x = rnd.nextInt(n), y = rnd.nextInt(n);
                    checks++;
                    long fx, fy;
                    try {
                        fx = ((Number) find.invoke(subject, x)).longValue();
                        fy = ((Number) find.invoke(subject, y)).longValue();
                    } catch (Throwable e) {
                        failed++;
                        System.out.println("CHECK FAIL n=" + n + " find threw " + cause(e));
                        continue;
                    }
                    boolean subjConn = fx == fy;
                    boolean refConn = ref.connected(x, y);
                    if (subjConn != refConn) {
                        failed++;
                        if (failed <= 10)
                            System.out.println("CHECK FAIL n=" + n + " after union(" + a + "," + b + "): connected(" + x + "," + y + ") subject=" + subjConn + " expected=" + refConn);
                    }
                    // find must return an element of the same component
                    checks++;
                    if (fx < 0 || fx >= n || !ref.connected(x, (int) fx)) {
                        failed++;
                        if (failed <= 10)
                            System.out.println("CHECK FAIL n=" + n + " find(" + x + ")=" + fx + " is not in x's component");
                    }
                }
            }
            // exhaustive final connectivity check for this trial
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    checks++;
                    long fx = ((Number) find.invoke(subject, x)).longValue();
                    long fy = ((Number) find.invoke(subject, y)).longValue();
                    if ((fx == fy) != ref.connected(x, y)) {
                        failed++;
                        if (failed <= 10)
                            System.out.println("CHECK FAIL n=" + n + " final connected(" + x + "," + y + ") subject=" + (fx == fy) + " expected=" + ref.connected(x, y));
                    }
                }
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " connectivity checks failed");
    }

    static String cause(Throwable e) {
        Throwable c = e.getCause() != null ? e.getCause() : e;
        return c.toString();
    }

    /** Independent reference DSU. */
    static final class RefDSU {
        final int[] p;
        RefDSU(int n) { p = new int[n]; for (int i = 0; i < n; i++) p[i] = i; }
        int find(int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }
        void union(int a, int b) { p[find(a)] = find(b); }
        boolean connected(int a, int b) { return find(a) == find(b); }
    }
}
