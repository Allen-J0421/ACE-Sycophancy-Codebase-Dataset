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
 * Correctness driver for R005_module_java ("Ultimate Simulator 3000": CSV-driven
 * habitat simulation. Species -> Animal -> Predator, Species -> Plant; Habitat
 * with seasons/temperature/climate-change scenarios, day/night Time, Swing
 * MenuView + SimulatorView; Initializer is the entry point).
 *
 * DETERMINISM: the baseline is NON-deterministic. Randomizer.useShared=false
 * (each static getRandom() call is a time-seeded new Random()), and Species
 * uses Math.random() for sex and temperature death. A golden trace is therefore
 * impossible; the oracle is a property suite of two parts.
 *
 * Part B — deterministic micro-scenarios on the model classes (30 rounds, each
 * with fresh objects so the random shuffles / sexes / food levels vary; every
 * checked outcome is independent of that randomness and was derived by hand
 * from the baseline contract):
 *   B1  horde attack, 1 lion(40) next to tiger(50): baseline horde strength is
 *       40 + 40 (the attacker is counted twice) = 80 > 50 -> tiger is eaten, the
 *       lion receives the tiger's 120 nutrition (2 shares of 60, both to it).
 *   B2  two lions next to tiger: baseline members list = [La,La,La] (the j-loop
 *       adds neighbours.get(i)), strength 120 > 50, 120/3 = 40 per share, so the
 *       food deltas of the two lions are exactly {120, 0}.
 *   B3  weak horde (snake 15 -> 30 <= 50) does not kill the tiger; tiger
 *       ignores predator "prey", moves, hunger -1. Boundary: snake next to
 *       fox(30): horde 15+15 == 30 is not > 30, fox survives.
 *   B4  same-species neighbours never form a horde.
 *   B5  predator eats adjacent prey: prey dies, predator +90-1, moves adjacent.
 *   B6  herbivore surrounded by 8 health-1 plants eats exactly one, moves into
 *       the freed cell, food +10-1, 7 plants remain.
 *   B7  overcrowding (1x1 field) -> death.  B8 starvation at foodLevel 1.
 *   B9  night: diurnal animal neither moves nor gets hungry; nocturnal moves
 *       without hunger.
 *   B10 hibernation at -30C: moves+hunger exactly at calls 1 and 11 of 12,
 *       never dies of the out-of-range temperature.
 *   B11 aging: maxAge 1 -> survives first yearPassed, dies on the second.
 *   B12 reproduction (female next to male, breedingAge 0, prob 1, litter 3):
 *       1..3 newborns of the parent's exact class, same name (and strength for
 *       predators), age 0, food in [nutr/2, nutr), placed on free adjacent
 *       cells; males and under-age females do not breed.
 *   B13 plant isEaten(): health 2 -> survives one bite, dies (cell cleared) on
 *       the second.  B14 plant reproduction prob 1 -> exactly one adjacent
 *       offspring by day, none at night.
 *
 * Part A — end-to-end runs through the real entry point
 * (new Initializer() -> initializeSimulation(habitat, animals, scenario) ->
 * Simulator.simulateOneStep()), three scenarios (savanna/none 300 steps,
 * arctic/high 420 steps, desert/medium 260 steps), fresh class loader each
 * (ClimateScenarios is a mutable enum). Checked against an independent oracle
 * built from the CSV files:
 *   - initial grid holds exactly the requested count of every species and
 *     (int)(9600 * plantConcentration) plants; list size == occupants.
 *   - after every step k: step counter == k; season == [spring, summer,
 *     autumn, winter][(k/50)%4]; isNight == (k odd); temperature within the
 *     season's CSV [avg-change, avg+change] (scenario "none") or >= avg-change
 *     (climate scenarios); every grid occupant is alive, in the species list,
 *     located where it sits, on exactly one cell; every alive Animal in the list
 *     is on the grid at its location.
 *   - grid layout changes over the run; simulate(n) runs; after
 *     endSimulation() a further step changes nothing.
 *
 * Runs NON-headless (Initializer opens the Swing MenuView, SimulatorView is a
 * JFrame); windows are disposed and the driver exits via System.exit.
 */
public class Driver {
    static final int ROUNDS = 30;
    static final String[] SEASONS = {"spring", "summer", "autumn", "winter"};

