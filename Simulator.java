/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2.0
 */
public class Simulator
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    // The default dimensions
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 80;

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    // Manages the lifecycle of all actors (animals and plants).
    private ActorManager actorManager;
    // The current state of the field.
    private Field field;

    private SimulatorClock clock;
    private WeatherHandler weatherHandler;
    // The current step of the simulation.
    private int step;
    // Bridges the simulation engine to the GUI (view + dashboard).
    private SimulationController controller;
    // handles the simulation of the disease
    private DiseaseHandler diseaseHandler;
    // generator for the initial terrain and population
    private PopulationGenerator populationGenerator;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTORS
    //////////////////////////////////////////////////////////////*/

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() throws Exception
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) throws Exception
    {
        if(depth % 20 != 0 || width % 20 != 0) {
            throw new Exception("Depth and Width must be factors of 20");
        }

        if(width <= 0 || depth <= 0) {
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actorManager = new ActorManager();
        field = new Field(depth, width);
        clock = new SimulatorClock();
        weatherHandler = new WeatherHandler(clock);
        diseaseHandler = new DiseaseHandler(field);
        SimulatorView view = new SimulatorView(depth, width);
        controller = new SimulationController(this, view);
        populationGenerator = new PopulationGenerator(controller.getView(), field);

        reset();
    }

    /*///////////////////////////////////////////////////////////////
                          ECOSYSTEM SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (1000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && controller.isViable(field); step++) {
            simulateOneStep();
            //delay(1000);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        clock.incrementStep();
        weatherHandler.updateWeather();
        diseaseHandler.simulateDiseaseStep();

        actorManager.stepAll(weatherHandler.getWeather(), clock.getDayState());

        controller.updateView(step, field, clock, weatherHandler.getWeather());
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actorManager.clear();
        populationGenerator.populate(actorManager.getAnimals(), actorManager.getPlants());

        // Show the starting state in the view.
        controller.updateView(step, field, clock, weatherHandler.getWeather());
    }

    /**
     * Pause for a given time.
     *
     * @param millisec The time to pause for, in milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
        }
    }
}
