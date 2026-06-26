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
    private final SimulationContext context;
    private final ActorService actorService;
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
        this.context = new SimulationContext(randomProvider, config);
        this.field = new Field(context, depth, width);
        context.setField(field);
        OrganismFactory organismFactory = new OrganismFactory(context);
        context.setOrganismFactory(organismFactory);
        this.actorService = new ActorService(context, field);
        context.setActorService(actorService);
        this.environment = new Environment(new Time(), context);

        reset();
    }

    public SimulationState getCurrentState()
    {
        return currentState;
    }

    public SimulationContext getSimulationContext()
    {
        return context;
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

        startSimulation();
        simulateOneStep();
        finishSimulation();
    }

    /**
     * Stop the simulation loop.
     */
    public void stop()
    {
        if(playingSimulation) {
            playingSimulation = false;
            publishEvent(new SimulationStopped(currentState));
        }
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        startSimulation();
        remainingSteps = numSteps;

        int completedSteps = 0;
        for(int currentStep = 0;
                currentStep < numSteps && playingSimulation && currentState.isViable();
                currentStep++) {
            simulateOneStep();
            completedSteps++;
//                delay(60);   // uncomment this to run more slowly
        }

        remainingSteps = Math.max(0, numSteps - completedSteps);
        if(!currentState.isViable()) {
            updateBestSimulationResult(completedSteps);
        }
        finishSimulation();
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
        context.getWeatherService().advance();
        actorService.updateActors(environment, step);
        publishState(new StepAdvanced(captureState()));
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
        context.getWeatherService().reset();
        context.getDiseaseService().reset();

        actorService.reset();

        publishState(new SimulationReset(captureState()));
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

    private SimulationState captureState()
    {
        currentState = SimulationState.capture(step, environment, field);
        return currentState;
    }

    private void publishState(SimulationEvent event)
    {
        publishEvent(event);
    }

    private void publishEvent(SimulationEvent event)
    {
        context.getEventService().publish(event);
    }

    private void startSimulation()
    {
        playingSimulation = true;
        publishEvent(new SimulationStarted(currentState));
    }

    private void finishSimulation()
    {
        if(playingSimulation) {
            playingSimulation = false;
            publishEvent(new SimulationStopped(currentState));
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
