import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing different organisms.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    private static final Random rand = Randomizer.getRandom();

    private boolean playingSimulation;
    private int remainingSteps;

    // List of actors in the field.
    private final List<Actor> actors;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // Environment in the simulation.
    private final Environment environment;
    // Configuration and actor creation rules for the simulation.
    private final SimulationConfig config;
    private final ActorFactory actorFactory;

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
        this(SimulationConfig.defaultConfig(), SimulationConfig.DEFAULT_DEPTH, SimulationConfig.DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(SimulationConfig.defaultConfig(), depth, width);
    }

    private Simulator(SimulationConfig config, int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = SimulationConfig.DEFAULT_DEPTH;
            width = SimulationConfig.DEFAULT_WIDTH;
        }

        this.config = config;
        this.actors = new ArrayList<>();
        this.field = new Field(depth, width);
        this.environment = new Environment(new Time(), new Weather());
        this.actorFactory = new ActorFactory(config);
        this.view = createView(depth, width);

        reset();
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
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        playingSimulation = true;
        remainingSteps = numSteps;

        int completedSteps = 0;
        while(playingSimulation){
            for(int currentStep = 0; currentStep < numSteps && playingSimulation && view.isViable(field); currentStep++) {
                simulateOneStep();
                completedSteps++;
//                delay(60);   // uncomment this to run more slowly
            }
            if(!view.isViable(field)){
                updateBestSimulationResult(completedSteps);
                playingSimulation = false;
            }
        }

        remainingSteps = Math.max(0, numSteps - completedSteps);
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each organism.
     */
    public void simulateOneStep()
    {
        if(!playingSimulation) {
            return;
        }

        step++;
        environment.advance(step);

        List<Actor> newActors = new ArrayList<>();
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            processActor(actor, newActors);
            maybeGrowPlant(actor);

            if(!actor.isAlive()) {
                it.remove();
            }
        }

        actors.addAll(newActors);
        actors.addAll(actorFactory.createGrassPatches(field, environment));
        showStatus();
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

        actors.clear();
        actors.addAll(actorFactory.populate(field));

        showStatus();
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

    private SimulatorView createView(int depth, int width)
    {
        SimulatorView simulatorView = new SimulatorView(depth, width);
        for(Class<?> cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            simulatorView.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }

        simulatorView.addLongButtonListener(longButtonListener);
        simulatorView.addOneStepButtonListener(startButtonListener);
        simulatorView.addStopButtonListener(stopButtonListener);
        simulatorView.addResetButtonListener(resetButtonListener);
        return simulatorView;
    }

    private void processActor(Actor actor, List<Actor> newActors)
    {
        if(actor instanceof Animal animal) {
            if(!animal.isAwake(environment)) {
                return;
            }
            if(animal.isDiseased() && animal.getDisease().getPropagationRate() <= rand.nextDouble()) {
                animal.setDead();
            }
        }

        if(actor.isAlive()) {
            actor.act(newActors, environment);
        }
    }

    private void maybeGrowPlant(Actor actor)
    {
        if(actor instanceof Plant plant && step % plant.getStepsPerStage() == 0) {
            plant.incrementGrowth();
        }
    }

    private void showStatus()
    {
        view.showStatus(
                step,
                environment.getWeather().getCurrentWeather().toString(),
                environment.getTime().getCurrentTimeString(),
                field
        );
    }

    private void updateBestSimulationResult(int completedSteps)
    {
        if(completedSteps > SimulationInfo.HIGHEST_STEPS) {
            SimulationInfo.HIGHEST_STEPS = completedSteps;
            SimulationInfo.HIGHEST_STEP_PROBS = config.getCreationProbabilities();
        }
    }

    /**
     * Whenever the start button is pressed, the simulateOneStep() method is called.
     */
    private final ActionListener startButtonListener = e -> {
        if(playingSimulation) {
            System.out.println("Stop the simulation first");
        }
        else {
            playingSimulation = true;
            simulateOneStep();
            playingSimulation = false;
        }
    };

    /**
     * Whenever the stop button is pressed, the simulation is stopped.
     */
    private final ActionListener stopButtonListener = e -> playingSimulation = false;

    /**
     * Whenever the reset button is pressed, the simulation is reset.
     */
    private final ActionListener resetButtonListener = e -> reset();

    /**
     * Whenever the long button is pressed, runLongSimulation() is called.
     */
    private final ActionListener longButtonListener = e -> {
        Thread runLongSimThread = new Thread(this::runLongSimulation, "SimulationRunThread");

        if(!playingSimulation) {
            runLongSimThread.start();
        }
        else {
            System.out.println("Stop the simulation first");
        }
    };
}
