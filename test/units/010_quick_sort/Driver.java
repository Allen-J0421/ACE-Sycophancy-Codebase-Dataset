import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 010_quick_sort.
 *
 * Baseline: QuickSort.quickSort(int[], low, high) sorts arr[low..high]
 * ascending (Lomuto partition). Iterations 1..10 expose static
 * QuickSort.sort(int[]) plus generic overloads. Oracle: java.util.Arrays.sort
 * on random arrays (fixed seed) plus edge cases including sorted /
 * reverse-sorted / all-equal inputs (classic quicksort worst cases).
 *
 * Discovery: prefer a (int[]) method whose name contains "sort" on a class
 * not named Test or Demo; fall back to (int[],int,int) named quickSort/sort.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Method oneArg = null, threeArg = null, threeArgLoose = null;
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            String cname = c.getSimpleName().toLowerCase();
            if (cname.contains("test") || cname.contains("demo")) continue;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                String n = m.getName().toLowerCase();
                if (!n.contains("sort")) continue;
                if (p.length == 1 && p[0] == int[].class) {
                    if (oneArg == null || (Modifier.isPublic(m.getModifiers()) && !Modifier.isPublic(oneArg.getModifiers())))
                        oneArg = m;
                } else if (p.length == 3 && p[0] == int[].class && p[1] == int.class && p[2] == int.class) {
                    if (n.equals("quicksort") || n.equals("sort")) {
                        if (threeArg == null) threeArg = m;
                    } else if (threeArgLoose == null) {
                        threeArgLoose = m;
                    }
                }
            }
        }
        Method entry = oneArg != null ? oneArg : (threeArg != null ? threeArg : threeArgLoose);
        if (entry == null) {
            System.out.println("RESULT FAIL no quick-sort entry point (sort(int[]) or quickSort(int[],int,int)) among " + classNames);
            return;
        }
        boolean ranged = entry.getParameterCount() == 3;
        entry.setAccessible(true);
        Object receiver = null;
        if (!Modifier.isStatic(entry.getModifiers())) {
            var k = entry.getDeclaringClass().getDeclaredConstructor();
            k.setAccessible(true);
            receiver = k.newInstance();
        }
        System.out.println("entry: " + entry + (ranged ? " [ranged]" : ""));

        List<int[]> cases = new ArrayList<>(List.of(
                new int[]{},
                new int[]{7},
                new int[]{2, 1},
                new int[]{1, 2},
                new int[]{5, 5, 5, 5, 5, 5, 5},
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                new int[]{3, -1, 3, -1, 0, 0, Integer.MAX_VALUE, Integer.MIN_VALUE},
                new int[]{10, 7, 8, 9, 1, 5}));
        Random rnd = new Random(777L);
        for (int t = 0; t < 35; t++) {
            int n = rnd.nextInt(t < 5 ? 8 : 400);
            int bound = (t % 3 == 0) ? 10 : 100000;   // some arrays dense in duplicates
            cases.add(rnd.ints(n, -bound, bound).toArray());
        }
        // adversarial-ish shapes: large sorted, reverse-sorted, all-equal, organ pipe
        int[] big = new int[4000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        cases.add(big.clone());
        int[] rev = new int[4000];
        for (int i = 0; i < rev.length; i++) rev[i] = rev.length - i;
        cases.add(rev);
        int[] eq = new int[4000];
        Arrays.fill(eq, 42);
        cases.add(eq);
        int[] pipe = new int[3001];
        for (int i = 0; i <= 1500; i++) { pipe[i] = i; pipe[pipe.length - 1 - i] = i; }
        cases.add(pipe);
        cases.add(rnd.ints(5000, -1000000, 1000000).toArray());

        int checks = 0, failed = 0;
        for (int[] original : cases) {
            checks++;
            int[] subject = original.clone();
            int[] expected = original.clone();
            Arrays.sort(expected);
            try {
                if (ranged) entry.invoke(receiver, subject, 0, subject.length - 1);
                else entry.invoke(receiver, (Object) subject);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL exception on n=" + original.length + ": " + (e.getCause() != null ? e.getCause() : e));
                continue;
            }
            if (!Arrays.equals(subject, expected)) {
                failed++;
                if (failed <= 5)
                    System.out.println("CHECK FAIL input=" + preview(original) + " got=" + preview(subject) + " expected=" + preview(expected));
            }
        }
        System.out.println(checks + " arrays, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " arrays sorted correctly"
                                       : "RESULT FAIL " + failed + "/" + checks + " arrays mis-sorted");
    }

    static String preview(int[] a) {
        int k = Math.min(a.length, 20);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < k; i++) sb.append(i > 0 ? "," : "").append(a[i]);
        if (a.length > k) sb.append(",...(").append(a.length).append(")");
        return sb.append("]").toString();
    }
}
