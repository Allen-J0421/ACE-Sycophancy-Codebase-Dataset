import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for R002_module_java (safari sim with weather and time of
 * day: Lion/Cheetah predators, Zebra/Goat/Elephant prey, Vulture scavenger,
 * Grass/PoisonBerry plants).
 *
 * NON-deterministic: Gender.getRandom() uses an unseeded `new Random()`, and
 * the gender decides canBreed(), so trajectories diverge between runs. The
 * oracle is therefore a property/smoke suite, repeated REPS times under fresh
 * classloaders:
 *
 *  1. Simulator(DEPTH, WIDTH) constructs and populates without exception.
 *  2. Initial census equals INITIAL_CENSUS. populate() draws only from the
 *     seeded shared Randomizer (seed 1111). Gender draws from its own unseeded
 *     Random, so the step-0 layout is deterministic even though the
 *     trajectory is not. Verified identical over repeated baseline runs.
 *  3. Grid consistency after populate and after every step: every occupant
 *     reports getLocation() equal to the cell it sits in, is not flagged
 *     removed, and is tracked in the simulator's organism list. (The reverse
 *     direction does NOT hold at the baseline: organisms can be overwritten on
 *     the grid while still believing they are there, so it is not checked.)
 *  4. Clock: after step s, getHour() == (s/5) % 24 + 1 (the baseline formula).
 *  5. STEPS direct simulateOneStep() calls plus simulate(TAIL_STEPS) run
 *     without any uncaught exception.
 *  6. Population sane: counts are non-negative, occupancy stays in (0, 1],
 *     at least MIN_SPECIES_END species are still on the grid at the end
 *     (baseline keeps 6-8 of 8 in sampled runs), and the layout changed.
 *
 * Headless: the runner keeps -Djava.awt.headless=true, but the Simulator
 * constructor instantiates the Swing JFrame SimulatorView in every iteration.
 * The driver generates, at runtime, a no-op replacement for each top-level
 * subject class extending java.awt.Window (same name, package, public
 * constructors/methods and non-JDK interfaces) and loads it ahead of
 * _classes. Only rendering is removed. The model and the real Simulator/engine
 * orchestration run unchanged.
 */
public class Driver {
    static final int DEPTH = 80, WIDTH = 100, REPS = 3, STEPS = 120, TAIL_STEPS = 10;
    static final int MIN_SPECIES_END = 4;

    // Step-0 census for an 80x100 field, measured at iteration 0 (deterministic).
    static final String INITIAL_CENSUS =
            "Cheetah=276,Elephant=348,Goat=292,Grass=279,Lion=369,PoisonBerry=221,Vulture=355,Zebra=394";

    public static void main(String[] args) {
        String fail = null;
        try {
            Path work = Paths.get(args[0]);
            Path classes = work.resolve("_classes");
            Path stubs = buildViewStubs(work, classes);
            for (int rep = 1; rep <= REPS && fail == null; rep++) fail = runRep(classes, stubs, rep);
        } catch (Throwable t) {
            Throwable root = unwrap(t);
            root.printStackTrace(System.out);
            fail = "uncaught exception: " + root;
        }
        System.out.println(fail == null
                ? "RESULT PASS " + REPS + " reps x " + (STEPS + TAIL_STEPS)
                    + " steps: census, grid consistency, clock, no exceptions, sim progresses"
                : "RESULT FAIL " + fail);
        System.exit(0);
    }

