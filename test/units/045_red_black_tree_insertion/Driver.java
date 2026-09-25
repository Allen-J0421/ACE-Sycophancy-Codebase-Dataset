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
 * Correctness driver for 045_red_black_tree_insertion.
 *
 * Baseline semantics (iteration 0, red_black_tree_insertion.java): class
 * RedBlackTree with a no-arg ctor, {@code void insert(int)}, a public root of
 * inner type Node{int data; Node left,right,parent; char colour 'R'/'B'},
 * {@code inorderTraversal()} printing "%d " per key and {@code printTree()}
 * printing a sideways tree. Duplicates are KEPT (equal keys go to the right
 * subtree). The algorithm is the recursive GfG variant (flags ll/rr/lr/rl set
 * in a child frame, consumed by the parent frame).
 *
 * Iterations 1+ move to package rbtree/ (Color enum, Node, RedBlackTree,
 * TreePrinter, Main), later generic RedBlackTree<T extends Comparable<T>>
 * with insert(T), private root, toSortedList().
 *
 * Discovery (reflection): the instantiable class with a no-arg ctor, an
 * instance insert(key) and a field whose type is a node class (two self-typed
 * fields + a color field). Colours are read as char 'R'/'B', an enum RED/BLACK,
 * or a boolean red/black flag.
 *
 * Oracle (independent):
 *   - inorder of the node structure == sorted inserted keys (duplicates kept);
 *   - root is black, no red node has a red child, equal black-height on all
 *     root-to-null paths;
 *   - exact shape + colours == a reference port of the baseline algorithm (the
 *     shape is observable through the baseline's printTree output);
 *   - inorderTraversal()/printTree() stdout and toSortedList() (when present)
 *     match what the reference tree implies;
 *   - the demo program's full stdout equals the baseline demo output.
 * Fixed seeds; demo + edge cases + 40 random sequences (some with duplicates).
 */
public class Driver {

    static int checks = 0, failed = 0;
    static List<String> reasons = new ArrayList<>();

    static Class<?> treeClass;
    static Constructor<?> treeCtor;
    static Method mInsert, mInorderPrint, mPrintTree, mSortedList;
    static Field rootField;
    static Class<?> nodeClass;
    static Field fKey, fLeft, fRight, fColor;

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        List<Class<?>> all = scan(work.resolve("_classes"));
        if (!discover(all)) return;
        System.out.println("tree: " + treeClass.getName() + " insert=" + mInsert + " root=" + rootField.getName());
        System.out.println("node: " + nodeClass.getName() + " key=" + fKey.getName() + " left=" + fLeft.getName()
                + " right=" + fRight.getName() + " color=" + fColor.getName() + ":" + fColor.getType().getSimpleName());

        int[] demo = {1, 4, 6, 3, 5, 7, 8, 2, 9};
        List<int[]> cases = new ArrayList<>();
        cases.add(demo);
        cases.add(new int[]{42});
        cases.add(new int[]{5, 5, 5, 5, 5, 5});
        cases.add(new int[]{3, 2, 1});
        cases.add(new int[]{1, 2, 3});
        cases.add(new int[]{3, 1, 2});
        cases.add(new int[]{1, 3, 2});
        cases.add(IntStream.rangeClosed(1, 200).toArray());
        cases.add(IntStream.iterate(300, i -> i - 1).limit(200).toArray());
        cases.add(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1, Integer.MAX_VALUE, Integer.MIN_VALUE});
        cases.add(new int[]{10, 20, 30, 15, 25, 5, 1, 7, 18, 16, 17, 40, 50, 45, 44});
        Random rnd = new Random(4505);
        for (int c = 0; c < 40; c++) {
            int n = 1 + rnd.nextInt(c < 10 ? 20 : 300);
            int range = (c % 3 == 0) ? Math.max(2, n / 3) : 10 * n + 10;
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(range) - range / 2;
            cases.add(a);
        }

        // empty tree
        try {
            Object t = treeCtor.newInstance();
            check(rootField.get(t) == null, "empty tree has non-null root");
            if (mSortedList != null) check(((List<?>) mSortedList.invoke(t)).isEmpty(), "empty toSortedList not empty");
            if (mInorderPrint != null) check(capture(() -> mInorderPrint.invoke(t)).isEmpty(), "empty inorderTraversal printed output");
        } catch (Throwable t) {
            fail("empty tree threw " + deep(t));
        }

        for (int ci = 0; ci < cases.size() && failed < 10; ci++) runCase(ci, cases.get(ci));

        checkDemo(all, demo);

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks over " + cases.size() + " insert sequences"
                : "RESULT FAIL " + failed + "/" + checks + " checks: " + String.join("; ", reasons.subList(0, Math.min(3, reasons.size()))));
    }

    static void runCase(int ci, int[] keys) {
        Object tree;
        try {
            tree = treeCtor.newInstance();
            for (int k : keys) mInsert.invoke(tree, k);
        } catch (Throwable t) {
            fail("case" + ci + " insert threw " + deep(t));
            return;
        }
        List<Integer> sorted = new ArrayList<>();
        for (int k : keys) sorted.add(k);
        Collections.sort(sorted);

        Ref ref = new Ref();
        for (int k : keys) ref.insert(k);
        String refShape = ref.shape(ref.root);

        Object root;
        List<Integer> in = new ArrayList<>();
        StringBuilder shape = new StringBuilder();
        String[] viol = {null};
        try {
            root = rootField.get(tree);
            walk(root, in, viol, 0);
            shape.append(shape(root));
        } catch (Throwable t) {
            fail("case" + ci + " reading node structure threw " + deep(t));
            return;
        }
        check(in.equals(sorted), "case" + ci + " inorder " + clip(in.toString()) + " != sorted keys " + clip(sorted.toString()));
        try {
            check(root == null || !isRed(root), "case" + ci + " root is red");
        } catch (Throwable t) { fail("case" + ci + " color read threw " + deep(t)); }
        check(viol[0] == null, "case" + ci + " " + viol[0]);
        check(shape.toString().equals(refShape), "case" + ci + " shape/colours " + clip(shape.toString())
                + " != baseline algorithm " + clip(refShape));

        try {
            if (mSortedList != null) {
                List<Integer> got = new ArrayList<>();
                for (Object o : (List<?>) mSortedList.invoke(tree)) got.add(((Number) o).intValue());
                check(got.equals(sorted), "case" + ci + " toSortedList " + clip(got.toString()));
            }
            if (mInorderPrint != null) {
                String got = capture(() -> mInorderPrint.invoke(tree));
                StringBuilder exp = new StringBuilder();
                for (int k : sorted) exp.append(k).append(' ');
                check(got.equals(exp.toString()), "case" + ci + " inorderTraversal printed '" + clip(got) + "'");
            }
            if (mPrintTree != null) {
                String got = capture(() -> mPrintTree.invoke(tree));
                String exp = ref.print();
                check(got.equals(exp), "case" + ci + " printTree output differs from baseline rendering");
            }
        } catch (Throwable t) {
            fail("case" + ci + " query threw " + deep(t));
        }
    }

    /** returns black height (counting the null leaf as 1). */
    static int walk(Object n, List<Integer> in, String[] viol, int depth) throws Exception {
        if (n == null) return 1;
        if (depth > 200) throw new IllegalStateException("tree depth > 200 (cycle?)");
        Object l = fLeft.get(n), r = fRight.get(n);
        int bl = walk(l, in, viol, depth + 1);
        in.add(key(n));
        int br = walk(r, in, viol, depth + 1);
        boolean red = isRed(n);
        if (viol[0] == null && red && ((l != null && isRed(l)) || (r != null && isRed(r))))
            viol[0] = "red-red violation at key " + key(n);
        if (viol[0] == null && bl != br)
            viol[0] = "black-height mismatch at key " + key(n) + " (" + bl + " vs " + br + ")";
        return bl + (red ? 0 : 1);
    }

    static String shape(Object n) throws Exception {
        if (n == null) return ".";
        return "(" + key(n) + (isRed(n) ? "R" : "B") + " " + shape(fLeft.get(n)) + " " + shape(fRight.get(n)) + ")";
    }

    static int key(Object n) throws Exception { return ((Number) fKey.get(n)).intValue(); }

    static boolean isRed(Object n) throws Exception {
        Object c = fColor.get(n);
        if (c instanceof Character) {
            char ch = Character.toUpperCase((Character) c);
            if (ch != 'R' && ch != 'B') throw new IllegalStateException("unknown colour char " + ch);
            return ch == 'R';
        }
        if (c instanceof Enum) {
            String nm = ((Enum<?>) c).name().toUpperCase();
            if (nm.startsWith("R")) return true;
            if (nm.startsWith("B")) return false;
            throw new IllegalStateException("unknown colour " + nm);
        }
        if (c instanceof Boolean) {
            boolean b = (Boolean) c;
            return fColor.getName().toLowerCase().contains("black") ? !b : b;
        }
        throw new IllegalStateException("unreadable colour " + c);
    }

    static void checkDemo(List<Class<?>> all, int[] demo) {
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
        if (main == null) { checks++; fail("no demo main found"); return; }
        Method fm = main;
        String got;
        try { got = capture(() -> fm.invoke(null, (Object) new String[0])); }
        catch (Throwable t) { checks++; fail("demo main threw " + deep(t)); return; }
        // baseline demo: after each insert println() + inorder "%d "; finally printTree
        StringBuilder exp = new StringBuilder();
        Ref ref = new Ref();
        List<Integer> sofar = new ArrayList<>();
        for (int k : demo) {
            ref.insert(k);
            sofar.add(k);
            Collections.sort(sofar);
            exp.append(System.lineSeparator());
            for (int v : sofar) exp.append(v).append(' ');
        }
        exp.append(ref.print());
        System.out.println("demo output: " + got.replace("\n", "\\n"));
        check(got.replace("\r\n", "\n").equals(exp.toString().replace("\r\n", "\n")),
                "demo output differs from baseline demo output");
    }

    // -------------------------------------------------------------- discovery

    static boolean discover(List<Class<?>> all) throws Exception {
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
            if (treeClass == null || c.getSimpleName().toLowerCase().contains("black")) {
                treeClass = c; treeCtor = ct; mInsert = ins; rootField = rf;
            }
        }
        if (treeClass == null) {
            System.out.println("RESULT FAIL no red-black tree entry point (no-arg ctor + insert(key) + node root) found");
            return false;
        }
        treeCtor.setAccessible(true); mInsert.setAccessible(true); rootField.setAccessible(true);
        for (Method m : treeClass.getDeclaredMethods()) {
            if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 0) continue;
            String n = m.getName().toLowerCase();
            if (n.startsWith("inorder") && m.getReturnType() == void.class) mInorderPrint = m;
            if (n.equals("printtree") && m.getReturnType() == void.class) mPrintTree = m;
            if ((n.equals("tosortedlist") || n.equals("inorder") || n.equals("tolist"))
                    && List.class.isAssignableFrom(m.getReturnType())) mSortedList = m;
        }
        for (Method m : new Method[]{mInorderPrint, mPrintTree, mSortedList}) if (m != null) m.setAccessible(true);

        nodeClass = rootField.getType();
        for (String want : new String[]{"data", "key", "value", "val"}) {
            for (Field f : nodeClass.getDeclaredFields())
                if (fKey == null && f.getName().equalsIgnoreCase(want) && f.getType() != nodeClass) fKey = f;
        }
        for (Field f : nodeClass.getDeclaredFields()) {
            String n = f.getName().toLowerCase();
            if (f.getType() == nodeClass && n.startsWith("left")) fLeft = f;
            if (f.getType() == nodeClass && n.startsWith("right")) fRight = f;
            if (fColor == null && (n.startsWith("colo") || n.equals("red") || n.equals("isred") || n.equals("black")))
                fColor = f;
        }
        if (fKey == null || fLeft == null || fRight == null || fColor == null) {
            System.out.println("RESULT FAIL could not bind node fields of " + nodeClass.getName());
            return false;
        }
        fKey.setAccessible(true); fLeft.setAccessible(true); fRight.setAccessible(true); fColor.setAccessible(true);
        return true;
    }

    static boolean isNodeClass(Class<?> c) {
        if (c.isPrimitive() || c.isArray() || c.getName().startsWith("java.")) return false;
        int self = 0;
        for (Field f : c.getDeclaredFields()) if (f.getType() == c) self++;
        return self >= 2;
    }

    // ---------------------------------------- reference: baseline algorithm port

    static final class Ref {
        final class N {
            int data; N left, right, parent; boolean red = true;
            N(int d) { data = d; }
        }
        N root;
        boolean ll, rr, lr, rl;

        N rotL(N node) { N x = node.right, y = x.left; x.left = node; node.right = y; node.parent = x; if (y != null) y.parent = node; return x; }
        N rotR(N node) { N x = node.left, y = x.right; x.right = node; node.left = y; node.parent = x; if (y != null) y.parent = node; return x; }

        N help(N r, int data) {
            boolean f = false;
            if (r == null) return new N(data);
            if (data < r.data) {
                r.left = help(r.left, data); r.left.parent = r;
                if (r != root && r.red && r.left.red) f = true;
            } else {
                r.right = help(r.right, data); r.right.parent = r;
                if (r != root && r.red && r.right.red) f = true;
            }
            if (ll) { r = rotL(r); r.red = false; r.left.red = true; ll = false; }
            else if (rr) { r = rotR(r); r.red = false; r.right.red = true; rr = false; }
            else if (rl) { r.right = rotR(r.right); r.right.parent = r; r = rotL(r); r.red = false; r.left.red = true; rl = false; }
            else if (lr) { r.left = rotL(r.left); r.left.parent = r; r = rotR(r); r.red = false; r.right.red = true; lr = false; }
            if (f) {
                if (r.parent.right == r) {
                    if (r.parent.left == null || !r.parent.left.red) {
                        if (r.left != null && r.left.red) rl = true;
                        else if (r.right != null && r.right.red) ll = true;
                    } else {
                        r.parent.left.red = false; r.red = false;
                        if (r.parent != root) r.parent.red = true;
                    }
                } else {
                    if (r.parent.right == null || !r.parent.right.red) {
                        if (r.left != null && r.left.red) rr = true;
                        else if (r.right != null && r.right.red) lr = true;
                    } else {
                        r.parent.right.red = false; r.red = false;
                        if (r.parent != root) r.parent.red = true;
                    }
                }
            }
            return r;
        }

        void insert(int d) {
            if (root == null) { root = new N(d); root.red = false; }
            else root = help(root, d);
        }

        String shape(N n) {
            if (n == null) return ".";
            return "(" + n.data + (n.red ? "R" : "B") + " " + shape(n.left) + " " + shape(n.right) + ")";
        }

        String print() { StringBuilder sb = new StringBuilder(); print(root, 0, sb); return sb.toString(); }
        void print(N n, int space, StringBuilder sb) {
            if (n == null) return;
            space += 10;
            print(n.right, space, sb);
            sb.append('\n');
            for (int i = 10; i < space; i++) sb.append(' ');
            sb.append(n.data).append('\n');
            print(n.left, space, sb);
        }
    }

    // ---------------------------------------------------------------- helpers

    interface Act { void run() throws Throwable; }

    static String capture(Act a) throws Throwable {
        PrintStream old = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buf, true, "UTF-8");
        System.setOut(ps);
        try {
            a.run();
        } finally {
            ps.flush();
            System.setOut(old);
        }
        return buf.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    static void check(boolean ok, String msg) {
        checks++;
        if (!ok) fail(msg);
    }

    static void fail(String msg) {
        failed++;
        reasons.add(msg);
        System.out.println("CHECK FAIL " + msg);
    }

    static String clip(String s) {
        s = s.replace("\n", "\\n");
        return s.length() > 140 ? s.substring(0, 137) + "..." : s;
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
