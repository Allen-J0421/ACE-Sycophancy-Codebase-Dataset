import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for R003_module_java (Australian savannah simulation:
 * Grasshopper/HarvesterAnt/Termite/Impala (primary consumers),
 * Pangolin/Aardvark/Mongoose (secondary), StarGrass/RedOatGrass/Acacia
 * (producers), Carcass).
 *
 * Randomness: almost everything flows through the shared Randomizer
 * (fixed seed 10001, useShared=true), but Gender.getRandom() uses the
 * JVM-global Math.random(), which makes the subject nondeterministic as
 * shipped. The driver pins that down by re-seeding java.lang.Math's internal
 * Random before every run (needs --add-opens java.base/java.lang, see
 * unit.conf). With that, the simulation is fully deterministic, so the
 * oracle is a GOLDEN TRACE recorded at iteration 0, plus property checks:
 *
 *  - Fresh URLClassLoader (platform parent) per run so all subject statics
 *    (Simulator.step/field, TimeSystem, WeatherSystem, Disease's static
 *    STEPS_BEFORE_DEATH, Randomizer) are re-initialised.
 *  - Construct Simulator(DEPTH, WIDTH) (ctor populates via reset()), then
 *    call simulateOneStep() STEPS times (simulate(n) is avoided: it stops
 *    early on non-viability, which would hide step-level divergence).
 *  - Per step record: per-class occupant counts + a hash of the full grid
 *    layout (cell -> class).
 *  - Properties (checked on every iteration, independent of the golden):
 *    no exception; initial occupancy fraction sane; all 10 species present
 *    initially; every grid occupant's own location equals its cell; the grid
 *    changes over time; two identical runs give identical traces.
 *  - Then the trace must equal the embedded GOLDEN trace.
 *
 * The Simulator is inseparable from its Swing JFrame view, so this unit runs
 * NON-headless; windows are disposed and the driver exits via System.exit.
 * Env GOLDEN_DUMP=1 prints the measured trace (used once at iteration 0).
 */
public class Driver {
    static final int DEPTH = 60, WIDTH = 80, STEPS = 120;
    static final long MATH_SEED = 20260925L;

