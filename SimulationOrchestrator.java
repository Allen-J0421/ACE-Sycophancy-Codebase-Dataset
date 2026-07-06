import javafx.application.Application;

/**
 * Entry point and lifecycle manager for the simulation.
 * Creates and wires the Simulator (model) and SimulatorView (GUI), drives the
 * step loop, and refreshes the view after every state change.
 */
public class SimulationOrchestrator
{
    private static final int DEFAULT_DEPTH = 160;
    private static final int DEFAULT_WIDTH  = 120;

    private final Simulator    simulator;
    private final SimulatorView view;

    /**
     * Create an orchestrator with the default field dimensions.
     */
    public SimulationOrchestrator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create an orchestrator with specific field dimensions.
     *
     * @param depth Height of the field.
     * @param width Width of the field.
     */
    public SimulationOrchestrator(int depth, int width)
    {
        simulator = new Simulator(depth, width);

        view = new SimulatorView(this, depth, width);
        new ColorRegistry().applyTo(view);

        new Thread(() -> Application.launch(StatisticsView.class)).start();

        refreshView();
    }

    /**
     * Run the simulation for a long period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation for the given number of steps, stopping early if the
     * simulation is no longer viable.
     *
     * @param numSteps The number of steps to run.
     */
    public void simulate(int numSteps)
    {
        for (int s = 1; s <= numSteps && Simulator.getPopulationStats().isViable(); s++)
        {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Advance the simulation by one step and update the view.
     */
    public void simulateOneStep()
    {
        simulator.simulateOneStep();
        refreshView();
    }

    /**
     * Reset the simulation to a fresh starting state and update the view.
     */
    public void reset()
    {
        simulator.reset();
        refreshView();
        Simulator.resetStatisticsView = true;
    }

    /**
     * Push the current simulation state to the view.
     */
    private void refreshView()
    {
        view.showStatus(Simulator.getCurrentStep(), Simulator.getCurrentField());
    }

    /**
     * Pause for the given number of milliseconds.
     */
    private void delay(int millisec)
    {
        try
        {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie)
        {
            // Wake up.
        }
    }
}
