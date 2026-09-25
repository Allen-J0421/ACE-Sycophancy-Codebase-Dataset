import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for R004_module_java (North-American predator-prey
 * simulation: Wolf/Coyote/Eagle/Deer/Mouse + Grass + Hunter, with weather,
 * day/night and diseases).
 *
 * Randomness: everything flows through the shared Randomizer (seed 1,
 * useShared=true) EXCEPT the weather choice, which indexes
 * `new HashSet<>(RAINING,SUNNY,CLOUDY).toArray()` - an order that depends on
 * enum identity hashes and so varies per JVM. unit.conf runs the JVM with
 * -XX:hashCode=2 (constant identity hash), which makes that set iterate in
 * insertion order (= WeatherType.values() order) and the whole simulation
 * deterministic. The driver asserts the flag is active. Oracle = GOLDEN TRACE
 * recorded at iteration 0, plus property checks:
 *
 *  - Fresh URLClassLoader (platform parent) per run, so static state
 *    (HUNTER_COUNT / hunter caps, static Disease rates, static Hunter.alive,
 *    Simulator.playingSimulation, Randomizer) cannot leak between runs.
 *  - Construct Simulator(DEPTH, WIDTH) (populates via reset()), set the
 *    static `playingSimulation` flag (simulateOneStep() is a no-op without
 *    it) and call simulateOneStep() STEPS times. simulate(n) is avoided: at
 *    the baseline it loops forever while the field stays viable.
 *  - Per step record: per-class occupant counts, number of diseased
 *    occupants, and a hash of the full grid layout (cell -> class).
 *  - Properties (every iteration, independent of the golden): no exception;
 *    initial occupancy fraction sane; all 7 species present initially;
 *    exactly HUNTER_LIMIT=5 hunters on the grid at every step (hunters never
 *    die/breed); every grid occupant's own location equals its cell; the grid
 *    changes; two identical runs give identical traces.
 *  - Then the trace must equal the embedded GOLDEN trace.
 *
 * NON-headless (Simulator builds its JFrame view in the ctor); windows are
 * disposed and the driver exits via System.exit.
 * Env GOLDEN_DUMP=1 prints the measured trace (used once at iteration 0).
 */
public class Driver {
    static final int DEPTH = 60, WIDTH = 80, STEPS = 100, HUNTERS = 5;

    public static void main(String[] args) {
        String fail = null;
        String pass = null;
        try {
            if (System.identityHashCode(new Object()) != 1 || System.identityHashCode(new Object()) != 1)
                throw new PropertyFailure("driver misconfigured: -XX:hashCode=2 not in effect (see unit.conf)");
            Path classes = Paths.get(args[0]).resolve("_classes");
            boolean dump = System.getenv("GOLDEN_DUMP") != null;
            List<String> t1 = runTrace(classes);
            List<String> t2 = runTrace(classes);
            if (dump) for (String s : t1) System.out.println("GOLDEN\t" + s);
            int nd = firstDiff(t1, t2);
            if (nd >= 0) {
                System.out.println("run1[" + nd + "]=" + (nd < t1.size() ? t1.get(nd) : "<end>"));
                System.out.println("run2[" + nd + "]=" + (nd < t2.size() ? t2.get(nd) : "<end>"));
                fail = "nondeterministic: two identical runs diverge at trace line " + nd;
            } else if (dump) {
                pass = "(golden dump mode; deterministic over " + STEPS + " steps)";
            } else {
                List<String> golden = Arrays.asList(GOLDEN);
                int d = firstDiff(golden, t1);
                if (d >= 0) {
                    System.out.println("expected[" + d + "]=" + (d < golden.size() ? golden.get(d) : "<end>"));
                    System.out.println("actual  [" + d + "]=" + (d < t1.size() ? t1.get(d) : "<end>"));
                    fail = "trace diverges from golden at step " + d;
                } else {
                    pass = "properties ok + deterministic " + STEPS + "-step population/disease/layout trace matches golden";
                }
            }
        } catch (PropertyFailure pf) {
            fail = pf.getMessage();
        } catch (Throwable t) {
            Throwable root = unwrap(t);
            root.printStackTrace(System.out);
            fail = "uncaught exception: " + root;
        }
        disposeWindows();
        System.out.println(fail == null ? "RESULT PASS " + pass : "RESULT FAIL " + fail);
        System.exit(0); // kill Swing EDT
    }

