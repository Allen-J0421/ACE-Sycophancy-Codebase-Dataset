import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 015_open_addressing_linear_probing.
 *
 * Iteration 0: an int->int open-addressing hash map (fixed capacity 20) with
 *   insertNode(int,int), get(int) -> value or -1, deleteNode(int) -> removed
 *   value or -1, sizeofMap(), isEmpty().
 * Iterations 1+: a generic java.util.Map<K,V> (OpenAddressingHashMap).
 *
 * Oracle: java.util.HashMap driven with the identical randomized op sequence
 * (fixed seed), including overwrites, removals, forced collisions and resizes.
 */
public class Driver {

    /** Key type with a lousy hash to force collision clusters in generic mode. */
    public static final class ColKey {
        final int id;
        ColKey(int id) { this.id = id; }
        @Override public boolean equals(Object o) { return o instanceof ColKey k && k.id == id; }
        @Override public int hashCode() { return id % 3; }
        @Override public String toString() { return "ColKey(" + id + ")"; }
    }

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);

        // Preferred: a concrete java.util.Map implementation with a no-arg ctor.
        Class<?> mapClass = null;
        for (Class<?> c : all) {
            if (!java.util.Map.class.isAssignableFrom(c)) continue;
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            try { c.getDeclaredConstructor(); } catch (NoSuchMethodException e) { continue; }
            String n = c.getSimpleName().toLowerCase();
            if (mapClass == null || n.contains("open") || n.contains("hash")) mapClass = c;
        }
        if (mapClass != null) {
            System.out.println("generic Map subject: " + mapClass.getName());
            Constructor<?> mc = mapClass.getDeclaredConstructor();
            mc.setAccessible(true);
            // Same logical inputs as iteration 0: the baseline demo + int workload,
            // driven through the Map API (absent key -> -1, as in the baseline).
            int[] cf = new int[2];
            IntMapFactory f = () -> {
                @SuppressWarnings("unchecked")
                Map<Object, Object> m = (Map<Object, Object>) mc.newInstance();
                return new IntMap() {
                    public void insert(int k, int v) { m.put(k, v); }
                    public long get(int k) { Object o = m.get(k); return o == null ? -1 : ((Number) o).longValue(); }
                    public long delete(int k) { Object o = m.remove(k); return o == null ? -1 : ((Number) o).longValue(); }
                    public long size() { return m.size(); }
                    public boolean isEmpty() { return m.isEmpty(); }
                };
            };
            runIntWorkload(f, cf);
            runGeneric(mapClass, cf);
            return;
        }

        // Fallback: the baseline int map (insertNode/get/deleteNode).
        for (Class<?> c : all) {
            Method ins = find(c, new Class<?>[]{int.class, int.class}, "insert", "put");
            Method get = find(c, new Class<?>[]{int.class}, "get");
            Method del = find(c, new Class<?>[]{int.class}, "delete", "remove");
            if (ins != null && get != null && del != null) {
                System.out.println("baseline int-map subject: " + c.getName());
                Constructor<?> ctor = c.getDeclaredConstructor();
                ctor.setAccessible(true);
                ins.setAccessible(true); get.setAccessible(true); del.setAccessible(true);
                Method size = find(c, new Class<?>[]{}, "size");
                Method empty = find(c, new Class<?>[]{}, "empty");
                if (size == null || empty == null) { System.out.println("RESULT FAIL size/isEmpty not found"); return; }
                size.setAccessible(true); empty.setAccessible(true);
                IntMapFactory f = () -> {
                    Object o = ctor.newInstance();
                    return new IntMap() {
                        public void insert(int k, int v) throws Exception { ins.invoke(o, k, v); }
                        public long get(int k) throws Exception { return ((Number) get.invoke(o, k)).longValue(); }
                        public long delete(int k) throws Exception { return ((Number) del.invoke(o, k)).longValue(); }
                        public long size() throws Exception { return ((Number) size.invoke(o)).longValue(); }
                        public boolean isEmpty() throws Exception { return (Boolean) empty.invoke(o); }
                    };
                };
                int[] cf = new int[2];
                runIntWorkload(f, cf);
                finish(cf[0], cf[1]);
                return;
            }
        }
        System.out.println("RESULT FAIL no hash-map entry point found");
    }

    // ------------------------------------------------------------------ generic

    static void runGeneric(Class<?> mapClass, int[] cf) throws Exception {
        Constructor<?> ctor = mapClass.getDeclaredConstructor();
        ctor.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Object, Object> subject = (Map<Object, Object>) ctor.newInstance();
        Map<Object, Object> model = new HashMap<>();

        Random rnd = new Random(424242);
        List<Object> keyPool = new ArrayList<>();
        for (int i = 0; i < 40; i++) keyPool.add(i);                 // Integers, forces resizes
        for (int i = 0; i < 16; i++) keyPool.add(new ColKey(i));     // heavy collisions
        for (int i = 0; i < 8; i++) keyPool.add("key" + i);          // string keys

        int checks = cf[0], failed = cf[1];
        for (int op = 0; op < 1500 && failed < 8; op++) {
            Object k = keyPool.get(rnd.nextInt(keyPool.size()));
            int r = rnd.nextInt(100);
            checks++;
            try {
                if (op == 700) {                       // one mid-run clear
                    subject.clear(); model.clear();
                    if (!subject.isEmpty() || subject.size() != 0) {
                        failed++; System.out.println("CHECK FAIL not empty after clear()");
                    }
                } else if (r < 40) {
                    Object v = rnd.nextInt(10) == 0 ? null : rnd.nextInt(1000);
                    Object got = subject.put(k, v), exp = model.put(k, v);
                    if (!Objects.equals(got, exp)) {
                        failed++; System.out.println("CHECK FAIL put(" + k + "," + v + ") returned " + got + ", expected " + exp);
                    }
                } else if (r < 65) {
                    Object got = subject.get(k), exp = model.get(k);
                    if (!Objects.equals(got, exp)) {
                        failed++; System.out.println("CHECK FAIL get(" + k + ") = " + got + ", expected " + exp);
                    }
                } else if (r < 80) {
                    Object got = subject.remove(k), exp = model.remove(k);
                    if (!Objects.equals(got, exp)) {
                        failed++; System.out.println("CHECK FAIL remove(" + k + ") returned " + got + ", expected " + exp);
                    }
                } else if (r < 90) {
                    boolean got = subject.containsKey(k), exp = model.containsKey(k);
                    if (got != exp) {
                        failed++; System.out.println("CHECK FAIL containsKey(" + k + ") = " + got + ", expected " + exp);
                    }
                } else {
                    if (subject.size() != model.size()) {
                        failed++; System.out.println("CHECK FAIL size() = " + subject.size() + ", expected " + model.size());
                    }
                }
            } catch (Throwable t) {
                failed++;
                System.out.println("CHECK FAIL op#" + op + " key=" + k + " threw " + deep(t));
            }
        }

        // Final structural comparison via entrySet iteration.
        checks++;
        try {
            Map<Object, Object> dumped = new HashMap<>();
            for (Map.Entry<Object, Object> e : subject.entrySet()) dumped.put(e.getKey(), e.getValue());
            if (!dumped.equals(model)) {
                failed++;
                System.out.println("CHECK FAIL entrySet dump != model (subject " + dumped.size()
                        + " entries, model " + model.size() + ")");
            }
        } catch (Throwable t) {
            failed++;
            System.out.println("CHECK FAIL entrySet iteration threw " + deep(t));
        }

        // Tombstone-heavy phase on a fresh map: bulk insert (several resizes),
        // delete a random half, re-put some deleted and some live keys, then
        // remove via the entrySet iterator; compare against HashMap throughout.
        try {
            @SuppressWarnings("unchecked")
            Map<Object, Object> sub = (Map<Object, Object>) ctor.newInstance();
            Map<Object, Object> mod = new HashMap<>();
            Random r2 = new Random(1515);
            for (int round = 0; round < 3; round++) {
                for (int i = 0; i < 400; i++) {
                    Object k = (i % 5 == 0) ? (Object) new ColKey(i) : (Object) (i * 7 - 900);
                    Integer v = r2.nextInt(100000);
                    checks++;
                    if (!Objects.equals(sub.put(k, v), mod.put(k, v))) { failed++; System.out.println("CHECK FAIL bulk put(" + k + ")"); }
                }
                for (int i = 0; i < 400; i++) {
                    if (r2.nextBoolean()) continue;
                    Object k = (i % 5 == 0) ? (Object) new ColKey(i) : (Object) (i * 7 - 900);
                    checks++;
                    if (!Objects.equals(sub.remove(k), mod.remove(k))) { failed++; System.out.println("CHECK FAIL bulk remove(" + k + ")"); }
                }
                for (int i = 0; i < 400; i += 3) {
                    Object k = (i % 5 == 0) ? (Object) new ColKey(i) : (Object) (i * 7 - 900);
                    checks++;
                    if (!Objects.equals(sub.get(k), mod.get(k)) || sub.containsKey(k) != mod.containsKey(k)) {
                        failed++; System.out.println("CHECK FAIL bulk get/containsKey(" + k + ")");
                    }
                }
                checks++;
                if (sub.size() != mod.size()) { failed++; System.out.println("CHECK FAIL bulk size " + sub.size() + " != " + mod.size()); }
            }
            // iterator removal of every entry whose value is odd
            Iterator<Map.Entry<Object, Object>> it = sub.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> e = it.next();
                if (((Integer) e.getValue()) % 2 != 0) it.remove();
            }
            mod.values().removeIf(v -> ((Integer) v) % 2 != 0);
            checks++;
            Map<Object, Object> dumped = new HashMap<>();
            for (Map.Entry<Object, Object> e : sub.entrySet()) dumped.put(e.getKey(), e.getValue());
            if (!dumped.equals(mod) || sub.size() != mod.size()) {
                failed++; System.out.println("CHECK FAIL after iterator.remove: subject " + dumped.size() + " entries, model " + mod.size());
            }
            for (Object k : mod.keySet()) {
                checks++;
                if (!Objects.equals(sub.get(k), mod.get(k))) { failed++; System.out.println("CHECK FAIL post-iter get(" + k + ")"); break; }
            }
        } catch (Throwable t) {
            failed++;
            System.out.println("CHECK FAIL tombstone phase threw " + deep(t));
        }
        finish(checks, failed);
    }

    // ----------------------------------------------------------------- baseline

    interface IntMap {
        void insert(int k, int v) throws Exception;
        long get(int k) throws Exception;
        long delete(int k) throws Exception;
        long size() throws Exception;
        boolean isEmpty() throws Exception;
    }
    interface IntMapFactory { IntMap make() throws Exception; }

    /**
     * The baseline's own domain: non-negative int keys (key -1 is its tombstone
     * marker), fixed capacity 20 with no resize -> stay at <= 12 distinct keys,
     * and delete only keys that are present (missing-key deletion can loop in a
     * full table). Absent key -> -1. Runs on every iteration via an adapter.
     */
    static void runIntWorkload(IntMapFactory factory, int[] cf) throws Exception {
        int checks = 0, failed = 0;
        // baseline main() demo, expected output: "1 1","2 3", 2, 3, 1, false, -1
        try {
            IntMap d = factory.make();
            d.insert(1, 1); d.insert(2, 2); d.insert(2, 3);
            long[] got = {d.size(), d.delete(2), d.size(), d.isEmpty() ? 1 : 0, d.get(2), d.get(1)};
            long[] exp = {2, 3, 1, 0, -1, 1};
            checks++;
            if (!Arrays.equals(got, exp)) {
                failed++; System.out.println("CHECK FAIL demo " + Arrays.toString(got) + " expected " + Arrays.toString(exp));
            }
            IntMap e = factory.make();
            checks++;
            if (!e.isEmpty() || e.size() != 0 || e.get(5) != -1) { failed++; System.out.println("CHECK FAIL fresh map not empty"); }
        } catch (Throwable t) {
            failed++; System.out.println("CHECK FAIL demo threw " + deep(t));
        }

        for (int seed : new int[]{99, 7, 2024}) {
            IntMap subject = factory.make();
            Map<Integer, Integer> model = new HashMap<>();
            Random rnd = new Random(seed);
            int keyRange = seed == 99 ? 12 : 20;       // distinct home slots: baseline insert has a tombstone-duplicate quirk on collisions
            for (int op = 0; op < 400 && failed < 8; op++) {
                int r = rnd.nextInt(100);
                int k = rnd.nextInt(keyRange);
                if (keyRange > 12 && !model.containsKey(k) && model.size() >= 12) k = new ArrayList<>(new TreeSet<>(model.keySet())).get(0);
                checks++;
                try {
                    if (r < 50 || model.isEmpty() && r >= 75) {
                        int v = rnd.nextInt(1000);
                        subject.insert(k, v);
                        model.put(k, v);
                        checks--;
                    } else if (r < 75) {
                        long got = subject.get(k);
                        long exp = model.getOrDefault(k, -1);
                        if (got != exp) { failed++; System.out.println("CHECK FAIL get(" + k + ") = " + got + ", expected " + exp); }
                    } else {
                        List<Integer> present = new ArrayList<>(new TreeSet<>(model.keySet()));
                        int pk = present.get(rnd.nextInt(present.size()));
                        long got = subject.delete(pk);
                        long exp = model.remove(pk);
                        if (got != exp) { failed++; System.out.println("CHECK FAIL delete(" + pk + ") returned " + got + ", expected " + exp); }
                    }
                    checks++;
                    long got = subject.size();
                    if (got != model.size() || subject.isEmpty() != model.isEmpty()) {
                        failed++; System.out.println("CHECK FAIL size = " + got + ", expected " + model.size());
                    }
                } catch (Throwable t) {
                    failed++; System.out.println("CHECK FAIL seed " + seed + " op#" + op + " threw " + deep(t));
                }
            }
            for (int k = 0; k < keyRange; k++) {
                checks++;
                long got = subject.get(k);
                long exp = model.getOrDefault(k, -1);
                if (got != exp) { failed++; System.out.println("CHECK FAIL final get(" + k + ") = " + got + ", expected " + exp); }
            }
        }
        cf[0] += checks; cf[1] += failed;
    }

    // ------------------------------------------------------------------ helpers

    static void finish(int checks, int failed) {
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " map operations diverged from HashMap oracle");
    }

    static Method find(Class<?> c, Class<?>[] params, String... nameParts) {
        for (Method m : c.getDeclaredMethods()) {
            if (!Arrays.equals(m.getParameterTypes(), params)) continue;
            String n = m.getName().toLowerCase();
            for (String part : nameParts) if (n.contains(part)) return m;
        }
        return null;
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
