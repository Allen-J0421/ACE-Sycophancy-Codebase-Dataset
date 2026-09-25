import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 039_bucket_sort.
 *
 * Baseline (iteration 0): Main.bucketSort(float[]) sorts ascending, in place,
 * by scattering into n buckets indexed (int)(n * value) — which constrains the
 * input domain to floats in [0, 1) (a value of 1.0 would index out of bounds).
 * The oracle therefore stays inside [0, 1). Iterations 1-10 move the algorithm
 * to BucketSort.sort(float[]) (min/max-scaled buckets, any floats); the
 * float[] signature is the common denominator this driver exercises.
 *
 * Compilation note: the baseline declares `public class Main` inside
 * bucket_sort.java; the runner now renames such a file to match its public
 * type before compiling, so the driver simply uses <workdir>/_classes like
 * every other unit (a broken build is scored COMPILE_ERROR by the runner).
 *
 * Discovery: reflection over the compiled classes (test/demo classes skipped,
 * bucket-named classes preferred) for a method named *sort* taking (float[])
 * and returning void or float[].
 *
 * Oracle: java.util.Arrays.sort(float[]) on an independent copy.
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
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        List<String> ordered = new ArrayList<>(classNames);
        ordered.sort(Comparator
                .comparingInt((String n) -> simple(n).toLowerCase().contains("bucket") ? 0 : 1)
                .thenComparing(n -> n));

        Method entry = null;
        Object receiver = null;
        outer:
        for (String cn : ordered) {
            String sn = simple(cn).toLowerCase();
            if (sn.contains("test") || sn.contains("bench") || sn.contains("demo")
                    || sn.contains("driver") || sn.contains("placeholder")) continue;
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || p[0] != float[].class) continue;
                if (m.getReturnType() != void.class && m.getReturnType() != float[].class) continue;
                if (!m.getName().toLowerCase().contains("sort")) continue;
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
            System.out.println("RESULT FAIL no bucket-sort entry point found among " + classNames);
            return;
        }
        System.out.println("entry: " + entry);

        // ---- test cases: floats in [0, 1) — the baseline's domain ----
        List<float[]> cases = new ArrayList<>(List.of(
                new float[]{0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f}, // baseline demo
                new float[]{},
                new float[]{0.5f},
                new float[]{0.0f},
                new float[]{0.25f, 0.25f, 0.25f},
                new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f},
                new float[]{0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f},
                new float[]{0.9999f, 0.0001f, 0.5f, 0.5f, 0.0f, 0.99f},
                new float[]{0.42f, 0.42f, 0.421f, 0.419f, 0.42f}));
        Random rnd = new Random(20260925);
        for (int t = 0; t < 36; t++) {
            int n = rnd.nextInt(201); // 0..200 inclusive
            float[] a = new float[n];
            for (int i = 0; i < n; i++) {
                // narrow-range every third case forces crowded buckets/duplicates
                a[i] = (t % 3 == 0) ? 0.4f + rnd.nextInt(50) / 1000.0f : rnd.nextFloat();
            }
            cases.add(a);
        }
        // larger arrays, clustered values, extremes of [0, 1)
        for (int t = 0; t < 8; t++) {
            int n = 500 + rnd.nextInt(3000);
            float[] a = new float[n];
            for (int i = 0; i < n; i++) {
                a[i] = switch (t % 4) {
                    case 0 -> rnd.nextFloat();
                    case 1 -> rnd.nextInt(4) / 4.0f;                        // 4 distinct values
                    case 2 -> 0.999f + rnd.nextFloat() * 0.0009f;           // all near 1
                    default -> (float) Math.pow(rnd.nextFloat(), 8);       // clustered near 0
                };
            }
            cases.add(a);
        }
        cases.add(new float[]{0.99999994f, 0.0f, Float.MIN_VALUE, 0.5f, 0.99999994f, 1e-30f});

        int checks = 0, failed = 0;
        for (float[] a : cases) {
            checks++;
            float[] expect = a.clone();
            Arrays.sort(expect);
            float[] in = a.clone();
            Object out;
            try {
                out = entry.invoke(receiver, in);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL exception on " + preview(a) + ": " + cause(e));
                continue;
            }
            float[] got = (out instanceof float[] r) ? r : in;
            if (!Arrays.equals(got, expect)) {
                failed++;
                System.out.println("CHECK FAIL input=" + preview(a)
                        + " expected=" + preview(expect) + " got=" + preview(got));
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    static String simple(String cn) {
        int i = cn.lastIndexOf('.');
        return i < 0 ? cn : cn.substring(i + 1);
    }

    static String preview(float[] a) {
        if (a.length <= 12) return Arrays.toString(a);
        return Arrays.toString(Arrays.copyOf(a, 12)).replace("]", ", ...len=" + a.length + "]");
    }

    static String cause(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null) t = t.getCause();
        return String.valueOf(t);
    }
}
