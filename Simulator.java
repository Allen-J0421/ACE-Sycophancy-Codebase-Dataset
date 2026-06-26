import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
    
    // List of animals in the field.
    private final List<Actor> animals;
    private final List<Actor> plants;
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
     * Sets the dashboard field to later updates the interface at the end of simulation steps.
     * 
     * @param newDashboard the dashboard to be updated.
     */
    public void setDashboard(Dashboard newDashboard) {
        this.dashboard = newDashboard;
    }

    /**
     * @return the disease infection history accumulated so far.
     */
    public Map<Integer, Integer> getDiseaseHistory()
    {
        return diseaseHandler.getInfectionHistory();
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
        for(int remainingSteps = numSteps; remainingSteps > 0 && view.isViable(field); remainingSteps--) {
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
        advanceSimulationState();
        Weather weather = weatherHandler.getWeather();
        DayState dayState = clock.getDayState();
        advanceActorGroup(plants, weather, dayState);
        advanceActorGroup(animals, weather, dayState);
        refreshViews();
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
     * Iterates through a set of actors and makes them act, then removes the dead and appends newborns.
     *
     * @param actors List of all actors in the field.
     * @param weather current weather state.
     * @param dayState current day state.
     */
    private void advanceActorGroup(List<Actor> actors, Weather weather, DayState dayState)
    {
        List<Actor> newActors = new ArrayList<>();
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, weather, dayState);
            if(! actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
    }

    /**
     * Advance the clock, weather, and disease state for the current step.
     */
    private void advanceSimulationState()
    {
        clock.incrementStep();
        weatherHandler.updateWeather();
        diseaseHandler.simulateDiseaseStep();
    }

    /**
     * Repaint the view and dashboard after a simulation step.
     */
    private void refreshViews()
    {
        view.showStatus(step, field, clock, weatherHandler.getWeather());
        if(dashboard != null) {
            dashboard.updateDashboard();
        }
    }
}
