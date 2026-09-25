import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 021_kmp_pattern_searching.
 *
 * Baseline contract: static ArrayList<Integer> search(String pattern, String text)
 * returning the 0-based start indices of every (overlapping) occurrence of the
 * pattern in the text, in ascending order. Iterations keep the same shape but
 * return List<Integer>.
 *
 * Oracle: a plain overlapping String.startsWith scan, fully independent of the
 * subject. Domain: non-empty pattern (the baseline throws on an empty pattern,
 * so that input is outside the tested contract); pattern longer than the text
 * is in-domain (baseline returns no matches).
 *
 * Argument order is calibrated once with a probe whose answer differs between
 * (pattern, text) and (text, pattern), so an API-level parameter swap in some
 * iteration is tolerated without weakening the oracle.
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
            System.out.println("RESULT FAIL no KMP search entry point found among "
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
                new String[]{"aaba", "aabaacaadaabaaba"},   // baseline demo -> 0 9 12
                new String[]{"ab", "abab"},
                new String[]{"aaa", "aaaaaa"},              // overlapping -> 0 1 2 3
                new String[]{"a", "a"},
                new String[]{"abc", "abc"},
                new String[]{"abc", "ab"},                  // pattern longer than text -> []
                new String[]{"z", "aaaa"},                  // no match
                new String[]{"a", ""},                      // empty text -> []
                new String[]{"aab", "aaab aabaab"},
                new String[]{"abab", "abababab"}));
        Random rnd = new Random(11);
        for (int t = 0; t < 40; t++) {
            String txt = randStr(rnd, "ab", rnd.nextInt(61));
            String pat = randStr(rnd, "ab", 1 + rnd.nextInt(4));
            cases.add(new String[]{pat, txt});
        }
        // Longer patterns with rich border structure (stress the LPS table's
        // fallback chain), patterns cut from the text, wider alphabets.
        String[] seeds = {"aab", "abaab", "aabaa", "abcab", "aaaab"};
        for (int t = 0; t < 30; t++) {
            String al = (t % 3 == 0) ? "abc" : "ab";
            String pat;
            String txt;
            if (t % 3 == 2) {
                String unit = seeds[rnd.nextInt(seeds.length)];
                StringBuilder sb = new StringBuilder();
                int reps = 2 + rnd.nextInt(4);
                for (int r = 0; r < reps; r++) sb.append(unit);
                pat = sb.substring(0, Math.max(1, sb.length() - rnd.nextInt(unit.length())));
                StringBuilder tb = new StringBuilder();
                while (tb.length() < 200) {
                    tb.append(rnd.nextInt(4) == 0 ? randStr(rnd, al, 1 + rnd.nextInt(3)) : unit);
                }
                txt = tb.toString();
            } else {
                txt = randStr(rnd, al, 50 + rnd.nextInt(250));
                if (rnd.nextBoolean()) {
                    int a = rnd.nextInt(txt.length() - 12);
                    pat = txt.substring(a, a + 1 + rnd.nextInt(12));
                } else {
                    pat = randStr(rnd, al, 1 + rnd.nextInt(8));
                }
            }
            cases.add(new String[]{pat, txt});
        }
        cases.add(new String[]{"\u00e9\u00e9", "\u00e9\u00e9\u00e9x\u00e9\u00e9"}); // non-ASCII chars -> 0 1 4

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
