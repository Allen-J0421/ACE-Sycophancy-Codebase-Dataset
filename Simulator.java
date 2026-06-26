import java.awt.event.ActionListener;
import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing different organisms.
 *
 * This class is a controller: it owns a {@link SimulationEngine} (the model) and
 * a {@link SimulatorView} (the GUI), wires the view's buttons to run controls,
 * and renders the engine's state after each step. All model logic lives in the
 * engine.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    private static boolean playingSimulation = false;
    private static int remainingSteps;

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 240;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 160;

    // The simulation model.
    private SimulationEngine engine;
    // A graphical view of the simulation.
    private SimulatorView view;


    /**
     * A simulator constructor to set the creation probability of each actor.
     * This constructor can be used to test different combinations of probabilities to find an optimal combination
     *
     * @param GrassProbability Probability of Grass being generated
     * @param DeerProbability Probability of Deer being generated
     * @param CoyoteProbability Probability of Coyote being generated
     * @param WolfProbability Probability of Wolf being generated
     * @param EagleProbability Probability of Eagle being generated
     * @param HunterProbability Probability of Hunter being generated
     * @param MouseProbability Probability of Mouse being generated
     */
    public Simulator(double GrassProbability, double DeerProbability, double CoyoteProbability, double WolfProbability, double EagleProbability, double HunterProbability, double MouseProbability){
        this(DEFAULT_WIDTH, DEFAULT_DEPTH);

        engine = new SimulationEngine(DEFAULT_DEPTH, DEFAULT_WIDTH);
        engine.setCreationProbabilities(Map.ofEntries(
                Map.entry(Coyote.class, CoyoteProbability),
                Map.entry(Deer.class, DeerProbability),
                Map.entry(Wolf.class, WolfProbability),
                Map.entry(Eagle.class, EagleProbability),
                Map.entry(Mouse.class, MouseProbability),
                Map.entry(Grass.class, GrassProbability),
                Map.entry(Hunter.class, HunterProbability)
        ));

        reset();
    }

    /**
     * Construct a simulation field with default size.
     */
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
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        engine = new SimulationEngine(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        for(Species species : Species.values()) {
            view.setColor(species.actorClass(), species.color());
        }

        // adding additional buttons to the GUI
        view.addLongButtonListener(longButtonListener);
        view.addOneStepButtonListener(startButtonListener);
        view.addStopButtonListener(stopButtonListener);
        view.addResetButtonListener(resetButtonListener);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        remainingSteps += 4000;
        simulate(remainingSteps);
    }

    /**
     * Whenever the startButton is pressed,the simulateOneStep()
     * method is called.
     */
    private ActionListener startButtonListener = e -> {
        if(playingSimulation){
            System.out.println("Stop the simulation first");
        }
        else {
            playingSimulation = true;
            simulateOneStep();
            playingSimulation = false;
        }
    };

    /**
     * Whenever the stopButton is pressed, the simulation is stopped.
     */
    private ActionListener stopButtonListener = e -> {
        playingSimulation = false;
        Thread.currentThread().interrupt();
    };

    /**
     * Whenever the resetButton is pressed, the simulation is reset.
     */
    private ActionListener resetButtonListener = e -> {
        playingSimulation = false;
        reset();
    };

    /**
     * Whenever the longButton is pressed,runLongSimulation() is called.
     */
    private ActionListener longButtonListener = e -> {
        Thread runLongSimThread = new Thread("SimulationRunThread"){
            public void run() { runLongSimulation(); }
        };

        if (!playingSimulation){
            runLongSimThread.start();
        }
        else{
            System.out.println("Stop the simulation first");
        }
    };


    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        playingSimulation = true;
        remainingSteps = numSteps;

        int backupCounter = 1;
        while(playingSimulation){
            for(int step = 1; step <= numSteps && view.isViable(engine.getField()); step++) {
                simulateOneStep();
                backupCounter++;
//            delay(60);   // uncomment this to run more slowly
            }
            if(!view.isViable(engine.getField())){
                // System.out.println("STEPS COMPLETED: "+backupCounter);
                // uncomment this while optimizing for population stability
                Thread.currentThread().interrupt();
                if(backupCounter > SimulationInfo.HIGHEST_STEPS){
                    // If multiple instances of the simulation are created in the same session (for testing),
                    // the fields in SimulationInfo can be used for optimizing the default probabilities
                    SimulationInfo.HIGHEST_STEPS = backupCounter;
                    SimulationInfo.HIGHEST_STEP_PROBS = engine.getCreationProbabilities();
                }
                playingSimulation = false;
                break;
            }
        }
        remainingSteps = numSteps - backupCounter;
    }

    /**
     * Run the simulation from its current state for a single step, then render
     * the new state.
     */
    public void simulateOneStep()
    {
        if(!playingSimulation){ return; }
        engine.step();
        showStatus();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        engine.reset();
        // Show the starting state in the view.
        showStatus();
    }

    /**
     * Render the engine's current state in the view.
     */
    private void showStatus()
    {
        Environment environment = engine.getEnvironment();
        view.showStatus(engine.getStep(),
                environment.getWeather().getCurrentWeather().toString(),
                environment.getTime().getCurrentTimeString(),
                engine.getField());
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


}
