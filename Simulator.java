import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing actors, or animals and plants.
 *
 * @version 27.02.22
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 250;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 250;
    // List of actors in the field.
    private List<Actor> actors;
    // The probabilities that actors will be created in any given grid position.
    private Map<Actor,Double> actorCreationProb; 
    // List of diseases in the simulation.
    static final List<Disease> diseases = createDiseases();
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // The current time in the field.
    private Time time;
    // The current weather condition of the field.
    private WeatherCond weatherCond;

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

        actors = new ArrayList<>();
        field = new Field(depth, width);
        time = new Time(3);
        actorCreationProb = createActorCreationProb();
        weatherCond = WeatherCond.Sunny; //Sunny weather is default 

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Lemur.class, Color.RED.darker());
        view.setColor(Panther.class, Color.ORANGE);
        view.setColor(Grass.class, Color.GREEN.darker());
        view.setColor(Water_Fern.class, Color.GREEN.brighter());
        view.setColor(Alligator.class, Color.LIGHT_GRAY);
        view.setColor(Salamander.class, Color.MAGENTA);
        view.setColor(Catfish.class, Color.BLACK);

        
        weatherCond.setTime(time);
        // Setup a valid starting point.
        reset();
    }

    /**
     * Create a Map with a key of an Actor object, one of each subclass of Animal and Plant,
     * and a value of the creation proability.
     * @return The actor creation prbability Map.
     */
    private Map<Actor,Double> createActorCreationProb()
    {
        Map<Actor,Double> tempMap = new HashMap<>();
        Location location = new Location(0, 0);
        tempMap.put(new Grass(time, field, location),0.8);
        tempMap.put(new Water_Fern(time, field, location),0.9);
        tempMap.put(new Salamander(time, field, location),0.5);
        tempMap.put(new Catfish(time, field, location),0.7);
        tempMap.put(new Lemur(time, field, location),0.45);
        tempMap.put(new Panther(time, field, location),0.3);
        tempMap.put(new Alligator(time, field, location),0.25);
        
        
        return tempMap;
    }

    /**
     * Create a List of all the diseases in the simulation, specify which actors start with the disease and which actors can catch the disease.
     * Starting and affected actors are specified by a Map with a String key and double value, of probability to get disease for starting actors,
     * and disease severity for affected actors. Severity ranges from 0.0 to 1.0, 0.0 being completely fatal as soon as contracted, and 1.0 meaning
     * the actor is just a disease carrier.
     * @return The diseases List.
     */
    private static List<Disease> createDiseases()
    {
        List<Disease> list = new ArrayList<>();
        Disease dengue = new Disease("dengue", true, true);

        dengue.getActorsAffectedMap().put(Lemur.class, 0.9);
        dengue.getActorsAffectedMap().put(Panther.class, 0.6);
        dengue.getActorsAffectedMap().put(Alligator.class, 0.6);

        dengue.getStartingActorsMap().put(Lemur.class, 0.4);
        list.add(dengue);

        Disease river_fever = new Disease("river_fever", true, false);

        river_fever.getActorsAffectedMap().put(Water_Fern.class, 1.0);
        river_fever.getActorsAffectedMap().put(Catfish.class, 0.7);
        river_fever.getActorsAffectedMap().put(Salamander.class, 0.8);

        river_fever.getStartingActorsMap().put(Water_Fern.class, 0.4);
        list.add(river_fever);
        
        return list;
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each actor.
     */
    public void simulateOneStep()
    {
        step++;
        time.incrementTime(); 
        weatherCond = weatherCond.nextCondition();
        // Provide space for newborn actors.

        List<Actor> newActors = new ArrayList<>();        
        // Let all actors act.
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, weatherCond);
            if(! actor.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born actors to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field, weatherCond, diseases);
        field.setWeatherField(weatherCond);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field, weatherCond, diseases);
    }

    /**
     * Randomly populate the field with actors.
     * Actors can only populate areas they will survive in
     * i.e. actors that will not survive in water cannot be placed there.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                for (Actor actor : actorCreationProb.keySet()){
                    Location location = new Location(row, col);
                    if((rand.nextDouble() <= actorCreationProb.get(actor)) && ((actor.canMoveOnLand() && actor.canMoveOnWater()) || (field.isUnderWater(location.getRow(), location.getCol()) && actor.canMoveOnWater()) || (!field.isUnderWater(location.getRow(), location.getCol()) && actor.canMoveOnLand()))) {
                        Actor newActor = actor.birth(new Location(row, col));
                        actors.add(newActor);
                        break;
                    }
                }
            }
        }
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
