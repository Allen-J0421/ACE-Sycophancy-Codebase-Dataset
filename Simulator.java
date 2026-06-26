import java.util.ArrayList;
import java.util.List;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing different organisms.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    private boolean playingSimulation;
    private int remainingSteps;

    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // Environment in the simulation.
    private final Environment environment;
    // Configuration and actor creation rules for the simulation.
    private final SimulationConfig config;
    private final DiseaseService diseaseService;
    private final WeatherService weatherService;
    private final ActorService actorService;
    // Published state for observers.
    private final List<SimulationObserver> observers;
    private SimulationState currentState;

    /**
     * A simulator constructor to set the creation probability of each actor.
     * This constructor can be used to test different combinations of probabilities.
     */
    public Simulator(double grassProbability,
            double deerProbability,
            double coyoteProbability,
            double wolfProbability,
            double eagleProbability,
            double hunterProbability,
            double mouseProbability)
    {
        this(
                new RandomProvider(),
                SimulationConfig.withCreationProbabilities(
                        grassProbability,
                        deerProbability,
                        coyoteProbability,
                        wolfProbability,
                        eagleProbability,
                        hunterProbability,
                        mouseProbability
                ),
                SimulationConfig.DEFAULT_DEPTH,
                SimulationConfig.DEFAULT_WIDTH
        );
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(new RandomProvider(), SimulationConfig.defaultConfig(), SimulationConfig.DEFAULT_DEPTH, SimulationConfig.DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(new RandomProvider(), SimulationConfig.defaultConfig(), depth, width);
    }

    public Simulator(RandomProvider randomProvider)
    {
        this(randomProvider, SimulationConfig.defaultConfig(), SimulationConfig.DEFAULT_DEPTH, SimulationConfig.DEFAULT_WIDTH);
    }

    public Simulator(RandomProvider randomProvider, int depth, int width)
    {
        this(randomProvider, SimulationConfig.defaultConfig(), depth, width);
    }

    public Simulator(RandomProvider randomProvider,
            double grassProbability,
            double deerProbability,
            double coyoteProbability,
            double wolfProbability,
            double eagleProbability,
            double hunterProbability,
            double mouseProbability)
    {
        this(
                randomProvider,
                SimulationConfig.withCreationProbabilities(
                        grassProbability,
                        deerProbability,
                        coyoteProbability,
                        wolfProbability,
                        eagleProbability,
                        hunterProbability,
                        mouseProbability
                ),
                SimulationConfig.DEFAULT_DEPTH,
                SimulationConfig.DEFAULT_WIDTH
        );
    }

    private Simulator(RandomProvider randomProvider, SimulationConfig config, int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = SimulationConfig.DEFAULT_DEPTH;
            width = SimulationConfig.DEFAULT_WIDTH;
        }

        this.config = config;
        this.diseaseService = new DiseaseService(randomProvider);
        this.weatherService = new WeatherService(randomProvider);
        MovementService movementService = new MovementService(randomProvider);
        OrganismFactory organismFactory = new OrganismFactory(randomProvider, config, diseaseService);
        this.field = new Field(randomProvider, organismFactory, diseaseService, movementService, depth, width);
        this.actorService = new ActorService(field, organismFactory, diseaseService);
        this.environment = new Environment(new Time(), weatherService);
        this.observers = new ArrayList<>();

        reset();
    }

    /**
     * Register an observer and immediately publish the current state to it.
     */
    public void addObserver(SimulationObserver observer)
    {
        observers.add(observer);
        observer.onStateChanged(currentState);
    }

    public void removeObserver(SimulationObserver observer)
    {
        observers.remove(observer);
    }

    public SimulationState getCurrentState()
    {
        return currentState;
    }

    public boolean isPlayingSimulation()
    {
        return playingSimulation;
    }

    /**
     * Run the simulation from its current state for a reasonably long period.
     */
    public void runLongSimulation()
    {
        remainingSteps += SimulationConfig.LONG_SIMULATION_STEPS;
        simulate(remainingSteps);
    }

    /**
     * Start a long simulation on a background thread.
     */
    public void startLongSimulation()
    {
        if(playingSimulation) {
            System.out.println("Stop the simulation first");
            return;
        }

        Thread runLongSimThread = new Thread(this::runLongSimulation, "SimulationRunThread");
        runLongSimThread.start();
    }

    /**
     * Run a single simulation step if the simulator is currently idle.
     */
    public void stepOnce()
    {
        if(playingSimulation) {
            System.out.println("Stop the simulation first");
            return;
        }

        playingSimulation = true;
        simulateOneStep();
        playingSimulation = false;
    }

    /**
     * Stop the simulation loop.
     */
    public void stop()
    {
        playingSimulation = false;
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        playingSimulation = true;
        remainingSteps = numSteps;

        int completedSteps = 0;
        while(playingSimulation) {
            for(int currentStep = 0;
                    currentStep < numSteps && playingSimulation && currentState.isViable();
                    currentStep++) {
                simulateOneStep();
                completedSteps++;
//                delay(60);   // uncomment this to run more slowly
            }
            if(!currentState.isViable()) {
                updateBestSimulationResult(completedSteps);
                playingSimulation = false;
            }
        }

        remainingSteps = Math.max(0, numSteps - completedSteps);
    }

    /**
     * Run the simulation from its current state for a single step.
     */
    public void simulateOneStep()
    {
        if(!playingSimulation) {
            return;
        }

        step++;
        environment.advanceTime();
        weatherService.advance();
        actorService.updateActors(environment, step);
        publishState();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        playingSimulation = false;
        remainingSteps = 0;
        step = 0;
        environment.reset();
        weatherService.reset();
        diseaseService.reset();

        actorService.reset();

        publishState();
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    private void publishState()
    {
        currentState = SimulationState.capture(step, environment, field);
        for(SimulationObserver observer : observers) {
            observer.onStateChanged(currentState);
        }
    }

    private void updateBestSimulationResult(int completedSteps)
    {
        if(completedSteps > SimulationInfo.HIGHEST_STEPS) {
            SimulationInfo.HIGHEST_STEPS = completedSteps;
            SimulationInfo.HIGHEST_STEP_PROBS = config.getCreationProbabilities();
        }
    }
}
