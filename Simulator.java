import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Seaweed, Salmon, Cod, Shark, Whale, and also the elmement of disease and weather
 *
 * @version 2022/03/02
 */
public class Simulator
{
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 80;
    private static final int LONG_SIMULATION_STEPS = 1000;
    private static final int DAY_LENGTH = 5;
    private static final int FULL_DAY_LENGTH = DAY_LENGTH * 2;
    private static final int STORM_SCOPE = 3;
    private static final double STORM_HAPPEN_PROBABILITY = 0.14;
    private static final Color WEATHER_COLOR = Color.BLUE;

    // List of creatures in the field.
    private List<Creature> creatures;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    private Disease disease;
    private Weather weather;
    private SpeciesDefinition[] speciesDefinitions;

    // The initial level of dissolved oxygen in the water.
    private double oxygenLevel;

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
        int[] dimensions = sanitizeDimensions(depth, width);
        creatures = new ArrayList<>();
        field = new Field(dimensions[0], dimensions[1]);
        weather = new Weather(field);
        disease = new Disease();
        oxygenLevel = 1;
        speciesDefinitions = new SpeciesCatalog().getDefinitions();

        initializeView(dimensions[0], dimensions[1]);
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period.
     */
    public void runLongSimulation()
    {
        simulate(LONG_SIMULATION_STEPS);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int simulatedSteps = 0; simulatedSteps < numSteps && view.isViable(field); simulatedSteps++) {
            simulateOneStep();
            delay(60);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     */
    public void simulateOneStep()
    {
        step++;
        disease.beginSpreadIfPossible(creatures, step);

        List<Creature> newCreatures = new ArrayList<>();
        double oxygenChange = actForAllCreatures(newCreatures);

        weather.updateStorm(STORM_HAPPEN_PROBABILITY, STORM_SCOPE);
        oxygenLevel += oxygenChange;
        disease.refreshSpreadState(creatures);
        creatures.addAll(newCreatures);
        showStatus();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        creatures.clear();
        populate();
        oxygenLevel = 1;
        Animal.populationDieOfDisease = 0;
        showStatus();
    }

    /**
     * 5 steps is considered as a day time followed by 5 steps considered as a night.
     *
     * @return true If currently step is day time false if it is night time.
     */
    public boolean timeOfDay()
    {
        return (step % FULL_DAY_LENGTH) < DAY_LENGTH;
    }

    private int[] sanitizeDimensions(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return new int[]{DEFAULT_DEPTH, DEFAULT_WIDTH};
        }
        return new int[]{depth, width};
    }

    private void initializeView(int depth, int width)
    {
        view = new SimulatorView(depth, width);
        for(SpeciesDefinition speciesDefinition : speciesDefinitions) {
            view.setColor(speciesDefinition.getCreatureClass(), speciesDefinition.getDisplayColor());
        }
        view.setColor(Weather.class, WEATHER_COLOR);
    }

    private double actForAllCreatures(List<Creature> newCreatures)
    {
        double oxygenChange = 0;
        boolean atDayTime = timeOfDay();
        for(Iterator<Creature> iterator = creatures.iterator(); iterator.hasNext(); ) {
            Creature creature = iterator.next();
            oxygenChange += creature.act(newCreatures, atDayTime, oxygenLevel, disease, step);
            if(!creature.isAlive()) {
                iterator.remove();
            }
        }
        return oxygenChange;
    }

    private void showStatus()
    {
        view.showStatus(step, field, timeOfDay(), weather, oxygenLevel);
    }

    /**
     * Randomly populate the field with species from the catalog.
     */
    private void populate()
    {
        Random random = Randomizer.getRandom();
        field.clear();
        field.forEachLocation((location, creature) ->
            addCreature(createRandomCreature(random, location)));
    }

    /**
     *  Pause for a given time.
     *  @param millisec  The time to pause for, in milliseconds
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

    private void addCreature(Creature creature)
    {
        if(creature != null) {
            creatures.add(creature);
        }
    }

    private Creature createRandomCreature(Random random, Location location)
    {
        for(SpeciesDefinition speciesDefinition : speciesDefinitions) {
            if(random.nextDouble() <= speciesDefinition.getCreationProbability()) {
                return speciesDefinition.create(field, location);
            }
        }
        return null;
    }
}
