/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * Acts as the high-level entry point: owns the view and the controller,
 * drives the simulation loop, and keeps the view in sync after each step.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 200;

    // A graphical view of the simulation.
    private SimulatorView view;
    // Owns all simulation state and step logic.
    private SimulationController controller;
    // Observes the field and maintains population counts.
    private PopulationStatsMonitor monitor;

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
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        view = new SimulatorView(depth, width);
        controller = new SimulationController(depth, width);
        monitor = new PopulationStatsMonitor();

        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stops early if the simulation ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for (int i = 1; i <= numSteps && monitor.isViable(controller.getField()); i++) {
            simulateOneStep();
            delay(5);
        }
    }

    /**
     * Advance the simulation by one step, then refresh all view panels.
     */
    public void simulateOneStep()
    {
        controller.advance();
        monitor.observe(controller.getField());
        view.showStatus(controller.getStep(), controller.getField());
        view.updatePopulation(monitor.getPopulationSummary(controller.getField()));
        view.updateTimeLabel(controller.getDay(), controller.getHour());
        view.updateEnvironmentLabel(controller.getCurrentWeather(), controller.getCurrentTime());
    }

    /**
     * @return The current simulated hour within the day.
     */
    public int getHour()
    {
        return controller.getHour();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        RandomFieldPopulator populator = RandomFieldPopulator.createDefault();
        for (SpeciesEntry entry : populator.getSpecies()) {
            view.setColor(entry.speciesClass, entry.displayColor);
        }
        controller.reset(populator);
        monitor.observe(controller.getField());
        view.showStatus(controller.getStep(), controller.getField());
        view.updatePopulation(monitor.getPopulationSummary(controller.getField()));
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
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
