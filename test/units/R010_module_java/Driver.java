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
 * Correctness driver for R010_module_java (outback predator-prey simulation:
 * Ant/Dingo/Eagle/Emu/Rat/Snake + Acacia/Grass plants).
 *
 * The subject is NON-deterministic by construction: Randomizer.useShared=false,
 * so every Randomizer.getRandom() returns a time-seeded `new Random()`, and
 * Simulator uses unseeded Collections.shuffle for weather effects. A golden
 * trace is therefore impossible; the oracle checks properties that hold on
 * every run of the baseline (validated over many baseline runs), repeated
 * REPS times, identically for every iteration:
 *
 *  1. Simulator(50,60) constructs and populates without exception; occupied
 *     fraction of the grid within [0.35, 0.98] (baseline ~0.79) and all 8
 *     species present initially.
 *  2. PRE_STEPS direct simulateOneStep() calls (15 at time-of-day 12, 15 at
 *     22), then simulate(100) (exercises the clock, weather (step%50) and
 *     disease (step%100) paths), then POST_STEPS more direct steps (same time
 *     pattern; they apply any weather simulate() picked), with no uncaught
 *     exception anywhere.  (Direct steps don't advance the clock, and the
 *     species are only active in given hours, hence the explicit times.)
 *  3. GRID CONSISTENCY after construction, after every direct step and after
 *     simulate(100):
 *       - every live animal/plant in the simulator's lists has an in-bounds
 *         location and the field holds exactly that organism there;
 *       - every object on the field is a live organism located at that very
 *         cell (no dead or stale "moved-away" occupants).  (The baseline's
 *         drought removes plants from the list but leaves them standing on
 *         the grid; those are live and in place, so this form of the check is
 *         exactly what the baseline guarantees.)
 *  4. LIFE-CYCLE DYNAMICS, aggregated over all reps (tracked by object
 *     identity): every species has births and deaths; the grid layout changes.
 *
 * Checks 3-4 (and all-8-species) were added in the T10 audit: the earlier
 * smoke oracle passed baselines with breeding disabled for a species or with
 * moves that leave stale occupants on the grid.
 *
 * Classes are found by reflection over _classes; each rep uses a fresh
 * URLClassLoader (platform parent, so subject statics are re-initialized).
 * The Simulator is inseparable from its Swing JFrame view at the baseline,
 * so this unit runs NON-headless (unit.conf); windows are disposed per rep
 * and the driver exits via System.exit.
 */
public class Driver {
    static final int DEPTH = 50, WIDTH = 60, REPS = Integer.getInteger("reps", 3);
    static final int PRE_STEPS = 30, SIMULATE_STEPS = 100, POST_STEPS = 30;
    static final String[] SPECIES = {"Ant", "Dingo", "Eagle", "Emu", "Rat", "Snake", "Acacia", "Grass"};

    static Method isAliveM, getLocM, rowM, colM, objAtM;
    static Map<String, Integer> births = new TreeMap<>(), deaths = new TreeMap<>();

