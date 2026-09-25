import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 013_binary_search_tree_searching.
 *
 * Baseline: static BinarySearchTree.search(Node root, int key) -> boolean over
 * a hand-built Node(data,left,right) tree; equal -> found, key > data -> go
 * right, else left. Iterations 1..10 replace this with an instance
 * BinarySearchTree<T> exposing insert(T) and contains(T) (later iterations
 * add comparators, AVL rebalancing, iterators — same set semantics).
 *
 * Oracle: a HashSet of the inserted keys. Every inserted key must be found;
 * probes outside the set must be misses; the empty tree contains nothing.
 *
 * Mode A (instance): discover class with insert(1-arg) + boolean
 * contains/search(1-arg); insert Integers, query hits and misses.
 * Mode B (baseline): discover static boolean search(Node, int); build the
 * tree via reflection over Node's int field and left/right links using
 * standard BST insertion (distinct keys, < left / > right).
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        // ---- Mode A: instance tree with insert + contains -----------------
        for (String cn : classNames) {
            if (cn.contains("$")) continue;
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            if (c.getSimpleName().toLowerCase().contains("test")) continue;
            Method ins = null, has = null;
            for (Method m : c.getDeclaredMethods()) {
                if (Modifier.isStatic(m.getModifiers())) continue;
                String n = m.getName().toLowerCase();
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 1) continue;
                if ((n.equals("insert") || n.equals("add") || n.equals("put")) && ins == null) ins = m;
                if ((n.equals("contains") || n.equals("search") || n.equals("find"))
                        && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)
                        && has == null) has = m;
            }
            if (ins == null || has == null) continue;
            Constructor<?> k;
            try { k = c.getDeclaredConstructor(); } catch (Throwable t) { continue; }
            k.setAccessible(true); ins.setAccessible(true); has.setAccessible(true);
            System.out.println("mode A: " + c.getName() + "." + ins.getName() + "/" + has.getName());
            runInstanceMode(k, ins, has);
            return;
        }

        // ---- Mode B: static search(Node, int) over a hand-built tree ------
        Method search = null;
        Class<?> nodeClass = null;
        for (String cn : classNames) {
            if (cn.contains("$")) continue;
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            for (Method m : c.getDeclaredMethods()) {
                String n = m.getName().toLowerCase();
                Class<?>[] p = m.getParameterTypes();
                if ((n.contains("search") || n.contains("contains") || n.contains("find"))
                        && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)
                        && p.length == 2 && p[1] == int.class && !p[0].isPrimitive() && !p[0].isArray()) {
                    search = m;
                    nodeClass = p[0];
                    break;
                }
            }
            if (search != null) break;
        }
        if (search == null) {
            System.out.println("RESULT FAIL no BST search entry point (insert+contains instance, or static search(Node,int)) among " + classNames);
            return;
        }
        search.setAccessible(true);
        System.out.println("mode B: " + search);
        runStaticMode(search, nodeClass);
    }

    // ------------------------------------------------------------------
    static void runInstanceMode(Constructor<?> ctor, Method insert, Method contains) throws Exception {
        int checks = 0, failed = 0;
        Random rnd = new Random(1313L);

        // empty tree
        Object empty = ctor.newInstance();
        for (int probe : new int[]{0, -5, 7, Integer.MAX_VALUE}) {
            checks++;
            Object r = contains.invoke(empty, Integer.valueOf(probe));
            if (Boolean.TRUE.equals(r)) {
                failed++;
                System.out.println("CHECK FAIL empty tree contains(" + probe + ") = true");
            }
        }

        for (int trial = 0; trial < 5; trial++) {
            Object tree = ctor.newInstance();
            Set<Integer> oracle = new TreeSet<>();
            int n = trial == 0 ? 5 : 40 + rnd.nextInt(60);
            List<Integer> inserted = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int v = rnd.nextInt(2000) - 1000;
                insert.invoke(tree, Integer.valueOf(v));
                oracle.add(v);
                inserted.add(v);
            }
            // re-insert some duplicates: set semantics must be unaffected
            for (int i = 0; i < Math.min(5, inserted.size()); i++) {
                insert.invoke(tree, inserted.get(i));
            }
            for (int v : oracle) {                       // hits
                checks++;
                if (!Boolean.TRUE.equals(contains.invoke(tree, Integer.valueOf(v)))) {
                    failed++;
                    if (failed <= 8) System.out.println("CHECK FAIL trial " + trial + ": inserted " + v + " but contains=false");
                }
            }
            for (int q = 0; q < 200; q++) {              // random probes
                int v = rnd.nextInt(6000) - 3000;
                checks++;
                boolean want = oracle.contains(v);
                if (!Boolean.valueOf(want).equals(contains.invoke(tree, Integer.valueOf(v)))) {
                    failed++;
                    if (failed <= 8) System.out.println("CHECK FAIL trial " + trial + ": contains(" + v + ") != " + want);
                }
            }
        }
        finish(checks, failed);
    }

    // ------------------------------------------------------------------
    static void runStaticMode(Method search, Class<?> nodeClass) throws Exception {
        // Locate Node internals: an int key field, left/right links, (int) ctor.
        Constructor<?> nodeCtor = null;
        for (Constructor<?> k : nodeClass.getDeclaredConstructors()) {
            if (k.getParameterCount() == 1 && k.getParameterTypes()[0] == int.class) { nodeCtor = k; break; }
        }
        Field keyField = null, leftField = null, rightField = null;
        for (Field f : nodeClass.getDeclaredFields()) {
            String n = f.getName().toLowerCase();
            if (f.getType() == int.class && (keyField == null || n.contains("data") || n.contains("key") || n.contains("val")))
                keyField = f;
            if (f.getType() == nodeClass && n.contains("left")) leftField = f;
            if (f.getType() == nodeClass && n.contains("right")) rightField = f;
        }
        if (nodeCtor == null || keyField == null || leftField == null || rightField == null) {
            System.out.println("RESULT FAIL found search method " + search + " but cannot introspect node type " + nodeClass);
            return;
        }
        nodeCtor.setAccessible(true); keyField.setAccessible(true);
        leftField.setAccessible(true); rightField.setAccessible(true);

        int checks = 0, failed = 0;
        Random rnd = new Random(1313L);

        // null root: nothing is present
        for (int probe : new int[]{0, -5, 7}) {
            checks++;
            if (Boolean.TRUE.equals(search.invoke(null, null, probe))) {
                failed++;
                System.out.println("CHECK FAIL search(null, " + probe + ") = true");
            }
        }

        for (int trial = 0; trial < 5; trial++) {
            Set<Integer> oracle = new LinkedHashSet<>();
            int n = trial == 0 ? 5 : 40 + rnd.nextInt(60);
            while (oracle.size() < n) oracle.add(rnd.nextInt(2000) - 1000);  // distinct keys
            Object root = null;
            for (int v : oracle) root = bstInsert(root, v, nodeCtor, keyField, leftField, rightField);

            for (int v : oracle) {
                checks++;
                if (!Boolean.TRUE.equals(search.invoke(null, root, v))) {
                    failed++;
                    if (failed <= 8) System.out.println("CHECK FAIL trial " + trial + ": inserted " + v + " but search=false");
                }
            }
            for (int q = 0; q < 200; q++) {
                int v = rnd.nextInt(6000) - 3000;
                checks++;
                boolean want = oracle.contains(v);
                if (!Boolean.valueOf(want).equals(search.invoke(null, root, v))) {
                    failed++;
                    if (failed <= 8) System.out.println("CHECK FAIL trial " + trial + ": search(" + v + ") != " + want);
                }
            }
        }
        finish(checks, failed);
    }

    /** Standard BST insertion over the subject's own Node type (distinct keys). */
    static Object bstInsert(Object root, int v, Constructor<?> ctor,
                            Field key, Field left, Field right) throws Exception {
        if (root == null) return ctor.newInstance(v);
        Object cur = root;
        while (true) {
            int k = key.getInt(cur);
            if (v == k) return root;
            Field dir = v < k ? left : right;
            Object child = dir.get(cur);
            if (child == null) { dir.set(cur, ctor.newInstance(v)); return root; }
            cur = child;
        }
    }

    static void finish(int checks, int failed) {
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " membership checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " membership checks wrong");
    }
}