    public static void main(String[] args) {
        String fail = null;
        String summary = "";
        try {
            Path work = Paths.get(args[0]);
            Path classes = work.resolve("_classes");
            try (URLClassLoader cl = newLoader(classes)) {
                Model m = new Model(classes, cl);
                for (int r = 1; r <= ROUNDS && fail == null; r++) {
                    fail = partB(m, r);
                }
            }
            if (fail == null) {
                System.out.println("part B: " + ROUNDS + " rounds x 14 micro-scenarios OK");
                Map<String, int[]> habitats = readHabitats(work.resolve("habitats.csv"));
                Map<String, Double> conc = readConcentrations(work.resolve("habitats.csv"));
                Object[][] scenarios = {
                    {"savanna", "none", 300, new Object[]{"lion", 25, "cheetah", 20, "zebra", 200,
                            "antelope", 200, "elephant", 40, "chimpanzee", 60}},
                    {"arctic", "high", 420, new Object[]{"fox", 40, "wolf", 30, "polar bear", 15,
                            "seal", 150, "penguin", 150, "reindeer", 120, "mouse", 100}},
                    {"desert", "medium", 260, new Object[]{"T-rex", 5, "snake", 30, "bobcat", 25,
                            "camel", 120, "meerkat", 200, "armadillo", 150}},
                };
                for (Object[] sc : scenarios) {
                    if (fail != null) break;
                    try (URLClassLoader cl = newLoader(classes)) {
                        fail = partA(classes, cl, (String) sc[0], (String) sc[1], (Integer) sc[2],
                                (Object[]) sc[3], habitats.get((String) sc[0]), conc.get((String) sc[0]));
                    } finally {
                        disposeWindows();
                    }
                }
                summary = "part B " + ROUNDS + " rounds x 14 scenarios; part A 3 end-to-end scenarios"
                        + " (initial counts, season/time/temperature schedule, grid invariants)";
            }
        } catch (Throwable t) {
            Throwable root = unwrap(t);
            root.printStackTrace(System.out);
            fail = "uncaught exception: " + root;
        }
        System.out.println(fail == null ? "RESULT PASS " + summary : "RESULT FAIL " + fail);
        disposeWindows();
        System.exit(0); // kill Swing EDT
    }

    static URLClassLoader newLoader(Path classes) throws Exception {
        return new URLClassLoader(new URL[]{classes.toUri().toURL()}, ClassLoader.getPlatformClassLoader());
    }

    // ------------------------------------------------------------------
    // Reflection model of the subject
    // ------------------------------------------------------------------
    static class Model {
        final List<Class<?>> all = new ArrayList<>();
        Class<?> fieldC, locC, animalC, predatorC, plantC;
        Constructor<?> fieldCtor, locCtor, animalCtor, predatorCtor, plantCtor;

        Model(Path classes, ClassLoader cl) throws Exception {
            for (String n : classNames(classes)) {
                try { all.add(Class.forName(n, false, cl)); } catch (Throwable t) { /* skip */ }
            }
            for (Class<?> c : all) {
                if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
                if (hasMethod(c, "getObjectAt", 2) && hasMethod(c, "getDepth", 0) && ctor(c, int.class, int.class) != null
                        && !java.awt.Component.class.isAssignableFrom(c)) { fieldC = c; }
                if (hasMethod(c, "getRow", 0) && hasMethod(c, "getCol", 0) && ctor(c, int.class, int.class) != null) { locC = c; }
            }
            if (fieldC == null || locC == null) throw new IllegalStateException("Field/Location classes not found");
            fieldCtor = ctor(fieldC, int.class, int.class);
            locCtor = ctor(locC, int.class, int.class);
            Class<?>[] animalSig = {fieldC, locC, String.class, int.class, int.class, int.class, double.class,
                    int.class, int.class, int.class, boolean.class, boolean.class, boolean.class};
            Class<?>[] predSig = new Class<?>[animalSig.length + 1];
            predSig[0] = int.class;
            System.arraycopy(animalSig, 0, predSig, 1, animalSig.length);
            Class<?>[] plantSig = {fieldC, locC, String.class, int.class, int.class, int.class, double.class, int.class};
            for (Class<?> c : all) {
                if (Modifier.isAbstract(c.getModifiers())) continue;
                Constructor<?> k;
                if ((k = ctor(c, animalSig)) != null) { animalC = c; animalCtor = k; }
                if ((k = ctor(c, predSig)) != null) { predatorC = c; predatorCtor = k; }
                for (Constructor<?> pc : c.getDeclaredConstructors()) {
                    Class<?>[] p = pc.getParameterTypes();
                    if (p.length >= plantSig.length && p.length <= plantSig.length + 1
                            && Arrays.equals(Arrays.copyOf(p, plantSig.length), plantSig)) {
                        plantC = c; plantCtor = pc; pc.setAccessible(true);
                    }
                }
            }
            if (animalC == null || predatorC == null || plantC == null)
                throw new IllegalStateException("Animal/Predator/Plant constructors not found: "
                        + animalC + " " + predatorC + " " + plantC);
        }

        Object field(int d, int w) throws Exception { return fieldCtor.newInstance(d, w); }
        Object loc(int r, int c) throws Exception { return locCtor.newInstance(r, c); }

        /** name,maxT,minT,nutr,prob,maxAge,breedAge,litter,hib,noct */
        Object animal(Object field, int r, int c, Object[] s) throws Exception {
            return animalCtor.newInstance(field, loc(r, c), s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], false, s[8], s[9]);
        }