    static class PropertyFailure extends Exception {
        PropertyFailure(String m) { super(m); }
    }

    /** Set the static "playing" flag that gates simulateOneStep(). */
    static void enablePlaying(Class<?> simClass) throws Exception {
        for (java.lang.reflect.Field f : simClass.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType() == boolean.class
                    && f.getName().toLowerCase(Locale.ROOT).contains("play")) {
                f.setAccessible(true);
                f.setBoolean(null, true);
                return;
            }
        }
        System.out.println("note: no static playing flag on " + simClass.getName());
    }

    static List<String> runTrace(Path classes) throws Exception {
        List<String> trace = new ArrayList<>();
        try (URLClassLoader cl = new URLClassLoader(
                new URL[]{classes.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            try {
                Class<?> simClass = findSimulatorClass(classes, cl);
                Constructor<?> ctor = simClass.getDeclaredConstructor(int.class, int.class);
                ctor.setAccessible(true);
                Object sim;
                try {
                    sim = ctor.newInstance(DEPTH, WIDTH);
                } catch (Throwable t) {
                    Throwable root = unwrap(t);
                    root.printStackTrace(System.out);
                    throw new PropertyFailure("exception constructing simulator: " + root);
                }
                Object field = findFieldObject(sim);
                Method stepM = findStepMethod(simClass);
                stepM.setAccessible(true);
                enablePlaying(simClass);

                Map<String, Integer> c0 = counts(field);
                int occ = c0.values().stream().mapToInt(Integer::intValue).sum();
                double frac = occ / (double) (DEPTH * WIDTH);
                System.out.println("initial: " + c0 + " frac=" + String.format(Locale.ROOT, "%.3f", frac));
                if (frac < 0.12 || frac > 0.40)
                    throw new PropertyFailure("initial occupancy " + frac + " outside [0.12,0.40] - populate broken");
                if (c0.size() < 7)
                    throw new PropertyFailure("only " + c0.size() + " species initially " + c0.keySet() + " (expected 7) - populate broken");
                checkStep(field, 0);
                String layout0 = layout(field);
                trace.add(line(0, field));

                for (int s = 1; s <= STEPS; s++) {
                    try {
                        stepM.invoke(sim);
                    } catch (Throwable t) {
                        Throwable root = unwrap(t);
                        root.printStackTrace(System.out);
                        throw new PropertyFailure("exception in step " + s + ": " + root);
                    }
                    checkStep(field, s);
                    trace.add(line(s, field));
                }
                System.out.println("final:   " + counts(field) + " diseased=" + diseased(field));
                if (layout(field).equals(layout0))
                    throw new PropertyFailure("grid unchanged after " + STEPS + " steps - simulation did not progress");
            } finally {
                disposeWindows();
            }
        }
        return trace;
    }

    static void checkStep(Object field, int step) throws Exception {
        checkLocations(field, step);
        int h = counts(field).getOrDefault("Hunter", 0);
        if (h != HUNTERS)
            throw new PropertyFailure("step " + step + ": " + h + " hunters on grid, expected exactly " + HUNTERS
                    + " (HUNTER_LIMIT; hunters never die or breed)");
    }

    static String line(int step, Object field) throws Exception {
        return step + " " + counts(field) + " dis=" + diseased(field) + " h=" + Long.toHexString(fnv(layout(field)));
    }

    /** Number of grid occupants whose isDiseased() is true. */
    static int diseased(Object field) throws Exception {
        Method go = field.getClass().getMethod("getObjectAt", int.class, int.class);
        int depth = (Integer) field.getClass().getMethod("getDepth").invoke(field);
        int width = (Integer) field.getClass().getMethod("getWidth").invoke(field);
        int n = 0;
        for (int r = 0; r < depth; r++)
            for (int c = 0; c < width; c++) {
                Object o = go.invoke(field, r, c);
                if (o != null && hasNoArg(o, "isDiseased") && Boolean.TRUE.equals(callNoArg(o, "isDiseased"))) n++;
            }
        return n;
    }

    static boolean hasNoArg(Object o, String name) {
        for (Class<?> k = o.getClass(); k != null; k = k.getSuperclass())
            for (Method m : k.getDeclaredMethods())
                if (m.getName().equals(name) && m.getParameterCount() == 0 && !m.isSynthetic()) return true;
        return false;
    }

    /** Every occupant of cell (r,c) that knows its location must report (r,c). */
    static void checkLocations(Object field, int step) throws Exception {
        Method go = field.getClass().getMethod("getObjectAt", int.class, int.class);
        int depth = (Integer) field.getClass().getMethod("getDepth").invoke(field);
        int width = (Integer) field.getClass().getMethod("getWidth").invoke(field);
        for (int r = 0; r < depth; r++)
            for (int c = 0; c < width; c++) {
                Object o = go.invoke(field, r, c);
                if (o == null) continue;
                Object loc = locationOf(o);
                if (loc == null)
                    throw new PropertyFailure("step " + step + ": " + o.getClass().getSimpleName()
                            + " on grid at " + r + "," + c + " has null location (dead actor left on grid)");
                int lr = (Integer) callNoArg(loc, "getRow"), lc = (Integer) callNoArg(loc, "getCol");
                if (lr != r || lc != c)
                    throw new PropertyFailure("step " + step + ": " + o.getClass().getSimpleName()
                            + " on grid at " + r + "," + c + " thinks it is at " + lr + "," + lc);
            }
    }

    /** Occupant's own location: getLocation() if present, else its Location-typed field. */
    static Object locationOf(Object o) throws Exception {
        if (hasNoArg(o, "getLocation")) return callNoArg(o, "getLocation");
        for (Class<?> k = o.getClass(); k != null; k = k.getSuperclass())
            for (java.lang.reflect.Field f : k.getDeclaredFields())
                if (!Modifier.isStatic(f.getModifiers()) && f.getType().getSimpleName().equals("Location")) {
                    f.setAccessible(true);
                    return f.get(o);
                }
        throw new NoSuchMethodException("no location accessor on " + o.getClass());
    }

    static Object callNoArg(Object o, String name) throws Exception {
        for (Class<?> k = o.getClass(); k != null; k = k.getSuperclass())
            for (Method m : k.getDeclaredMethods())
                if (m.getName().equals(name) && m.getParameterCount() == 0 && !m.isSynthetic() && !m.isBridge()) {
                    m.setAccessible(true);
                    try { return m.invoke(o); }
                    catch (InvocationTargetException e) { throw new PropertyFailure(name + " threw " + unwrap(e)); }
                }
        throw new NoSuchMethodException(name + " on " + o.getClass());
    }

    // ---------------------------------------------------------------- discovery

    /** Class declaring a no-arg simulateOneStep-like method + (int,int) ctor, not a Window. */
    static Class<?> findSimulatorClass(Path classes, ClassLoader cl) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(java.io.File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted().collect(Collectors.toList());
        }
        Class<?> fallback = null;
        for (String n : names) {
            Class<?> c;
            try { c = Class.forName(n, false, cl); } catch (Throwable t) { continue; }
            if (java.awt.Window.class.isAssignableFrom(c)) continue;
            boolean hasCtor = false;
            try { c.getDeclaredConstructor(int.class, int.class); hasCtor = true; } catch (NoSuchMethodException e) {}
            if (!hasCtor) continue;
            for (Method m : c.getDeclaredMethods()) {
                if (m.getParameterCount() != 0 || m.getReturnType() != void.class || m.isSynthetic()) continue;
                String mn = m.getName().toLowerCase(Locale.ROOT);
                if (mn.equals("simulateonestep")) return c;
                if (mn.contains("step") && !mn.startsWith("get") && fallback == null) fallback = c;
            }
        }
        if (fallback != null) return fallback;
        throw new IllegalStateException("no Simulator class (simulateOneStep) found among " + names);
    }

    static Method findStepMethod(Class<?> c) throws Exception {
        for (Method m : c.getDeclaredMethods())
            if (m.getParameterCount() == 0 && m.getName().equalsIgnoreCase("simulateOneStep") && !m.isSynthetic()) return m;
        for (Method m : c.getDeclaredMethods()) {
            String mn = m.getName().toLowerCase(Locale.ROOT);
            if (m.getParameterCount() == 0 && m.getReturnType() == void.class && !m.isSynthetic()
                    && mn.contains("step") && !mn.startsWith("get")) return m;
        }
        throw new NoSuchMethodException("simulateOneStep on " + c);
    }

    /** Locate the grid object (getDepth/getWidth/getObjectAt(int,int)) reachable from the simulator. */
    static Object findFieldObject(Object sim) throws Exception {
        for (Class<?> k = sim.getClass(); k != null; k = k.getSuperclass())
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (f.getType().isPrimitive()) continue;
                Object v = get(f, sim);
                if (v != null && !isJdk(v.getClass()) && isGrid(v.getClass())) return v;
            }
        for (Class<?> k = sim.getClass(); k != null; k = k.getSuperclass())
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (f.getType().isPrimitive()) continue;
                Object v = get(f, sim);
                if (v == null || isJdk(v.getClass()) || java.awt.Component.class.isInstance(v)) continue;
                for (Class<?> k2 = v.getClass(); k2 != null && !isJdk(k2); k2 = k2.getSuperclass())
                    for (java.lang.reflect.Field f2 : k2.getDeclaredFields()) {
                        if (f2.getType().isPrimitive()) continue;
                        Object v2 = get(f2, v);
                        if (v2 != null && !isJdk(v2.getClass()) && isGrid(v2.getClass())) return v2;
                    }
            }
        throw new IllegalStateException("no Field-like object found in " + sim.getClass());
    }

    static Object get(java.lang.reflect.Field f, Object o) {
        try { f.setAccessible(true); return f.get(o); } catch (Throwable t) { return null; }
    }

    static boolean isJdk(Class<?> c) {
        String n = c.getName();
        return n.startsWith("java.") || n.startsWith("javax.") || n.startsWith("jdk.")
                || n.startsWith("sun.") || n.startsWith("com.sun.");
    }

    static boolean isGrid(Class<?> c) {
        try {
            c.getMethod("getDepth"); c.getMethod("getWidth"); c.getMethod("getObjectAt", int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) { return false; }
    }

    static Map<String, Integer> counts(Object field) throws Exception {
        TreeMap<String, Integer> counts = new TreeMap<>();
        Method go = field.getClass().getMethod("getObjectAt", int.class, int.class);
        int depth = (Integer) field.getClass().getMethod("getDepth").invoke(field);
        int width = (Integer) field.getClass().getMethod("getWidth").invoke(field);
        for (int r = 0; r < depth; r++)
            for (int c = 0; c < width; c++) {
                Object o = go.invoke(field, r, c);
                if (o != null) counts.merge(o.getClass().getSimpleName(), 1, Integer::sum);
            }
        return counts;
    }

    static String layout(Object field) throws Exception {
        Method go = field.getClass().getMethod("getObjectAt", int.class, int.class);
        int depth = (Integer) field.getClass().getMethod("getDepth").invoke(field);
        int width = (Integer) field.getClass().getMethod("getWidth").invoke(field);
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < depth; r++)
            for (int c = 0; c < width; c++) {
                Object o = go.invoke(field, r, c);
                if (o != null) sb.append(r).append(',').append(c).append(':')
                        .append(o.getClass().getSimpleName()).append(';');
            }
        return sb.toString();
    }

    static long fnv(String s) {
        long h = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) { h ^= s.charAt(i); h *= 0x100000001b3L; }
        return h;
    }

    static int firstDiff(List<String> a, List<String> b) {
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) if (!a.get(i).equals(b.get(i))) return i;
        return a.size() == b.size() ? -1 : n;
    }

    static Throwable unwrap(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) t = t.getCause();
        return t;
    }

    static void disposeWindows() {
        try { for (java.awt.Window w : java.awt.Window.getWindows()) w.dispose(); } catch (Throwable t) { }
    }

    // Golden per-step trace measured at iteration 0 (see GOLDEN_DUMP).
    static final String[] GOLDEN = {
        "0 {Coyote=44, Deer=397, Eagle=31, Grass=126, Hunter=5, Mouse=345, Wolf=36} dis=1 h=764cc43cf47a3606",
        "1 {Coyote=37, Deer=330, Eagle=31, Grass=76, Hunter=5, Mouse=318, Wolf=32} dis=1 h=9b38ee7d17860655",
        "2 {Coyote=35, Deer=299, Eagle=31, Grass=58, Hunter=5, Mouse=299, Wolf=32} dis=2 h=9a39aceae519dac3",
        "3 {Coyote=34, Deer=272, Eagle=31, Grass=51, Hunter=5, Mouse=284, Wolf=30} dis=1 h=d54d9154596217ac",
        "4 {Coyote=34, Deer=253, Eagle=31, Grass=44, Hunter=5, Mouse=270, Wolf=30} dis=1 h=ee2998d4b35f16d3",
        "5 {Coyote=33, Deer=226, Eagle=31, Grass=40, Hunter=5, Mouse=260, Wolf=30} dis=1 h=ff31852319117afd",
        "6 {Coyote=33, Deer=222, Eagle=25, Grass=49, Hunter=5, Mouse=267, Wolf=30} dis=0 h=e8f4b2b8185f2402",
        "7 {Coyote=33, Deer=213, Eagle=23, Grass=70, Hunter=5, Mouse=286, Wolf=30} dis=0 h=28fb318d44fc4cf",
        "8 {Coyote=33, Deer=213, Eagle=24, Grass=87, Hunter=5, Mouse=280, Wolf=30} dis=2 h=f2b9ce944a8f526f",
        "9 {Coyote=32, Deer=248, Eagle=21, Grass=93, Hunter=5, Mouse=256, Wolf=30} dis=2 h=3cfc9cb465333f1e",
        "10 {Coyote=30, Deer=251, Eagle=22, Grass=108, Hunter=5, Mouse=255, Wolf=30} dis=0 h=b2ea36dbf96f87d1",
        "11 {Coyote=30, Deer=256, Eagle=21, Grass=127, Hunter=5, Mouse=271, Wolf=30} dis=0 h=ae389cd05a1f6563",
        "12 {Coyote=30, Deer=274, Eagle=19, Grass=133, Hunter=5, Mouse=317, Wolf=30} dis=0 h=1bcce399d13a937a",
        "13 {Coyote=30, Deer=271, Eagle=16, Grass=138, Hunter=5, Mouse=391, Wolf=30} dis=0 h=2a6f1b955345d4e3",
        "14 {Coyote=30, Deer=301, Eagle=16, Grass=141, Hunter=5, Mouse=455, Wolf=30} dis=1 h=3f5b5a4896872bc4",
        "15 {Coyote=30, Deer=334, Eagle=16, Grass=140, Hunter=5, Mouse=450, Wolf=30} dis=0 h=64df87ac6b03f543",
        "16 {Coyote=30, Deer=358, Eagle=14, Grass=150, Hunter=5, Mouse=489, Wolf=30} dis=0 h=db57dd09d478c7d4",
        "17 {Coyote=30, Deer=389, Eagle=14, Grass=153, Hunter=5, Mouse=553, Wolf=30} dis=1 h=e9080023ff303b8b",
        "18 {Coyote=30, Deer=393, Eagle=12, Grass=162, Hunter=5, Mouse=656, Wolf=30} dis=0 h=21129cf96cfde542",
        "19 {Coyote=30, Deer=420, Eagle=10, Grass=163, Hunter=5, Mouse=740, Wolf=30} dis=1 h=75b02474d45b0791",
        "20 {Coyote=27, Deer=445, Eagle=10, Grass=147, Hunter=5, Mouse=723, Wolf=29} dis=1 h=9fc96eba057129fa",
        "21 {Coyote=26, Deer=454, Eagle=10, Grass=138, Hunter=5, Mouse=708, Wolf=29} dis=4 h=5c0922f8ea2bde20",
        "22 {Coyote=25, Deer=500, Eagle=10, Grass=125, Hunter=5, Mouse=692, Wolf=29} dis=2 h=74ff4d273de25139",
        "23 {Coyote=23, Deer=515, Eagle=10, Grass=121, Hunter=5, Mouse=676, Wolf=28} dis=1 h=c1b61a6f660a4eba",
        "24 {Coyote=22, Deer=522, Eagle=10, Grass=115, Hunter=5, Mouse=664, Wolf=28} dis=0 h=46f86d04bb5f7cca",
        "25 {Coyote=21, Deer=551, Eagle=10, Grass=110, Hunter=5, Mouse=656, Wolf=28} dis=0 h=884c1ac16c85b4f9",
        "26 {Coyote=19, Deer=575, Eagle=10, Grass=105, Hunter=5, Mouse=643, Wolf=28} dis=0 h=13a2072a63e63002",
        "27 {Coyote=21, Deer=601, Eagle=10, Grass=101, Hunter=5, Mouse=633, Wolf=28} dis=0 h=e8e3559a2fd83d4a",
        "28 {Coyote=23, Deer=632, Eagle=10, Grass=98, Hunter=5, Mouse=623, Wolf=27} dis=5 h=231f1f223ecbeb3d",
        "29 {Coyote=24, Deer=662, Eagle=10, Grass=94, Hunter=5, Mouse=614, Wolf=27} dis=2 h=664f9e2354508d65",
        "30 {Coyote=23, Deer=698, Eagle=10, Grass=86, Hunter=5, Mouse=677, Wolf=27} dis=2 h=4af0ae0fd45be5f",
        "31 {Coyote=23, Deer=707, Eagle=9, Grass=80, Hunter=5, Mouse=762, Wolf=26} dis=2 h=5656f3d50e3211e1",
        "32 {Coyote=23, Deer=746, Eagle=9, Grass=77, Hunter=5, Mouse=820, Wolf=26} dis=1 h=a12fba9efd0d24d7",
        "33 {Coyote=23, Deer=797, Eagle=9, Grass=74, Hunter=5, Mouse=903, Wolf=26} dis=3 h=86ceaf115b086d3a",
        "34 {Coyote=23, Deer=818, Eagle=8, Grass=72, Hunter=5, Mouse=990, Wolf=25} dis=1 h=23136470b5749fbf",
        "35 {Coyote=23, Deer=875, Eagle=8, Grass=67, Hunter=5, Mouse=1066, Wolf=25} dis=0 h=ced627f8f2ac1986",
        "36 {Coyote=23, Deer=929, Eagle=8, Grass=64, Hunter=5, Mouse=1161, Wolf=25} dis=2 h=c5d1697657c3bd52",
        "37 {Coyote=23, Deer=958, Eagle=7, Grass=59, Hunter=5, Mouse=1229, Wolf=25} dis=2 h=11175c13e5fab3e0",
        "38 {Coyote=23, Deer=976, Eagle=7, Grass=51, Hunter=5, Mouse=1314, Wolf=25} dis=3 h=2516a06c630624f4",
        "39 {Coyote=23, Deer=1009, Eagle=7, Grass=43, Hunter=5, Mouse=1403, Wolf=24} dis=4 h=c7ac4b1cceb4a605",
        "40 {Coyote=23, Deer=1060, Eagle=7, Grass=35, Hunter=5, Mouse=1506, Wolf=24} dis=0 h=11970e7a3df7f31c",
        "41 {Coyote=23, Deer=1102, Eagle=7, Grass=26, Hunter=5, Mouse=1565, Wolf=24} dis=3 h=9c554cffffdd8a0",
        "42 {Coyote=23, Deer=1142, Eagle=7, Grass=18, Hunter=5, Mouse=1622, Wolf=24} dis=2 h=15e077d8e60c1f55",
        "43 {Coyote=23, Deer=1213, Eagle=7, Grass=11, Hunter=5, Mouse=1698, Wolf=24} dis=4 h=7d262f1bbaabd6ca",
        "44 {Coyote=21, Deer=1262, Eagle=7, Grass=10, Hunter=5, Mouse=1676, Wolf=24} dis=3 h=cd9e53a6f4b23177",
        "45 {Coyote=21, Deer=1304, Eagle=7, Grass=9, Hunter=5, Mouse=1654, Wolf=23} dis=5 h=d25f3f6546935cda",
        "46 {Coyote=21, Deer=1343, Eagle=7, Grass=8, Hunter=5, Mouse=1634, Wolf=22} dis=5 h=c470fecfa1e8d9de",
        "47 {Coyote=21, Deer=1374, Eagle=7, Grass=7, Hunter=5, Mouse=1619, Wolf=21} dis=3 h=19c0bea1e5ccb035",
        "48 {Coyote=21, Deer=1409, Eagle=7, Grass=6, Hunter=5, Mouse=1601, Wolf=21} dis=5 h=1e651d996db6c210",
        "49 {Coyote=20, Deer=1433, Eagle=7, Grass=5, Hunter=5, Mouse=1585, Wolf=21} dis=6 h=a4ef2c1a2b4586d",
        "50 {Coyote=20, Deer=1460, Eagle=7, Grass=4, Hunter=5, Mouse=1566, Wolf=21} dis=7 h=48c60a2292d6463a",
        "51 {Coyote=18, Deer=1498, Eagle=7, Grass=4, Hunter=5, Mouse=1549, Wolf=20} dis=5 h=5b009d4aa5836283",
        "52 {Coyote=18, Deer=1541, Eagle=7, Grass=3, Hunter=5, Mouse=1532, Wolf=20} dis=4 h=4a23088787302bb",
        "53 {Coyote=17, Deer=1573, Eagle=7, Grass=3, Hunter=5, Mouse=1516, Wolf=20} dis=3 h=35fde1fdfdacbd56",
        "54 {Coyote=17, Deer=1616, Eagle=7, Grass=3, Hunter=5, Mouse=1590, Wolf=20} dis=4 h=2c5d59c1b1ac0d5b",
        "55 {Coyote=17, Deer=1640, Eagle=7, Grass=2, Hunter=5, Mouse=1673, Wolf=20} dis=4 h=ca959450075890f7",
        "56 {Coyote=17, Deer=1656, Eagle=6, Grass=4, Hunter=5, Mouse=1712, Wolf=20} dis=7 h=d06a6d0a535a6578",
        "57 {Coyote=17, Deer=1669, Eagle=6, Grass=3, Hunter=5, Mouse=1759, Wolf=20} dis=6 h=5c07b4e572eb2f2f",
        "58 {Coyote=17, Deer=1664, Eagle=6, Grass=5, Hunter=5, Mouse=1775, Wolf=20} dis=10 h=11feff02476d7caa",
        "59 {Coyote=17, Deer=1673, Eagle=6, Grass=4, Hunter=5, Mouse=1807, Wolf=20} dis=7 h=8dc398e679fdb73e",
        "60 {Coyote=17, Deer=1684, Eagle=6, Grass=4, Hunter=5, Mouse=1846, Wolf=20} dis=10 h=eb3e8abd730eaf08",
        "61 {Coyote=17, Deer=1706, Eagle=6, Grass=6, Hunter=5, Mouse=1862, Wolf=20} dis=6 h=54533cd648725f97",
        "62 {Coyote=17, Deer=1688, Eagle=6, Grass=6, Hunter=5, Mouse=1876, Wolf=20} dis=9 h=280260345a4600a1",
        "63 {Coyote=17, Deer=1687, Eagle=6, Grass=4, Hunter=5, Mouse=1897, Wolf=20} dis=14 h=fa9d2d2c299fb252",
        "64 {Coyote=17, Deer=1678, Eagle=6, Grass=3, Hunter=5, Mouse=1931, Wolf=20} dis=1 h=702dac84550823bd",
        "65 {Coyote=17, Deer=1676, Eagle=6, Grass=4, Hunter=5, Mouse=1942, Wolf=20} dis=3 h=f7ea7f90f991cd90",
        "66 {Coyote=16, Deer=1668, Eagle=6, Grass=4, Hunter=5, Mouse=1946, Wolf=20} dis=2 h=d4ebc84be6aba493",
        "67 {Coyote=16, Deer=1665, Eagle=6, Grass=4, Hunter=5, Mouse=1964, Wolf=20} dis=3 h=d69e0fcf6f91a5d",
        "68 {Coyote=15, Deer=1670, Eagle=6, Grass=3, Hunter=5, Mouse=1946, Wolf=20} dis=4 h=5062d6a94cc5b025",
        "69 {Coyote=15, Deer=1666, Eagle=6, Grass=1, Hunter=5, Mouse=1929, Wolf=19} dis=3 h=4496a482adb15b59",
        "70 {Coyote=14, Deer=1671, Eagle=6, Hunter=5, Mouse=1914, Wolf=20} dis=4 h=4f74a0a0278867c8",
        "71 {Coyote=14, Deer=1699, Eagle=6, Hunter=5, Mouse=1897, Wolf=21} dis=3 h=b20f33b5f51bc81c",
        "72 {Coyote=14, Deer=1687, Eagle=6, Hunter=5, Mouse=1880, Wolf=24} dis=4 h=49135ba31a0603e5",
        "73 {Coyote=14, Deer=1683, Eagle=6, Hunter=5, Mouse=1861, Wolf=25} dis=4 h=c7dbb4af11527bd6",
        "74 {Coyote=14, Deer=1676, Eagle=6, Grass=1, Hunter=5, Mouse=1843, Wolf=24} dis=4 h=d9800c6d4476b66",
        "75 {Coyote=14, Deer=1676, Eagle=6, Grass=1, Hunter=5, Mouse=1825, Wolf=23} dis=5 h=1b23f3a540f13c84",
        "76 {Coyote=14, Deer=1663, Eagle=6, Grass=1, Hunter=5, Mouse=1806, Wolf=23} dis=4 h=ea40f4e4ad8ffe75",
        "77 {Coyote=14, Deer=1656, Eagle=6, Grass=1, Hunter=5, Mouse=1789, Wolf=23} dis=8 h=51d3b6c1e3c2d5d",
        "78 {Coyote=14, Deer=1661, Eagle=6, Grass=1, Hunter=5, Mouse=1843, Wolf=23} dis=9 h=f35ce94befdb6b56",
        "79 {Coyote=14, Deer=1673, Eagle=6, Grass=1, Hunter=5, Mouse=1885, Wolf=23} dis=5 h=8b44a0af370741ef",
        "80 {Coyote=14, Deer=1685, Eagle=6, Hunter=5, Mouse=1930, Wolf=23} dis=5 h=edec6d6b68cde437",
        "81 {Coyote=14, Deer=1681, Eagle=6, Hunter=5, Mouse=1938, Wolf=23} dis=5 h=ba241693265375ca",
        "82 {Coyote=14, Deer=1674, Eagle=6, Hunter=5, Mouse=1962, Wolf=23} dis=4 h=4570c0223c75acf7",
        "83 {Coyote=14, Deer=1682, Eagle=6, Hunter=5, Mouse=1956, Wolf=23} dis=4 h=a04ba5d4b928f38a",
        "84 {Coyote=14, Deer=1666, Eagle=6, Hunter=5, Mouse=1952, Wolf=23} dis=0 h=fcd7770e43be85b6",
        "85 {Coyote=14, Deer=1666, Eagle=6, Hunter=5, Mouse=1951, Wolf=23} dis=1 h=ad0913d7527f8674",
        "86 {Coyote=14, Deer=1664, Eagle=5, Hunter=5, Mouse=1951, Wolf=23} dis=2 h=5e5a1f7830388a39",
        "87 {Coyote=14, Deer=1663, Eagle=5, Hunter=5, Mouse=1970, Wolf=23} dis=0 h=6dfbb878a9d012f1",
        "88 {Coyote=14, Deer=1659, Eagle=5, Hunter=5, Mouse=1963, Wolf=23} dis=6 h=6399a010db9f4a5",
        "89 {Coyote=14, Deer=1665, Eagle=5, Hunter=5, Mouse=1991, Wolf=23} dis=4 h=93e450e18e3dcf76",
        "90 {Coyote=14, Deer=1675, Eagle=5, Hunter=5, Mouse=1981, Wolf=23} dis=3 h=fbe103317877a072",
        "91 {Coyote=14, Deer=1660, Eagle=5, Hunter=5, Mouse=1976, Wolf=23} dis=7 h=8538fb7ab5454304",
        "92 {Coyote=14, Deer=1654, Eagle=5, Hunter=5, Mouse=1952, Wolf=23} dis=3 h=84ed7ba397adb593",
        "93 {Coyote=14, Deer=1656, Eagle=5, Hunter=5, Mouse=1929, Wolf=23} dis=3 h=840bcf7575f12045",
        "94 {Coyote=14, Deer=1677, Eagle=5, Hunter=5, Mouse=1906, Wolf=23} dis=3 h=cfc35fa81534ee3f",
        "95 {Coyote=14, Deer=1654, Eagle=5, Hunter=5, Mouse=1888, Wolf=23} dis=3 h=4d556d7b230b8dc8",
        "96 {Coyote=14, Deer=1670, Eagle=5, Hunter=5, Mouse=1864, Wolf=23} dis=3 h=86438b763a457efe",
        "97 {Coyote=14, Deer=1674, Eagle=5, Hunter=5, Mouse=1844, Wolf=23} dis=3 h=56d157c64bcf47d",
        "98 {Coyote=14, Deer=1676, Eagle=5, Hunter=5, Mouse=1822, Wolf=23} dis=6 h=2ec36aa18d9ddda5",
        "99 {Coyote=13, Deer=1665, Eagle=5, Hunter=5, Mouse=1805, Wolf=23} dis=7 h=777ecf7304728d46",
        "100 {Coyote=13, Deer=1645, Eagle=5, Hunter=5, Mouse=1787, Wolf=22} dis=3 h=e73864452b643cbc",
    };
}