    public static void main(String[] args) {
        String fail = null;
        String pass = null;
        try {
            Path classes = Paths.get(args[0]).resolve("_classes");
            boolean dump = System.getenv("GOLDEN_DUMP") != null;
            List<String> t1 = runTrace(classes);
            List<String> t2 = runTrace(classes);
            if (dump) for (String s : t1) System.out.println("GOLDEN\t" + s);
            int nd = firstDiff(t1, t2);
            if (nd >= 0) {
                System.out.println("run1[" + nd + "]=" + (nd < t1.size() ? t1.get(nd) : "<end>"));
                System.out.println("run2[" + nd + "]=" + (nd < t2.size() ? t2.get(nd) : "<end>"));
                fail = "nondeterministic: two identical seeded runs diverge at trace line " + nd;
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
                    pass = "properties ok + deterministic " + STEPS + "-step population/layout trace matches golden";
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

    /** Seed java.lang.Math.random()'s hidden generator (used by Gender.getRandom). */
    static void seedMathRandom(long seed) throws Exception {
        Math.random(); // force holder initialisation
        Class<?> holder = Class.forName("java.lang.Math$RandomNumberGeneratorHolder");
        java.lang.reflect.Field f = holder.getDeclaredField("randomNumberGenerator");
        f.setAccessible(true);
        ((Random) f.get(null)).setSeed(seed);
    }

    static List<String> runTrace(Path classes) throws Exception {
        List<String> trace = new ArrayList<>();
        try (URLClassLoader cl = new URLClassLoader(
                new URL[]{classes.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            try {
                seedMathRandom(MATH_SEED);
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

                Map<String, Integer> c0 = counts(field);
                int occ = c0.values().stream().mapToInt(Integer::intValue).sum();
                double frac = occ / (double) (DEPTH * WIDTH);
                System.out.println("initial: " + c0 + " frac=" + String.format(Locale.ROOT, "%.3f", frac));
                if (frac < 0.5 || frac > 0.99)
                    throw new PropertyFailure("initial occupancy " + frac + " outside [0.5,0.99] - populate broken");
                if (c0.size() < 10)
                    throw new PropertyFailure("only " + c0.size() + " species initially " + c0.keySet() + " (expected 10) - populate broken");
                checkLocations(field, 0);
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
                    checkLocations(field, s);
                    trace.add(line(s, field));
                }
                System.out.println("final:   " + counts(field));
                if (layout(field).equals(layout0))
                    throw new PropertyFailure("grid unchanged after " + STEPS + " steps - simulation did not progress");
            } finally {
                disposeWindows();
            }
        }
        return trace;
    }

    static String line(int step, Object field) throws Exception {
        return step + " " + counts(field) + " h=" + Long.toHexString(fnv(layout(field)));
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
                Object loc = callNoArg(o, "getLocation");
                if (loc == null)
                    throw new PropertyFailure("step " + step + ": " + o.getClass().getSimpleName()
                            + " on grid at " + r + "," + c + " has null location (dead actor left on grid)");
                int lr = (Integer) callNoArg(loc, "getRow"), lc = (Integer) callNoArg(loc, "getCol");
                if (lr != r || lc != c)
                    throw new PropertyFailure("step " + step + ": " + o.getClass().getSimpleName()
                            + " on grid at " + r + "," + c + " thinks it is at " + lr + "," + lc);
            }
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
        "0 {Aardvark=215, Acacia=51, Grasshopper=743, HarvesterAnt=1011, Impala=344, Mongoose=158, Pangolin=267, RedOatGrass=67, StarGrass=65, Termite=647} h=8018b35b845c3e28",
        "1 {Aardvark=234, Acacia=56, Carcass=282, Grasshopper=776, HarvesterAnt=1086, Impala=362, Mongoose=172, Pangolin=298, RedOatGrass=67, StarGrass=66, Termite=655} h=8c0474ac70255798",
        "2 {Aardvark=95, Acacia=62, Carcass=368, Grasshopper=803, HarvesterAnt=1107, Impala=341, Mongoose=73, Pangolin=187, RedOatGrass=69, StarGrass=68, Termite=680} h=536fcd86f3f9ca32",
        "3 {Aardvark=63, Acacia=68, Carcass=411, Grasshopper=860, HarvesterAnt=1209, Impala=325, Mongoose=30, Pangolin=136, RedOatGrass=71, StarGrass=70, Termite=712} h=5cac7c932f995ead",
        "4 {Aardvark=56, Acacia=72, Carcass=430, Grasshopper=912, HarvesterAnt=1263, Impala=295, Mongoose=15, Pangolin=95, RedOatGrass=74, StarGrass=72, Termite=729} h=7d950c76765ccd4",
        "5 {Aardvark=53, Acacia=76, Carcass=447, Grasshopper=935, HarvesterAnt=1269, Impala=278, Mongoose=9, Pangolin=77, RedOatGrass=78, StarGrass=72, Termite=722} h=21f17ce1d1434a77",
        "6 {Aardvark=58, Acacia=81, Carcass=452, Grasshopper=964, HarvesterAnt=1268, Impala=254, Mongoose=2, Pangolin=59, RedOatGrass=82, StarGrass=73, Termite=713} h=b0090824e5f7c0e",
        "7 {Aardvark=67, Acacia=84, Carcass=453, Grasshopper=977, HarvesterAnt=1285, Impala=244, Mongoose=1, Pangolin=48, RedOatGrass=84, StarGrass=76, Termite=744} h=38be5b4663e481a",
        "8 {Aardvark=77, Acacia=88, Carcass=450, Grasshopper=1005, HarvesterAnt=1285, Impala=228, Pangolin=43, RedOatGrass=86, StarGrass=76, Termite=751} h=1dd3f8c780e6b381",
        "9 {Aardvark=91, Acacia=93, Carcass=455, Grasshopper=995, HarvesterAnt=1265, Impala=201, Pangolin=44, RedOatGrass=87, StarGrass=78, Termite=746} h=ff7e0c92fd86e639",
        "10 {Aardvark=110, Acacia=101, Carcass=447, Grasshopper=985, HarvesterAnt=1271, Impala=185, Pangolin=33, RedOatGrass=91, StarGrass=81, Termite=757} h=a87fa5142239b102",
        "11 {Aardvark=127, Acacia=110, Carcass=446, Grasshopper=956, HarvesterAnt=1277, Impala=182, Pangolin=27, RedOatGrass=93, StarGrass=84, Termite=784} h=3c6e8df832a22db3",
        "12 {Aardvark=143, Acacia=112, Carcass=451, Grasshopper=952, HarvesterAnt=1280, Impala=183, Pangolin=21, RedOatGrass=96, StarGrass=86, Termite=766} h=d806aefa9cbfd74f",
        "13 {Aardvark=159, Acacia=121, Carcass=449, Grasshopper=938, HarvesterAnt=1269, Impala=178, Pangolin=18, RedOatGrass=102, StarGrass=88, Termite=781} h=914f80e9d0a8b8ba",
        "14 {Aardvark=177, Acacia=125, Carcass=451, Grasshopper=959, HarvesterAnt=1240, Impala=155, Pangolin=13, RedOatGrass=106, StarGrass=89, Termite=776} h=12103c3c0d434d9a",
        "15 {Aardvark=201, Acacia=131, Carcass=442, Grasshopper=952, HarvesterAnt=1253, Impala=141, Pangolin=13, RedOatGrass=108, StarGrass=91, Termite=782} h=227c435c8337836c",
        "16 {Aardvark=213, Acacia=137, Carcass=443, Grasshopper=953, HarvesterAnt=1235, Impala=130, Pangolin=6, RedOatGrass=110, StarGrass=94, Termite=778} h=65db2a266f2f0bfe",
        "17 {Aardvark=233, Acacia=148, Carcass=445, Grasshopper=932, HarvesterAnt=1237, Impala=130, Pangolin=3, RedOatGrass=112, StarGrass=97, Termite=779} h=6a1686eab3810b2a",
        "18 {Aardvark=254, Acacia=151, Carcass=438, Grasshopper=927, HarvesterAnt=1226, Impala=119, Pangolin=2, RedOatGrass=117, StarGrass=99, Termite=784} h=88cd07e620213554",
        "19 {Aardvark=271, Acacia=157, Carcass=445, Grasshopper=916, HarvesterAnt=1212, Impala=111, Pangolin=1, RedOatGrass=122, StarGrass=100, Termite=790} h=b6388e9692cfb6cb",
        "20 {Aardvark=297, Acacia=161, Carcass=436, Grasshopper=907, HarvesterAnt=1184, Impala=109, Pangolin=1, RedOatGrass=128, StarGrass=102, Termite=772} h=c9d7369d5fd21d0d",
        "21 {Aardvark=318, Acacia=169, Carcass=431, Grasshopper=899, HarvesterAnt=1165, Impala=109, Pangolin=1, RedOatGrass=131, StarGrass=104, Termite=784} h=a7a77c8c36550be8",
        "22 {Aardvark=337, Acacia=175, Carcass=419, Grasshopper=889, HarvesterAnt=1155, Impala=100, Pangolin=1, RedOatGrass=134, StarGrass=106, Termite=789} h=81b5dfec490af855",
        "23 {Aardvark=353, Acacia=182, Carcass=421, Grasshopper=886, HarvesterAnt=1132, Impala=95, Pangolin=1, RedOatGrass=139, StarGrass=109, Termite=791} h=927dc77ed3bfc6c8",
        "24 {Aardvark=369, Acacia=193, Carcass=414, Grasshopper=880, HarvesterAnt=1089, Impala=91, Pangolin=1, RedOatGrass=141, StarGrass=115, Termite=801} h=531a4a83e948f6f0",
        "25 {Aardvark=380, Acacia=203, Carcass=424, Grasshopper=844, HarvesterAnt=1080, Impala=93, Pangolin=1, RedOatGrass=143, StarGrass=118, Termite=825} h=161edb97f0514665",
        "26 {Aardvark=395, Acacia=213, Carcass=421, Grasshopper=836, HarvesterAnt=1045, Impala=93, RedOatGrass=146, StarGrass=120, Termite=829} h=cc313f0b05763d9c",
        "27 {Aardvark=424, Acacia=220, Carcass=412, Grasshopper=846, HarvesterAnt=1027, Impala=91, RedOatGrass=151, StarGrass=122, Termite=839} h=632837a1f8a40a93",
        "28 {Aardvark=447, Acacia=228, Carcass=399, Grasshopper=843, HarvesterAnt=1001, Impala=92, RedOatGrass=156, StarGrass=125, Termite=824} h=f0371912ee049f4",
        "29 {Aardvark=477, Acacia=237, Carcass=396, Grasshopper=818, HarvesterAnt=977, Impala=87, RedOatGrass=161, StarGrass=127, Termite=847} h=20543af09f97bc5f",
        "30 {Aardvark=498, Acacia=248, Carcass=393, Grasshopper=819, HarvesterAnt=964, Impala=75, RedOatGrass=168, StarGrass=130, Termite=835} h=1526ad239dc12f7",
        "31 {Aardvark=521, Acacia=200, Carcass=397, Grasshopper=844, HarvesterAnt=902, Impala=68, RedOatGrass=173, StarGrass=136, Termite=849} h=23147ea746380496",
        "32 {Aardvark=546, Acacia=204, Carcass=401, Grasshopper=857, HarvesterAnt=881, Impala=61, RedOatGrass=177, StarGrass=141, Termite=866} h=8840aba8d6f38013",
        "33 {Aardvark=563, Acacia=210, Carcass=391, Grasshopper=840, HarvesterAnt=842, Impala=63, RedOatGrass=180, StarGrass=144, Termite=875} h=631768b2d4e6db49",
        "34 {Aardvark=583, Acacia=213, Carcass=386, Grasshopper=847, HarvesterAnt=814, Impala=66, RedOatGrass=184, StarGrass=149, Termite=890} h=a875ad96fc02b04b",
        "35 {Aardvark=596, Acacia=222, Carcass=380, Grasshopper=834, HarvesterAnt=807, Impala=60, RedOatGrass=187, StarGrass=153, Termite=907} h=63eaebf543cbefb2",
        "36 {Aardvark=620, Acacia=234, Carcass=370, Grasshopper=815, HarvesterAnt=783, Impala=63, RedOatGrass=188, StarGrass=160, Termite=919} h=aa1ef01462f310cd",
        "37 {Aardvark=651, Acacia=238, Carcass=370, Grasshopper=784, HarvesterAnt=766, Impala=61, RedOatGrass=191, StarGrass=171, Termite=922} h=445fece907041aa3",
        "38 {Aardvark=651, Acacia=244, Carcass=371, Grasshopper=771, HarvesterAnt=758, Impala=54, RedOatGrass=195, StarGrass=176, Termite=949} h=377267c432a47146",
        "39 {Aardvark=662, Acacia=249, Carcass=362, Grasshopper=751, HarvesterAnt=723, Impala=50, RedOatGrass=198, StarGrass=182, Termite=966} h=6d6766cb2735f001",
        "40 {Aardvark=666, Acacia=257, Carcass=361, Grasshopper=740, HarvesterAnt=699, Impala=48, RedOatGrass=201, StarGrass=184, Termite=986} h=4468d261b3b2c6fa",
        "41 {Aardvark=690, Acacia=258, Carcass=358, Grasshopper=729, HarvesterAnt=676, Impala=44, RedOatGrass=209, StarGrass=190, Termite=1009} h=6c7f1187b546425",
        "42 {Aardvark=702, Acacia=265, Carcass=350, Grasshopper=698, HarvesterAnt=658, Impala=44, RedOatGrass=211, StarGrass=198, Termite=1012} h=189f6eefd79715e4",
        "43 {Aardvark=710, Acacia=271, Carcass=335, Grasshopper=692, HarvesterAnt=639, Impala=45, RedOatGrass=216, StarGrass=210, Termite=1026} h=85bcc456dd3baaf9",
        "44 {Aardvark=725, Acacia=278, Carcass=330, Grasshopper=677, HarvesterAnt=625, Impala=44, RedOatGrass=222, StarGrass=212, Termite=1050} h=c9ed8cb6e9b2f43",
        "45 {Aardvark=740, Acacia=287, Carcass=332, Grasshopper=669, HarvesterAnt=600, Impala=41, RedOatGrass=226, StarGrass=220, Termite=1051} h=7bba9157ab0bce2a",
        "46 {Aardvark=751, Acacia=295, Carcass=319, Grasshopper=664, HarvesterAnt=569, Impala=43, RedOatGrass=236, StarGrass=226, Termite=1060} h=290935ab27c6aa45",
        "47 {Aardvark=745, Acacia=303, Carcass=311, Grasshopper=647, HarvesterAnt=534, Impala=47, RedOatGrass=239, StarGrass=231, Termite=1094} h=d348c3c8b5660f6d",
        "48 {Aardvark=765, Acacia=307, Carcass=307, Grasshopper=626, HarvesterAnt=515, Impala=54, RedOatGrass=242, StarGrass=236, Termite=1112} h=2f324ebeb2f7a1de",
        "49 {Aardvark=778, Acacia=316, Carcass=302, Grasshopper=602, HarvesterAnt=507, Impala=58, RedOatGrass=252, StarGrass=241, Termite=1118} h=9314eaf779436391",
        "50 {Aardvark=780, Acacia=312, Carcass=297, Grasshopper=582, HarvesterAnt=483, Impala=63, RedOatGrass=255, StarGrass=241, Termite=1139} h=2a0a737a7a25be2a",
        "51 {Aardvark=787, Acacia=310, Carcass=290, Grasshopper=587, HarvesterAnt=482, Impala=60, RedOatGrass=255, StarGrass=242, Termite=1161} h=6c3a49bbd1301377",
        "52 {Aardvark=799, Acacia=305, Carcass=284, Grasshopper=591, HarvesterAnt=471, Impala=64, RedOatGrass=257, StarGrass=245, Termite=1198} h=fa22542d250528e4",
        "53 {Aardvark=787, Acacia=302, Carcass=281, Grasshopper=560, HarvesterAnt=469, Impala=66, RedOatGrass=257, StarGrass=246, Termite=1215} h=280b9e7142c8ed3b",
        "54 {Aardvark=771, Acacia=298, Carcass=273, Grasshopper=574, HarvesterAnt=443, Impala=68, RedOatGrass=259, StarGrass=247, Termite=1230} h=be62008aabd5e11a",
        "55 {Aardvark=775, Acacia=291, Carcass=277, Grasshopper=562, HarvesterAnt=436, Impala=73, RedOatGrass=259, StarGrass=250, Termite=1257} h=936d2549bfa42237",
        "56 {Aardvark=760, Acacia=283, Carcass=276, Grasshopper=549, HarvesterAnt=419, Impala=70, RedOatGrass=260, StarGrass=250, Termite=1293} h=2e14ac538ff824ad",
        "57 {Aardvark=758, Acacia=274, Carcass=271, Grasshopper=537, HarvesterAnt=412, Impala=80, RedOatGrass=260, StarGrass=252, Termite=1331} h=aed4ea8644ff205a",
        "58 {Aardvark=763, Acacia=271, Carcass=271, Grasshopper=535, HarvesterAnt=388, Impala=73, RedOatGrass=262, StarGrass=253, Termite=1345} h=3a52892722356869",
        "59 {Aardvark=766, Acacia=267, Carcass=273, Grasshopper=536, HarvesterAnt=364, Impala=74, RedOatGrass=264, StarGrass=254, Termite=1365} h=4f99dde82fb16bcf",
        "60 {Aardvark=769, Acacia=259, Carcass=273, Grasshopper=540, HarvesterAnt=360, Impala=72, RedOatGrass=266, StarGrass=255, Termite=1363} h=2c2e9ad03544567e",
        "61 {Aardvark=774, Acacia=250, Carcass=266, Grasshopper=549, HarvesterAnt=366, Impala=69, RedOatGrass=266, StarGrass=258, Termite=1383} h=c08438df00ce7db3",
        "62 {Aardvark=767, Acacia=248, Carcass=261, Grasshopper=546, HarvesterAnt=365, Impala=64, RedOatGrass=266, StarGrass=259, Termite=1378} h=bc8de96a92b39a32",
        "63 {Aardvark=792, Acacia=240, Carcass=256, Grasshopper=539, HarvesterAnt=350, Impala=66, RedOatGrass=266, StarGrass=262, Termite=1401} h=7ac9cd519d19da2f",
        "64 {Aardvark=782, Acacia=231, Carcass=249, Grasshopper=540, HarvesterAnt=344, Impala=66, RedOatGrass=267, StarGrass=265, Termite=1414} h=963259200714346f",
        "65 {Aardvark=757, Acacia=225, Carcass=247, Grasshopper=554, HarvesterAnt=331, Impala=67, RedOatGrass=268, StarGrass=268, Termite=1445} h=6e228e4bf9839e42",
        "66 {Aardvark=754, Acacia=216, Carcass=240, Grasshopper=554, HarvesterAnt=316, Impala=73, RedOatGrass=268, StarGrass=268, Termite=1478} h=a741c884b70a4809",
        "67 {Aardvark=733, Acacia=202, Carcass=241, Grasshopper=549, HarvesterAnt=318, Impala=65, RedOatGrass=271, StarGrass=269, Termite=1486} h=4ab44a4ca3708823",
        "68 {Aardvark=728, Acacia=194, Carcass=241, Grasshopper=555, HarvesterAnt=326, Impala=65, RedOatGrass=273, StarGrass=270, Termite=1531} h=95520b399fd19e8a",
        "69 {Aardvark=717, Acacia=186, Carcass=241, Grasshopper=569, HarvesterAnt=320, Impala=72, RedOatGrass=273, StarGrass=270, Termite=1523} h=35ebce0532a43e0",
        "70 {Aardvark=719, Acacia=177, Carcass=242, Grasshopper=567, HarvesterAnt=317, Impala=71, RedOatGrass=274, StarGrass=270, Termite=1529} h=2508bde359981aae",
        "71 {Aardvark=730, Acacia=167, Carcass=242, Grasshopper=619, HarvesterAnt=333, Impala=66, RedOatGrass=208, StarGrass=207, Termite=1604} h=db8e3df66e291e66",
        "72 {Aardvark=714, Acacia=160, Carcass=240, Grasshopper=631, HarvesterAnt=328, Impala=68, RedOatGrass=211, StarGrass=207, Termite=1621} h=c77b18ef01a97371",
        "73 {Aardvark=699, Acacia=145, Carcass=240, Grasshopper=644, HarvesterAnt=320, Impala=70, RedOatGrass=210, StarGrass=207, Termite=1621} h=153d9c70ffed3add",
        "74 {Aardvark=700, Acacia=138, Carcass=239, Grasshopper=639, HarvesterAnt=315, Impala=68, RedOatGrass=209, StarGrass=206, Termite=1661} h=9cec505bf5bad003",
        "75 {Aardvark=675, Acacia=125, Carcass=238, Grasshopper=647, HarvesterAnt=320, Impala=71, RedOatGrass=215, StarGrass=207, Termite=1668} h=faf6680a7f6f0fe9",
        "76 {Aardvark=665, Acacia=117, Carcass=238, Grasshopper=641, HarvesterAnt=318, Impala=72, RedOatGrass=220, StarGrass=211, Termite=1668} h=19efc38f27e200ad",
        "77 {Aardvark=638, Acacia=108, Carcass=237, Grasshopper=655, HarvesterAnt=312, Impala=72, RedOatGrass=222, StarGrass=213, Termite=1694} h=8239aa8697025f2f",
        "78 {Aardvark=626, Acacia=95, Carcass=237, Grasshopper=665, HarvesterAnt=298, Impala=68, RedOatGrass=227, StarGrass=219, Termite=1696} h=2a09d78822e4e460",
        "79 {Aardvark=619, Acacia=89, Carcass=237, Grasshopper=671, HarvesterAnt=302, Impala=67, RedOatGrass=227, StarGrass=223, Termite=1730} h=4b12eb8ee5a2b036",
        "80 {Aardvark=601, Acacia=80, Carcass=237, Grasshopper=670, HarvesterAnt=288, Impala=64, RedOatGrass=236, StarGrass=229, Termite=1741} h=b66345aebad9eb51",
        "81 {Aardvark=585, Acacia=82, Carcass=236, Grasshopper=663, HarvesterAnt=294, Impala=63, RedOatGrass=240, StarGrass=231, Termite=1767} h=c7776e26f261e71b",
        "82 {Aardvark=586, Acacia=85, Carcass=236, Grasshopper=664, HarvesterAnt=277, Impala=61, RedOatGrass=245, StarGrass=232, Termite=1782} h=6329f93d4c8fb4c4",
        "83 {Aardvark=577, Acacia=84, Carcass=236, Grasshopper=662, HarvesterAnt=281, Impala=55, RedOatGrass=247, StarGrass=233, Termite=1789} h=b185aa104bef073c",
        "84 {Aardvark=578, Acacia=88, Carcass=236, Grasshopper=655, HarvesterAnt=284, Impala=56, RedOatGrass=251, StarGrass=236, Termite=1779} h=df3befd4ea30ba00",
        "85 {Aardvark=560, Acacia=88, Carcass=236, Grasshopper=654, HarvesterAnt=286, Impala=56, RedOatGrass=253, StarGrass=238, Termite=1772} h=d2f325959ca54c02",
        "86 {Aardvark=563, Acacia=91, Carcass=236, Grasshopper=668, HarvesterAnt=285, Impala=59, RedOatGrass=258, StarGrass=246, Termite=1787} h=9a57830b7f437e2f",
        "87 {Aardvark=543, Acacia=94, Carcass=236, Grasshopper=670, HarvesterAnt=277, Impala=61, RedOatGrass=262, StarGrass=248, Termite=1757} h=d3a2df1195812f0",
        "88 {Aardvark=541, Acacia=98, Carcass=236, Grasshopper=665, HarvesterAnt=289, Impala=68, RedOatGrass=268, StarGrass=251, Termite=1772} h=27d65a16d2eefe60",
        "89 {Aardvark=532, Acacia=99, Carcass=236, Grasshopper=644, HarvesterAnt=278, Impala=71, RedOatGrass=276, StarGrass=252, Termite=1780} h=5298e187ebd28abd",
        "90 {Aardvark=533, Acacia=99, Carcass=236, Grasshopper=646, HarvesterAnt=269, Impala=72, RedOatGrass=283, StarGrass=260, Termite=1797} h=1cda51c6440122d",
        "91 {Aardvark=520, Acacia=101, Carcass=235, Grasshopper=646, HarvesterAnt=274, Impala=69, RedOatGrass=282, StarGrass=268, Termite=1800} h=aeb9b059a6745b96",
        "92 {Aardvark=510, Acacia=102, Carcass=233, Grasshopper=630, HarvesterAnt=265, Impala=68, RedOatGrass=288, StarGrass=270, Termite=1814} h=2252c8e8e8cd927d",
        "93 {Aardvark=506, Acacia=105, Carcass=232, Grasshopper=612, HarvesterAnt=266, Impala=66, RedOatGrass=294, StarGrass=274, Termite=1821} h=896a6ce35ebe25cb",
        "94 {Aardvark=495, Acacia=108, Carcass=232, Grasshopper=608, HarvesterAnt=257, Impala=69, RedOatGrass=296, StarGrass=280, Termite=1861} h=69ee93662fd02070",
        "95 {Aardvark=490, Acacia=109, Carcass=232, Grasshopper=598, HarvesterAnt=251, Impala=64, RedOatGrass=304, StarGrass=278, Termite=1857} h=46321980d460119",
        "96 {Aardvark=482, Acacia=112, Carcass=231, Grasshopper=602, HarvesterAnt=246, Impala=64, RedOatGrass=313, StarGrass=284, Termite=1851} h=f69347cefa169177",
        "97 {Aardvark=472, Acacia=115, Carcass=231, Grasshopper=595, HarvesterAnt=249, Impala=67, RedOatGrass=322, StarGrass=289, Termite=1842} h=c1e3b959572e675a",
        "98 {Aardvark=470, Acacia=116, Carcass=231, Grasshopper=602, HarvesterAnt=241, Impala=64, RedOatGrass=322, StarGrass=298, Termite=1831} h=f60d9de88b62a62c",
        "99 {Aardvark=463, Acacia=120, Carcass=231, Grasshopper=614, HarvesterAnt=222, Impala=65, RedOatGrass=323, StarGrass=298, Termite=1851} h=78c56988b8fc0ba8",
        "100 {Aardvark=460, Acacia=119, Carcass=231, Grasshopper=610, HarvesterAnt=220, Impala=67, RedOatGrass=319, StarGrass=298, Termite=1867} h=b50ebd27f1511fc9",
        "101 {Aardvark=458, Acacia=120, Carcass=231, Grasshopper=614, HarvesterAnt=223, Impala=62, RedOatGrass=315, StarGrass=296, Termite=1865} h=e52d26482a53abf1",
        "102 {Aardvark=446, Acacia=117, Carcass=231, Grasshopper=617, HarvesterAnt=225, Impala=61, RedOatGrass=314, StarGrass=290, Termite=1871} h=9af5c953aec4544d",
        "103 {Aardvark=436, Acacia=116, Carcass=231, Grasshopper=620, HarvesterAnt=227, Impala=62, RedOatGrass=310, StarGrass=287, Termite=1915} h=7799f0217581bd4c",
        "104 {Aardvark=437, Acacia=117, Carcass=231, Grasshopper=605, HarvesterAnt=230, Impala=57, RedOatGrass=307, StarGrass=285, Termite=1916} h=9fac2bdcd5aeed50",
        "105 {Aardvark=437, Acacia=116, Carcass=231, Grasshopper=618, HarvesterAnt=223, Impala=58, RedOatGrass=305, StarGrass=281, Termite=1887} h=940362af57ddeb77",
        "106 {Aardvark=428, Acacia=113, Carcass=231, Grasshopper=643, HarvesterAnt=221, Impala=55, RedOatGrass=305, StarGrass=278, Termite=1910} h=557684d672ab2f3f",
        "107 {Aardvark=402, Acacia=111, Carcass=231, Grasshopper=649, HarvesterAnt=219, Impala=55, RedOatGrass=306, StarGrass=272, Termite=1920} h=39dfef1c80cb9b12",
        "108 {Aardvark=391, Acacia=107, Carcass=231, Grasshopper=653, HarvesterAnt=217, Impala=55, RedOatGrass=306, StarGrass=263, Termite=1952} h=92edbd623685833b",
        "109 {Aardvark=384, Acacia=108, Carcass=231, Grasshopper=666, HarvesterAnt=213, Impala=54, RedOatGrass=302, StarGrass=260, Termite=1963} h=39720ff6e169e94d",
        "110 {Aardvark=366, Acacia=99, Carcass=231, Grasshopper=664, HarvesterAnt=216, Impala=50, RedOatGrass=299, StarGrass=255, Termite=1989} h=7e23e361bf9ea758",
        "111 {Aardvark=370, Acacia=97, Carcass=231, Grasshopper=663, HarvesterAnt=219, Impala=52, RedOatGrass=298, StarGrass=254, Termite=1978} h=3b48f982cbb68a3e",
        "112 {Aardvark=358, Acacia=95, Carcass=231, Grasshopper=665, HarvesterAnt=219, Impala=52, RedOatGrass=294, StarGrass=249, Termite=2014} h=df0c394ea7dfe973",
        "113 {Aardvark=350, Acacia=91, Carcass=231, Grasshopper=671, HarvesterAnt=215, Impala=51, RedOatGrass=294, StarGrass=243, Termite=2038} h=3e7003ce96560312",
        "114 {Aardvark=353, Acacia=89, Carcass=231, Grasshopper=673, HarvesterAnt=213, Impala=47, RedOatGrass=296, StarGrass=231, Termite=2039} h=f80a84a8362d2142",
        "115 {Aardvark=345, Acacia=83, Carcass=231, Grasshopper=677, HarvesterAnt=212, Impala=47, RedOatGrass=291, StarGrass=231, Termite=2081} h=676c9ee796b07ccb",
        "116 {Aardvark=339, Acacia=80, Carcass=230, Grasshopper=666, HarvesterAnt=212, Impala=51, RedOatGrass=291, StarGrass=225, Termite=2094} h=5aa980ba894477f1",
        "117 {Aardvark=338, Acacia=73, Carcass=230, Grasshopper=654, HarvesterAnt=209, Impala=49, RedOatGrass=282, StarGrass=220, Termite=2131} h=de020cbd511b396e",
        "118 {Aardvark=335, Acacia=69, Carcass=230, Grasshopper=657, HarvesterAnt=211, Impala=53, RedOatGrass=280, StarGrass=215, Termite=2120} h=3094f11ed53a28fe",
        "119 {Aardvark=317, Acacia=64, Carcass=230, Grasshopper=642, HarvesterAnt=207, Impala=53, RedOatGrass=277, StarGrass=211, Termite=2144} h=69d30dd368bb1047",
        "120 {Aardvark=319, Acacia=60, Carcass=230, Grasshopper=649, HarvesterAnt=216, Impala=55, RedOatGrass=268, StarGrass=209, Termite=2165} h=2f4a23734657ae1",
    };
}
