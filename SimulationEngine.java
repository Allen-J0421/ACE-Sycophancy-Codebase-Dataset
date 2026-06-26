import java.util.*;

/**
 * The core simulation engine: owns simulation state and drives the step loop.
 * It is deliberately decoupled from the GUI — the only way it communicates
 * with the view layer is through the {@link SimulationObserver} passed at
 * construction time.
 * Temporal concerns (step counting, time-of-day, weather) are owned by
 * {@link SimulationClock}; actor lifecycle is delegated to {@link ActorManager};
 * world-building is delegated to {@link FieldPopulator}.
 *
 * @version 2022.03.02
 */
public class SimulationEngine implements SimulationControls
{
    static final int DEFAULT_DEPTH = 160;
    static final int DEFAULT_WIDTH = 240;

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

    private final Map<Class<?>, Double> creationProbabilities;
    private final SimulationObserver observer;
    private final SimulationClock clock;
    private final ActorManager actorManager;
    private final FieldPopulator populator;
    private final Field field;

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
     * @param depth                 Depth of the simulation field.
     * @param width                 Width of the simulation field.
     * @param creationProbabilities Per-class spawn probability used during populate().
     * @param observer              Observer that receives step notifications and answers viability queries.
     */
    public SimulationEngine(int depth, int width, Map<Class<?>, Double> creationProbabilities,
                            SimulationObserver observer)
    {
        this.creationProbabilities = creationProbabilities;
        this.observer = observer;
        field = new Field(depth, width);
        clock = new SimulationClock();
        actorManager = new ActorManager();
        populator = new FieldPopulator(field, actorManager, creationProbabilities);
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
        clock.reset();
        actorManager.clear();
        populator.reset();
        populator.populate();
        observer.onStep(clock.getStep(), clock.getEnvironment(), field);
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
     * Advance the simulation by one step: tick the clock, update all actors,
     * spread rain-driven grass, then notify the observer.
     */
    public void simulateOneStep()
    {
        if (!playingSimulation) { return; }
        clock.advance();
        actorManager.tick(clock.getEnvironment());
        populator.plantGrassInPatches(clock.getEnvironment());
        observer.onStep(clock.getStep(), clock.getEnvironment(), field);
    }
}
