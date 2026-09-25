import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 020_longest_common_subsequence.
 *
 * Baseline contract (iter 0): static int lcs(String, String) returning the LCS
 * *length*. From iter 1 the entry point is named length(String, String); iter 1+
 * also exposes subsequence(String, String) returning one actual LCS string, and
 * iter 10 wraps everything in pluggable strategy classes.
 *
 * The driver finds every (String,String)->int "length" candidate and every
 * (String,String)->String "subsequence" candidate by reflection (skipping
 * *Test* classes) and checks them against an independent DP oracle:
 *  - length must equal the reference LCS length;
 *  - subsequence must be a common subsequence of both inputs whose length
 *    equals the reference LCS length.
 */
public class Driver {

    record Cand(Method m, Object recv, String label) {}

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> loaded = loadClasses(classes);

        List<Cand> lengthCands = new ArrayList<>();
        List<Cand> subseqCands = new ArrayList<>();
        for (Class<?> c : loaded) {
            if (c.getName().toLowerCase().contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || Modifier.isAbstract(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 2 || p[0] != String.class || p[1] != String.class) continue;
                String n = m.getName().toLowerCase();
                boolean nameOk = n.contains("lcs") || n.contains("length")
                        || n.contains("subsequence") || n.contains("commonsub");
                if (!nameOk) continue;
                Class<?> r = m.getReturnType();
                boolean isLen = (r == int.class || r == long.class || r == Integer.class || r == Long.class);
                boolean isStr = (r == String.class || r == CharSequence.class);
                if (!isLen && !isStr) continue;
                Object recv = null;
                m.setAccessible(true);
                if (!Modifier.isStatic(m.getModifiers())) {
                    recv = instantiate(c);
                    if (recv == null) continue;
                }
                Cand cand = new Cand(m, recv, c.getName() + "#" + m.getName());
                if (isLen) lengthCands.add(cand); else subseqCands.add(cand);
            }
        }

        if (lengthCands.isEmpty()) {
            System.out.println("RESULT FAIL no LCS length entry point found among "
                    + loaded.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }
        System.out.println("length candidates: " + lengthCands.stream().map(Cand::label).toList());
        System.out.println("subsequence candidates: " + subseqCands.stream().map(Cand::label).toList());

        List<String[]> cases = new ArrayList<>(List.of(
                new String[]{"AGGTAB", "GXTXAYB"},        // baseline demo, LCS length 4
                new String[]{"", ""},
                new String[]{"", "ABC"},
                new String[]{"ABC", ""},
                new String[]{"ABCBDAB", "BDCABA"},
                new String[]{"AAAA", "AAAA"},
                new String[]{"ABC", "DEF"},
                new String[]{"A", "A"},
                new String[]{"A", "B"}));
        Random rnd = new Random(7);
        String alpha = "ABCD";
        for (int t = 0; t < 40; t++) {
            cases.add(new String[]{randStr(rnd, alpha, rnd.nextInt(26)),
                                   randStr(rnd, alpha, rnd.nextInt(26))});
        }
        // longer inputs (exercise divide-and-conquer / space-optimized paths),
        // binary and wide alphabets, and non-ASCII chars (baseline is char-based)
        String[] alphas = {"01", "ACGT", "abcdefghijklmnopqrstuvwxyz", "\u00e9\u4e2d\u6587a"};
        for (int t = 0; t < 12; t++) {
            String al = alphas[t % alphas.length];
            cases.add(new String[]{randStr(rnd, al, 40 + rnd.nextInt(260)),
                                   randStr(rnd, al, 40 + rnd.nextInt(260))});
        }
        String big = randStr(rnd, "ACGT", 250);
        cases.add(new String[]{big, big});                                 // identical
        cases.add(new String[]{big, new StringBuilder(big).reverse().toString()});
        cases.add(new String[]{big.substring(30, 180), big});              // substring

        int checks = 0, failed = 0;
        for (String[] cs : cases) {
            int ref = refLcsLength(cs[0], cs[1]);
            for (Cand cand : lengthCands) {
                checks++;
                try {
                    long got = ((Number) cand.m().invoke(cand.recv(), cs[0], cs[1])).longValue();
                    if (got != ref) {
                        failed++;
                        System.out.println("CHECK FAIL " + cand.label() + "(\"" + cs[0] + "\",\""
                                + cs[1] + "\") = " + got + ", expected " + ref);
                    }
                } catch (Throwable e) {
                    failed++;
                    System.out.println("CHECK FAIL " + cand.label() + " threw on (\"" + cs[0]
                            + "\",\"" + cs[1] + "\"): " + cause(e));
                }
            }
            for (Cand cand : subseqCands) {
                checks++;
                try {
                    String got = String.valueOf(cand.m().invoke(cand.recv(), cs[0], cs[1]));
                    boolean ok = got.length() == ref
                            && isSubsequence(got, cs[0]) && isSubsequence(got, cs[1]);
                    if (!ok) {
                        failed++;
                        System.out.println("CHECK FAIL " + cand.label() + "(\"" + cs[0] + "\",\""
                                + cs[1] + "\") = \"" + got + "\" not a common subsequence of length " + ref);
                    }
                } catch (Throwable e) {
                    failed++;
                    System.out.println("CHECK FAIL " + cand.label() + " threw on (\"" + cs[0]
                            + "\",\"" + cs[1] + "\"): " + cause(e));
                }
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0
                ? "RESULT PASS " + checks + " checks over " + (lengthCands.size() + subseqCands.size()) + " entry points"
                : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    // ---------- independent oracle ----------

    static int refLcsLength(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                        ? dp[i - 1][j - 1] + 1
                        : Math.max(dp[i - 1][j], dp[i][j - 1]);
        return dp[m][n];
    }

    static boolean isSubsequence(String sub, String s) {
        int i = 0;
        for (int j = 0; i < sub.length() && j < s.length(); j++)
            if (sub.charAt(i) == s.charAt(j)) i++;
        return i == sub.length();
    }

    // ---------- helpers ----------

    static String randStr(Random rnd, String alpha, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(alpha.charAt(rnd.nextInt(alpha.length())));
        return sb.toString();
    }

    static Object instantiate(Class<?> c) {
        if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) return null;
        try {
            Constructor<?> ctor = c.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Throwable t) {
            return null;
        }
    }

    static String cause(Throwable e) {
        Throwable t = e.getCause() != null ? e.getCause() : e;
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }

    static List<Class<?>> loadClasses(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
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
