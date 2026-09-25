import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 003_prefix_sum_array.
 *
 * Baseline semantics (iteration 0, PrefixSum.prefSum(int[]) -> ArrayList<Integer>):
 * returns a sequence of length n where element i is the sum of arr[0..i]
 * (inclusive running total, no leading 0). The baseline crashes on empty input
 * (unconditional arr[0]), so empty arrays are OUTSIDE the tested domain: the
 * empty case is probed only as a non-scored diagnostic.
 *
 * Compilation note: the baseline declares `public class PrefixSum` inside
 * prefix_sum_array.java, which javac rejects on filename grounds even though
 * the code is semantically valid. unit.conf therefore excludes the subject
 * file from the runner's compile step, and this driver compiles the subject
 * sources itself, first copying each file to a name matching its public
 * top-level class (a no-op for iterations whose classes are package-private).
 * A genuine agent-broken build therefore surfaces here as
 * `RESULT FAIL subject sources failed to compile`.
 *
 * The entry point is found by reflection over every compiled class: a method
 * taking exactly one int[] parameter, returning an int[]/long[]/Integer[]/Long[]
 * or a List, whose name suggests prefix/cumulative sums. Result containers are
 * normalized to long[] before comparison, so the iteration-1+ signature change
 * (ArrayList<Integer> -> long[]) is tolerated; all test values stay well within
 * int range so widening cannot mask a value regression.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = compileSubject(work);
        if (classes == null) return; // RESULT line already printed
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
        outer:
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || p[0] != int[].class) continue;
                Class<?> r = m.getReturnType();
                boolean retOk = r == int[].class || r == long[].class
                        || r == Integer[].class || r == Long[].class
                        || List.class.isAssignableFrom(r) || Collection.class.isAssignableFrom(r);
                if (!retOk) continue;
                String n = m.getName().toLowerCase();
                if (n.equals("main")) continue;
                if (!(n.contains("pref") || n.contains("sum") || n.contains("cumul")
                        || n.contains("running") || n.contains("accum") || n.contains("scan"))) continue;
                m.setAccessible(true);
                Object rec = null;
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        var ctor = c.getDeclaredConstructor();
                        ctor.setAccessible(true);
                        rec = ctor.newInstance();
                    } catch (Throwable t) { continue; }
                }
                entry = m;
                receiver = rec;
                break outer;
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no prefix-sum entry point found among " + classNames);
            return;
        }
        System.out.println("entry: " + entry);

        // ---- fixed + randomized test cases (all non-empty; sums stay in int range) ----
        List<int[]> cases = new ArrayList<>(List.of(
                new int[]{10, 20, 10, 5, 15},          // baseline demo -> 10 30 40 45 60
                new int[]{7},
                new int[]{-7},
                new int[]{0},
                new int[]{0, 0, 0, 0},
                new int[]{-1, -2, -3, -4, -5},
                new int[]{5, -5, 5, -5, 5},
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                new int[]{Integer.MAX_VALUE / 2, -Integer.MAX_VALUE / 2, 42},
                new int[]{-100, 100, -100, 100}));
        Random rnd = new Random(20260925);
        for (int t = 0; t < 32; t++) {
            int n = 1 + rnd.nextInt(80);
            int[] a = rnd.ints(n, -1000, 1001).toArray();
            cases.add(a);
        }

        int checks = 0, failed = 0;
        for (int[] a : cases) {
            checks++;
            long[] expect = new long[a.length];
            long run = 0;
            for (int i = 0; i < a.length; i++) { run += a[i]; expect[i] = run; }

            Object out;
            try {
                out = entry.invoke(receiver, (Object) a.clone());
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL exception on " + Arrays.toString(a) + ": " + cause(e));
                continue;
            }
            long[] got;
            try {
                got = toLongs(out);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL unusable return value " + out + " for " + Arrays.toString(a));
                continue;
            }
            if (!Arrays.equals(got, expect)) {
                failed++;
                System.out.println("CHECK FAIL arr=" + Arrays.toString(a)
                        + " expected=" + Arrays.toString(expect)
                        + " got=" + Arrays.toString(got));
            }
        }

        // Non-scored probe: empty input is out of the baseline's domain (iter 0 throws).
        try {
            Object out = entry.invoke(receiver, (Object) new int[0]);
            System.out.println("empty-input probe (informational): returned " + Arrays.toString(toLongs(out)));
        } catch (Throwable e) {
            System.out.println("empty-input probe (informational): threw " + cause(e));
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    /**
     * Compiles every subject .java under the workdir (skipping the runner's
     * _classes output, this driver's own scratch dirs, and the inert
     * UnitCompilePlaceholder stub) into <workdir>/_dclasses. Each source is
     * first copied to a file named after its first public top-level type so
     * the baseline's public-class/filename mismatch does not abort javac.
     * Returns the output dir, or null (after printing a RESULT line) if the
     * subject genuinely does not compile or has no sources.
     */
    static Path compileSubject(Path work) throws Exception {
        Path srcDir = work.resolve("_dsrc");
        Path outDir = work.resolve("_dclasses");
        for (Path d : new Path[]{srcDir, outDir}) {
            if (Files.exists(d)) {
                try (Stream<Path> s = Files.walk(d)) {
                    s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
                }
            }
            Files.createDirectories(d);
        }
        List<String> toCompile = new ArrayList<>();
        try (Stream<Path> s = Files.walk(work)) {
            for (Path p : s.filter(f -> f.toString().endsWith(".java")).collect(Collectors.toList())) {
                String rel = work.relativize(p).toString();
                if (rel.startsWith("_classes") || rel.startsWith("_dsrc") || rel.startsWith("_dclasses")) continue;
                if (rel.endsWith("UnitCompilePlaceholder.java")) continue;
                String content = Files.readString(p);
                // Strip comments before hunting for the public top-level type name.
                String bare = content.replaceAll("(?s)/\\*.*?\\*/", " ").replaceAll("//[^\n]*", " ");
                var m = java.util.regex.Pattern.compile(
                        "\\bpublic\\s+(?:final\\s+|abstract\\s+|strictfp\\s+)*(?:class|interface|enum|record)\\s+(\\w+)")
                        .matcher(bare);
                String base = m.find() ? m.group(1) + ".java" : p.getFileName().toString();
                Path dest = srcDir.resolve(base);
                for (int k = 2; Files.exists(dest); k++)  // avoid clobbering on duplicate names
                    dest = srcDir.resolve(base.replace(".java", "_" + k + ".java"));
                Files.writeString(dest, content);
                toCompile.add(dest.toString());
            }
        }
        if (toCompile.isEmpty()) {
            System.out.println("RESULT FAIL no subject .java sources found in " + work);
            return null;
        }
        var compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
        var errBuf = new java.io.ByteArrayOutputStream();
        List<String> jargs = new ArrayList<>(List.of("-nowarn", "-encoding", "utf-8",
                "-d", outDir.toString(), "-cp", outDir.toString()));
        jargs.addAll(toCompile);
        int rc = compiler.run(null, null, errBuf, jargs.toArray(new String[0]));
        if (rc != 0) {
            String err = errBuf.toString().replaceAll("\\s+", " ");
            System.out.println("javac output: " + err);
            System.out.println("RESULT FAIL subject sources failed to compile (agent broke the build): "
                    + err.substring(0, Math.min(400, err.length())));
            return null;
        }
        return outDir;
    }

    static long[] toLongs(Object o) {
        if (o instanceof int[] a) return Arrays.stream(a).asLongStream().toArray();
        if (o instanceof long[] a) return a;
        if (o instanceof Object[] a)
            return Arrays.stream(a).mapToLong(x -> ((Number) x).longValue()).toArray();
        if (o instanceof Collection<?> c)
            return c.stream().mapToLong(x -> ((Number) x).longValue()).toArray();
        throw new IllegalArgumentException("unrecognized result type: " + o);
    }

    static String cause(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null) t = t.getCause();
        return String.valueOf(t);
    }
}