    public static void main(String[] args) {
        String fail = null;
        try {
            Path classes = Paths.get(args[0]).resolve("_classes");
            for (int rep = 1; rep <= REPS && fail == null; rep++) {
                fail = runRep(classes, rep);
            }
            if (fail == null) {
                System.out.println("births " + births + ", deaths " + deaths);
                for (String sp : SPECIES) {
                    if (births.getOrDefault(sp, 0) == 0) {
                        fail = "no " + sp + " was ever born in " + REPS + " reps (births " + births + ")";
                        break;
                    }
                    if (deaths.getOrDefault(sp, 0) == 0) {
                        fail = "no " + sp + " ever died in " + REPS + " reps (deaths " + deaths + ")";
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            Throwable root = unwrap(t);
            root.printStackTrace(System.out);
            fail = "uncaught exception: " + root;
        }
        System.out.println(fail == null
                ? "RESULT PASS " + REPS + " reps x " + (PRE_STEPS + SIMULATE_STEPS + POST_STEPS)
                    + " steps: grid consistent, all species born and died, sim progresses"
                : "RESULT FAIL " + fail);
        disposeWindows();
        System.exit(0); // kill Swing EDT
    }

    /** One repetition under a fresh classloader. Returns null on success, else failure reason. */
    static String runRep(Path classes, int rep) throws Exception {
        isAliveM = getLocM = rowM = colM = objAtM = null;   // new loader, new classes
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
                    return "rep " + rep + ": exception constructing simulator: " + root;
                }
                Object field = findFieldObject(sim);
                objAtM = field.getClass().getMethod("getObjectAt", int.class, int.class);
                objAtM.setAccessible(true);

                Map<String, Integer> counts0 = counts(field);
                String layout0 = layout(field);
                int occupied = counts0.values().stream().mapToInt(Integer::intValue).sum();
                double frac = occupied / (double) (DEPTH * WIDTH);
                System.out.println("rep " + rep + " initial: " + counts0 + " frac="
                        + String.format(Locale.ROOT, "%.3f", frac));
                if (frac < 0.35 || frac > 0.98)
                    return "rep " + rep + ": initial occupancy " + frac + " outside [0.35,0.98] — populate broken";
                for (String sp : SPECIES)
                    if (counts0.getOrDefault(sp, 0) == 0)
                        return "rep " + rep + ": species " + sp + " absent after populate(): " + counts0;

                IdentityHashMap<Object, Boolean> alive = new IdentityHashMap<>();
                String err = checkGrid(sim, field, alive);
                if (err != null) return "rep " + rep + " after populate: " + err;

                Method simulateM = null;
                for (Method m : simClass.getDeclaredMethods())
                    if (m.getName().equalsIgnoreCase("simulate") && m.getParameterCount() == 1
                            && m.getParameterTypes()[0] == int.class) { simulateM = m; break; }
                if (simulateM == null) return "rep " + rep + ": no simulate(int) on " + simClass.getName();
                simulateM.setAccessible(true);
                Method stepM = findStepMethod(simClass);
                stepM.setAccessible(true);

                // Direct simulateOneStep() calls never advance the clock (simulate()
                // does), so set the time-of-day field to values inside the baseline's
                // 0..24 cycle at which the species are active: blocks of 15 steps at
                // 12 (all but Emu), then 22 (Dingo/Eagle/Emu/Snake).
                java.lang.reflect.Field timeF = null;
                try {
                    timeF = simClass.getDeclaredField("time");
                    if (timeF.getType() != int.class) timeF = null; else timeF.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    System.out.println("note: no int 'time' field; direct steps run at the current time of day");
                }
                for (int phase = 0; phase < 3; phase++) {
                    int n = phase == 1 ? 1 : (phase == 0 ? PRE_STEPS : POST_STEPS);
                    for (int i = 0; i < n; i++) {
                        if (timeF != null && phase != 1 && i % 15 == 0)
                            timeF.setInt(sim, (i / 15) % 2 == 0 ? 12 : 22);
                        try {
                            if (phase == 1) simulateM.invoke(sim, SIMULATE_STEPS);
                            else stepM.invoke(sim);
                        } catch (Throwable t) {
                            Throwable root = unwrap(t);
                            root.printStackTrace(System.out);
                            return "rep " + rep + ": exception while stepping ("
                                    + (phase == 1 ? "simulate(" + SIMULATE_STEPS + ")" : "phase " + phase + " step " + (i + 1))
                                    + "): " + root;
                        }
                        IdentityHashMap<Object, Boolean> now = new IdentityHashMap<>();
                        err = checkGrid(sim, field, now);
                        if (err != null)
                            return "rep " + rep + " " + (phase == 1 ? "after simulate(" + SIMULATE_STEPS + ")"
                                    : "phase " + phase + " step " + (i + 1)) + ": " + err;
                        for (Object o : now.keySet())
                            if (!alive.containsKey(o)) births.merge(o.getClass().getSimpleName(), 1, Integer::sum);
                        for (Object o : alive.keySet())
                            if (!now.containsKey(o)) deaths.merge(o.getClass().getSimpleName(), 1, Integer::sum);
                        alive = now;
                    }
                }

                Map<String, Integer> countsN = counts(field);
                System.out.println("rep " + rep + " final:   " + countsN);
                if (layout(field).equals(layout0))
                    return "rep " + rep + ": grid layout unchanged after "
                            + (PRE_STEPS + SIMULATE_STEPS + POST_STEPS) + " steps — simulation did not progress";
                return null;
            } finally {
                disposeWindows();
            }
        }
    }

