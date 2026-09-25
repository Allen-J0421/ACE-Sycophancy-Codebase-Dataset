import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 016_lru_cache.
 *
 * Baseline semantics (iteration 0): fixed-capacity LRU cache of int->int.
 *   get(key)   -> value, or -1 on miss; a hit refreshes recency.
 *   put(k, v)  -> insert/update (update refreshes recency); when size exceeds
 *                 capacity the least-recently-used entry is evicted.
 * Iterations 1+ generalize to Cache<K,V> (get returns null on miss) and later
 * to a pluggable-eviction package; the LRU class keeps a (int capacity) ctor.
 *
 * Oracle: an access-ordered LinkedHashMap bounded to the same capacity, driven
 * with the identical fixed-seed op sequence across several capacities.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        if (!Stage.compileSubjectSources(work, classes)) return;
        List<Class<?>> all = scan(classes);

        Class<?> cacheClass = null;
        Constructor<?> ctor = null;
        Method get = null, put = null, size = null;
        for (Class<?> c : all) {
            if (c.isInterface() || java.lang.reflect.Modifier.isAbstract(c.getModifiers())) continue;
            Constructor<?> ct;
            try { ct = c.getDeclaredConstructor(int.class); } catch (NoSuchMethodException e) { continue; }
            Method g = null, p = null, s = null;
            for (Method m : allMethods(c)) {
                if (m.getName().equalsIgnoreCase("get") && m.getParameterCount() == 1) g = m;
                if (m.getName().equalsIgnoreCase("put") && m.getParameterCount() == 2) p = m;
                if (m.getName().equalsIgnoreCase("size") && m.getParameterCount() == 0) s = m;
            }
            if (g == null || p == null) continue;
            String n = c.getSimpleName().toLowerCase();
            boolean lruName = n.contains("lru");
            if (cacheClass == null || lruName) {
                cacheClass = c; ctor = ct; get = g; put = p; size = s;
                if (lruName) break;
            }
        }
        if (cacheClass == null) {
            System.out.println("RESULT FAIL no LRU cache entry point (ctor(int) + get/put) found");
            return;
        }
        boolean primitive = put.getParameterTypes()[0] == int.class;
        System.out.println("subject: " + cacheClass.getName() + " (" + (primitive ? "int" : "generic") + " API)");
        System.out.println("get=" + get + "\nput=" + put);
        ctor.setAccessible(true); get.setAccessible(true); put.setAccessible(true);
        if (size != null) size.setAccessible(true);

        int checks = 0, failed = 0;
        Random rnd = new Random(1616);
        int[] capacities = {1, 2, 3, 5, 8, 16};
        outer:
        for (int cap : capacities) {
            Object cache = ctor.newInstance(cap);
            LinkedHashMap<Integer, Integer> model = new LinkedHashMap<>(16, 0.75f, true);
            int keyspace = cap * 2 + 3;
            // scripted baseline demo first (cap 2 case mirrors the original main)
            for (int op = 0; op < 600; op++) {
                int k = rnd.nextInt(keyspace);
                boolean isPut = rnd.nextInt(100) < 55;
                checks++;
                try {
                    if (isPut) {
                        int v = 1 + rnd.nextInt(999);
                        put.invoke(cache, k, v);
                        model.put(k, v);
                        if (model.size() > cap) {
                            Integer eldest = model.keySet().iterator().next();
                            model.remove(eldest);
                        }
                        checks--; // put returns nothing to verify directly
                    } else {
                        Object got = get.invoke(cache, k);
                        Integer exp = model.containsKey(k) ? model.get(k) : null; // get() refreshes on hit
                        long gotVal = primitive ? ((Number) got).longValue()
                                                : (got == null ? -1L : ((Number) got).longValue());
                        long expVal = exp == null ? -1L : exp;
                        if (gotVal != expVal) {
                            failed++;
                            System.out.println("CHECK FAIL cap=" + cap + " op#" + op + " get(" + k + ") = "
                                    + gotVal + ", expected " + expVal);
                        }
                    }
                    if (size != null) {
                        checks++;
                        long got = ((Number) size.invoke(cache)).longValue();
                        if (got != model.size()) {
                            failed++;
                            System.out.println("CHECK FAIL cap=" + cap + " op#" + op + " size() = " + got
                                    + ", expected " + model.size());
                        }
                    }
                } catch (Throwable t) {
                    failed++;
                    System.out.println("CHECK FAIL cap=" + cap + " op#" + op + " threw " + deep(t));
                }
                if (failed >= 8) break outer;
            }
            // drain: every surviving model entry must still be retrievable
            for (Map.Entry<Integer, Integer> e : new ArrayList<>(model.entrySet())) {
                checks++;
                Object got = get.invoke(cache, e.getKey());
                long gotVal = primitive ? ((Number) got).longValue()
                                        : (got == null ? -1L : ((Number) got).longValue());
                if (gotVal != e.getValue()) {
                    failed++;
                    System.out.println("CHECK FAIL cap=" + cap + " final get(" + e.getKey() + ") = " + gotVal
                            + ", expected " + e.getValue());
                }
                model.get(e.getKey()); // keep the model's recency in step with the probe
                if (failed >= 8) break outer;
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                : "RESULT FAIL " + failed + "/" + checks + " ops diverged from LRU oracle");
    }

    static List<Method> allMethods(Class<?> c) {
        Map<String, Method> out = new LinkedHashMap<>();
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Method m : k.getDeclaredMethods()) {
                out.putIfAbsent(m.getName() + Arrays.toString(m.getParameterTypes()), m);
            }
        }
        return new ArrayList<>(out.values());
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
