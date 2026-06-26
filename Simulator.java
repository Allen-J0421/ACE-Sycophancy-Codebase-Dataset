import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

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
    private List<ActorCreationRule> actorCreationRules; 
    // List of diseases in the simulation.
    public static final List<Disease> diseases = createDiseases();
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
     * Creates an actor at a location.
     */
    private interface ActorFactory
    {
        Actor create(Time time, Field field, Location location);
    }

    /**
     * Describes how a species is initially spawned.
     */
    private static class ActorCreationRule
    {
        private final double probability;
        private final boolean canMoveOnLand;
        private final boolean canMoveOnWater;
        private final ActorFactory factory;

        private ActorCreationRule(double probability, boolean canMoveOnLand,
                                  boolean canMoveOnWater, ActorFactory factory)
        {
            this.probability = probability;
            this.canMoveOnLand = canMoveOnLand;
            this.canMoveOnWater = canMoveOnWater;
            this.factory = factory;
        }

        private boolean canCreateAt(Random rand, Field field, Location location)
        {
            return rand.nextDouble() <= probability &&
                field.canOccupy(canMoveOnLand, canMoveOnWater, location);
        }

        private Actor createActor(Time time, Field field, Location location)
        {
            return factory.create(time, field, location);
        }
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

        actors = new ArrayList<>();
        field = new Field(depth, width);
        time = new Time(3);
        actorCreationRules = createActorCreationRules();
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
     * Create the initial actor creation rules.
     * @return The actor creation rules.
     */
    private List<ActorCreationRule> createActorCreationRules()
    {
        List<ActorCreationRule> rules = new ArrayList<>();
        rules.add(new ActorCreationRule(0.8, true, false, Grass::new));
        rules.add(new ActorCreationRule(0.9, false, true, Water_Fern::new));
        rules.add(new ActorCreationRule(0.5, true, true, Salamander::new));
        rules.add(new ActorCreationRule(0.7, false, true, Catfish::new));
        rules.add(new ActorCreationRule(0.45, true, false, Lemur::new));
        rules.add(new ActorCreationRule(0.3, true, false, Panther::new));
        rules.add(new ActorCreationRule(0.25, true, true, Alligator::new));
        return rules;
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
        Disease dengue = new Disease("dengue", true, true)
            .addAffectedActor(Lemur.name, 0.9)
            .addAffectedActor(Panther.name, 0.6)
            .addAffectedActor(Alligator.name, 0.6)
            .addStartingActor(Lemur.name, 0.4);
        list.add(dengue);
        
        Disease river_fever = new Disease("river_fever", true, false)
            .addAffectedActor(Water_Fern.name, 1.0)
            .addAffectedActor(Catfish.name, 0.7)
            .addAffectedActor(Salamander.name, 0.8)
            .addStartingActor(Water_Fern.name, 0.4);
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
    public final void reset()
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
                for (ActorCreationRule actorCreationRule : actorCreationRules){
                    Location location = new Location(row, col);
                    if(actorCreationRule.canCreateAt(rand, field, location)) {
                        Actor newActor = actorCreationRule.createActor(time, field, location);
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
