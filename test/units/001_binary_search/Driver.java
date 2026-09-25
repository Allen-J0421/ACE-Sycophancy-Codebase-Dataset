import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 001_binary_search.
 * Locates a binary-search entry point by reflection over every compiled class
 * (robust to renames across iterations) and checks it against
 * Arrays.binarySearch semantics: returns the index of the target when present
 * (any matching index accepted), and -1 (or any negative value) when absent.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Method entry = null;
        Object receiver = null;
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                boolean shape = p.length == 2 && p[0] == int[].class && p[1] == int.class
                        && (m.getReturnType() == int.class || m.getReturnType() == long.class
                            || m.getReturnType() == Integer.class || m.getReturnType() == OptionalInt.class);
                if (!shape) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("search") || n.contains("find") || n.contains("indexof"))) continue;
                m.setAccessible(true);
                if (!Modifier.isStatic(m.getModifiers())) {
                    try { receiver = c.getDeclaredConstructor().newInstance(); }
                    catch (Throwable t) { continue; }
                }
                entry = m;
                break;
            }
            if (entry != null) break;
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no binary-search entry point found among " + classNames);
            return;
        }
        System.out.println("entry: " + entry);

        int checks = 0, failed = 0;
        Random rnd = new Random(42);
        List<int[]> arrays = new ArrayList<>(List.of(
                new int[]{2, 3, 4, 10, 40},
                new int[]{},
                new int[]{7},
                new int[]{1, 2},
                new int[]{-5, -2, 0, 3, 3, 9, 100}));
        for (int t = 0; t < 30; t++) {
            int n = rnd.nextInt(60);
            int[] a = rnd.ints(n, -50, 50).sorted().toArray();
            arrays.add(a);
        }
        for (int[] a : arrays) {
            int[] probes = new int[Math.min(a.length, 8) + 4];
            for (int i = 0; i < probes.length; i++)
                probes[i] = i < Math.min(a.length, 8) ? a[rnd.nextInt(a.length)] : rnd.nextInt(300) - 150;
            for (int x : probes) {
                checks++;
                Object rObj;
                try {
                    rObj = entry.invoke(receiver, a, x);
                } catch (Throwable e) {
                    failed++;
                    System.out.println("CHECK FAIL exception on " + Arrays.toString(a) + " target " + x + ": " + e.getCause());
                    continue;
                }
                long r = toLong(rObj);
                boolean present = Arrays.stream(a).anyMatch(v -> v == x);
                boolean ok = present ? (r >= 0 && r < a.length && a[(int) r] == x) : r < 0;
                if (!ok) {
                    failed++;
                    System.out.println("CHECK FAIL arr=" + Arrays.toString(a) + " target=" + x + " -> " + r);
                }
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " checks"
                                       : "RESULT FAIL " + failed + "/" + checks + " checks failed");
    }

    static long toLong(Object o) {
        if (o instanceof OptionalInt oi) return oi.isPresent() ? oi.getAsInt() : -1;
        return ((Number) o).longValue();
    }
}
