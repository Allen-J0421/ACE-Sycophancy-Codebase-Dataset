import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 042_balanced_parentheses.
 *
 * Baseline (iteration 0): public static boolean BalancedParentheses.isBalanced(String)
 * over the bracket set () {} []. Every other character (letters, spaces,
 * angle brackets, non-ASCII, ...) is ignored. An empty string is balanced.
 * A closer with an empty stack or a mismatched top returns false; leftover
 * openers return false. Every iteration keeps this 1-arg entry point
 * (iteration 10 adds an overload with a custom pair map, not tested here).
 *
 * Oracle: an independent stack-based reference over the same bracket set,
 * plus a hand-written truth table. Fixed-seed random strings: fully random
 * over a mixed alphabet, generated-balanced strings with interleaved noise,
 * and single-character mutations of balanced strings.
 */
public class Driver {
    static final String OPEN = "({[", CLOSE = ")}]";

    static boolean reference(String s) {
        char[] st = new char[s.length() + 1];
        int top = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int o = OPEN.indexOf(c), cl = CLOSE.indexOf(c);
            if (o >= 0) st[top++] = c;
            else if (cl >= 0) {
                if (top == 0 || st[top - 1] != OPEN.charAt(cl)) return false;
                top--;
            }
        }
        return top == 0;
    }

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);
        Method entry = null;
        for (int pass = 0; pass < 2 && entry == null; pass++) {
            for (Class<?> c : all) {
                String cn = c.getSimpleName().toLowerCase();
                if (cn.contains("test")) continue;
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                    if (m.getReturnType() != boolean.class && m.getReturnType() != Boolean.class) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length != 1 || !(p[0] == String.class || p[0] == CharSequence.class)) continue;
                    String n = m.getName().toLowerCase();
                    if (pass == 0 && !(n.contains("balanced") || n.contains("valid"))) continue;
                    entry = m;
                    break;
                }
                if (entry != null) break;
            }
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no entry point static boolean isBalanced(String) found");
            return;
        }
        entry.setAccessible(true);
        System.out.println("entry: " + entry);

        // Hand-written truth table (expected values stated explicitly, and also
        // cross-checked against the reference so the table itself can't drift).
        Object[][] table = {
            {"[()()]{}", true},          // baseline demo
            {"", true},
            {"()", true}, {"[]", true}, {"{}", true},
            {"({[]})", true}, {"()[]{}", true}, {"{[()()]}[]", true},
            {"(", false}, {")", false}, {"[", false}, {"}", false},
            {"(]", false}, {"[)", false}, {"{)", false}, {"(}", false},
            {"([)]", false}, {"{[}]", false}, {"((())", false}, {"(()))", false},
            {")(", false}, {"][", false}, {"}{", false},
            {"a", true}, {"abc def", true}, {"a(b)c", true}, {"f(x[i]){return;}", true},
            {"a(b", false}, {"a)b(", false}, {"x[y}z", false},
            {"<>", true}, {"<(>)", true}, {"(<)>", true}, {">>", true}, {"<(", false},
            {"«»", true}, {"«(»)", true}, {"(«)", true}, {"\u00e9(\u4e2d)\u00e9", true},
            {"\u4e2d(", false}, {"(\u4e2d]", false},
            {"   ", true}, {"\n(\t)\n", true},
            {"((((((((((((((((((((((((((((((((((((((((((((((((((", false},
            {"(((((((((((((((((((((((((())))))))))))))))))))))))))", true},
        };
        List<String> inputs = new ArrayList<>();
        List<Boolean> expected = new ArrayList<>();
        for (Object[] row : table) {
            String s = (String) row[0];
            boolean e = (Boolean) row[1];
            if (reference(s) != e) throw new IllegalStateException("oracle/table disagree on " + s);
            inputs.add(s); expected.add(e);
        }
        Random rnd = new Random(4242);
        String alphabet = "(){}[]ab <>";
        for (int t = 0; t < 80; t++) {                // fully random strings
            int n = rnd.nextInt(t < 50 ? 10 : 40);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
            inputs.add(sb.toString()); expected.add(reference(sb.toString()));
        }
        for (int t = 0; t < 60; t++) {                // generated balanced + noise, and mutants
            String bal = balanced(rnd, 1 + rnd.nextInt(12), t % 2 == 0);
            inputs.add(bal); expected.add(reference(bal));
            if (!reference(bal)) throw new IllegalStateException("generator produced unbalanced " + bal);
            StringBuilder mut = new StringBuilder(bal);
            List<Integer> bracketPos = new ArrayList<>();
            for (int i = 0; i < bal.length(); i++) if ((OPEN + CLOSE).indexOf(bal.charAt(i)) >= 0) bracketPos.add(i);
            int pos = bracketPos.get(rnd.nextInt(bracketPos.size()));
            switch (rnd.nextInt(3)) {
                case 0: mut.deleteCharAt(pos); break;                                  // drop a bracket
                case 1: mut.setCharAt(pos, "(){}[]".charAt(rnd.nextInt(6))); break;    // replace a bracket
                default: mut.insert(rnd.nextInt(mut.length() + 1), "(){}[]".charAt(rnd.nextInt(6)));
            }
            inputs.add(mut.toString()); expected.add(reference(mut.toString()));
        }

        int checks = 0, failed = 0, trues = 0;
        String first = null;
        for (int i = 0; i < inputs.size(); i++) {
            String s = inputs.get(i);
            boolean exp = expected.get(i);
            if (exp) trues++;
            checks++;
            String msg = null;
            try {
                boolean got = (Boolean) entry.invoke(null, s);
                if (got != exp) msg = "isBalanced(\"" + esc(s) + "\") = " + got + ", expected " + exp;
            } catch (Throwable t) {
                msg = "isBalanced(\"" + esc(s) + "\") threw " + deep(t);
            }
            if (msg != null) {
                failed++;
                if (first == null) first = msg;
                if (failed <= 10) System.out.println("CHECK FAIL " + msg);
            }
        }
        System.out.println(checks + " checks (" + trues + " balanced), " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " checks: " + first);
    }

    static String balanced(Random rnd, int pairs, boolean noise) {
        StringBuilder sb = new StringBuilder();
        Deque<Character> open = new ArrayDeque<>();
        int remaining = pairs;
        while (remaining > 0 || !open.isEmpty()) {
            if (noise && rnd.nextInt(4) == 0) sb.append("xy <>\u00e9".charAt(rnd.nextInt(6)));
            if (remaining > 0 && (open.isEmpty() || rnd.nextBoolean())) {
                int k = rnd.nextInt(3);
                sb.append(OPEN.charAt(k)); open.push(CLOSE.charAt(k)); remaining--;
            } else {
                sb.append(open.pop());
            }
        }
        return sb.toString();
    }

    static String esc(String s) {
        return s.replace("\n", "\\n").replace("\t", "\\t");
    }

    static String deep(Throwable t) {
        Throwable c = t;
        while (c instanceof java.lang.reflect.InvocationTargetException && c.getCause() != null) c = c.getCause();
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
