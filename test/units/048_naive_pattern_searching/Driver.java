import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 048_naive_pattern_searching.
 *
 * Baseline contract (iteration 0, class NaivePatternSearch):
 *   static ArrayList<Integer> search(String pat, String txt)
 * returns every start index i (ascending, overlapping matches included) with
 * txt.startsWith(pat, i). Pattern longer than text -> empty list.
 * The baseline DEFINES the empty pattern: its loop runs i = 0..n and the inner
 * loop never executes, so search("", txt) = [0, 1, ..., txt.length()] — which is
 * exactly what an indexOf scan yields. That case is therefore scored.
 * Iterations 1+ keep search(String pattern, String text) returning List<Integer>.
 *
 * Oracle: repeated String.indexOf(pat, from) scan, advancing by one so that
 * overlapping occurrences are all reported.
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
                if (p.length != 2 || p[0] != String.class || p[1] != String.class) continue;
                Class<?> r = m.getReturnType();
                if (!(Collection.class.isAssignableFrom(r) || r == int[].class)) continue;
                Object recv = null;
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        java.lang.reflect.Constructor<?> k = c.getDeclaredConstructor();
                        k.setAccessible(true);
                        recv = k.newInstance();
                    } catch (Throwable t) { continue; }
                }
                if (entry == null || m.getName().toLowerCase().contains("search")) {
                    entry = m;
                    receiver = recv;
                }
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no search(String pattern, String text) entry point found");
            return;
        }
        entry.setAccessible(true);
        System.out.println("entry: " + entry);

        // (pattern, text) cases
        List<String[]> cases = new ArrayList<>();
        cases.add(new String[]{"aaba", "aabaacaadaabaaba"});   // baseline demo -> [0, 9, 12]
        cases.add(new String[]{"aaa", "aaaaa"});               // overlapping -> [0,1,2]
        cases.add(new String[]{"abab", "abababab"});           // overlapping -> [0,2,4]
        cases.add(new String[]{"abc", "ab"});                  // pattern longer than text
        cases.add(new String[]{"abcdef", ""});                 // pattern longer, empty text
        cases.add(new String[]{"abc", "abc"});                 // equals text
        cases.add(new String[]{"c", "abc"});                   // match at end
        cases.add(new String[]{"xyz", "abcabc"});              // no match
        cases.add(new String[]{"", "abc"});                    // empty pattern -> [0,1,2,3] (baseline-defined)
        cases.add(new String[]{"", ""});                       // empty pattern, empty text -> [0]
        cases.add(new String[]{"a", "a"});
        cases.add(new String[]{"éé", "éééxéé"}); // non-ASCII
        Random rnd = new Random(4848);
        for (int t = 0; t < 60; t++) {
            int alpha = 1 + rnd.nextInt(t < 30 ? 2 : 4);     // tiny alphabets force overlaps
            String txt = randStr(rnd, rnd.nextInt(40), alpha);
            String pat = randStr(rnd, 1 + rnd.nextInt(t % 10 == 9 ? 50 : 5), alpha);
            cases.add(new String[]{pat, txt});
        }
        for (int t = 0; t < 10; t++) {                         // pattern taken from the text
            String txt = randStr(rnd, 5 + rnd.nextInt(60), 3);
            int s = rnd.nextInt(txt.length());
            int e = s + 1 + rnd.nextInt(Math.min(6, txt.length() - s));
            cases.add(new String[]{txt.substring(s, e), txt});
        }

        int checks = 0, failed = 0, emptyPatFailed = 0;
        for (int ci = 0; ci < cases.size(); ci++) {
            String pat = cases.get(ci)[0], txt = cases.get(ci)[1];
            List<Integer> exp = oracle(pat, txt);
            if (ci == 0 && !exp.equals(List.of(0, 9, 12))) throw new IllegalStateException("oracle broken");
            checks++;
            String err = null;
            try {
                List<Integer> got = toList(entry.invoke(receiver, pat, txt));
                if (!got.equals(exp)) err = "got " + got + ", expected " + exp;
            } catch (Throwable t) { err = "threw " + deep(t) + ", expected " + exp; }
            if (err != null) {
                failed++;
                if (pat.isEmpty()) emptyPatFailed++;
                System.out.println("CHECK FAIL search(\"" + pat + "\", \"" + txt + "\"): " + err);
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        if (failed == 0) {
            System.out.println("RESULT PASS " + checks + " checks");
        } else if (failed == emptyPatFailed) {
            System.out.println("RESULT FAIL empty pattern no longer returns [0..n] as the baseline does ("
                    + failed + "/" + checks + " checks)");
        } else {
            System.out.println("RESULT FAIL " + failed + "/" + checks + " searches diverged from indexOf oracle");
        }
    }

    static List<Integer> oracle(String pat, String txt) {
        List<Integer> out = new ArrayList<>();
        int from = 0;
        while (from <= txt.length()) {
            int i = txt.indexOf(pat, from);
            if (i < 0) break;
            out.add(i);
            from = i + 1;
        }
        return out;
    }

    static List<Integer> toList(Object o) {
        List<Integer> out = new ArrayList<>();
        if (o instanceof int[]) for (int v : (int[]) o) out.add(v);
        else for (Object v : (Collection<?>) o) out.add(((Number) v).intValue());
        return out;
    }

    static String randStr(Random r, int len, int alpha) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + r.nextInt(alpha)));
        return sb.toString();
    }

    static String deep(Throwable t) {
        Throwable c = t.getCause() != null ? t.getCause() : t;
        return c.toString();
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
