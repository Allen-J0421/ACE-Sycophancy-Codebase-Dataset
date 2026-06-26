import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
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
    private static final int DIMENSION_FACTOR = 20;
    private static final int LONG_SIMULATION_STEPS = 1000;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    // List of animals in the field.
    private List<Actor> animals;
    private List<Actor> plants;
    // The current state of the field.
    private Field field;
    
    private SimulatorClock clock;
    private WeatherHandler weatherHandler;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // handles the simulation of the disease
    private DiseaseHandler diseaseHandler;
    // swing component dashboard
    private Dashboard dashboard;
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
        depth = normalizeDimension(depth, DEFAULT_DEPTH);
        width = normalizeDimension(width, DEFAULT_WIDTH);
        validateDimensions(depth, width);
        
        animals = new ArrayList<>();
        plants = new ArrayList<>();
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
     * Use the default dimension when an invalid non-positive value is supplied.
     */
    private int normalizeDimension(int value, int defaultValue)
    {
        if(value <= 0) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Validate dimensions against the field block size used by the simulation.
     */
    private void validateDimensions(int depth, int width) throws Exception
    {
        if(depth % DIMENSION_FACTOR != 0 || width % DIMENSION_FACTOR != 0) {
            throw new Exception("Depth and Width must be factors of " + DIMENSION_FACTOR);
        }
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
        simulate(LONG_SIMULATION_STEPS);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * 
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int currentStep = 1; currentStep <= numSteps && view.isViable(field); currentStep++) {
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
        
        List<Actor> newAnimals = new ArrayList<>();
        List<Actor> newPlants = new ArrayList<>();
        
        actorsAct(plants, newPlants);
        actorsAct(animals, newAnimals);        
        
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
        animals.clear();
        plants.clear();
        populationGenerator.populate(animals, plants);
        
        // Show the starting state in the view.
        view.showStatus(step, field, clock, weatherHandler.getWeather());
    }
    
    /**
     * Iterates through a set of actors and makes them act, die if they are dead and subsequently adds the newborns.
     * 
     * @param actors List of all actors in the field.
     * @param newActors List of newborns after actors act.
     */
    private void actorsAct(List<Actor> actors, List<Actor> newActors) {
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, weatherHandler.getWeather(), clock.getDayState());
            if(! actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
    }
}