    /**
     * Grid consistency; fills {@code alive} with the live organisms of the
     * simulator's lists.
     */
    static String checkGrid(Object sim, Object field, IdentityHashMap<Object, Boolean> alive) {
        try {
            for (Object o : listed(sim)) {
                Method am = isAliveM != null && isAliveM.getDeclaringClass().isInstance(o) ? isAliveM
                        : (isAliveM = findNoArg(o.getClass(), "isalive", boolean.class));
                if (am == null) return "no isAlive() on " + o.getClass().getName();
                if (!(Boolean) am.invoke(o)) continue;
                alive.put(o, Boolean.TRUE);
                int[] rc = cellOf(o);
                if (rc == null) return "live " + o.getClass().getSimpleName() + " has a null location";
                if (rc[0] < 0 || rc[0] >= DEPTH || rc[1] < 0 || rc[1] >= WIDTH)
                    return "live " + o.getClass().getSimpleName() + " at out-of-field location " + Arrays.toString(rc);
                Object at = objAtM.invoke(field, rc[0], rc[1]);
                if (at != o)
                    return "live " + o.getClass().getSimpleName() + " claims " + Arrays.toString(rc)
                            + " but the field holds " + (at == null ? "nothing" : at.getClass().getSimpleName()) + " there";
            }
            for (int r = 0; r < DEPTH; r++)
                for (int c = 0; c < WIDTH; c++) {
                    Object at = objAtM.invoke(field, r, c);
                    if (at == null || alive.containsKey(at)) continue;
                    Method am = findNoArg(at.getClass(), "isalive", boolean.class);
                    if (am == null || !(Boolean) am.invoke(at))
                        return "dead " + at.getClass().getSimpleName() + " left on the field at (" + r + "," + c + ")";
                    int[] rc = cellOf(at);
                    if (rc == null || rc[0] != r || rc[1] != c)
                        return "stale " + at.getClass().getSimpleName() + " on the field at (" + r + "," + c
                                + ") while its location is " + (rc == null ? "null" : Arrays.toString(rc));
                }
            return null;
        } catch (Throwable t) {
            return "grid inspection threw " + unwrap(t);
        }
    }

    static int[] cellOf(Object o) throws Exception {
        Method lm = getLocM != null && getLocM.getDeclaringClass().isInstance(o) ? getLocM
                : (getLocM = findNoArg(o.getClass(), "getlocation", null));
        if (lm == null) throw new IllegalStateException("no getLocation() on " + o.getClass().getName());
        Object loc = lm.invoke(o);
        if (loc == null) return null;
        if (rowM == null || !rowM.getDeclaringClass().isInstance(loc)) {
            rowM = findNoArg(loc.getClass(), "getrow", int.class);
            colM = findNoArg(loc.getClass(), "getcol", int.class);
            if (rowM == null || colM == null)
                throw new IllegalStateException("cannot read row/col of " + loc.getClass().getName());
        }
        return new int[]{(Integer) rowM.invoke(loc), (Integer) colM.invoke(loc)};
    }

