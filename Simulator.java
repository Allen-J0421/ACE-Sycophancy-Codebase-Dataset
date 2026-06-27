import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The number of steps in a long simulation run.
    private static final int LONG_SIMULATION_STEPS = 4000;
    // The probability that a hippopotamus will be created in any given grid position.
    private static final double HIPPOPOTAMUS_CREATION_PROBABILITY = 0.01;
    // The probability that a leopard will be created in any given grid position.
    private static final double LEOPARD_CREATION_PROBABILITY = 0.03;
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.03;
    // The probability that a monkey will be created in any given grid position.
    private static final double MONKEY_CREATION_PROBABILITY = 0.08;
    // The probability that a sloth will be created in any given grid position.
    private static final double SLOTH_CREATION_PROBABILITY = 0.08;
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.13;
    // Number of steps between automatic weather changes.
    private static final int WEATHER_CHANGE_INTERVAL = 450;
    // Display colors for each organism type.
    private static final SpeciesColor[] SPECIES_COLORS = {
        new SpeciesColor(Hippopotamus.class, Color.DARK_GRAY),
        new SpeciesColor(Leopard.class, Color.MAGENTA),
        new SpeciesColor(Bear.class, Color.RED),
        new SpeciesColor(Monkey.class, Color.ORANGE),
        new SpeciesColor(Sloth.class, Color.YELLOW),
        new SpeciesColor(Plant.class, Color.GREEN)
    };
    // Initial population rules for animals that compete for the same grid location.
    private static final PopulationRule[] PRIMARY_POPULATION_RULES = {
        new PopulationRule(
            HIPPOPOTAMUS_CREATION_PROBABILITY,
            (field, location) -> new Hippopotamus(true, field, location)),
        new PopulationRule(
            LEOPARD_CREATION_PROBABILITY,
            (field, location) -> new Leopard(true, field, location)),
        new PopulationRule(
            BEAR_CREATION_PROBABILITY,
            (field, location) -> new Bear(true, field, location)),
        new PopulationRule(
            MONKEY_CREATION_PROBABILITY,
            (field, location) -> new Monkey(true, field, location))
    };
    // Initial population rules for organisms applied after the primary animal rules.
    private static final PopulationRule[] SECONDARY_POPULATION_RULES = {
        new PopulationRule(
            SLOTH_CREATION_PROBABILITY,
            (field, location) -> new Sloth(true, field, location)),
        new PopulationRule(
            PLANT_CREATION_PROBABILITY,
            (field, location) -> new Plant(true, field, location))
    };
    // A shared Weather object between organisms and the simulator
    public static final Weather weather = Weather.getWeather();


    // List of animals in the field.
    private final List<Organism> organisms;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

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

        organisms = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, step);
        for (SpeciesColor speciesColor : SPECIES_COLORS) {
            view.setColor(speciesColor.getOrganismClass(), speciesColor.getColor());
        }

        // Setup a valid starting point.
        reset();
    }

    // Accessor and mutator methods

    /**
     * Return the current step number of the simulation
     *
     * @return int of the current step in the simulation
     */
    public int getStepNumber()
    {
        return step;
    }

    // Functional methods

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
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
        for(int currentStep = 1; currentStep <= numSteps && view.isViable(field); currentStep++) {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the organism list and update each active organism.
     */
    public void simulateOneStep()
    {
        step++;
        randomWeather();
        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let all active organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();

            if (DayCycle.isActive(organism, step)) {
                organism.act(newOrganisms);
            }
            if(! organism.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);
        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        weather.resetWeather();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                applyPopulationRules(rand, row, col, PRIMARY_POPULATION_RULES);
                applyPopulationRules(rand, row, col, SECONDARY_POPULATION_RULES);
            }
        }
    }

    /**
     * Apply the first matching population rule for one grid location.
     */
    private void applyPopulationRules(Random rand, int row, int col, PopulationRule[] rules)
    {
        for (PopulationRule rule : rules) {
            if (rand.nextDouble() <= rule.getProbability()) {
                Location location = new Location(row, col);
                organisms.add(rule.createOrganism(field, location));
                return;
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

    /**
     * Randomises the weather
     */
    public void randomWeather() {
        if (step % WEATHER_CHANGE_INTERVAL == 0) {
            weather.changeWeather();
        }
    }

    private interface OrganismFactory
    {
        Organism create(Field field, Location location);
    }

    private static final class PopulationRule
    {
        private final double probability;
        private final OrganismFactory organismFactory;

        public PopulationRule(double probability, OrganismFactory organismFactory)
        {
            this.probability = probability;
            this.organismFactory = organismFactory;
        }

        public double getProbability()
        {
            return probability;
        }

        public Organism createOrganism(Field field, Location location)
        {
            return organismFactory.create(field, location);
        }
    }

    private static final class SpeciesColor
    {
        private final Class<?> organismClass;
        private final Color color;

        public SpeciesColor(Class<?> organismClass, Color color)
        {
            this.organismClass = organismClass;
            this.color = color;
        }

        public Class<?> getOrganismClass()
        {
            return organismClass;
        }

        public Color getColor()
        {
            return color;
        }
    }
}
