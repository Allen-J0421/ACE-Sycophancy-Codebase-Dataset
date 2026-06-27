import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing bears, badgers, wolves, frogs and hedgehogs.
 *
 * @version 2022.02.24 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.

    private static final int DEFAULT_WIDTH = 150;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;
    // The number of simulation steps between day/night transitions.
    private static final int DAY_NIGHT_CYCLE_LENGTH = 35;

    // The probability that a hedgehog will be created in any given grid position.
    private static final double HEDGEHOG_CREATION_PROBABILITY = 0.115;
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.005;
    // The probability that a badger will be created in any given grid position.
    private static final double BADGER_CREATION_PROBABILITY = 0.1;
    // The probability that a frog will be created in any given grid position.
    private static final double FROG_CREATION_PROBABILITY = 0.125;
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.005;
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.2;

    @FunctionalInterface
    private interface LivingBeingFactory
    {
        LivingBeing create(Field field, Location location);
    }

    private static final class SpeciesConfig
    {
        private final Class<? extends LivingBeing> type;
        private final Color color;
        private final double creationProbability;
        private final LivingBeingFactory factory;

        private SpeciesConfig(Class<? extends LivingBeing> type, Color color,
                              double creationProbability, LivingBeingFactory factory)
        {
            this.type = type;
            this.color = color;
            this.creationProbability = creationProbability;
            this.factory = factory;
        }

        private LivingBeing create(Field field, Location location)
        {
            return factory.create(field, location);
        }
    }

    private static final List<SpeciesConfig> SPECIES = List.of(
        new SpeciesConfig(Hedgehog.class, Color.YELLOW, HEDGEHOG_CREATION_PROBABILITY,
                          (field, location) -> new Hedgehog(true, field, location)),
        new SpeciesConfig(Bear.class, Color.RED, BEAR_CREATION_PROBABILITY,
                          (field, location) -> new Bear(true, field, location)),
        new SpeciesConfig(Badger.class, Color.CYAN, BADGER_CREATION_PROBABILITY,
                          (field, location) -> new Badger(true, field, location)),
        new SpeciesConfig(Frog.class, Color.MAGENTA, FROG_CREATION_PROBABILITY,
                          (field, location) -> new Frog(true, field, location)),
        new SpeciesConfig(Wolf.class, Color.PINK, WOLF_CREATION_PROBABILITY,
                          (field, location) -> new Wolf(true, field, location)),
        new SpeciesConfig(Plant.class, Color.GREEN, PLANT_CREATION_PROBABILITY,
                          (field, location) -> new Plant(true, field, location))
    );

    // List of living beings in the field.
    private final List<LivingBeing> livingBeings;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;

    private boolean night;
    // A graphical view of the simulation.
    private final SimulatorView view;
    
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runMediumSimulation();
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

        livingBeings = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        configureViewColors();

        // Setup a valid starting point.
        resetSimulation();
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
     * Run the simulation from its current state for a medium period, 1000 steps
     */
    public void runMediumSimulation() {
        simulate(1000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {

        for(int step = 1; step <= numSteps && FieldStats.isViable(field); step++) {
            simulateOneStep();
        }
    }

    /**
     * Returns if it is night time or not
     */
    public boolean isNightTime() {
        return night;
    }

    /**
     * Switches between night time and day time
     */
    private void switchNight() {
        setNight(!night);
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * living being
     */
    public void simulateOneStep()
    {
        step++;
        if(step % DAY_NIGHT_CYCLE_LENGTH == 0) {
            switchNight();
        }

        // Provide space for newborn living beings.
        List<LivingBeing> newBeings = new ArrayList<>();
        // Let all living beings act.
        for(Iterator<LivingBeing> it = livingBeings.iterator(); it.hasNext(); ) {

            LivingBeing being = it.next();
            being.act(newBeings);

            if(! being.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born living beings to the main list.
        livingBeings.addAll(newBeings);

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        resetSimulation();
    }

    private void resetSimulation()
    {
        step = 0;
        setNight(false);
        livingBeings.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with living beings.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        
        // Goes through each cell and populates based on configured probability.
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                populate(location, rand);
            }
        }
    }

    private void populate(Location location, Random rand)
    {
        for(SpeciesConfig species : SPECIES) {
            if(rand.nextDouble() <= species.creationProbability) {
                livingBeings.add(species.create(field, location));
                return;
            }
        }
        // Else leave the location empty.
    }

    private void configureViewColors()
    {
        for(SpeciesConfig species : SPECIES) {
            view.setColor(species.type, species.color);
        }
    }

    private void setNight(boolean night)
    {
        this.night = night;
        LivingBeing.setNightTime(night);
        view.setNight(night);
    }
}