        Object predator(Object field, int r, int c, int strength, Object[] s) throws Exception {
            return predatorCtor.newInstance(strength, field, loc(r, c), s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], false, s[8], s[9]);
        }

        Object plant(Object field, int r, int c, double prob, int maxHealth) throws Exception {
            Class<?>[] p = plantCtor.getParameterTypes();
            Object[] a = new Object[p.length];
            a[0] = field; a[1] = loc(r, c); a[2] = "plant"; a[3] = 50; a[4] = -10; a[5] = 10; a[6] = prob; a[7] = maxHealth;
            if (p.length > 8) a[8] = build(p[8], 0); // e.g. Habitat (iteration 10)
            return plantCtor.newInstance(a);
        }

        /** Generic builder for extra ctor collaborators (Habitat -> SimulationStep, ClimateScenarios, int[]). */
        Object build(Class<?> t, int depth) throws Exception {
            if (t == int.class) return 0;
            if (t == boolean.class) return false;
            if (t == double.class) return 0.0;
            if (t == String.class) return "spring";
            if (t == int[].class) return new int[]{20, 5};
            if (t.isEnum()) return t.getEnumConstants()[0];
            if (depth > 3) throw new IllegalStateException("cannot build " + t);
            Constructor<?>[] cs = t.getDeclaredConstructors();
            Arrays.sort(cs, Comparator.comparingInt(Constructor::getParameterCount));
            for (Constructor<?> c : cs) {
                try {
                    c.setAccessible(true);
                    Class<?>[] p = c.getParameterTypes();
                    Object[] a = new Object[p.length];
                    for (int i = 0; i < p.length; i++) a[i] = build(p[i], depth + 1);
                    return c.newInstance(a);
                } catch (Throwable e) { /* try next */ }
            }
            throw new IllegalStateException("cannot build " + t);
        }
    }

    // ------------------------------------------------------------------
    // Part B: deterministic micro-scenarios
    // ------------------------------------------------------------------
    static final Object[] TIGER = {"tiger", 40, 15, 120, 0.85, 20, 2, 4, false, false};
    static final Object[] LION = {"lion", 40, 10, 100, 0.3, 20, 2, 4, false, false};
    static final Object[] SNAKE = {"snake", 60, 0, 75, 0.4, 8, 3, 1, false, true};
    static final Object[] ZEBRA = {"zebra", 40, 10, 90, 0.85, 20, 1, 3, false, false};
    static final Object[] CHIMP = {"chimpanzee", 40, 10, 85, 0.85, 15, 1, 2, false, true};
    static final Object[] FOX = {"fox", 35, -20, 80, 0.65, 12, 3, 1, true, false};
    static final Object[] TIGER_BREED = {"tiger", 40, 15, 120, 1.0, 20, 0, 3, false, false};
    static final Object[] ZEBRA_BREED = {"zebra", 40, 10, 90, 1.0, 20, 0, 3, false, false};
    static final Object[] ZEBRA_OLD = {"zebra", 40, 10, 90, 0.0, 1, 1, 3, false, false};
    static final Object[] TIGER_YOUNG = {"tiger", 40, 15, 120, 1.0, 20, 5, 3, false, false};

    static String partB(Model m, int round) throws Exception {
        String p = "B round " + round + ": ";
        // B1 single lion horde kills tiger (baseline double-counts the attacker)
        {
            Object f = m.field(3, 3);
            Object tiger = m.predator(f, 1, 1, 50, TIGER);
            Object lion = m.predator(f, 0, 0, 40, LION);
            int l0 = food(lion);
            act(tiger, new ArrayList<>(), false, 25, false);
            if (alive(tiger)) return p + "B1 tiger(50) next to one lion(40) survived; baseline horde strength 40+40=80>50 eats it";
            if (at(f, 1, 1) != null) return p + "B1 eaten tiger still on grid";
            if (food(lion) - l0 != 120) return p + "B1 lion food delta " + (food(lion) - l0) + " != 120 (tiger nutrition, 2 shares of 60)";
            if (!alive(lion) || at(f, 0, 0) != lion) return p + "B1 lion disturbed";
        }
        // B2 two lions: baseline food deltas {120, 0}
        {
            Object f = m.field(3, 3);
            Object tiger = m.predator(f, 1, 1, 50, TIGER);
            Object la = m.predator(f, 0, 0, 40, LION);
            Object lb = m.predator(f, 2, 2, 40, LION);
            int a0 = food(la), b0 = food(lb);
            act(tiger, new ArrayList<>(), false, 25, false);
            if (alive(tiger)) return p + "B2 tiger survived a horde of two lions";
            int[] d = {food(la) - a0, food(lb) - b0};
            Arrays.sort(d);
            if (d[0] != 0 || d[1] != 120)
                return p + "B2 horde food deltas " + Arrays.toString(d) + " != [0, 120] (baseline: members=[La,La,La], 40 each to La)";
        }
        // B3 weak horde does not kill
        {
            Object f = m.field(3, 3);
            Object tiger = m.predator(f, 1, 1, 50, TIGER);
            Object snake = m.predator(f, 0, 0, 15, SNAKE);
            int t0 = food(tiger), s0 = food(snake);
            act(tiger, new ArrayList<>(), false, 25, false);
            if (!alive(tiger)) return p + "B3 tiger killed by a lone snake (15+15=30<=50)";
            if (!alive(snake) || at(f, 0, 0) != snake || food(snake) != s0) return p + "B3 snake (a predator) was eaten/changed";
            if (food(tiger) - t0 != -1) return p + "B3 tiger food delta " + (food(tiger) - t0) + " != -1";
            if (!movedAdjacent(tiger, 1, 1)) return p + "B3 tiger did not move to an adjacent cell";
            // boundary: horde strength equal to the victim's (15+15 == 30) is not enough (strict >)
            Object f2 = m.field(3, 3);
            Object fox = m.predator(f2, 1, 1, 30, FOX);
            Object snake2 = m.predator(f2, 2, 2, 15, SNAKE);
            act(fox, new ArrayList<>(), false, 25, false);
            if (!alive(fox)) return p + "B3 fox(30) killed by a lone snake whose horde strength 15+15 only equals 30";
            if (!alive(snake2)) return p + "B3 snake killed by fox";
        }
        // B4 same species never a horde
        {
            Object f = m.field(3, 3);
            Object ta = m.predator(f, 1, 1, 50, TIGER);
            Object tb = m.predator(f, 0, 1, 50, TIGER);
            setFemale(ta, false); setFemale(tb, false);
            int a0 = food(ta), b0 = food(tb);
            act(ta, new ArrayList<>(), false, 25, false);
            if (!alive(ta) || !alive(tb)) return p + "B4 same-species tigers attacked each other";
            if (food(ta) - a0 != -1 || food(tb) != b0) return p + "B4 food changed unexpectedly";
        }
        // B5 predator eats prey
        {
            Object f = m.field(3, 3);
            Object tiger = m.predator(f, 1, 1, 50, TIGER);
            Object zebra = m.animal(f, 0, 0, ZEBRA);
            int t0 = food(tiger);
            act(tiger, new ArrayList<>(), false, 25, false);
            if (alive(zebra)) return p + "B5 adjacent prey not eaten";
            if (at(f, 0, 0) == zebra) return p + "B5 eaten prey still on grid";
            if (food(tiger) - t0 != 89) return p + "B5 tiger food delta " + (food(tiger) - t0) + " != 90-1";
            if (!alive(tiger) || !movedAdjacent(tiger, 1, 1)) return p + "B5 tiger did not move to an adjacent cell";
        }
        // B6 herbivore eats exactly one plant and moves into the freed cell
        {
            Object f = m.field(3, 3);
            Object zebra = m.animal(f, 1, 1, ZEBRA);
            List<Object> plants = new ArrayList<>();
            for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) if (r != 1 || c != 1) plants.add(m.plant(f, r, c, 0.0, 1));
            int z0 = food(zebra);
            act(zebra, new ArrayList<>(), false, 25, false);
            int onGrid = 0, dead = 0;
            Object deadOne = null;
            for (Object pl : plants) {
                if (!alive(pl)) { dead++; deadOne = pl; }
            }
            for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) if (m.plantC.isInstance(at(f, r, c))) onGrid++;
            if (dead != 1 || onGrid != 7) return p + "B6 plants eaten=" + dead + " remaining=" + onGrid + " (expected 1 eaten, 7 remain)";
            if (!alive(zebra)) return p + "B6 zebra died";
            if (food(zebra) - z0 != 9) return p + "B6 zebra food delta " + (food(zebra) - z0) + " != 10-1";
            if (!movedAdjacent(zebra, 1, 1)) return p + "B6 zebra did not move into the freed cell";
        }
        // B7 overcrowding
        {
            Object f = m.field(1, 1);
            Object zebra = m.animal(f, 0, 0, ZEBRA);
            act(zebra, new ArrayList<>(), false, 25, false);
            if (alive(zebra) || at(f, 0, 0) != null) return p + "B7 animal with no free cell did not die of overcrowding";
        }
        // B8 starvation
        {
            Object f = m.field(3, 3);
            Object zebra = m.animal(f, 1, 1, ZEBRA);
            setFood(zebra, 1);
            act(zebra, new ArrayList<>(), false, 25, false);
            if (alive(zebra)) return p + "B8 animal at foodLevel 1 survived a hungry day step";
            if (count(f, zebra) != 0) return p + "B8 starved animal left on grid";
        }
        // B9 night behaviour
        {
            Object f = m.field(3, 3);
            Object zebra = m.animal(f, 1, 1, ZEBRA);
            int z0 = food(zebra);
            act(zebra, new ArrayList<>(), true, 25, false);
            if (!alive(zebra) || rc(zebra)[0] != 1 || rc(zebra)[1] != 1 || food(zebra) != z0)
                return p + "B9 diurnal animal moved or got hungry at night";
            Object f2 = m.field(3, 3);
            Object chimp = m.animal(f2, 1, 1, CHIMP);
            int c0 = food(chimp);
            act(chimp, new ArrayList<>(), true, 25, false);
            if (!alive(chimp) || !movedAdjacent(chimp, 1, 1)) return p + "B9 nocturnal animal did not move at night";
            if (food(chimp) != c0) return p + "B9 nocturnal animal got hungry at night";
        }
        // B10 hibernation cadence
        {
            Object f = m.field(5, 5);
            Object fox = m.predator(f, 2, 2, 30, FOX);
            int[] prev = rc(fox);
            int pf = food(fox);
            for (int call = 1; call <= 12; call++) {
                act(fox, new ArrayList<>(), false, -30, false);
                if (!alive(fox)) return p + "B10 hibernating fox died at call " + call;
                int[] now = rc(fox);
                boolean moved = now[0] != prev[0] || now[1] != prev[1];
                int df = food(fox) - pf;
                boolean expectMove = call == 1 || call == 11;
                if (moved != expectMove || df != (expectMove ? -1 : 0))
                    return p + "B10 hibernation call " + call + ": moved=" + moved + " foodDelta=" + df
                            + " (expected move+hunger only at calls 1 and 11)";
                prev = now; pf = food(fox);
            }
        }
        // B11 aging
        {
            Object f = m.field(3, 3);
            Object z = m.animal(f, 1, 1, ZEBRA_OLD);
            act(z, new ArrayList<>(), false, 25, true);
            if (!alive(z)) return p + "B11 animal died at age 1 == maxAge";
            act(z, new ArrayList<>(), false, 25, true);
            if (alive(z)) return p + "B11 animal survived age 2 > maxAge 1";
            if (count(f, z) != 0) return p + "B11 dead animal left on grid";
        }
        // B12 reproduction
        {
            String r = breedCheck(m, true, p);
            if (r != null) return r;
            r = breedCheck(m, false, p);
            if (r != null) return r;
            // male does not breed
            Object f = m.field(3, 3);
            Object male = m.predator(f, 1, 1, 50, TIGER_BREED);
            Object fem = m.predator(f, 0, 0, 50, TIGER_BREED);
            setFemale(male, false); setFemale(fem, true);
            List<Object> born = new ArrayList<>();
            act(male, born, false, 25, false);
            if (!born.isEmpty()) return p + "B12 a male gave birth";
            // under-age female does not breed
            Object f2 = m.field(3, 3);
            Object yf = m.predator(f2, 1, 1, 50, TIGER_YOUNG);
            Object ym = m.predator(f2, 0, 0, 50, TIGER_YOUNG);
            setFemale(yf, true); setFemale(ym, false);
            act(yf, born, false, 25, false);
            if (!born.isEmpty()) return p + "B12 under-age female gave birth";
        }
        // B13 plant bites
        {
            Object f = m.field(3, 3);
            Object pl = m.plant(f, 1, 1, 0.0, 2);
            call(pl, "isEaten");
            if (!alive(pl) || at(f, 1, 1) != pl) return p + "B13 plant with health 2 died after one bite";
            call(pl, "isEaten");
            if (alive(pl) || at(f, 1, 1) != null) return p + "B13 plant with health 2 survived two bites";
        }
        // B14 plant reproduction
        {
            Object f = m.field(3, 3);
            Object pl = m.plant(f, 1, 1, 1.0, 4);
            List<Object> born = new ArrayList<>();
            act(pl, born, true, 20, false);
            if (!born.isEmpty()) return p + "B14 plant reproduced at night";
            act(pl, born, false, 20, false);
            if (born.size() != 1) return p + "B14 plant with reproduction prob 1 produced " + born.size() + " offspring (expected 1)";
            Object kid = born.get(0);
            if (kid.getClass() != pl.getClass() || !movedAdjacentFrom(kid, 1, 1) || at(f, rc(kid)[0], rc(kid)[1]) != kid)
                return p + "B14 plant offspring wrong class/placement";
            if (!alive(pl) || at(f, 1, 1) != pl) return p + "B14 parent plant disturbed";
        }
        return null;
    }

    static String breedCheck(Model m, boolean predator, String p) throws Exception {
        Object f = m.field(3, 3);
        Object mom = predator ? m.predator(f, 1, 1, 50, TIGER_BREED) : m.animal(f, 1, 1, ZEBRA_BREED);
        Object dad = predator ? m.predator(f, 0, 0, 50, TIGER_BREED) : m.animal(f, 0, 0, ZEBRA_BREED);
        setFemale(mom, true); setFemale(dad, false);
        List<Object> born = new ArrayList<>();
        act(mom, born, false, 25, false);
        String w = "B12 " + (predator ? "predator" : "prey") + " ";
        if (born.size() < 1 || born.size() > 3) return p + w + "litter size " + born.size() + " not in [1,3]";
        Set<String> cells = new HashSet<>();
        for (Object kid : born) {
            if (kid.getClass() != mom.getClass()) return p + w + "newborn class " + kid.getClass().getName() + " != parent " + mom.getClass().getName();
            if (!"tiger zebra".contains(name(kid)) || !name(kid).equals(name(mom))) return p + w + "newborn name " + name(kid);
            if (predator && strength(kid) != 50) return p + w + "newborn strength " + strength(kid) + " != 50";
            if (intField(kid, "age") != 0) return p + w + "newborn age != 0";
            int fl = food(kid);
            if (fl < 45 && !predator || fl < 60 && predator || fl >= (predator ? 120 : 90)) return p + w + "newborn foodLevel " + fl + " outside [nutr/2, nutr)";
            if (!alive(kid)) return p + w + "newborn not alive";
            int[] k = rc(kid);
            if (!movedAdjacentFrom(kid, 1, 1) || (k[0] == 0 && k[1] == 0) || at(f, k[0], k[1]) != kid)
                return p + w + "newborn not on a free adjacent cell";
            if (!cells.add(k[0] + "," + k[1])) return p + w + "two newborns on one cell";
        }
        if (!alive(mom)) return p + w + "mother died";
        return null;
    }

    // ------------------------------------------------------------------
    // Part A: end-to-end via Initializer
    // ------------------------------------------------------------------
    static String partA(Path classes, ClassLoader cl, String habitat, String scenario, int steps,
                        Object[] animals, int[] temps, Double concentration) throws Exception {
        String p = "A[" + habitat + "/" + scenario + "]: ";
        if (temps == null || concentration == null) throw new IllegalStateException("habitat not in CSV: " + habitat);
        Model m = new Model(classes, cl);
        Class<?> initC = null;
        Method initM = null;
        for (Class<?> c : m.all) {
            for (Method mm : c.getDeclaredMethods()) {
                if (mm.isSynthetic() || mm.isBridge()) continue;
                Class<?>[] pt = mm.getParameterTypes();
                if (pt.length == 3 && pt[0] == String.class && Map.class.isAssignableFrom(pt[1])
                        && pt[1].isAssignableFrom(HashMap.class) && pt[2] == String.class
                        && mm.getReturnType() != void.class && ctor(c) != null) {
                    initC = c; initM = mm;
                }
            }
        }
        if (initM == null) throw new IllegalStateException("no Initializer.initializeSimulation(String, Map, String) found");
        Constructor<?> ic = ctor(initC);
        Object init = ic.newInstance();
        HashMap<String, Integer> req = new LinkedHashMap<>();
        for (int i = 0; i < animals.length; i += 2) req.put((String) animals[i], (Integer) animals[i + 1]);
        initM.setAccessible(true);
        Object sim = initM.invoke(init, habitat, new HashMap<>(req), scenario);
        if (sim == null) return p + "initializeSimulation returned null";
        Method stepM = null;
        for (Method mm : sim.getClass().getDeclaredMethods())
            if (!mm.isSynthetic() && mm.getParameterCount() == 0 && mm.getName().equals("simulateOneStep")) stepM = mm;
        if (stepM == null) throw new IllegalStateException("no simulateOneStep on " + sim.getClass());
        stepM.setAccessible(true);

        Object field = findByMethods(sim, "getObjectAt", "getDepth", "getWidth");
        Object hab = findByMethods(sim, "getCurrentSeason", "getCurrentTemperature");
        Object time = findByMethods(sim, "getIsNight");
        Object stepC = findByMethods(sim, "getCurrentStep");
        List<?> list = null;
        for (Class<?> k = sim.getClass(); k != null && list == null; k = k.getSuperclass())
            for (java.lang.reflect.Field fd : k.getDeclaredFields())
                if (List.class.isAssignableFrom(fd.getType())) { list = (List<?>) get(fd, sim); break; }
        if (field == null || hab == null || time == null || stepC == null || list == null)
            throw new IllegalStateException("simulator collaborators not found");

        int depth = (Integer) call(field, "getDepth"), width = (Integer) call(field, "getWidth");
        // --- initial population
        Map<String, Integer> counts = new TreeMap<>();
        int occupants = 0;
        for (int r = 0; r < depth; r++) for (int c = 0; c < width; c++) {
            Object o = at(field, r, c);
            if (o != null) { counts.merge(name(o), 1, Integer::sum); occupants++; }
        }
        Map<String, Integer> expected = new TreeMap<>(req);
        expected.put("plant", (int) (depth * width * concentration));
        System.out.println(p + "initial " + counts);
        if (!counts.equals(expected)) return p + "initial population " + counts + " != requested " + expected;
        if (list.size() != occupants) return p + "species list size " + list.size() + " != grid occupants " + occupants;
        String err = invariants(m, field, list, depth, width);
        if (err != null) return p + "step 0: " + err;
        if (!SEASONS[0].equals(call(hab, "getCurrentSeason"))) return p + "initial season " + call(hab, "getCurrentSeason");
        String layout0 = layout(field, depth, width);

        for (int k = 1; k <= steps; k++) {
            stepM.invoke(sim);
            int stepNo = (Integer) call(stepC, "getCurrentStep");
            if (stepNo != k) return p + "step counter " + stepNo + " != " + k;
            String season = (String) call(hab, "getCurrentSeason");
            int si = (k / 50) % 4;
            if (!SEASONS[si].equals(season)) return p + "step " + k + ": season " + season + " != " + SEASONS[si];
            boolean night = (Boolean) call(time, "getIsNight");
            if (night != (k % 2 == 1)) return p + "step " + k + ": isNight " + night + " != " + (k % 2 == 1);
            int temp = (Integer) call(hab, "getCurrentTemperature");
            int avg = temps[2 * si], ch = temps[2 * si + 1];
            if (temp < avg - ch || ("none".equals(scenario) && temp > avg + ch))
                return p + "step " + k + ": temperature " + temp + " outside " + season + " bounds [" + (avg - ch) + "," + (avg + ch) + "]";
            err = invariants(m, field, list, depth, width);
            if (err != null) return p + "step " + k + ": " + err;
        }
        counts.clear();
        for (int r = 0; r < depth; r++) for (int c = 0; c < width; c++) {
            Object o = at(field, r, c);
            if (o != null) counts.merge(name(o), 1, Integer::sum);
        }
        System.out.println(p + "after " + steps + " steps " + counts);
        if (layout(field, depth, width).equals(layout0)) return p + "grid unchanged after " + steps + " steps";

        // simulate(n) loop (stops early only if not viable)
        Method simulate = null;
        for (Method mm : sim.getClass().getDeclaredMethods())
            if (!mm.isSynthetic() && mm.getName().equals("simulate") && mm.getParameterCount() == 1 && mm.getParameterTypes()[0] == int.class) simulate = mm;
        if (simulate != null) {
            simulate.setAccessible(true);
            int before = (Integer) call(stepC, "getCurrentStep");
            simulate.invoke(sim, 10);
            int after = (Integer) call(stepC, "getCurrentStep");
            if (after < before || after > before + 10) return p + "simulate(10) advanced " + (after - before) + " steps";
            err = invariants(m, field, list, depth, width);
            if (err != null) return p + "after simulate(10): " + err;
        }
        // endSimulation -> further steps are no-ops
        Method end = null;
        for (Method mm : sim.getClass().getDeclaredMethods())
            if (!mm.isSynthetic() && mm.getName().equals("endSimulation") && mm.getParameterCount() == 0) end = mm;
        if (end != null) {
            end.setAccessible(true);
            end.invoke(sim);
            String l1 = layout(field, depth, width);
            int s1 = (Integer) call(stepC, "getCurrentStep");
            stepM.invoke(sim);
            if (!layout(field, depth, width).equals(l1) || (Integer) call(stepC, "getCurrentStep") != s1)
                return p + "simulateOneStep still acts after endSimulation()";
        }
        return null;
    }

    static String invariants(Model m, Object field, List<?> list, int depth, int width) throws Exception {
        Set<Object> inList = Collections.newSetFromMap(new IdentityHashMap<>());
        inList.addAll(list);
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int r = 0; r < depth; r++) for (int c = 0; c < width; c++) {
            Object o = at(field, r, c);
            if (o == null) continue;
            if (!seen.add(o)) return "object " + name(o) + " on two cells";
            if (!alive(o)) return "dead " + name(o) + " still on grid at " + r + "," + c;
            if (!inList.contains(o)) return name(o) + " at " + r + "," + c + " not in species list";
            int[] k = rc(o);
            if (k == null || k[0] != r || k[1] != c) return name(o) + " at " + r + "," + c + " thinks it is at " + Arrays.toString(k);
        }
        for (Object o : list) {
            if (m.animalC.isInstance(o) && alive(o)) {
                int[] k = rc(o);
                if (k == null) return "alive animal " + name(o) + " has no location";
                if (at(field, k[0], k[1]) != o) return "alive animal " + name(o) + " not on grid at its location " + Arrays.toString(k);
            }
        }
        return null;
    }

    static String layout(Object field, int depth, int width) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < depth; r++) for (int c = 0; c < width; c++) {
            Object o = at(field, r, c);
            if (o != null) sb.append(r).append(',').append(c).append(':').append(System.identityHashCode(o)).append(';');
        }
        return sb.toString();
    }

    static Object findByMethods(Object sim, String... methods) {
        for (Class<?> k = sim.getClass(); k != null; k = k.getSuperclass())
            for (java.lang.reflect.Field fd : k.getDeclaredFields()) {
                if (fd.getType().isPrimitive()) continue;
                Object v = get(fd, sim);
                if (v == null || java.awt.Component.class.isInstance(v)) continue;
                boolean ok = true;
                for (String mn : methods) if (!hasMethod(v.getClass(), mn, mn.equals("getObjectAt") ? 2 : 0)) { ok = false; break; }
                if (ok) return v;
            }
        return null;
    }

    // ------------------------------------------------------------------
    // CSV oracle data
    // ------------------------------------------------------------------
    /** habitat -> {springAvg, springCh, summerAvg, summerCh, autumnAvg, autumnCh, winterAvg, winterCh} */
    static Map<String, int[]> readHabitats(Path csv) throws Exception {
        List<String> lines = Files.readAllLines(csv);
        List<String> hdr = Arrays.asList(lines.get(0).trim().split(","));
        Map<String, int[]> out = new HashMap<>();
        String[] keys = {"avgSpringTemp", "springTempChange", "avgSummerTemp", "summerTempChange",
                "avgAutumnTemp", "autumnTempChange", "avgWinterTemp", "winterTempChange"};
        for (String l : lines.subList(1, lines.size())) {
            if (l.isBlank()) continue;
            String[] a = l.trim().split(",");
            int[] t = new int[8];
            for (int i = 0; i < 8; i++) t[i] = Integer.parseInt(a[hdr.indexOf(keys[i])]);
            out.put(a[0], t);
        }
        return out;
    }

    static Map<String, Double> readConcentrations(Path csv) throws Exception {
        List<String> lines = Files.readAllLines(csv);
        int idx = Arrays.asList(lines.get(0).trim().split(",")).indexOf("plantConcentration");
        Map<String, Double> out = new HashMap<>();
        for (String l : lines.subList(1, lines.size())) {
            if (l.isBlank()) continue;
            String[] a = l.trim().split(",");
            out.put(a[0], Double.valueOf(a[idx]));
        }
        return out;
    }

    // ------------------------------------------------------------------
    // Reflection helpers
    // ------------------------------------------------------------------
    static List<String> classNames(Path classes) throws Exception {
        try (Stream<Path> s = Files.walk(classes)) {
            return s.filter(q -> q.toString().endsWith(".class"))
                    .map(q -> classes.relativize(q).toString().replace(".class", "").replace(java.io.File.separatorChar, '.'))
                    .filter(n -> !n.contains("$")).sorted().collect(Collectors.toList());
        }
    }

    static Constructor<?> ctor(Class<?> c, Class<?>... sig) {
        try { Constructor<?> k = c.getDeclaredConstructor(sig); k.setAccessible(true); return k; }
        catch (Throwable e) { return null; }
    }

    static boolean hasMethod(Class<?> c, String name, int params) {
        return method(c, name, params) != null;
    }

    static final Map<String, Method> MCACHE = new HashMap<>();

    static Method method(Class<?> c, String name, int params) {
        String key = c.getName() + "#" + System.identityHashCode(c.getClassLoader()) + "#" + name + "#" + params;
        if (MCACHE.containsKey(key)) return MCACHE.get(key);
        Method found = null;
        outer:
        for (Class<?> k = c; k != null; k = k.getSuperclass())
            for (Method mm : k.getDeclaredMethods())
                if (!mm.isSynthetic() && !mm.isBridge() && mm.getName().equals(name) && mm.getParameterCount() == params) {
                    try { mm.setAccessible(true); } catch (Throwable t) { continue; }
                    found = mm;
                    break outer;
                }
        MCACHE.put(key, found);
        return found;
    }

    static Object call(Object o, String name, Object... args) throws Exception {
        Method mm = method(o.getClass(), name, args.length);
        if (mm == null) throw new NoSuchMethodException(o.getClass().getName() + "." + name + "/" + args.length);
        try { return mm.invoke(o, args); }
        catch (InvocationTargetException e) { throw e; }
    }

    static java.lang.reflect.Field fieldNamed(Class<?> c, String name) {
        for (Class<?> k = c; k != null; k = k.getSuperclass())
            for (java.lang.reflect.Field fd : k.getDeclaredFields())
                if (fd.getName().equals(name)) { fd.setAccessible(true); return fd; }
        throw new IllegalStateException("no field " + name + " on " + c.getName());
    }

    static Object get(java.lang.reflect.Field f, Object o) {
        try { f.setAccessible(true); return f.get(o); } catch (Throwable t) { return null; }
    }

    static int intField(Object o, String n) throws Exception { return fieldNamed(o.getClass(), n).getInt(o); }
    static int food(Object o) throws Exception { return intField(o, "foodLevel"); }
    static void setFood(Object o, int v) throws Exception { fieldNamed(o.getClass(), "foodLevel").setInt(o, v); }
    static void setFemale(Object o, boolean v) throws Exception { fieldNamed(o.getClass(), "isFemale").setBoolean(o, v); }
    static boolean alive(Object o) throws Exception { return (Boolean) call(o, "isAlive"); }
    static String name(Object o) throws Exception { return (String) call(o, "getName"); }
    static int strength(Object o) throws Exception { return (Integer) call(o, "getStrength"); }

    @SuppressWarnings("unchecked")
    static void act(Object o, List<?> born, boolean night, int temp, boolean year) throws Exception {
        Method mm = method(o.getClass(), "act", 4);
        if (mm == null) throw new NoSuchMethodException("act/4 on " + o.getClass());
        mm.invoke(o, born, night, temp, year);
    }

    static Object at(Object field, int r, int c) throws Exception {
        return method(field.getClass(), "getObjectAt", 2) == null ? null : invokeAt(field, r, c);
    }

    static Object invokeAt(Object field, int r, int c) throws Exception {
        for (Class<?> k = field.getClass(); k != null; k = k.getSuperclass())
            for (Method mm : k.getDeclaredMethods())
                if (mm.getName().equals("getObjectAt") && mm.getParameterCount() == 2 && mm.getParameterTypes()[0] == int.class) {
                    mm.setAccessible(true);
                    return mm.invoke(field, r, c);
                }
        throw new NoSuchMethodException("getObjectAt(int,int)");
    }

    static int[] rc(Object o) throws Exception {
        Object l = call(o, "getLocation");
        if (l == null) return null;
        return new int[]{(Integer) call(l, "getRow"), (Integer) call(l, "getCol")};
    }

    static int count(Object field, Object o) throws Exception {
        int n = 0;
        int d = (Integer) call(field, "getDepth"), w = (Integer) call(field, "getWidth");
        for (int r = 0; r < d; r++) for (int c = 0; c < w; c++) if (at(field, r, c) == o) n++;
        return n;
    }

    static boolean movedAdjacent(Object o, int r0, int c0) throws Exception {
        int[] k = rc(o);
        return k != null && !(k[0] == r0 && k[1] == c0) && Math.abs(k[0] - r0) <= 1 && Math.abs(k[1] - c0) <= 1;
    }

    static boolean movedAdjacentFrom(Object o, int r0, int c0) throws Exception { return movedAdjacent(o, r0, c0); }

    static Throwable unwrap(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) t = t.getCause();
        return t;
    }

    static void disposeWindows() {
        try { for (java.awt.Window w : java.awt.Window.getWindows()) w.dispose(); } catch (Throwable t) { /* ignore */ }
    }
}
