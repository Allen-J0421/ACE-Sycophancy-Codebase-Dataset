import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 014_trie_insert_and_search.
 *
 * Baseline semantics (iteration 0): a trie over lowercase 'a'-'z' with
 *   insert(String)            -> void   (adds a word)
 *   search(String)  -> boolean          (true iff the exact word was inserted)
 *   isPrefix(String) -> boolean         (true iff some inserted word starts with it)
 *
 * Oracle: an independent model (HashSet of words + derived prefix set).
 * Probes use only non-empty lowercase words, the domain the baseline defines.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        if (!Stage.compileSubjectSources(work, classes)) return;
        List<Class<?>> all = scan(classes);

        Method insert = null, search = null, prefix = null;
        Object trie = null;
        for (Class<?> c : all) {
            Method ins = null, sea = null, pre = null;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1 || (p[0] != String.class && p[0] != CharSequence.class)) continue;
                String n = m.getName().toLowerCase();
                if (m.getReturnType() == void.class
                        && (n.contains("insert") || n.contains("add"))) ins = m;
                if ((m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
                    if (n.contains("prefix") || n.contains("startswith")) pre = m;
                    else if (n.contains("search") || n.contains("contains") || n.contains("hasword")) sea = m;
                }
            }
            if (ins != null && sea != null && pre != null) {
                Object inst = null;
                if (!Modifier.isStatic(ins.getModifiers())) {
                    try {
                        var ctor = c.getDeclaredConstructor();
                        ctor.setAccessible(true);
                        inst = ctor.newInstance();
                    } catch (Throwable t) {
                        continue;
                    }
                }
                ins.setAccessible(true); sea.setAccessible(true); pre.setAccessible(true);
                insert = ins; search = sea; prefix = pre; trie = inst;
                System.out.println("entry class: " + c.getName());
                break;
            }
        }
        if (insert == null) {
            System.out.println("RESULT FAIL no trie entry point (insert/search/isPrefix) found");
            return;
        }
        System.out.println("insert=" + insert + "\nsearch=" + search + "\nprefix=" + prefix);

        // Build word set: baseline demo words + random words over a-e.
        Random rnd = new Random(20260925);
        Set<String> words = new LinkedHashSet<>(List.of("and", "ant", "do", "dad", "a", "abcde"));
        while (words.size() < 70) {
            int len = 1 + rnd.nextInt(8);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(5)));
            words.add(sb.toString());
        }
        for (String w : words) {
            try {
                insert.invoke(trie, w);
            } catch (Throwable t) {
                System.out.println("RESULT FAIL insert(\"" + w + "\") threw " + cause(t));
                return;
            }
        }
        Set<String> prefixes = new HashSet<>();
        for (String w : words)
            for (int i = 1; i <= w.length(); i++) prefixes.add(w.substring(0, i));

        int checks = 0, failed = 0;
        List<String> probes = new ArrayList<>();
        for (String w : words) {
            probes.add(w);
            for (int i = 1; i < w.length(); i++) probes.add(w.substring(0, i));
            probes.add(w + "a");
            probes.add(w + "f");
        }
        for (int t = 0; t < 500; t++) {
            int len = 1 + rnd.nextInt(8);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(6)));
            probes.add(sb.toString());
        }
        for (String p : probes) {
            boolean expWord = words.contains(p);
            boolean expPref = prefixes.contains(p);
            checks += 2;
            try {
                boolean gotWord = (Boolean) search.invoke(trie, p);
                if (gotWord != expWord) {
                    failed++;
                    if (failed <= 8) System.out.println("CHECK FAIL search(\"" + p + "\") = " + gotWord + ", expected " + expWord);
                }
            } catch (Throwable t) {
                failed++;
                if (failed <= 8) System.out.println("CHECK FAIL search(\"" + p + "\") threw " + cause(t));
            }
            try {
                boolean gotPref = (Boolean) prefix.invoke(trie, p);
                if (gotPref != expPref) {
                    failed++;
                    if (failed <= 8) System.out.println("CHECK FAIL isPrefix(\"" + p + "\") = " + gotPref + ", expected " + expPref);
                }
            } catch (Throwable t) {
                failed++;
                if (failed <= 8) System.out.println("CHECK FAIL isPrefix(\"" + p + "\") threw " + cause(t));
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " trie lookups wrong");
    }

    static String cause(Throwable t) {
        Throwable c = t.getCause() != null ? t.getCause() : t;
        return c.toString();
    }

    static List<Class<?>> scan(Path classes) throws Exception {
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
