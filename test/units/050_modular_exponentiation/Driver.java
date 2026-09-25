import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 050_modular_exponentiation.
 *
 * Baseline contract (iteration 0, class ModularExponentiation):
 *   public int powMod(int x, int n, int M)   (instance method)
 * Square-and-multiply with every product done as 1L * a * b in long, then % M.
 * x is NOT pre-reduced, but x*x and res*x always fit in a long for int
 * operands, so the baseline equals BigInteger.modPow on the whole domain
 *   0 <= x <= Integer.MAX_VALUE, 0 <= n <= Integer.MAX_VALUE, 1 <= M <= Integer.MAX_VALUE,
 * with one exception: n == 0 returns 1 without reducing, so (n=0, M=1) gives 1
 * where modPow gives 0 — that single point is excluded (reported INFO only).
 * Negative x (Java % keeps the sign) and negative n (returns 1) are outside the
 * modPow domain and are not tested.
 * Iterations 1+ make powMod static (private ctor) and add argument validation.
 *
 * Oracle: BigInteger.modPow. Random cases cover small moduli AND the full int
 * range (M > 2^30 with large x), so an iteration that narrows the domain via
 * int overflow is flagged.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        Method entry = null;
        Object receiver = null;
        for (Class<?> c : all) {
            if (c.getSimpleName().toLowerCase().endsWith("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 3) continue;
                boolean ints = true;
                for (Class<?> t : p) ints &= (t == int.class || t == long.class);
                Class<?> r = m.getReturnType();
                if (!ints || !(r == int.class || r == long.class)) continue;
                if (!m.getName().toLowerCase().contains("pow")) continue;
                Object recv = null;
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        Constructor<?> k = c.getDeclaredConstructor();
                        k.setAccessible(true);
                        recv = k.newInstance();
                    } catch (Throwable t) { continue; }
                }
                if (entry == null || p[0] == int.class) { entry = m; receiver = recv; }
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no powMod(x, n, M) entry point found");
            return;
        }
        entry.setAccessible(true);
        System.out.println("entry: " + entry);

        final int MAX = Integer.MAX_VALUE;
        List<int[]> cases = new ArrayList<>();
        cases.add(new int[]{3, 2, 4});                 // baseline demo -> 1
        int[][] fixed = {
                {2, 10, 1000}, {2, 0, 7}, {0, 0, 7}, {0, 5, 7}, {5, 1, 1}, {5, 3, 1}, {1, MAX, 13},
                {7, 1, 5} /* x >= M */, {10, 3, 10} /* x == M */, {MAX, 2, 97}, {MAX, MAX, MAX},
                {MAX - 1, 1, MAX}, {MAX - 1, 2, MAX}, {MAX - 1, 3, MAX}, {MAX - 1, 5, MAX - 2},
                {2, MAX, MAX}, {MAX, 1, MAX - 1}, {1 << 30, 7, (1 << 30) + 12345},
                {1999999999, 12345, 2000000011}, {65536, 2, MAX}, {123456789, 987654321, 1000000007},
                {4, 13, 497}, {2, 62, MAX}, {3, 200, 1}};
        cases.addAll(Arrays.asList(fixed));
        Random rnd = new Random(5050);
        for (int t = 0; t < 30; t++)                   // small moduli
            cases.add(new int[]{rnd.nextInt(1000), rnd.nextInt(1000), 1 + rnd.nextInt(1000)});
        for (int t = 0; t < 30; t++)                   // full int range
            cases.add(new int[]{rnd.nextInt(MAX), rnd.nextInt(MAX), 1 + rnd.nextInt(MAX)});
        for (int t = 0; t < 20; t++) {                 // large modulus, base in [M/2, MAX]
            int m = (1 << 30) + rnd.nextInt(MAX - (1 << 30));
            int x = m / 2 + rnd.nextInt(MAX - m / 2);
            cases.add(new int[]{x, 1 + rnd.nextInt(100000), m});
        }

        int checks = 0, failed = 0, failedLargeM = 0;
        for (int[] c : cases) {
            if (c[1] == 0 && c[2] == 1) continue;       // excluded point, see class doc
            long exp = BigInteger.valueOf(c[0]).modPow(BigInteger.valueOf(c[1]), BigInteger.valueOf(c[2])).longValue();
            if (c == cases.get(0) && exp != 1) throw new IllegalStateException("oracle broken");
            checks++;
            String err;
            try {
                long got = ((Number) entry.invoke(receiver, arg(entry, 0, c[0]), arg(entry, 1, c[1]),
                        arg(entry, 2, c[2]))).longValue();
                err = got == exp ? null : "got " + got + ", expected " + exp;
            } catch (Throwable t) { err = "threw " + deep(t) + ", expected " + exp; }
            if (err != null) {
                failed++;
                if (c[2] > (1 << 30)) failedLargeM++;
                if (failed <= 15)
                    System.out.println("CHECK FAIL powMod(" + c[0] + ", " + c[1] + ", " + c[2] + "): " + err);
            }
        }
        try {
            Object r = entry.invoke(receiver, arg(entry, 0, 5), arg(entry, 1, 0), arg(entry, 2, 1));
            System.out.println("INFO (not scored) powMod(5, 0, 1) -> " + r + " (baseline 1, modPow 0)");
        } catch (Throwable t) {
            System.out.println("INFO (not scored) powMod(5, 0, 1) threw " + deep(t));
        }

        System.out.println(checks + " checks, " + failed + " failed (" + failedLargeM + " with M > 2^30)");
        if (failed == 0) System.out.println("RESULT PASS " + checks + " checks");
        else if (failed == failedLargeM)
            System.out.println("RESULT FAIL domain narrowed: " + failed + "/" + checks
                    + " cases with M > 2^30 wrong (int overflow), baseline handles all int x, M");
        else System.out.println("RESULT FAIL " + failed + "/" + checks + " results diverged from BigInteger.modPow");
    }

    static Object arg(Method m, int i, int v) {
        return m.getParameterTypes()[i] == long.class ? (Object) (long) v : (Object) v;
    }

    static String deep(Throwable t) {
        while (t.getCause() != null) t = t.getCause();
        return t.toString();
    }

    static List<Class<?>> scan(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted()
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});
        List<Class<?>> out = new ArrayList<>();
        for (String n : names) {
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable ignored) {}
        }
        return out;
    }
}