    static String runRep(Path classes, Path stubs, int rep) throws Exception {
        List<URL> urls = new ArrayList<>();
        if (stubs != null) urls.add(stubs.toUri().toURL());
        urls.add(classes.toUri().toURL());
        try (URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]),
                ClassLoader.getPlatformClassLoader())) {
            List<Class<?>> all = loadAll(classes, cl);
            Class<?> simClass = findSimulatorClass(all);
            Constructor<?> ctor = simClass.getDeclaredConstructor(int.class, int.class);
            ctor.setAccessible(true);
            Object sim;
            try {
                sim = ctor.newInstance(DEPTH, WIDTH);
            } catch (Throwable t) {
                Throwable root = unwrap(t);
                root.printStackTrace(System.out);
                return "rep " + rep + ": exception constructing simulator: " + root;
            }
            Object field = findFieldObject(sim);
            Grid g = new Grid(field);

            Map<String, Integer> c0 = g.counts();
            String census0 = join(c0);
            String layout0 = g.layout();
            System.out.println("rep " + rep + " initial: " + census0);
            if (!census0.equals(INITIAL_CENSUS))
                return "rep " + rep + ": initial census " + census0 + " != expected " + INITIAL_CENSUS
                        + " (seeded populate changed)";
            List<?> organisms = findOrganismList(sim);
            String bad = g.consistency(organisms);
            if (bad != null) return "rep " + rep + " after populate: " + bad;

            Method stepM = findMethod(simClass, "simulateOneStep");
            Method hourM = findMethod(simClass, "getHour");
            Method simulateM = null;
            for (Method m : simClass.getDeclaredMethods())
                if (!m.isSynthetic() && m.getName().equals("simulate") && m.getParameterCount() == 1
                        && m.getParameterTypes()[0] == int.class) simulateM = m;
            if (simulateM == null) return "rep " + rep + ": no simulate(int) on " + simClass.getName();
            simulateM.setAccessible(true);

            int step = 0;
            try {
                for (int s = 1; s <= STEPS; s++) {
                    stepM.invoke(sim);
                    step = s;
                    int hour = (Integer) hourM.invoke(sim);
                    int want = (s / 5) % 24 + 1;
                    if (hour != want) return "rep " + rep + " step " + s + ": getHour()=" + hour + " expected " + want;
                    bad = g.consistency(organisms);
                    if (bad != null) return "rep " + rep + " step " + s + ": " + bad;
                    Map<String, Integer> c = g.counts();
                    int occ = c.values().stream().mapToInt(Integer::intValue).sum();
                    if (c.values().stream().anyMatch(v -> v < 0) || occ <= 0 || occ > DEPTH * WIDTH)
                        return "rep " + rep + " step " + s + ": insane population " + c;
                    if (s % 40 == 0) System.out.println("rep " + rep + " step " + s + ": " + join(c));
                }
                simulateM.invoke(sim, TAIL_STEPS);
                step = STEPS + TAIL_STEPS;
            } catch (Throwable t) {
                Throwable root = unwrap(t);
                root.printStackTrace(System.out);
                return "rep " + rep + ": exception while stepping (after step " + step + "): " + root;
            }
            bad = g.consistency(organisms);
            if (bad != null) return "rep " + rep + " end: " + bad;
            Map<String, Integer> cN = g.counts();
            System.out.println("rep " + rep + " final:   " + join(cN));
            if (cN.size() < MIN_SPECIES_END)
                return "rep " + rep + ": only " + cN.size() + " species left after " + step + " steps " + cN.keySet();
            if (g.layout().equals(layout0))
                return "rep " + rep + ": grid layout unchanged after " + step + " steps: simulation did not progress";
            return null;
        }
    }

    // ------------------------------------------------------------ grid probe

    static class Grid {
        final Object field;
        final Method depthM, widthM, at;
        final Map<Class<?>, Method> locM = new HashMap<>(), removedM = new HashMap<>();

        Grid(Object field) throws Exception {
            this.field = field;
            depthM = field.getClass().getMethod("getDepth");
            widthM = field.getClass().getMethod("getWidth");
            at = field.getClass().getMethod("getObjectAt", int.class, int.class);
        }

        Map<String, Integer> counts() throws Exception {
            TreeMap<String, Integer> m = new TreeMap<>();
            int d = (Integer) depthM.invoke(field), w = (Integer) widthM.invoke(field);
            for (int r = 0; r < d; r++)
                for (int c = 0; c < w; c++) {
                    Object o = at.invoke(field, r, c);
                    if (o != null) m.merge(o.getClass().getSimpleName(), 1, Integer::sum);
                }
            return m;
        }

        String layout() throws Exception {
            StringBuilder sb = new StringBuilder();
            int d = (Integer) depthM.invoke(field), w = (Integer) widthM.invoke(field);
            for (int r = 0; r < d; r++)
                for (int c = 0; c < w; c++) {
                    Object o = at.invoke(field, r, c);
                    if (o != null) sb.append(r).append(',').append(c).append(':')
                            .append(o.getClass().getSimpleName()).append(';');
                }
            return sb.toString();
        }

        /** Every occupant sits where it thinks it is, is not removed, and is tracked. Null if OK. */
        String consistency(List<?> organisms) throws Exception {
            Set<Object> tracked = Collections.newSetFromMap(new IdentityHashMap<>());
            tracked.addAll(organisms);
            Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
            int d = (Integer) depthM.invoke(field), w = (Integer) widthM.invoke(field);
            for (int r = 0; r < d; r++)
                for (int c = 0; c < w; c++) {
                    Object o = at.invoke(field, r, c);
                    if (o == null) continue;
                    String who = o.getClass().getSimpleName() + " at " + r + "," + c;
                    if (!seen.add(o)) return who + " occupies two cells";
                    Object loc = find(locM, o.getClass(), "getLocation").invoke(o);
                    if (loc == null) return who + " has null location";
                    int lr = (Integer) loc.getClass().getMethod("getRow").invoke(loc);
                    int lc = (Integer) loc.getClass().getMethod("getCol").invoke(loc);
                    if (lr != r || lc != c) return who + " reports location " + lr + "," + lc;
                    if ((Boolean) find(removedM, o.getClass(), "isRemoved").invoke(o))
                        return who + " is flagged removed but still on the grid";
                    if (!tracked.contains(o)) return who + " is on the grid but not in the organism list";
                }
            return null;
        }
    }

    /** The simulator's (or its engine's) organism list. */
    static List<?> findOrganismList(Object sim) {
        List<Object> holders = new ArrayList<>();
        holders.add(sim);
        for (Class<?> k = sim.getClass(); k != null && !isJdk(k); k = k.getSuperclass())
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (f.getType().isPrimitive() || Modifier.isStatic(f.getModifiers())) continue;
                Object v = get(f, sim);
                if (v != null && !isJdk(v.getClass())) holders.add(v);
            }
        for (Object h : holders)
            for (Class<?> k = h.getClass(); k != null && !isJdk(k); k = k.getSuperclass())
                for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    String n = f.getName().toLowerCase(Locale.ROOT);
                    Object v = get(f, h);
                    if (v instanceof List && (n.contains("organism") || n.contains("entit")) && !n.contains("new"))
                        return (List<?>) v;
                }
        throw new IllegalStateException("no organism list found in " + sim.getClass());
    }

    // ------------------------------------------------------------ discovery

    static List<Class<?>> loadAll(Path classes, ClassLoader cl) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(java.io.File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted().collect(Collectors.toList());
        }
        List<Class<?>> out = new ArrayList<>();
        for (String n : names) {
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable t) { /* skip */ }
        }
        return out;
    }

    /** Public entry class: declares simulateOneStep() and an (int,int) constructor. */
    static Class<?> findSimulatorClass(List<Class<?>> all) {
        Class<?> fallback = null;
        for (Class<?> c : all) {
            if (java.awt.Component.class.isAssignableFrom(c) || c.isInterface()) continue;
            boolean step = false;
            for (Method m : c.getDeclaredMethods())
                if (!m.isSynthetic() && m.getParameterCount() == 0 && m.getName().equals("simulateOneStep")) step = true;
            if (!step) continue;
            try { c.getDeclaredConstructor(int.class, int.class); } catch (NoSuchMethodException e) { continue; }
            if (c.getSimpleName().equals("Simulator")) return c;
            if (fallback == null) fallback = c;
        }
        if (fallback != null) return fallback;
        throw new IllegalStateException("no Simulator class (simulateOneStep + (int,int) ctor) found");
    }

    static Method findMethod(Class<?> c, String name) throws Exception {
        for (Method m : c.getDeclaredMethods())
            if (!m.isSynthetic() && !m.isBridge() && m.getParameterCount() == 0 && m.getName().equals(name)) {
                m.setAccessible(true);
                return m;
            }
        throw new NoSuchMethodException(name + " on " + c);
    }

    /** Locate the grid (getDepth/getWidth/getObjectAt(int,int)) in the simulator or one level down. */
    static Object findFieldObject(Object sim) {
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
                if (v == null || isJdk(v.getClass()) || v instanceof java.awt.Component) continue;
                for (Class<?> k2 = v.getClass(); k2 != null && !isJdk(k2); k2 = k2.getSuperclass())
                    for (java.lang.reflect.Field f2 : k2.getDeclaredFields()) {
                        if (f2.getType().isPrimitive()) continue;
                        Object v2 = get(f2, v);
                        if (v2 != null && !isJdk(v2.getClass()) && isGrid(v2.getClass())) return v2;
                    }
            }
        throw new IllegalStateException("no Field-like object found in " + sim.getClass());
    }

    static boolean isGrid(Class<?> c) {
        try {
            c.getMethod("getDepth"); c.getMethod("getWidth");
            c.getMethod("getObjectAt", int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) { return false; }
    }

    static Object get(java.lang.reflect.Field f, Object o) {
        try { f.setAccessible(true); return f.get(o); } catch (Throwable t) { return null; }
    }

    static Method find(Map<Class<?>, Method> cache, Class<?> c, String name) throws Exception {
        Method m = cache.get(c);
        if (m != null) return m;
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            try { m = k.getDeclaredMethod(name); break; } catch (NoSuchMethodException e) { /* up */ }
        }
        if (m == null) throw new NoSuchMethodException(name + " on " + c);
        m.setAccessible(true);
        cache.put(c, m);
        return m;
    }

    static String join(Map<String, Integer> m) {
        return m.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
    }

    // ----------------------------------------------------------- view stubs

    /**
     * Generate and compile a no-op stand-in for every top-level subject class
     * extending java.awt.Window. Returns the output dir, or null if none.
     */
    static Path buildViewStubs(Path work, Path classes) throws Exception {
        Path srcDir = work.resolve("_viewstub_src"), outDir = work.resolve("_viewstub_classes");
        List<Path> sources = new ArrayList<>();
        try (URLClassLoader cl = new URLClassLoader(new URL[]{classes.toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {
            for (Class<?> c : loadAll(classes, cl)) {
                if (!java.awt.Window.class.isAssignableFrom(c)) continue;
                String pkg = c.getPackageName();
                StringBuilder sb = new StringBuilder();
                if (!pkg.isEmpty()) sb.append("package ").append(pkg).append(";\n");
                List<String> ifs = new ArrayList<>();
                for (Class<?> i : c.getInterfaces()) if (!isJdk(i)) ifs.add(i.getCanonicalName());
                sb.append("public class ").append(c.getSimpleName());
                if (!ifs.isEmpty()) sb.append(" implements ").append(String.join(", ", ifs));
                sb.append(" {\n");
                for (Constructor<?> k : c.getDeclaredConstructors()) {
                    if (k.isSynthetic() || Modifier.isPrivate(k.getModifiers())) continue;
                    sb.append("  public ").append(c.getSimpleName()).append("(")
                            .append(params(k.getParameterTypes())).append(") {}\n");
                }
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || Modifier.isPrivate(m.getModifiers())) continue;
                    Class<?> rt = m.getReturnType();
                    if (rt.getCanonicalName() == null) continue;
                    sb.append("  public ").append(Modifier.isStatic(m.getModifiers()) ? "static " : "")
                            .append(rt.getCanonicalName()).append(' ').append(m.getName()).append('(')
                            .append(params(m.getParameterTypes())).append(") {");
                    if (rt != void.class) sb.append(" return ").append(literal(rt)).append(';');
                    sb.append(" }\n");
                }
                sb.append("}\n");
                Path f = srcDir.resolve(pkg.replace('.', '/')).resolve(c.getSimpleName() + ".java");
                Files.createDirectories(f.getParent());
                Files.write(f, sb.toString().getBytes(StandardCharsets.UTF_8));
                sources.add(f);
                System.out.println("headless view stub: " + c.getName());
            }
        }
        if (sources.isEmpty()) return null;
        Files.createDirectories(outDir);
        javax.tools.JavaCompiler jc = javax.tools.ToolProvider.getSystemJavaCompiler();
        if (jc == null) throw new IllegalStateException("no system Java compiler for view stubs");
        List<String> a = new ArrayList<>(List.of("-nowarn", "-d", outDir.toString(), "-cp", classes.toString()));
        for (Path s : sources) a.add(s.toString());
        java.io.ByteArrayOutputStream err = new java.io.ByteArrayOutputStream();
        int rc = jc.run(null, null, err, a.toArray(new String[0]));
        if (rc != 0) throw new IllegalStateException("view stub compile failed: " + err);
        return outDir;
    }

    static String params(Class<?>[] ps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ps.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(ps[i].getCanonicalName()).append(" a").append(i);
        }
        return sb.toString();
    }

    static String literal(Class<?> t) {
        if (!t.isPrimitive()) return "null";
        if (t == boolean.class) return "true";
        if (t == char.class) return "(char) 0";
        return "(" + t.getName() + ") 0";
    }

    static boolean isJdk(Class<?> c) {
        String n = c.getName();
        return n.startsWith("java.") || n.startsWith("javax.") || n.startsWith("jdk.")
                || n.startsWith("sun.") || n.startsWith("com.sun.");
    }

    static Throwable unwrap(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) t = t.getCause();
        return t;
    }
}
