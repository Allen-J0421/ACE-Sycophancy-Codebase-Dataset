import java.util.*;
import java.util.function.BiFunction;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing different organisms.
 *
 * @version 2022.03.02
 */
public class Simulator implements SimulationControls
{
    private boolean playingSimulation = false;
    private int remainingSteps;

    private static final Random rand = Randomizer.getRandom();

    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 240;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 160;

    // Maximum number of hunters allowed in the simulation
    private static final int HUNTER_LIMIT = 5;
    private int hunterCount = 0;

    // Creation probability for each actor class (instance field — not shared across instances)
    private Map<Class<?>, Double> CREATION_PROBABILITIES = Map.ofEntries(
            Map.entry(Coyote.class, 0.010),
            Map.entry(Deer.class,   0.080),
            Map.entry(Wolf.class,   0.010),
            Map.entry(Eagle.class,  0.010),
            Map.entry(Mouse.class,  0.080),
            Map.entry(Grass.class,  0.030),
            Map.entry(Hunter.class, 0.030)
    );

    // List of actors in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Environment in the simulation.
    private Environment environment;


    /**
     * A simulator constructor that sets a custom creation probability for each actor class.
     * Useful for testing different probability combinations to find an optimal configuration.
     */
    public Simulator(double GrassProbability, double DeerProbability, double CoyoteProbability,
                     double WolfProbability, double EagleProbability, double HunterProbability,
                     double MouseProbability)
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
        CREATION_PROBABILITIES = Map.ofEntries(
                Map.entry(Coyote.class, CoyoteProbability),
                Map.entry(Deer.class,   DeerProbability),
                Map.entry(Wolf.class,   WolfProbability),
                Map.entry(Eagle.class,  EagleProbability),
                Map.entry(Mouse.class,  MouseProbability),
                Map.entry(Grass.class,  GrassProbability),
                Map.entry(Hunter.class, HunterProbability)
        );
        reset();
    }

    /** Construct a simulation field with default size. */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());

        view = new SimulatorView(depth, width);
        for (Class cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            view.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }
        view.wireButtons(this);

        reset();
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
        view.showStatus(step, environment, field);
    }

    @Override
    public boolean isPlaying()
    {
        return playingSimulation;
    }

    // --- Simulation engine ---

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
            for (int s = 1; s <= numSteps && view.isViable(field); s++) {
                simulateOneStep();
                stepsDone++;
            }
            if (!view.isViable(field)) {
                if (stepsDone > SimulationInfo.HIGHEST_STEPS) {
                    SimulationInfo.HIGHEST_STEPS = stepsDone;
                    SimulationInfo.HIGHEST_STEP_PROBS = this.CREATION_PROBABILITIES;
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

        view.showStatus(step, environment, field);
    }

    /**
     * Randomly generate grass in free patches on the field, only if it is raining.
     */
    private void plantGrassInPatches()
    {
        for (Location location : field.getRandomFreePatches(CREATION_PROBABILITIES.get(Grass.class))) {
            if (rand.nextDouble() <= CREATION_PROBABILITIES.get(Grass.class)
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
        LinkedHashMap<Class<?>, BiFunction<Location, Animal.Gender, Actor>> factories = new LinkedHashMap<>();
        factories.put(Grass.class,  (loc, sex) -> new Grass(field, loc));
        factories.put(Deer.class,   (loc, sex) -> new Deer(true, field, loc, sex));
        factories.put(Coyote.class, (loc, sex) -> new Coyote(true, field, loc, sex));
        factories.put(Wolf.class,   (loc, sex) -> new Wolf(true, field, loc, sex));
        factories.put(Eagle.class,  (loc, sex) -> new Eagle(true, field, loc, sex));
        factories.put(Mouse.class,  (loc, sex) -> new Mouse(true, field, loc, sex));
        factories.put(Hunter.class, (loc, sex) -> new Hunter(field, loc, environment));

        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Animal.Gender sex = Randomizer.getRandomSex();
                for (Map.Entry<Class<?>, BiFunction<Location, Animal.Gender, Actor>> entry : factories.entrySet()) {
                    Class<?> cls = entry.getKey();
                    if (cls == Hunter.class && hunterCount >= HUNTER_LIMIT) continue;
                    if (rand.nextDouble() <= CREATION_PROBABILITIES.get(cls)) {
                        actors.add(entry.getValue().apply(location, sex));
                        if (cls == Hunter.class) hunterCount++;
                        break;
                    }
                }
                // else leave the location empty
            }
        }
    }

    /** Pause for a given time. */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }
}