    static Method findNoArg(Class<?> c, String lname, Class<?> ret) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Method m : k.getDeclaredMethods()) {
                if (m.getParameterCount() == 0 && !m.isSynthetic() && m.getName().toLowerCase().equals(lname)
                        && (ret == null || m.getReturnType() == ret)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        return null;
    }

    static List<Object> listed(Object sim) throws IllegalAccessException {
        List<Object> out = new ArrayList<>();
        for (java.lang.reflect.Field f : sim.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            if (f.get(sim) instanceof List<?> list)
                for (Object o : list) if (o != null) out.add(o);
        }
        return out;
    }

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
            for (Method m : c.getDeclaredMethods()) {
                if (m.getParameterCount() != 0 || m.getReturnType() != void.class) continue;
                String mn = m.getName().toLowerCase(Locale.ROOT);
                if (mn.equals("simulateonestep")) return c;
                if (hasCtor && mn.contains("step") && !mn.startsWith("get") && fallback == null) fallback = c;
            }
        }
        if (fallback != null) return fallback;
        throw new IllegalStateException("no Simulator class (simulateOneStep) found among " + names);
    }

    static Method findStepMethod(Class<?> c) throws Exception {
        for (Method m : c.getDeclaredMethods())
            if (m.getParameterCount() == 0 && m.getName().equalsIgnoreCase("simulateOneStep")) return m;
        for (Method m : c.getDeclaredMethods()) {
            String mn = m.getName().toLowerCase(Locale.ROOT);
            if (m.getParameterCount() == 0 && m.getReturnType() == void.class
                    && mn.contains("step") && !mn.startsWith("get")) return m;
        }
        throw new NoSuchMethodException("simulateOneStep on " + c);
    }

    /** Locate the grid object (has getDepth/getWidth/getObjectAt(int,int)) inside the simulator. */
    static Object findFieldObject(Object sim) throws Exception {
        for (Class<?> k = sim.getClass(); k != null; k = k.getSuperclass()) {
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (f.getType().isPrimitive()) continue;
                Object v = get(f, sim);
                if (v == null || isJdk(v.getClass())) continue;
                if (isGrid(v.getClass())) return v;
            }
        }
        throw new IllegalStateException("no Field-like object found in " + sim.getClass());
    }

    static Object get(java.lang.reflect.Field f, Object o) {
        try { f.setAccessible(true); return f.get(o); }
        catch (Throwable t) { return null; }
    }

    static boolean isJdk(Class<?> c) {
        String n = c.getName();
        return n.startsWith("java.") || n.startsWith("javax.") || n.startsWith("jdk.")
                || n.startsWith("sun.") || n.startsWith("com.sun.");
    }

    static boolean isGrid(Class<?> c) {
        try {
            c.getMethod("getDepth"); c.getMethod("getWidth");
            c.getMethod("getObjectAt", int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) { return false; }
    }

    /** Per-class occupant counts over the whole grid. */
    static Map<String, Integer> counts(Object field) throws Exception {
        TreeMap<String, Integer> counts = new TreeMap<>();
        for (int r = 0; r < DEPTH; r++)
            for (int c = 0; c < WIDTH; c++) {
                Object o = objAtM.invoke(field, r, c);
                if (o != null) counts.merge(o.getClass().getSimpleName(), 1, Integer::sum);
            }
        return counts;
    }

    /** Full grid layout string (cell -> occupant class), for progress detection. */
    static String layout(Object field) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < DEPTH; r++)
            for (int c = 0; c < WIDTH; c++) {
                Object o = objAtM.invoke(field, r, c);
                if (o != null) sb.append(r).append(',').append(c).append(':')
                        .append(o.getClass().getSimpleName()).append(';');
            }
        return sb.toString();
    }

    static Throwable unwrap(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) t = t.getCause();
        return t;
    }

    static void disposeWindows() {
        try {
            for (java.awt.Window w : java.awt.Window.getWindows()) w.dispose();
        } catch (Throwable t) { /* ignore */ }
    }
}
