import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 049_euclidean_algorithms.
 *
 * Baseline contract (iteration 0, class EuclideanAlgorithm):
 *   static int findGCD(int a, int b) { if (a == 0) return b; return findGCD(b % a, a); }
 * Defined for every int pair (no exceptions). For non-negative inputs it is the
 * gcd (gcd(0,0) = 0, gcd(0,b) = b). For negative inputs its magnitude is
 * |gcd| but its SIGN follows Java's truncating % — e.g. findGCD(4, -6) = -2,
 * findGCD(0, -5) = -5, findGCD(-4, 6) = 2. The task requires matching this.
 * No extended gcd exists at any iteration (iterations 1+ add
 * gcd(long,long), gcdRecursive(long,long) and lcm; every gcd-named method is scored).
 *
 * Oracle: BigInteger.gcd for non-negative inputs (incl. zeros); for negative
 * inputs the exact baseline value, computed by the independent iterative
 * equivalent of the baseline recurrence, with |value| also cross-checked
 * against BigInteger.gcd.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        List<Method> entries = new ArrayList<>();
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge()) continue;
                if (!Modifier.isStatic(m.getModifiers())) continue;
                if (!m.getName().toLowerCase().contains("gcd")) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 2 || !integral(p[0]) || !integral(p[1])) continue;
                if (!integral(m.getReturnType())) continue;
                m.setAccessible(true);
                entries.add(m);
            }
        }
        if (entries.isEmpty()) {
            System.out.println("RESULT FAIL no static gcd(int|long, int|long) entry point found");
            return;
        }
        for (Method m : entries) System.out.println("entry: " + m);

        List<int[]> nonNeg = new ArrayList<>();
        nonNeg.add(new int[]{35, 15});                 // baseline demo -> 5
        int[][] fixed = {{0, 0}, {0, 7}, {7, 0}, {1, 1}, {1, 0}, {0, 1}, {12, 18}, {18, 12},
                {17, 13}, {1071, 462}, {Integer.MAX_VALUE, 1}, {Integer.MAX_VALUE, Integer.MAX_VALUE},
                {Integer.MAX_VALUE, 0}, {0, Integer.MAX_VALUE}, {1 << 30, 1 << 20},
                {1836311903, 1134903170} /* consecutive Fibonacci: worst case */};
        nonNeg.addAll(Arrays.asList(fixed));
        Random rnd = new Random(4949);
        for (int t = 0; t < 40; t++) {
            int g = 1 + rnd.nextInt(t < 20 ? 50 : 5000);
            int lim = Integer.MAX_VALUE / g;
            nonNeg.add(new int[]{g * rnd.nextInt(Math.min(lim, 100000)), g * rnd.nextInt(Math.min(lim, 100000))});
        }
        for (int t = 0; t < 20; t++) nonNeg.add(new int[]{rnd.nextInt(Integer.MAX_VALUE), rnd.nextInt(Integer.MAX_VALUE)});

        List<int[]> neg = new ArrayList<>();
        int[][] negFixed = {{-4, 6}, {4, -6}, {-4, -6}, {-6, 4}, {0, -5}, {-5, 0}, {-35, 15}, {35, -15},
                {-1, -1}, {-12, 18}, {12, -18}, {Integer.MIN_VALUE, 6}, {6, Integer.MIN_VALUE}};
        neg.addAll(Arrays.asList(negFixed));
        for (int t = 0; t < 30; t++) {
            int a = rnd.nextInt(20001) - 10000, b = rnd.nextInt(20001) - 10000;
            if (a >= 0 && b >= 0) a = -a - 1;
            neg.add(new int[]{a, b});
        }

        int checks = 0, failedNonNeg = 0, failedNeg = 0, signOnly = 0;
        for (Method m : entries) {
            for (int[] c : nonNeg) {
                long exp = BigInteger.valueOf(c[0]).gcd(BigInteger.valueOf(c[1])).longValueExact();
                checks++;
                String err = check(m, c, exp);
                if (err != null) {
                    failedNonNeg++;
                    System.out.println("CHECK FAIL " + m.getName() + "(" + c[0] + ", " + c[1] + "): " + err);
                }
            }
            for (int[] c : neg) {
                long exp = baselineGcd(c[0], c[1]);
                long mag = BigInteger.valueOf(c[0]).gcd(BigInteger.valueOf(c[1])).longValueExact();
                if (Math.abs(exp) != mag) throw new IllegalStateException("oracle broken at " + Arrays.toString(c));
                checks++;
                String err = check(m, c, exp);
                if (err != null) {
                    failedNeg++;
                    Long got = value(m, c);
                    if (got != null && got == -exp) signOnly++;
                    System.out.println("CHECK FAIL " + m.getName() + "(" + c[0] + ", " + c[1] + "): " + err
                            + " (baseline sign convention)");
                }
            }
        }
        int failed = failedNonNeg + failedNeg;
        System.out.println(checks + " checks, " + failed + " failed (non-negative: " + failedNonNeg
                + ", negative-input: " + failedNeg + ", of which sign-only: " + signOnly + ")");
        if (failed == 0) System.out.println("RESULT PASS " + checks + " checks");
        else if (failedNonNeg == 0 && signOnly == failedNeg)
            System.out.println("RESULT FAIL negative-input sign convention changed vs baseline ("
                    + failedNeg + "/" + checks + " checks; magnitudes correct, e.g. baseline findGCD(4,-6) = -2)");
        else System.out.println("RESULT FAIL " + failed + "/" + checks + " gcd results diverged");
    }

    /** Iterative equivalent of the baseline recurrence findGCD(a, b) (int arithmetic). */
    static long baselineGcd(int a, int b) {
        while (a != 0) {
            int r = b % a;
            b = a;
            a = r;
        }
        return b;
    }

    static String check(Method m, int[] c, long exp) {
        try {
            Long got = value(m, c);
            if (got == null) return "null result, expected " + exp;
            if (got != exp) return "got " + got + ", expected " + exp;
            return null;
        } catch (Throwable t) {
            return "threw " + deep(t) + ", expected " + exp;
        }
    }

    static Long value(Method m, int[] c) {
        try {
            Object r = m.invoke(null, arg(m.getParameterTypes()[0], c[0]), arg(m.getParameterTypes()[1], c[1]));
            return r == null ? null : ((Number) r).longValue();
        } catch (Exception e) {
            throw new RuntimeException(e.getCause() != null ? e.getCause() : e);
        }
    }

    static Object arg(Class<?> t, int v) {
        return (t == long.class || t == Long.class) ? (Object) (long) v : (Object) v;
    }

    static boolean integral(Class<?> t) {
        return t == int.class || t == long.class || t == Integer.class || t == Long.class;
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
