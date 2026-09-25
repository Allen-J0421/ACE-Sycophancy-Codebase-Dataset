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
 * Correctness driver for 046_b_tree.
 *
 * Baseline semantics (iteration 0, b_tree.java): CLRS B-tree of ints with
 * MINIMUM DEGREE t passed to {@code BTree(int t)}; every node holds at most
 * 2t-1 keys, every non-root node at least t-1; proactive split on the way down;
 * duplicates are KEPT. {@code insert(int)}, {@code search(int)} returning the
 * containing node or null, {@code traverse()} printing " k" per key in order.
 * Node: {@code int[] keys; int n; BTreeNode[] C; boolean leaf}.
 * Demo (Main): t=3, insert 10 20 5 6 12 30 7 17, print traversal, then
 * " | Present" (search 6) and " | Not Present" (search 15).
 *
 * Drift: iter 1 makes it generic BTree<K>(int minDegree) (rejects < 2) with
 * contains(K) and toSortedList(); node fields become K[] keys/keyCount/children;
 * iter 7 switches node storage to List<K>/List<Node>; iter 9 adds delete() and
 * extra demo lines. The ctor argument stays the minimum degree throughout.
 *
 * Discovery (reflection): the class with an (int) ctor, an instance insert(key)
 * and a field whose type is a node class (boolean leaf + key storage). Node
 * internals are read generically: keys (array+count or List), children
 * (array or List), leaf flag.
 *
 * Oracle (independent), for degrees t = 2,3,4,5:
 *   - in-order walk of the node structure == sorted inserted keys (dups kept);
 *   - node bounds: root 1..2t-1 keys, non-root t-1..2t-1 keys, internal node has
 *     keys+1 children, leaf flag consistent, all leaves at the same depth;
 *   - degree semantics: 2t-1 inserts fit in a single root, the 2t-th splits it;
 *   - search/contains hit for every inserted key, miss for absent keys;
 *   - public traversal (List-returning or printed) == sorted keys;
 *   - demo stdout starts with the baseline's three lines.
 * Exact node layout vs a reference CLRS B-tree is reported as NOTE only (not
 * externally observable in the baseline).
 */
public class Driver {

    static int checks = 0, failed = 0, notes = 0;
    static List<String> reasons = new ArrayList<>();

    static Class<?> treeClass;
    static Constructor<?> treeCtor;
    static Method mInsert, mSearch, mList, mTraversePrint;
    static Field rootField;
    static Class<?> nodeClass;
    static Field fKeys, fCount, fChildren, fLeaf;

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        List<Class<?>> all = scan(work.resolve("_classes"));
        if (!discover(all)) return;
        System.out.println("tree: " + treeClass.getName() + " ctor=" + treeCtor + " insert=" + mInsert
                + " search=" + mSearch + " list=" + mList + " traverse=" + mTraversePrint);
        System.out.println("node: " + nodeClass.getName() + " keys=" + fKeys.getName() + " count="
                + (fCount == null ? "-" : fCount.getName()) + " children=" + fChildren.getName() + " leaf=" + fLeaf.getName());

