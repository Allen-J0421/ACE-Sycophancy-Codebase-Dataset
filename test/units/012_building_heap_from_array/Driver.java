import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 012_building_heap_from_array.
 *
 * Baseline: BuildHeap.buildHeap(int[]) turns the array into a MAX-heap
 * in place (parent >= children, standard implicit binary heap layout).
 * Iterations 1..10 rename this to MaxHeap.build(int[]) (plus a
 * build(int[],int) prefix variant). The oracle is property-based and fully
 * independent of the subject:
 *   1. the result must be a permutation of the input, and
 *   2. every node i must satisfy a[i] >= a[2i+1] and a[i] >= a[2i+2].
 *
 * Discovery: a static void method taking exactly (int[]) whose name contains
 * "build" (or exactly "heapify"/"makeHeap") on a class not named Test or
 * Demo; falls back to (int[],int) invoked with the full length.
 *
 * The subject sources are compiled here rather than by the runner (see
 * unit.conf): the iteration-0 baseline keeps `public class BuildHeap` in
 * building_heap_from_array.java, which plain javac rejects on filename
 * grounds, so each file is staged under the name its public type requires.
 * The compiled code is byte-identical to the agent's; a genuinely broken
 * build still fails here with the javac error.
 */
public class Driver {

    /** Compiles the subject's .java sources into classesDir with filename staging. */
    static boolean compileSubjectSources(Path work, Path classesDir) throws Exception {
        List<Path> sources;
        try (Stream<Path> s = Files.walk(work)) {
            sources = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.startsWith(classesDir))
                    .filter(p -> !p.getFileName().toString().equals("DriverCompatPlaceholder.java"))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (sources.isEmpty()) {
            System.out.println("RESULT FAIL no subject .java sources found in " + work);
            return false;
        }
        Path stage = Files.createTempDirectory("bh_stage");
        List<String> staged = new ArrayList<>();
        java.util.regex.Pattern pub = java.util.regex.Pattern.compile(
                "public\\s+(?:final\\s+|abstract\\s+|sealed\\s+|non-sealed\\s+|strictfp\\s+)*"
                        + "(?:class|interface|enum|record)\\s+(\\w+)");
        java.util.regex.Pattern pkg = java.util.regex.Pattern.compile(
                "(?m)^\\s*package\\s+([\\w.]+)\\s*;");
        for (Path src : sources) {
            String text = Files.readString(src);
            String noComments = text.replaceAll("(?s)/\\*.*?\\*/", " ")
                                    .replaceAll("(?m)//.*$", " ");
            String name = src.getFileName().toString();
            java.util.regex.Matcher pm = pub.matcher(noComments);
            if (pm.find()) name = pm.group(1) + ".java";
            Path dir = stage;
            java.util.regex.Matcher km = pkg.matcher(noComments);
            if (km.find()) dir = stage.resolve(km.group(1).replace('.', File.separatorChar));
            Files.createDirectories(dir);
            Path dest = dir.resolve(name);
            Files.writeString(dest, text);
            staged.add(dest.toString());
            System.out.println("staged " + work.relativize(src) + " -> " + stage.relativize(dest));
        }
        List<String> cmd = new ArrayList<>(List.of("javac", "-nowarn", "-encoding", "utf-8",
                "-cp", classesDir.toString(), "-d", classesDir.toString()));
        cmd.addAll(staged);
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes(), "UTF-8");
        int rc = p.waitFor();
        if (rc != 0) {
            System.out.println(out);
            System.out.println("RESULT FAIL subject sources do not compile (agent broke the build)");
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        if (!compileSubjectSources(work, classes)) return;
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Method oneArg = null, twoArg = null;
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            String cname = c.getSimpleName().toLowerCase();
            if (cname.contains("test") || cname.contains("demo")) continue;
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                String n = m.getName().toLowerCase();
                boolean named = n.contains("build") || n.equals("heapify") || n.equals("makeheap");
                if (!named) continue;
                if (p.length == 1 && p[0] == int[].class) {
                    if (oneArg == null || n.contains("build")) oneArg = m;
                } else if (p.length == 2 && p[0] == int[].class && p[1] == int.class) {
                    if (twoArg == null || n.contains("build")) twoArg = m;
                }
            }
        }
        Method entry = oneArg != null ? oneArg : twoArg;
        if (entry == null) {
            System.out.println("RESULT FAIL no build-heap entry point (build*(int[]) or build*(int[],int)) among " + classNames);
            return;
        }
        boolean sized = entry.getParameterCount() == 2;
        entry.setAccessible(true);
        Object receiver = null;
        if (!Modifier.isStatic(entry.getModifiers())) {
            var k = entry.getDeclaringClass().getDeclaredConstructor();
            k.setAccessible(true);
            receiver = k.newInstance();
        }
        System.out.println("entry: " + entry + (sized ? " [sized]" : ""));

        List<int[]> cases = new ArrayList<>(List.of(
                new int[]{},
                new int[]{7},
                new int[]{1, 2},
                new int[]{2, 1},
                new int[]{5, 5, 5, 5, 5},
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                new int[]{-3, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 0},
                new int[]{1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17}));
        Random rnd = new Random(1212L);
        for (int t = 0; t < 35; t++) {
            int n = rnd.nextInt(t < 5 ? 8 : 300);
            int bound = (t % 3 == 0) ? 8 : 50000;   // duplicate-heavy and spread-out mixes
            cases.add(rnd.ints(n, -bound, bound).toArray());
        }
        cases.add(rnd.ints(5000, -1000000, 1000000).toArray());

        int checks = 0, failed = 0;
        for (int[] original : cases) {
            checks++;
            int[] subject = original.clone();
            try {
                if (sized) entry.invoke(receiver, subject, subject.length);
                else entry.invoke(receiver, (Object) subject);
            } catch (Throwable e) {
                failed++;
                System.out.println("CHECK FAIL exception on n=" + original.length + ": " + (e.getCause() != null ? e.getCause() : e));
                continue;
            }
            // permutation check
            int[] sIn = original.clone(), sOut = subject.clone();
            Arrays.sort(sIn); Arrays.sort(sOut);
            if (!Arrays.equals(sIn, sOut)) {
                failed++;
                if (failed <= 5)
                    System.out.println("CHECK FAIL not a permutation: input=" + preview(original) + " output=" + preview(subject));
                continue;
            }
            // max-heap property
            int bad = -1;
            for (int i = 0; i < subject.length; i++) {
                int l = 2 * i + 1, r = 2 * i + 2;
                if (l < subject.length && subject[i] < subject[l]) { bad = i; break; }
                if (r < subject.length && subject[i] < subject[r]) { bad = i; break; }
            }
            if (bad >= 0) {
                failed++;
                if (failed <= 5)
                    System.out.println("CHECK FAIL heap property violated at index " + bad
                            + " in output=" + preview(subject) + " (input=" + preview(original) + ")");
            }
        }
        System.out.println(checks + " arrays, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " arrays heapified (max-heap + permutation)"
                                       : "RESULT FAIL " + failed + "/" + checks + " arrays violated heap/permutation property");
    }

    static String preview(int[] a) {
        int k = Math.min(a.length, 20);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < k; i++) sb.append(i > 0 ? "," : "").append(a[i]);
        if (a.length > k) sb.append(",...(").append(a.length).append(")");
        return sb.append("]").toString();
    }
}
