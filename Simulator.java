import java.util.NavigableMap;
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
    
    private final SimulationPopulation population;
    // The current state of the field.
    private final Field field;
    
    private final SimulatorClock clock;
    private final WeatherHandler weatherHandler;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // handles the simulation of the disease
    private final DiseaseHandler diseaseHandler;
    // swing component dashboard
    private Dashboard dashboard;
    // generator for the initial terrain and population
    private final PopulationGenerator populationGenerator;
    
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
        
        population = new SimulationPopulation();
        field = new Field(depth, width);
        clock = new SimulatorClock();
        weatherHandler = new WeatherHandler(clock);
        diseaseHandler = new DiseaseHandler( field);
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, this);
        populationGenerator = new PopulationGenerator(view, field);
        
        reset();
    }
    
    /**
     * Sets the dashboard field to later updates the interface at the end of simulation steps.
     * 
     * @param newDashboard the dashboard to be updated.
     */
    public void setDashboard(Dashboard newDashboard) {
        this.dashboard = newDashboard;
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
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
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
        population.simulateStep(weatherHandler.getWeather(), clock.getDayState());
        
        view.showStatus(step, field, clock, weatherHandler.getWeather());

        if(dashboard != null) {
            dashboard.updateDashboard();            
        }
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        clock.reset();
        weatherHandler.reset();
        diseaseHandler.reset();
        population.clear();
        populationGenerator.populate(population);
        
        // Show the starting state in the view.
        view.showStatus(step, field, clock, weatherHandler.getWeather());
    }

    /**
     * Returns the tracked disease history.
     */
    public NavigableMap<Integer, Integer> getDiseaseHistory()
    {
        return diseaseHandler.getInfectionCounts();
    }
}
