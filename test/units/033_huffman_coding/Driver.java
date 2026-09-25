import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 033_huffman_coding.
 *
 * Baseline (iter 0): HuffmanCoding.huffmanCodes(String s, int[] freq) returns an
 * ArrayList&lt;String&gt; of the leaf codes in PRE-ORDER over the Huffman tree.
 * Iterations 1-10: same method returns Map&lt;Character, String&gt; (symbol -> code).
 *
 * In every iteration the tree construction is deterministic: the priority queue
 * orders nodes by (frequency, smallest original symbol index), merged nodes carry
 * the min index of their children, the first node polled becomes the LEFT child
 * (bit '0'). That deterministic tie-breaking is the natural contract, so the
 * oracle is an independent reference implementation of exactly that construction,
 * compared exactly (per-symbol map, or pre-order list, depending on return type).
 *
 * Additional independent properties verified on every case: the code set is
 * prefix-free and its weighted length equals the optimal Huffman cost computed
 * by a tie-break-free frequency merge.
 */
public class Driver {

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> cs = loadClasses(classes);

        Method entry = null;
        Object receiver = null;
        for (Class<?> c : cs) {
            if (c.getSimpleName().toLowerCase().contains("test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (!(p.length == 2 && p[0] == String.class && p[1] == int[].class)) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("huffman") || n.contains("code"))) continue;
                if (m.getReturnType() == void.class) continue;
                m.setAccessible(true);
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        Constructor<?> k = c.getDeclaredConstructor();
                        k.setAccessible(true);
                        receiver = k.newInstance();
                    } catch (Throwable t) { continue; }
                }
                entry = m;
                break;
            }
            if (entry != null) break;
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no huffman-codes entry point found among "
                    + cs.stream().map(Class::getName).collect(Collectors.toList()));
            return;
        }
        System.out.println("entry: " + entry);

        List<int[]> cases = new ArrayList<>();
        cases.add(new int[]{5, 9, 12, 13, 16, 45});      // baseline demo
        cases.add(new int[]{7});                          // single symbol -> "0"
        cases.add(new int[]{3, 3});                       // tie
        cases.add(new int[]{1, 1, 1, 1, 1, 1, 1, 1});    // all equal
        cases.add(new int[]{0, 0, 5, 1});                 // zero frequencies
        cases.add(new int[]{1, 2, 4, 8, 16, 32, 64});    // skewed / deep tree
        Random rnd = new Random(777);
        for (int t = 0; t < 25; t++) {
            int n = 1 + rnd.nextInt(16);
            int[] f = new int[n];
            for (int i = 0; i < n; i++) f[i] = rnd.nextInt(101);
            cases.add(f);
        }
        // Larger alphabets (symbols beyond 'a'..'z'), heavy ties, big frequencies.
        for (int t = 0; t < 15; t++) {
            int n = 17 + rnd.nextInt(44);
            int[] f = new int[n];
            int hi = (t % 3 == 0) ? 3 : (t % 3 == 1 ? 100000 : 50);
            for (int i = 0; i < n; i++) f[i] = 1 + rnd.nextInt(hi);
            cases.add(f);
        }

        int checks = 0, failed = 0;
        for (int[] freq : cases) {
            int n = freq.length;
            String symbols = SYMBOLS.substring(0, n);            // distinct symbols

            String[] refBySymbol = new String[n];
            List<String> refPreorder = new ArrayList<>();
            reference(freq, refBySymbol, refPreorder);
            // driver self-check: reference codes must be prefix-free and optimal
            if (!prefixFree(Arrays.asList(refBySymbol))
                    || cost(freq, refBySymbol) != optimalCost(freq)) {
                throw new IllegalStateException("driver reference inconsistent for "
                        + Arrays.toString(freq));
            }

            checks++;
            Object r;
            try {
                r = entry.invoke(receiver, symbols, freq.clone());
            } catch (Throwable e) {
                failed++;
                Throwable cause = e;
                while (cause.getCause() != null) cause = cause.getCause();
                System.out.println("CHECK FAIL freq=" + Arrays.toString(freq) + " threw " + cause);
                continue;
            }

            String err = null;
            if (r instanceof Map<?, ?> map) {
                if (map.size() != n) {
                    err = "map size " + map.size() + " != " + n;
                } else {
                    for (int i = 0; i < n && err == null; i++) {
                        Object code = map.get(symbols.charAt(i));
                        if (code == null) err = "no code for symbol '" + symbols.charAt(i) + "'";
                        else if (!refBySymbol[i].equals(code.toString()))
                            err = "symbol '" + symbols.charAt(i) + "' code " + code
                                    + " != expected " + refBySymbol[i];
                    }
                    if (err == null) {
                        List<String> codes = map.values().stream().map(Object::toString)
                                .collect(Collectors.toList());
                        if (!prefixFree(codes)) err = "code set not prefix-free: " + codes;
                    }
                }
            } else if (r instanceof Collection<?> col) {
                List<String> codes = col.stream().map(Object::toString).collect(Collectors.toList());
                if (!codes.equals(refPreorder))
                    err = "preorder codes " + codes + " != expected " + refPreorder;
                else if (!prefixFree(codes))
                    err = "code set not prefix-free: " + codes;
            } else {
                err = "unexpected return type " + (r == null ? "null" : r.getClass().getName());
            }

            if (err != null) {
                failed++;
                System.out.println("CHECK FAIL freq=" + Arrays.toString(freq) + ": " + err);
            }
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " huffman instances"
                : "RESULT FAIL " + failed + "/" + checks + " huffman instances wrong");
    }

    /** Distinct symbols; the first 26 are 'a'..'z' as in the baseline demo. */
    static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 !?.,;:-_+=*/\u00e9\u00df\u4e2d";

    // ------------------------------------------------------------------ reference

    static final class RNode {
        final int f, idx;
        final RNode l, r;
        RNode(int f, int idx) { this.f = f; this.idx = idx; l = r = null; }
        RNode(RNode l, RNode r) {
            this.f = l.f + r.f;
            this.idx = Math.min(l.idx, r.idx);
            this.l = l;
            this.r = r;
        }
    }

    /** Rebuilds the deterministic (frequency, min-index) Huffman tree of the baseline. */
    static void reference(int[] freq, String[] bySymbol, List<String> preorder) {
        int n = freq.length;
        if (n == 0) return;
        if (n == 1) {
            bySymbol[0] = "0";
            preorder.add("0");
            return;
        }
        PriorityQueue<RNode> pq = new PriorityQueue<>(
                Comparator.comparingInt((RNode a) -> a.f).thenComparingInt(a -> a.idx));
        for (int i = 0; i < n; i++) pq.add(new RNode(freq[i], i));
        while (pq.size() >= 2) {
            RNode l = pq.poll(), r = pq.poll();
            pq.add(new RNode(l, r));
        }
        walk(pq.peek(), "", bySymbol, preorder);
    }

    static void walk(RNode node, String path, String[] bySymbol, List<String> preorder) {
        if (node.l == null && node.r == null) {
            String code = path.isEmpty() ? "0" : path;
            bySymbol[node.idx] = code;
            preorder.add(code);
            return;
        }
        walk(node.l, path + '0', bySymbol, preorder);
        walk(node.r, path + '1', bySymbol, preorder);
    }

    /** Optimal Huffman cost by plain frequency merging (tie-break independent). */
    static long optimalCost(int[] freq) {
        if (freq.length < 2) return 0;
        PriorityQueue<Long> pq = new PriorityQueue<>();
        for (int f : freq) pq.add((long) f);
        long cost = 0;
        while (pq.size() >= 2) {
            long m = pq.poll() + pq.poll();
            cost += m;
            pq.add(m);
        }
        return cost;
    }

    static long cost(int[] freq, String[] codes) {
        if (freq.length < 2) return 0;
        long c = 0;
        for (int i = 0; i < freq.length; i++) c += (long) freq[i] * codes[i].length();
        return c;
    }

    static boolean prefixFree(List<String> codes) {
        for (int i = 0; i < codes.size(); i++)
            for (int j = 0; j < codes.size(); j++)
                if (i != j && codes.get(j).startsWith(codes.get(i))) return false;
        return true;
    }

    static List<Class<?>> loadClasses(Path classes) throws Exception {
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
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable ignore) {}
        }
        return out;
    }
}
