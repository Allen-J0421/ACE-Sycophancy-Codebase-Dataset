import java.util.*;
import java.util.function.BiFunction;

/**
 * The core simulation engine: owns all simulation state and drives each step.
 * It is deliberately decoupled from the GUI — the only way it communicates
 * with the view layer is through the {@link SimulationObserver} passed at
 * construction time.
 *
 * @version 2022.03.02
 */
public class SimulationEngine implements SimulationControls
{
    static final int DEFAULT_DEPTH = 160;
    static final int DEFAULT_WIDTH = 240;

    private static final int HUNTER_LIMIT = 5;
    private static final Random rand = Randomizer.getRandom();

    private static final Map<Class<?>, Double> DEFAULT_PROBABILITIES = Map.ofEntries(
            Map.entry(Coyote.class, 0.010),
            Map.entry(Deer.class,   0.080),
            Map.entry(Wolf.class,   0.010),
            Map.entry(Eagle.class,  0.010),
            Map.entry(Mouse.class,  0.080),
            Map.entry(Grass.class,  0.030),
            Map.entry(Hunter.class, 0.030)
    );

    private boolean playingSimulation = false;
    private int remainingSteps;
    private int hunterCount = 0;

    private final Map<Class<?>, Double> creationProbabilities;
    private final SimulationObserver observer;

    private List<Actor> actors;
    private Field field;
    private int step;
    private Environment environment;

    /**
     * Create an engine with default creation probabilities.
     * @param depth    Depth of the simulation field.
     * @param width    Width of the simulation field.
     * @param observer Observer that receives step notifications and answers viability queries.
     */
    public SimulationEngine(int depth, int width, SimulationObserver observer)
    {
        this(depth, width, DEFAULT_PROBABILITIES, observer);
    }

    /**
     * Create an engine with custom creation probabilities.
     * @param depth                Depth of the simulation field.
     * @param width                Width of the simulation field.
     * @param creationProbabilities Per-class spawn probability used during populate().
     * @param observer             Observer that receives step notifications and answers viability queries.
     */
    public SimulationEngine(int depth, int width, Map<Class<?>, Double> creationProbabilities,
                            SimulationObserver observer)
    {
        this.creationProbabilities = creationProbabilities;
        this.observer = observer;
        actors = new ArrayList<>();
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());
    }

    // --- SimulationControls implementation ---

    @Override
    public void stepOnce()
    {
        if (!playingSimulation) {
            playingSimulation = true;
            simulateOneStep();
            playingSimulation = false;
        }
    }

    @Override
    public void runLongSimulation()
    {
        remainingSteps += 4000;
        simulate(remainingSteps);
    }

    @Override
    public void stop()
    {
        playingSimulation = false;
    }

    @Override
    public void reset()
    {
        step = 0;
        hunterCount = 0;
        actors.clear();
        populate();
        environment.getTime().reset();
        observer.onStep(step, environment, field);
    }

    @Override
    public boolean isPlaying()
    {
        return playingSimulation;
    }

    // --- Simulation loop ---

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stops early if the population ceases to be viable.
     */
    public void simulate(int numSteps)
    {
        playingSimulation = true;
        remainingSteps = numSteps;

        int stepsDone = 1;
        while (playingSimulation) {
            for (int s = 1; s <= numSteps && observer.isViable(field); s++) {
                simulateOneStep();
                stepsDone++;
            }
            if (!observer.isViable(field)) {
                if (stepsDone > SimulationInfo.HIGHEST_STEPS) {
                    SimulationInfo.HIGHEST_STEPS = stepsDone;
                    SimulationInfo.HIGHEST_STEP_PROBS = this.creationProbabilities;
                }
                playingSimulation = false;
                break;
            }
        }
        remainingSteps = numSteps - stepsDone;
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterates over the whole field, letting each actor act.
     */
    public void simulateOneStep()
    {
        if (!playingSimulation) { return; }
        step++;
        environment.getTime().incrementTime();
        environment.getWeather().checkWeatherChange(step);

        List<Actor> newActors = new ArrayList<>();
        for (Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, environment);
            if (!actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
        plantGrassInPatches();

        observer.onStep(step, environment, field);
    }

    // --- Field management ---

    /**
     * Randomly generate grass in free patches on the field, only if it is raining.
     */
    private void plantGrassInPatches()
    {
        for (Location location : field.getRandomFreePatches(creationProbabilities.get(Grass.class))) {
            if (rand.nextDouble() <= creationProbabilities.get(Grass.class)
                    && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
                actors.add(new Grass(field, location));
            }
        }
    }

    /**
     * Randomly populate the field with organisms.
     * The factory map determines which actor is spawned at each cell; entries
     * are tried in insertion order and only the first match is placed per cell.
     */
    private void populate()
    {
        LinkedHashMap<Class<?>, BiFunction<Location, Gender, Actor>> factories = new LinkedHashMap<>();
        factories.put(Grass.class,  (loc, sex) -> new Grass(field, loc));
        factories.put(Deer.class,   (loc, sex) -> new Deer(true, field, loc, sex));
        factories.put(Coyote.class, (loc, sex) -> new Coyote(true, field, loc, sex));
        factories.put(Wolf.class,   (loc, sex) -> new Wolf(true, field, loc, sex));
        factories.put(Eagle.class,  (loc, sex) -> new Eagle(true, field, loc, sex));
        factories.put(Mouse.class,  (loc, sex) -> new Mouse(true, field, loc, sex));
        factories.put(Hunter.class, (loc, sex) -> new Hunter(field, loc));

        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Gender sex = Randomizer.getRandomSex();
                for (Map.Entry<Class<?>, BiFunction<Location, Gender, Actor>> entry : factories.entrySet()) {
                    Class<?> cls = entry.getKey();
                    if (cls == Hunter.class && hunterCount >= HUNTER_LIMIT) continue;
                    if (rand.nextDouble() <= creationProbabilities.get(cls)) {
                        actors.add(entry.getValue().apply(location, sex));
                        if (cls == Hunter.class) hunterCount++;
                        break;
                    }
                }
                // else leave the location empty
            }
        }
    }
}
