import java.util.Map;
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

    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 80;

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private List<Actor> animals;
    private List<Actor> plants;
    private Field field;

    private SimulatorClock clock;
    private WeatherHandler weatherHandler;
    private int step;
    private SimulatorView view;
    private DiseaseHandler diseaseHandler;
    private Dashboard dashboard;
    private PopulationGenerator populationGenerator;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTORS
    //////////////////////////////////////////////////////////////*/

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be a positive multiple of {@link Field#BLOCK_SIZE}.
     * @param width Width of the field. Must be a positive multiple of {@link Field#BLOCK_SIZE}.
     * @throws IllegalArgumentException if depth or width are non-positive or not multiples of BLOCK_SIZE.
     */
    public Simulator(int depth, int width)
    {
        if(depth <= 0 || width <= 0) {
            throw new IllegalArgumentException("Depth and Width must be greater than zero");
        }
        if(depth % Field.BLOCK_SIZE != 0 || width % Field.BLOCK_SIZE != 0) {
            throw new IllegalArgumentException(
                "Depth and Width must be multiples of " + Field.BLOCK_SIZE);
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        clock = new SimulatorClock();
        weatherHandler = new WeatherHandler(clock);
        diseaseHandler = new DiseaseHandler(field);
        view = new SimulatorView(depth, width, this);
        populationGenerator = new PopulationGenerator(view, field);

        reset();
    }

    /**
     * Returns the per-step infection count recorded by the disease handler.
     */
    public Map<Integer, Integer> getDiseaseCount()
    {
        return diseaseHandler.getCount();
    }

    /**
     * Sets the dashboard to be updated at the end of each simulation step.
     *
     * @param newDashboard the dashboard to be updated.
     */
    public void setDashboard(Dashboard newDashboard)
    {
        this.dashboard = newDashboard;
    }

    /*///////////////////////////////////////////////////////////////
                          ECOSYSTEM SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Run the simulation for 1000 steps.
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stops early if the ecosystem is no longer viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int i = 1; i <= numSteps && view.isViable(field); i++) {
            simulateOneStep();
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     */
    public void simulateOneStep()
    {
        step++;
        clock.incrementStep();
        weatherHandler.updateWeather();
        diseaseHandler.simulateDiseaseStep(step);

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
        view.showStatus(step, field, clock, weatherHandler.getWeather());
    }

    /**
     * Iterates through a set of actors, makes them act, removes the dead, and adds newborns.
     *
     * @param actors List of all actors in the field.
     * @param newActors List of newborns after actors act.
     */
    private void actorsAct(List<Actor> actors, List<Actor> newActors)
    {
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, weatherHandler.getWeather(), clock.getDayState());
            if(!actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
    }
}
