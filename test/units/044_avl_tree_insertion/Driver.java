import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 044_avl_tree_insertion.
 *
 * Baseline semantics (iteration 0, avl_tree_insertion.java): int-keyed AVL tree
 * with a static functional API {@code static Node insert(Node root, int key)}
 * returning the new subtree root. Duplicate keys are IGNORED (tree unchanged).
 * Standard AVL insertion (LL/RR/LR/RL rotations at the lowest unbalanced node).
 * The demo inserts 10,20,30,40,50,25 and prints the preorder "30 20 10 25 40 50".
 *
 * Iterations 1+ turn this into a generic instance class {@code AVLTree<T>} with
 * {@code void insert(T)}, a private root, and helper queries (contains, size,
 * inOrder, preOrder, iterator...). A separate {@code Main} prints the demo.
 *
 * Discovery (reflection): either a static insert(Node, key) -> Node, or an
 * instantiable class with a no-arg ctor + instance insert(key) and a field
 * holding a node type (a class with left/right self-typed fields). The tree
 * shape is read directly through the node fields (key/left/right/height).
 *
 * Oracle (independent):
 *   - inorder of the node structure == sorted distinct inserted keys;
 *   - AVL invariant |h(L)-h(R)| <= 1 at every node, stored height == real height;
 *   - preorder == an independent reference AVL (insertion is canonical, so the
 *     exact shape is the baseline's observable output, e.g. its preOrder print);
 *   - optional public queries (size/contains/inOrder/preOrder/iterator) agree;
 *   - the demo program's printed preorder equals "30 20 10 25 40 50".
 * Fixed seeds; demo + edge cases + 40 random sequences (with duplicates).
 */
public class Driver {

    static int checks = 0, failed = 0;
    static List<String> reasons = new ArrayList<>();

    // discovered API
    static Method staticInsert;              // mode A
    static Class<?> treeClass;               // mode B
    static Constructor<?> treeCtor;
    static Method instInsert;
    static Field rootField;
    static Class<?> nodeClass;
    static Field fKey, fLeft, fRight, fHeight;
    static Method mSize, mContains, mInOrder, mPreOrder;

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        List<Class<?>> all = scan(work.resolve("_classes"));
        if (!discover(all)) return;

        System.out.println("mode: " + (staticInsert != null ? "static " + staticInsert : "instance " + treeClass.getName()
                + " insert=" + instInsert));
        System.out.println("node: " + nodeClass.getName() + " key=" + fKey.getName() + " left=" + fLeft.getName()
                + " right=" + fRight.getName() + " height=" + (fHeight == null ? "-" : fHeight.getName()));

        List<int[]> cases = new ArrayList<>();
        cases.add(new int[]{10, 20, 30, 40, 50, 25});           // baseline demo
        cases.add(new int[]{});
        cases.add(new int[]{42});
        cases.add(new int[]{7, 7, 7, 7});
        cases.add(new int[]{3, 2, 1});                          // LL
        cases.add(new int[]{1, 2, 3});                          // RR
        cases.add(new int[]{3, 1, 2});                          // LR
        cases.add(new int[]{1, 3, 2});                          // RL
        cases.add(IntStream.rangeClosed(1, 127).toArray());
        cases.add(IntStream.iterate(200, i -> i - 1).limit(150).toArray());
        cases.add(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1, Integer.MAX_VALUE, Integer.MIN_VALUE});
        cases.add(new int[]{50, 40, 60, 30, 45, 55, 70, 20, 35, 42, 47, 10, 5, 1});
        Random rnd = new Random(4404);
        for (int c = 0; c < 40; c++) {
            int n = 1 + rnd.nextInt(c < 10 ? 20 : 300);
            int range = (c % 3 == 0) ? Math.max(2, n / 3) : 10 * n + 10;   // every third case: many duplicates
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(range) - range / 2;
            cases.add(a);
        }

        for (int ci = 0; ci < cases.size() && failed < 10; ci++) {
            runCase(ci, cases.get(ci));
        }

        // demo program output
        checkDemo(all);

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks over " + cases.size() + " insert sequences"
                : "RESULT FAIL " + failed + "/" + checks + " checks: " + String.join("; ", reasons.subList(0, Math.min(3, reasons.size()))));
    }

    // ------------------------------------------------------------------ cases

    static void runCase(int ci, int[] keys) {
        Object root;
        Object tree = null;
        try {
            if (staticInsert != null) {
                root = null;
                for (int k : keys) root = staticInsert.invoke(null, root, k);
            } else {
                tree = treeCtor.newInstance();
                for (int k : keys) instInsert.invoke(tree, k);
                root = rootField.get(tree);
            }
        } catch (Throwable t) {
            fail("case" + ci + " insert threw " + deep(t));
            return;
        }
        TreeSet<Integer> distinct = new TreeSet<>();
        for (int k : keys) distinct.add(k);
        List<Integer> expSorted = new ArrayList<>(distinct);

        // structure read-out
        List<Integer> in = new ArrayList<>(), pre = new ArrayList<>();
        String[] viol = {null};
        try {
            walk(root, in, pre, viol, 0);
        } catch (Throwable t) {
            fail("case" + ci + " reading node structure threw " + deep(t));
            return;
        }
        check(in.equals(expSorted), "case" + ci + " inorder " + clip(in) + " != sorted distinct " + clip(expSorted));
        check(viol[0] == null, "case" + ci + " " + viol[0]);

        Ref ref = null;
        for (int k : keys) ref = Ref.insert(ref, k);
        List<Integer> refPre = new ArrayList<>();
        Ref.pre(ref, refPre);
        check(pre.equals(refPre), "case" + ci + " preorder " + clip(pre) + " != reference AVL " + clip(refPre));

        // optional public queries on the instance API
        if (tree != null) {
            try {
                if (mSize != null) {
                    long s = ((Number) mSize.invoke(tree)).longValue();
                    check(s == distinct.size(), "case" + ci + " size()=" + s + " expected " + distinct.size());
                }
                if (mContains != null) {
                    Random r = new Random(ci * 31L + 7);
                    for (int q = 0; q < 20; q++) {
                        int k = (keys.length > 0 && r.nextBoolean()) ? keys[r.nextInt(keys.length)] : r.nextInt(2000) - 1000;
                        boolean got = (Boolean) mContains.invoke(tree, k);
                        check(got == distinct.contains(k), "case" + ci + " contains(" + k + ")=" + got);
                    }
                }
                if (mInOrder != null) {
                    List<Integer> got = toIntList(mInOrder.invoke(tree));
                    check(expSorted.equals(got), "case" + ci + " inOrder() " + clip(got) + " != " + clip(expSorted));
                }
                if (mPreOrder != null) {
                    List<Integer> got = toIntList(mPreOrder.invoke(tree));
                    check(refPre.equals(got), "case" + ci + " preOrder() " + clip(got) + " != " + clip(refPre));
                }
                if (tree instanceof Iterable) {
                    List<Integer> got = new ArrayList<>();
                    for (Object o : (Iterable<?>) tree) got.add(((Number) o).intValue());
                    check(expSorted.equals(got), "case" + ci + " iterator " + clip(got) + " != " + clip(expSorted));
                }
            } catch (Throwable t) {
                fail("case" + ci + " query threw " + deep(t));
            }
        }
    }

    /** returns real height; records first violation. */
    static int walk(Object n, List<Integer> in, List<Integer> pre, String[] viol, int depth) throws Exception {
        if (n == null) return 0;
        if (depth > 200) throw new IllegalStateException("tree depth > 200 (cycle?)");
        int key = ((Number) fKey.get(n)).intValue();
        pre.add(key);
        int hl = walk(fLeft.get(n), in, pre, viol, depth + 1);
        in.add(key);
        int hr = walk(fRight.get(n), in, pre, viol, depth + 1);
        int h = 1 + Math.max(hl, hr);
        if (viol[0] == null && Math.abs(hl - hr) > 1)
            viol[0] = "AVL balance violated at key " + key + " (hL=" + hl + ", hR=" + hr + ")";
        if (viol[0] == null && fHeight != null) {
            int stored = ((Number) fHeight.get(n)).intValue();
            if (stored != h) viol[0] = "stored height " + stored + " != real height " + h + " at key " + key;
        }
        return h;
    }

    static void checkDemo(List<Class<?>> all) {
        Method main = null;
        for (int pass = 0; pass < 2 && main == null; pass++) {
            for (Class<?> c : all) {
                if (c.getSimpleName().endsWith("Test")) continue;
                if (pass == 0 && !c.getSimpleName().equals("Main")) continue;
                try {
                    Method m = c.getDeclaredMethod("main", String[].class);
                    if (Modifier.isStatic(m.getModifiers())) { main = m; break; }
                } catch (NoSuchMethodException ignored) {}
            }
        }
        checks++;
        if (main == null) { fail("no demo main found"); return; }
        String out = capture(main);
        List<String> toks = Arrays.asList(out.trim().split("\\s+"));
        System.out.println("demo output: " + out.trim().replace("\n", "\\n"));
        if (!toks.equals(Arrays.asList("30", "20", "10", "25", "40", "50"))) {
            failed++;
            reasons.add("demo printed '" + out.trim() + "' expected preorder '30 20 10 25 40 50'");
            System.out.println("CHECK FAIL " + reasons.get(reasons.size() - 1));
        }
    }

    // -------------------------------------------------------------- discovery

    static boolean discover(List<Class<?>> all) {
        // Mode A: static Node insert(Node, key)
        for (Class<?> c : all) {
            if (c.getSimpleName().endsWith("Test")) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || !Modifier.isStatic(m.getModifiers())) continue;
                if (!m.getName().toLowerCase().startsWith("insert")) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 2 && m.getReturnType() == p[0] && isNodeClass(p[0])
                        && (p[1] == int.class || p[1] == Integer.class || p[1] == Object.class || p[1] == Comparable.class)) {
                    m.setAccessible(true);
                    staticInsert = m;
                    return bindNode(p[0]);
                }
            }
        }
        // Mode B: instance tree
        Class<?> best = null;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || c.getSimpleName().endsWith("Test")) continue;
            Constructor<?> ct;
            try { ct = c.getDeclaredConstructor(); } catch (NoSuchMethodException e) { continue; }
            Method ins = null;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                String n = m.getName().toLowerCase();
                if ((n.equals("insert") || n.equals("add")) && m.getParameterCount() == 1
                        && !isNodeClass(m.getParameterTypes()[0])) {
                    if (ins == null || Modifier.isPublic(m.getModifiers())) ins = m;
                }
            }
            if (ins == null) continue;
            Field rf = null;
            for (Field f : c.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers()) && isNodeClass(f.getType())) { rf = f; break; }
            }
            if (rf == null) continue;
            if (best == null || c.getSimpleName().toLowerCase().contains("avl")) {
                best = c; treeCtor = ct; instInsert = ins; rootField = rf;
            }
        }
        if (best == null) {
            System.out.println("RESULT FAIL no AVL entry point found (static insert(Node,key) or tree with insert(key) + root)");
            return false;
        }
        treeClass = best;
        treeCtor.setAccessible(true); instInsert.setAccessible(true); rootField.setAccessible(true);
        for (Method m : best.getMethods()) {
            if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
            String n = m.getName();
            if (n.equals("size") && m.getParameterCount() == 0) mSize = m;
            if (n.equals("contains") && m.getParameterCount() == 1) mContains = m;
            if (n.equalsIgnoreCase("inOrder") && m.getParameterCount() == 0 && List.class.isAssignableFrom(m.getReturnType())) mInOrder = m;
            if (n.equalsIgnoreCase("preOrder") && m.getParameterCount() == 0 && List.class.isAssignableFrom(m.getReturnType())) mPreOrder = m;
        }
        return bindNode(rootField.getType());
    }

    static boolean isNodeClass(Class<?> c) {
        if (c.isPrimitive() || c.isArray() || c.getName().startsWith("java.")) return false;
        int self = 0;
        for (Field f : c.getDeclaredFields()) if (f.getType() == c) self++;
        return self >= 2;
    }

    static boolean bindNode(Class<?> nc) {
        nodeClass = nc;
        Field key = null;
        for (String want : new String[]{"key", "value", "data", "val"}) {
            for (Field f : nc.getDeclaredFields()) {
                if (f.getName().equalsIgnoreCase(want) && f.getType() != nc) { key = f; break; }
            }
            if (key != null) break;
        }
        for (Field f : nc.getDeclaredFields()) {
            String n = f.getName().toLowerCase();
            if (f.getType() == nc && n.startsWith("left")) fLeft = f;
            if (f.getType() == nc && n.startsWith("right")) fRight = f;
            if ((f.getType() == int.class) && n.startsWith("height")) fHeight = f;
        }
        fKey = key;
        if (fKey == null || fLeft == null || fRight == null) {
            System.out.println("RESULT FAIL could not bind node fields of " + nc.getName());
            return false;
        }
        fKey.setAccessible(true); fLeft.setAccessible(true); fRight.setAccessible(true);
        if (fHeight != null) fHeight.setAccessible(true);
        return true;
    }

    // ------------------------------------------------------ reference AVL tree

    static final class Ref {
        int key, h = 1;
        Ref l, r;
        Ref(int k) { key = k; }
        static int h(Ref n) { return n == null ? 0 : n.h; }
        static void upd(Ref n) { n.h = 1 + Math.max(h(n.l), h(n.r)); }
        static Ref rotR(Ref y) { Ref x = y.l; y.l = x.r; x.r = y; upd(y); upd(x); return x; }
        static Ref rotL(Ref x) { Ref y = x.r; x.r = y.l; y.l = x; upd(x); upd(y); return y; }
        static Ref insert(Ref n, int k) {
            if (n == null) return new Ref(k);
            if (k < n.key) n.l = insert(n.l, k);
            else if (k > n.key) n.r = insert(n.r, k);
            else return n;
            upd(n);
            int b = h(n.l) - h(n.r);
            if (b > 1) { if (k > n.l.key) n.l = rotL(n.l); return rotR(n); }
            if (b < -1) { if (k < n.r.key) n.r = rotR(n.r); return rotL(n); }
            return n;
        }
        static void pre(Ref n, List<Integer> out) {
            if (n == null) return;
            out.add(n.key); pre(n.l, out); pre(n.r, out);
        }
    }

    // ---------------------------------------------------------------- helpers

    static void check(boolean ok, String msg) {
        checks++;
        if (!ok) fail(msg);
    }

    static void fail(String msg) {
        failed++;
        reasons.add(msg);
        System.out.println("CHECK FAIL " + msg);
    }

    static List<Integer> toIntList(Object o) {
        List<Integer> out = new ArrayList<>();
        for (Object x : (Iterable<?>) o) out.add(((Number) x).intValue());
        return out;
    }

    static String clip(List<Integer> l) {
        String s = l.toString();
        return s.length() > 120 ? s.substring(0, 117) + "..." : s;
    }

    static String capture(Method main) {
        PrintStream old = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buf, true, "UTF-8"));
            main.setAccessible(true);
            main.invoke(null, (Object) new String[0]);
        } catch (Throwable t) {
            System.setOut(old);
            return "<threw " + deep(t) + ">";
        } finally {
            System.out.flush();
            System.setOut(old);
        }
        return buf.toString(java.nio.charset.StandardCharsets.UTF_8);
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
