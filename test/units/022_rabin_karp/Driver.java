import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 022_rabin_karp.
 *
 * Baseline contract: static ArrayList<Integer> search(String pattern, String text)
 * returning the 0-based start indices of every (overlapping) occurrence, in
 * ascending order. The baseline only supports 1 <= pattern.length() <= text.length()
 * (it throws on an empty pattern's rolling-hash seed loop when the pattern is
 * longer than the text, and reports every window for an empty pattern), so all
 * test cases stay inside that domain.
 *
 * Oracle: a plain overlapping String.startsWith scan. Texts up to ~200 chars over
 * a 2-letter alphabet force plenty of spurious-hash-collision verification.
 *
 * Argument order is calibrated once with a probe whose answer differs between
 * (pattern, text) and (text, pattern).
 */
public class Driver {

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> loaded = loadClasses(classes);

        Method entry = null;
        Object recv = null;
        outer:
        for (Class<?> c : loaded) {
            if (c.getName().toLowerCase().contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || Modifier.isAbstract(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 2 || p[0] != String.class || p[1] != String.class) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("search") || n.contains("find") || n.contains("occurrence")
                        || n.contains("match") || n.contains("indices") || n.contains("indexes"))) continue;
                Class<?> r = m.getReturnType();
                boolean retOk = Collection.class.isAssignableFrom(r) || r == int[].class
                        || r == Integer[].class;
                if (!retOk) continue;
                m.setAccessible(true);
                if (!Modifier.isStatic(m.getModifiers())) {
                    recv = instantiate(c);
                    if (recv == null) continue;
                }
                entry = m;
                break outer;
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no Rabin-Karp search entry point found among "
                    + loaded.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }
        System.out.println("entry: " + entry);

        // Calibrate argument order: search("ab", "abab") -> [0, 2]; swapped -> [].
        boolean patternFirst = true;
        List<Integer> probe = invoke(entry, recv, "ab", "abab", true);
        if (probe == null || !probe.equals(List.of(0, 2))) {
            List<Integer> swapped = invoke(entry, recv, "ab", "abab", false);
            if (swapped != null && swapped.equals(List.of(0, 2))) {
                patternFirst = false;
                System.out.println("calibration: arguments are (text, pattern)");
            }
        }

        List<String[]> cases = new ArrayList<>(List.of(
                new String[]{"geeks", "geeksforgeeks"},     // baseline demo -> 0 8
                new String[]{"ab", "abab"},
                new String[]{"aaa", "aaaaaa"},              // overlapping -> 0 1 2 3
                new String[]{"a", "a"},
                new String[]{"abc", "abc"},
                new String[]{"z", "aaaa"},                  // no match
                new String[]{"aab", "aaab aabaab"},
                new String[]{"abab", "abababab"},
                new String[]{"e", "geeksforgeeks"}));
        Random rnd = new Random(13);
        for (int t = 0; t < 40; t++) {
            int n = 1 + rnd.nextInt(200);
            String txt = randStr(rnd, "ab", n);
            int m = 1 + rnd.nextInt(Math.min(5, n));
            String pat;
            if (rnd.nextBoolean() && n >= m) {
                int at = rnd.nextInt(n - m + 1);
                pat = txt.substring(at, at + m);       // guaranteed occurrence
            } else {
                pat = randStr(rnd, "ab", m);
            }
            cases.add(new String[]{pat, txt});
        }
        // Longer patterns (rolling-hash weight h = d^(m-1) mod q over many
        // digits), wider alphabets incl. chars > 255, pattern == text length.
        String[] alphas = {"abcdefghijklmnopqrstuvwxyz", "ab", "xyz \u00e9\u4e2d"};
        for (int t = 0; t < 25; t++) {
            String al = alphas[t % alphas.length];
            int n = 20 + rnd.nextInt(280);
            String txt = randStr(rnd, al, n);
            int m = 1 + rnd.nextInt(Math.min(30, n));
            String pat;
            if (t % 5 == 4) {
                pat = txt;                               // whole text
            } else if (rnd.nextInt(3) > 0) {
                int at = rnd.nextInt(n - m + 1);
                pat = txt.substring(at, at + m);         // guaranteed occurrence
            } else {
                pat = randStr(rnd, al, m);
            }
            cases.add(new String[]{pat, txt});
        }
        cases.add(new String[]{"abc", "xxabcabcab"});    // match at the last window boundary
        cases.add(new String[]{"cab", "xxabcabcab"});    // match ends at text end

        int checks = 0, failed = 0;
        for (String[] cs : cases) {
            String pat = cs[0], txt = cs[1];
            checks++;
            List<Integer> expected = refOccurrences(pat, txt);
            List<Integer> got;
            try {
                got = invokeOrThrow(entry, recv, pat, txt, patternFirst);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL threw on pat=\"" + pat + "\" txt=\"" + txt + "\": " + cause(e));
                continue;
            }
            if (!expected.equals(got)) {
                failed++;
                System.out.println("CHECK FAIL pat=\"" + pat + "\" txt=\"" + txt + "\" -> " + got
                        + ", expected " + expected);
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    // ---------- independent oracle ----------

    static List<Integer> refOccurrences(String pat, String txt) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i + pat.length() <= txt.length(); i++)
            if (txt.startsWith(pat, i)) out.add(i);
        return out;
    }

    // ---------- helpers ----------

    static List<Integer> invoke(Method m, Object recv, String pat, String txt, boolean patternFirst) {
        try {
            return invokeOrThrow(m, recv, pat, txt, patternFirst);
        } catch (Throwable t) {
            return null;
        }
    }

    static List<Integer> invokeOrThrow(Method m, Object recv, String pat, String txt,
                                       boolean patternFirst) throws Throwable {
        Object r = patternFirst ? m.invoke(recv, pat, txt) : m.invoke(recv, txt, pat);
        return toIntList(r);
    }

    static List<Integer> toIntList(Object o) {
        if (o == null) return null;
        List<Integer> out = new ArrayList<>();
        if (o instanceof int[] a) { for (int v : a) out.add(v); return out; }
        if (o instanceof Integer[] a) { out.addAll(Arrays.asList(a)); return out; }
        if (o instanceof Collection<?> c) {
            for (Object v : c) out.add(((Number) v).intValue());
            return out;
        }
        return null;
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

    static String randStr(Random rnd, String alpha, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(alpha.charAt(rnd.nextInt(alpha.length())));
        return sb.toString();
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
