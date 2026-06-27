import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Seaweed, Salmon, Cod, Shark, Whale, and also the elmement of disease and weather
 *
 * @version 2022/03/02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // The probability that a fox will be created in any given grid position.
    private static final double SALMON_CREATION_PROBABILITY = 0.08;
    // The probability that a rabbit will be created in any given grid position.
    private static final double COD_CREATION_PROBABILITY = 0.08;
    // The probability that a seaweed will be created in any given grid position.
    private static final double SEAWEED_CREATION_PROBABILITY = 0.03;
    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.04;
    // The probability that a whale will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.03;

    // The probability that a storm may occur.
    private static final double STORM_HAPPEN_PROBABILITY = 0.14;

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

    // The inital level of dissolved oxygen in the water.
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
        this(depth, width, new DefaultCellColorStrategy(), new DefaultStatusTextStrategy());
    }

    /**
     * Create a simulation field with the given size and explicit UI strategies.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param cellColorStrategy Strategy for rendering cell colors.
     * @param statusTextStrategy Strategy for formatting status text and viability.
     */
    public Simulator(int depth, int width, CellColorStrategy cellColorStrategy, StatusTextStrategy statusTextStrategy)
    {
        if(cellColorStrategy == null) {
            cellColorStrategy = new DefaultCellColorStrategy();
        }
        if(statusTextStrategy == null) {
            statusTextStrategy = new DefaultStatusTextStrategy();
        }

        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        creatures = new ArrayList<>();

        field = new Field(depth, width);
        weather = new Weather(field);
        disease = new Disease();

        oxygenLevel = 1;

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, cellColorStrategy, statusTextStrategy);

        // Setup a valid starting point.
        reset();
    }

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
     *
     * Iterate over the field, update each creature, track oxygen exchange,
     * apply weather effects, and update disease state.
     */
    public void simulateOneStep()
    {
        step++;
        double totalOxygenInvolved = 0;
        disease.creationSourceOfInfection(creatures, step);

        // Provide space for newborn animals.
        List<Creature> newCreatures = new ArrayList<>();
        // Let all creatures act.
        for(Iterator<Creature> it = creatures.iterator(); it.hasNext(); ) {
            Creature creature = it.next();
            totalOxygenInvolved += creature.act(newCreatures, timeOfDay(), oxygenLevel, disease, step);
            if(!creature.isAlive()) {
                it.remove();
            }
        }

        updateStormState();
        oxygenLevel += totalOxygenInvolved;
        updateDiseaseState();
        creatures.addAll(newCreatures);
        view.showStatus(step, field, timeOfDay(), weather, oxygenLevel);
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

        // Show the starting state in the view.
        view.showStatus(step, field, timeOfDay(), weather, oxygenLevel);
    }

    /**
     * Randomly populate the field with Salmon, Seaweed, Shark and Whale.
     * notice if and else if is based on the creation probability of a creature in an ascending order
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                populateLocation(rand, row, col);
            }
        }
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

    /**
     * 5 steps is considered as a day time followed by 5 steps considered as a night.
     *
     * @return true If currently step is day time false if it is night time.
     */
    public boolean timeOfDay()
    {
        return (step % 10) < 5;
    }

    /**
     * If all animals infected die or all animals get immunity, the disease stops.
     * Only animals can be infected.
     */
    public boolean identifyIfDiseaseStops()
    {
        for(Iterator<Creature> it = creatures.iterator(); it.hasNext(); ) {
            Creature creature = it.next();
            if(creature instanceof Animal) {
                Animal ani = (Animal)creature;
                if(ani.getIsInfected() && !ani.getIsImmuned()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Decide whether a storm starts this step and update the storm state.
     */
    private void updateStormState()
    {
        if(Randomizer.getRandom().nextDouble() <= STORM_HAPPEN_PROBABILITY) {
            weather.underwaterStorm(3);
            weather.setStormStart(true);
        }
        else {
            weather.setStormStart(false);
        }
    }

    /**
     * Update the disease spread flag after all creatures have acted.
     */
    private void updateDiseaseState()
    {
        if(disease.getIsSpread()) {
            disease.setIsSpread(identifyIfDiseaseStops());
        }
    }

    /**
     * Populate a single grid location with at most one creature.
     */
    private void populateLocation(Random rand, int row, int col)
    {
        Location location = new Location(row, col);
        if(rand.nextDouble() <= SALMON_CREATION_PROBABILITY) {
            creatures.add(new Salmon(true, field, location));
        }
        else if(rand.nextDouble() <= COD_CREATION_PROBABILITY) {
            creatures.add(new Cod(true, field, location));
        }
        else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
            creatures.add(new Seaweed(true, field, location));
        }
        else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
            creatures.add(new Shark(true, field, location));
        }
        else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
            creatures.add(new Whale(true, field, location));
        }
    }
}