        int[] demo = {10, 20, 5, 6, 12, 30, 7, 17};
        int[] degrees = {2, 3, 4, 5};
        Random rnd = new Random(4606);
        int caseNo = 0;
        for (int t : degrees) {
            List<int[]> cases = new ArrayList<>();
            cases.add(demo);
            cases.add(new int[]{});
            cases.add(new int[]{42});
            cases.add(IntStream.rangeClosed(1, 2 * t - 1).toArray());       // exactly one full root
            cases.add(IntStream.rangeClosed(1, 2 * t).toArray());           // first root split
            cases.add(new int[]{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9});
            cases.add(IntStream.rangeClosed(1, 300).toArray());
            cases.add(IntStream.iterate(300, i -> i - 1).limit(300).toArray());
            cases.add(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1, Integer.MAX_VALUE, Integer.MIN_VALUE});
            for (int c = 0; c < 10; c++) {
                int n = 1 + rnd.nextInt(c < 3 ? 25 : 400);
                int range = (c % 3 == 0) ? Math.max(2, n / 3) : 10 * n + 10;
                int[] a = new int[n];
                for (int i = 0; i < n; i++) a[i] = rnd.nextInt(range) - range / 2;
                cases.add(a);
            }
            for (int ci = 0; ci < cases.size() && failed < 10; ci++) runCase("t" + t + "/case" + ci, t, cases.get(ci), ci);
            caseNo += cases.size();
        }

        checkDemo(all);

        System.out.println(checks + " checks, " + failed + " failed, " + notes + " layout notes");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks over " + caseNo + " insert sequences (t=2..5)"
                : "RESULT FAIL " + failed + "/" + checks + " checks: " + String.join("; ", reasons.subList(0, Math.min(3, reasons.size()))));
    }

    static void runCase(String id, int t, int[] keys, int ci) {
        Object tree;
        try {
            tree = treeCtor.newInstance(t);
            for (int k : keys) mInsert.invoke(tree, k);
        } catch (Throwable e) {
            fail(id + " insert threw " + deep(e));
            return;
        }
        List<Integer> sorted = new ArrayList<>();
        for (int k : keys) sorted.add(k);
        Collections.sort(sorted);

        Object root;
        List<Integer> in = new ArrayList<>();
        String[] viol = {null};
        int[] leafDepth = {-1};
        String layout;
        try {
            root = rootField.get(tree);
            if (root != null) walk(root, true, t, 0, in, viol, leafDepth);
            layout = root == null ? "." : layout(root);
        } catch (Throwable e) {
            fail(id + " reading node structure threw " + deep(e));
            return;
        }
        check(in.equals(sorted), id + " in-order keys " + clip(in.toString()) + " != sorted " + clip(sorted.toString()));
        check(viol[0] == null, id + " " + viol[0]);
        if (keys.length == 0) check(root == null || count(root) == 0, id + " empty tree has keys");

        // degree semantics (sequential 1..2t-1 then 1..2t)
        if (ci == 3) check(root != null && childList(root).isEmpty() && count(root) == 2 * t - 1,
                id + " 2t-1 keys should fit in one root node for degree " + t);
        if (ci == 4) check(root != null && count(root) == 1 && childList(root).size() == 2,
                id + " the 2t-th key should split the root for degree " + t);

        Ref ref = new Ref(t);
        for (int k : keys) ref.insert(k);
        String refLayout = ref.root == null ? "." : ref.layout(ref.root);
        if (!layout.equals(refLayout)) {
            notes++;
            if (notes <= 3) System.out.println("NOTE " + id + " node layout differs from reference CLRS B-tree: "
                    + clip(layout) + " vs " + clip(refLayout));
        }

        try {
            if (mSearch != null) {
                Set<Integer> present = new HashSet<>(sorted);
                Random r = new Random(ci * 131L + t);
                List<Integer> probes = new ArrayList<>(present);
                for (int q = 0; q < 25; q++) probes.add(r.nextInt(4000) - 2000);
                probes.add(Integer.MAX_VALUE); probes.add(Integer.MIN_VALUE);
                for (int k : probes) {
                    Object res = mSearch.invoke(tree, k);
                    boolean hit = res instanceof Boolean ? (Boolean) res : res != null;
                    check(hit == present.contains(k), id + " search(" + k + ") hit=" + hit + " expected " + present.contains(k));
                    if (failed >= 10) return;
                }
            }
            if (mList != null) {
                List<Integer> got = new ArrayList<>();
                for (Object o : (Iterable<?>) mList.invoke(tree)) got.add(((Number) o).intValue());
                check(got.equals(sorted), id + " " + mList.getName() + "() " + clip(got.toString()));
            }
            if (mTraversePrint != null) {
                String out = capture(() -> mTraversePrint.invoke(tree));
                List<Integer> got = new ArrayList<>();
                for (String s : out.trim().split("\\s+")) if (!s.isEmpty()) got.add(Integer.parseInt(s));
                check(got.equals(sorted), id + " traverse() printed " + clip(out));
            }
        } catch (Throwable e) {
            fail(id + " query threw " + deep(e));
        }
    }

    static void walk(Object n, boolean isRoot, int t, int depth, List<Integer> in, String[] viol, int[] leafDepth) throws Exception {
        if (depth > 64) throw new IllegalStateException("depth > 64 (cycle?)");
        int cnt = count(n);
        List<Object> keys = keyList(n);
        List<Object> kids = childList(n);
        boolean leaf = fLeaf.getBoolean(n);
        int lo = isRoot ? 1 : t - 1, hi = 2 * t - 1;
        if (viol[0] == null && (cnt < lo || cnt > hi))
            viol[0] = (isRoot ? "root" : "node") + " at depth " + depth + " has " + cnt + " keys, allowed " + lo + ".." + hi;
        if (viol[0] == null && leaf != kids.isEmpty())
            viol[0] = "leaf flag " + leaf + " inconsistent with " + kids.size() + " children at depth " + depth;
        if (viol[0] == null && !leaf && kids.size() != cnt + 1)
            viol[0] = "internal node with " + cnt + " keys has " + kids.size() + " children";
        if (leaf) {
            if (leafDepth[0] < 0) leafDepth[0] = depth;
            else if (viol[0] == null && leafDepth[0] != depth)
                viol[0] = "leaves at different depths " + leafDepth[0] + " and " + depth;
        }
        for (int i = 0; i < cnt; i++) {
            if (!leaf && i < kids.size()) walk(kids.get(i), false, t, depth + 1, in, viol, leafDepth);
            in.add(((Number) keys.get(i)).intValue());
        }
        if (!leaf && cnt < kids.size()) walk(kids.get(cnt), false, t, depth + 1, in, viol, leafDepth);
    }

    static String layout(Object n) throws Exception {
        StringBuilder sb = new StringBuilder("[");
        List<Object> keys = keyList(n);
        int cnt = count(n);
        for (int i = 0; i < cnt; i++) sb.append(i == 0 ? "" : ",").append(keys.get(i));
        List<Object> kids = childList(n);
        if (!kids.isEmpty()) {
            sb.append(" |");
            for (Object k : kids) sb.append(' ').append(layout(k));
        }
        return sb.append(']').toString();
    }

    static int count(Object n) {
        try {
            if (fCount != null) return fCount.getInt(n);
            return ((List<?>) fKeys.get(n)).size();
        } catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    static List<Object> keyList(Object n) throws Exception {
        Object v = fKeys.get(n);
        List<Object> out = new ArrayList<>();
        if (v instanceof List) out.addAll((List<?>) v);
        else for (int i = 0; i < java.lang.reflect.Array.getLength(v); i++) out.add(java.lang.reflect.Array.get(v, i));
        return out;
    }

    /** children actually in use (keys+1 for internal nodes using fixed arrays). */
    static List<Object> childList(Object n) {
        try {
            Object v = fChildren.get(n);
            List<Object> out = new ArrayList<>();
            if (v instanceof List) { out.addAll((List<?>) v); return out; }
            int len = java.lang.reflect.Array.getLength(v);
            if (fLeaf.getBoolean(n)) {
                // fixed array: a leaf must not reference children in its live range
                for (int i = 0; i <= count(n) && i < len; i++)
                    if (java.lang.reflect.Array.get(v, i) != null) out.add(java.lang.reflect.Array.get(v, i));
                return out;
            }
            for (int i = 0; i <= count(n) && i < len; i++) out.add(java.lang.reflect.Array.get(v, i));
            return out;
        } catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    static void checkDemo(List<Class<?>> all) {
        Method main = null;
        for (Class<?> c : all) {
            if (c.getSimpleName().endsWith("Test")) continue;
            try {
                Method m = c.getDeclaredMethod("main", String[].class);
                if (Modifier.isStatic(m.getModifiers()) && (main == null || c.getSimpleName().equals("Main"))) main = m;
            } catch (NoSuchMethodException ignored) {}
        }
        if (main == null) { checks++; fail("no demo main found"); return; }
        Method fm = main;
        fm.setAccessible(true);
        String out;
        try { out = capture(() -> fm.invoke(null, (Object) new String[0])); }
        catch (Throwable e) { checks++; fail("demo main threw " + deep(e)); return; }
        System.out.println("demo output: " + out.replace("\n", "\\n"));
        String[] lines = out.replace("\r\n", "\n").split("\n", -1);
        String[] exp = {"Traversal of the constructed tree is  5 6 7 10 12 17 20 30", " | Present", " | Not Present"};
        boolean ok = lines.length >= 3;
        for (int i = 0; ok && i < 3; i++) ok = lines[i].equals(exp[i]);
        check(ok, "demo output does not start with the baseline's three lines");
    }

    // -------------------------------------------------------------- discovery

    static boolean discover(List<Class<?>> all) throws Exception {
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || c.getSimpleName().endsWith("Test")) continue;
            Constructor<?> ct;
            try { ct = c.getDeclaredConstructor(int.class); } catch (NoSuchMethodException e) { continue; }
            Method ins = null;
            for (Method m : c.getDeclaredMethods()) {
                if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                if (m.getName().equalsIgnoreCase("insert") && m.getParameterCount() == 1
                        && !isNodeClass(m.getParameterTypes()[0])) ins = m;
            }
            if (ins == null) continue;
            Field rf = null;
            for (Field f : c.getDeclaredFields())
                if (!Modifier.isStatic(f.getModifiers()) && isNodeClass(f.getType())) rf = f;
            if (rf == null) continue;
            treeClass = c; treeCtor = ct; mInsert = ins; rootField = rf;
            break;
        }
        if (treeClass == null) {
            System.out.println("RESULT FAIL no B-tree entry point (ctor(int) + insert(key) + node root) found");
            return false;
        }
        treeCtor.setAccessible(true); mInsert.setAccessible(true); rootField.setAccessible(true);
        for (Method m : treeClass.getDeclaredMethods()) {
            if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
            String n = m.getName().toLowerCase();
            Class<?> r = m.getReturnType();
            if (m.getParameterCount() == 1 && (n.equals("search") || n.equals("contains") || n.equals("find"))
                    && (r == boolean.class || r == Boolean.class || isNodeClass(r)))
                if (mSearch == null || r == boolean.class) mSearch = m;
            if (m.getParameterCount() == 0 && List.class.isAssignableFrom(r)
                    && (n.contains("sorted") || n.contains("inorder") || n.contains("traverse") || n.equals("tolist") || n.equals("keys")))
                mList = m;
            if (m.getParameterCount() == 0 && r == void.class && n.equals("traverse")) mTraversePrint = m;
        }
        for (Method m : new Method[]{mSearch, mList, mTraversePrint}) if (m != null) m.setAccessible(true);

        nodeClass = rootField.getType();
        for (Field f : nodeClass.getDeclaredFields()) {
            String n = f.getName().toLowerCase();
            Class<?> ty = f.getType();
            if (n.contains("key") && (ty.isArray() || List.class.isAssignableFrom(ty))) fKeys = f;
            if ((ty.isArray() && ty.getComponentType() == nodeClass)
                    || (List.class.isAssignableFrom(ty) && (n.startsWith("child") || n.equals("c")))) fChildren = f;
            if ((ty == boolean.class) && n.contains("leaf")) fLeaf = f;
        }
        if (fKeys != null && fKeys.getType().isArray()) {
            for (String want : new String[]{"n", "keycount", "numkeys", "count", "size"})
                for (Field f : nodeClass.getDeclaredFields())
                    if (fCount == null && f.getType() == int.class && f.getName().equalsIgnoreCase(want)) fCount = f;
        }
        if (fKeys == null || fChildren == null || fLeaf == null || (fKeys.getType().isArray() && fCount == null)) {
            System.out.println("RESULT FAIL could not bind node fields of " + nodeClass.getName());
            return false;
        }
        fKeys.setAccessible(true); fChildren.setAccessible(true); fLeaf.setAccessible(true);
        if (fCount != null) fCount.setAccessible(true);
        return true;
    }

    static boolean isNodeClass(Class<?> c) {
        if (c.isPrimitive() || c.isArray() || c.getName().startsWith("java.")) return false;
        boolean leaf = false, store = false;
        for (Field f : c.getDeclaredFields()) {
            if (f.getType() == boolean.class && f.getName().toLowerCase().contains("leaf")) leaf = true;
            if (f.getType().isArray() || List.class.isAssignableFrom(f.getType())) store = true;
        }
        return leaf && store;
    }

    // -------------------------------------------- reference CLRS B-tree (layout)

    static final class Ref {
        final int t;
        RN root;
        Ref(int t) { this.t = t; }
        final class RN {
            List<Integer> keys = new ArrayList<>();
            List<RN> kids = new ArrayList<>();
            boolean leaf;
            RN(boolean leaf) { this.leaf = leaf; }
        }
        void insert(int k) {
            if (root == null) { root = new RN(true); root.keys.add(k); return; }
            if (root.keys.size() == 2 * t - 1) {
                RN s = new RN(false);
                s.kids.add(root);
                split(s, 0);
                root = s;
                // baseline: after a root split descend left unless keys[0] < k
                nonFull(s.kids.get(s.keys.get(0) < k ? 1 : 0), k);
                return;
            }
            nonFull(root, k);
        }
        void split(RN x, int i) {
            RN y = x.kids.get(i), z = new RN(y.leaf);
            z.keys.addAll(y.keys.subList(t, 2 * t - 1));
            if (!y.leaf) z.kids.addAll(y.kids.subList(t, 2 * t));
            int med = y.keys.get(t - 1);
            y.keys = new ArrayList<>(y.keys.subList(0, t - 1));
            if (!y.leaf) y.kids = new ArrayList<>(y.kids.subList(0, t));
            x.kids.add(i + 1, z);
            x.keys.add(i, med);
        }
        void nonFull(RN x, int k) {
            int i = x.keys.size() - 1;
            while (i >= 0 && x.keys.get(i) > k) i--;
            if (x.leaf) { x.keys.add(i + 1, k); return; }
            i++;
            if (x.kids.get(i).keys.size() == 2 * t - 1) {
                split(x, i);
                if (x.keys.get(i) < k) i++;
            }
            nonFull(x.kids.get(i), k);
        }
        String layout(RN n) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < n.keys.size(); i++) sb.append(i == 0 ? "" : ",").append(n.keys.get(i));
            if (!n.kids.isEmpty()) {
                sb.append(" |");
                for (RN k : n.kids) sb.append(' ').append(layout(k));
            }
            return sb.append(']').toString();
        }
    }

    // ---------------------------------------------------------------- helpers

    interface Act { void run() throws Throwable; }

    static String capture(Act a) throws Throwable {
        PrintStream old = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buf, true, "UTF-8");
        System.setOut(ps);
        try { a.run(); } finally { ps.flush(); System.setOut(old); }
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
