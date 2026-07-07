import java.awt.event.ActionListener;
import java.util.*;

/**
 * Coordinates the simulation GUI and run lifecycle. Simulation state and
 * per-step actor logic live in SimulationEngine.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    private static boolean playingSimulation = false;
    private static int remainingSteps;

    private SimulationEngine engine;
    private SimulatorView view;

    /**
     * A simulator constructor to set the CREATION_PROBABILITY of each actor.
     * This constructor can be used to test different combinations of probabilities
     * to find an optimal combination.
     */
    public Simulator(double GrassProbability, double DeerProbability, double CoyoteProbability,
                     double WolfProbability, double EagleProbability, double HunterProbability,
                     double MouseProbability)
    {
        this(SimulationEngine.DEFAULT_WIDTH, SimulationEngine.DEFAULT_DEPTH);

        engine = new SimulationEngine(SimulationEngine.DEFAULT_DEPTH, SimulationEngine.DEFAULT_WIDTH,
                Map.ofEntries(
                        Map.entry(Coyote.class, CoyoteProbability),
                        Map.entry(Deer.class,   DeerProbability),
                        Map.entry(Wolf.class,   WolfProbability),
                        Map.entry(Eagle.class,  EagleProbability),
                        Map.entry(Mouse.class,  MouseProbability),
                        Map.entry(Grass.class,  GrassProbability),
                        Map.entry(Hunter.class, HunterProbability)
                ));
        showCurrentStatus();
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(SimulationEngine.DEFAULT_DEPTH, SimulationEngine.DEFAULT_WIDTH);
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
            depth = SimulationEngine.DEFAULT_DEPTH;
            width = SimulationEngine.DEFAULT_WIDTH;
        }

        engine = new SimulationEngine(depth, width);

        view = new SimulatorView(depth, width);
        for(Class cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            view.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }

        view.addLongButtonListener(longButtonListener);
        view.addOneStepButtonListener(startButtonListener);
        view.addStopButtonListener(stopButtonListener);
        view.addResetButtonListener(resetButtonListener);

        showCurrentStatus();
    }

    /**
     * Run the simulation from its current state for a reasonably long period
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        remainingSteps += 4000;
        simulate(remainingSteps);
    }

    /**
     * Whenever the startButton is pressed, simulateOneStep() is called.
     */
    private ActionListener startButtonListener = e -> {
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
     * Whenever the longButton is pressed, runLongSimulation() is called.
     */
    private ActionListener longButtonListener = e -> {
        Thread runLongSimThread = new Thread("SimulationRunThread") {
            public void run() { runLongSimulation(); }
        };

        if(!playingSimulation) {
            runLongSimThread.start();
        }
        else {
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
        while(playingSimulation) {
            for(int step = 1; step <= numSteps && view.isViable(engine.getField()); step++) {
                simulateOneStep();
                backupCounter++;
//              delay(60);   // uncomment this to run more slowly
            }
            if(!view.isViable(engine.getField())) {
                Thread.currentThread().interrupt();
                if(backupCounter > SimulationInfo.HIGHEST_STEPS) {
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
     * Advance the simulation by one step and refresh the view.
     */
    public void simulateOneStep()
    {
        if(!playingSimulation) { return; }
        engine.step();
        showCurrentStatus();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        engine.reset();
        showCurrentStatus();
    }

    /**
     * Refresh the view from the current engine state.
     */
    private void showCurrentStatus()
    {
        Environment env = engine.getEnvironment();
        view.showStatus(engine.getStep(),
                env.getWeather().getCurrentWeather().toString(),
                env.getTime().getCurrentTimeString(),
                engine.getField());
    }

    /**
     * Pause for a given time.
     * @param millisec The time to pause for, in milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch(InterruptedException ie) {
            // wake up
        }
    }
}
