import java.util.*;
import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants
 *
 * @version 01.03.22
 */
public class Simulator
{
    private interface OrganismFactory<T extends Organism>
    {
        T create(Location location);
    }

    private static class PopulationRule<T extends Organism>
    {
        private final double creationProbability;
        private final Class<? extends T> organismClass;
        private final Color displayColor;
        private final OrganismFactory<T> factory;
        private final List<? super T> population;

        PopulationRule(double creationProbability, Class<? extends T> organismClass,
                Color displayColor, OrganismFactory<T> factory, List<? super T> population) {
            this.creationProbability = creationProbability;
            this.organismClass = organismClass;
            this.displayColor = displayColor;
            this.factory = factory;
            this.population = population;
        }

        public Class<? extends T> getOrganismClass() {
            return organismClass;
        }

        public Color getDisplayColor() {
            return displayColor;
        }

        public boolean tryPopulate(Random random, Location location) {
            if(random.nextDouble() <= creationProbability) {
                population.add(factory.create(location));
                return true;
            }
            return false;
        }
    }

    private enum Weather
    {
        NONE("none"),
        RAIN("rain"),
        FLOOD("flood"),
        DROUGHT("drought"),
        FOG("fog");

        private final String displayName;

        Weather(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a dingo will be created in any given grid position.
    private static final double DINGO_CREATION_PROBABILITY = 0.09;
    // The probability that an ant will be created in any given grid position.
    private static final double ANT_CREATION_PROBABILITY = 0.13;
    // The probability that a rat will be created in any given grid position.
    private static final double RAT_CREATION_PROBABILITY = 0.11;
    // The probability that an eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.12;
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.12;
    // The probability that an emu will be created in any given grid position.
    private static final double EMU_CREATION_PROBABILITY = 0.08;
    // The probability that acacia will be created in any given grid position.
    private static final double ACACIA_CREATION_PROBABILITY = 0.34;
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.36;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // Population rules for all species, in population priority order.
    private List<PopulationRule<? extends Organism>> populationRules;
    //the current weather of the simulation
    private Weather weather;
    // The current state of the field.
    private Field  field;
    // The current step of the simulation.
    private int step;
    // The current daytime of the simulation
    private int time;
    // A graphical view of the simulation.
    private SimulatorView view;
    
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
    public Simulator(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        plants = new ArrayList<>();

        weather = Weather.NONE;

        field = new Field(depth, width);
        populationRules = createPopulationRules();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        configureViewColors();

        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000 );
    }

    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runLongSimulation();
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for(int currentStep = 1; currentStep <= numSteps && view.isViable(field); currentStep++) {
            simulateOneStep();
            if (time == 24) {
                time = 0;
            }
            if (currentStep % 5 == 0) {
                time++;
            }
            if (currentStep % 50 == 0) {
                simulateWeather();
            }
            if (currentStep % 100 == 0) {
                resetDisease();
                simulateDisease();
            }

            delay(20);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal and plant
     * Apply the effects of weather on the simulation
     */
    public void simulateOneStep() {
        step++;

        applyWeatherEffects();

        updatePopulation(animals, new ArrayList<>(), (animal, newborns) -> animal.act(newborns, time));
        updatePopulation(plants, new ArrayList<>(), Plant::act);

        showStatus();
    }



    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        time = 0;
        animals.clear();
        plants.clear();
        weather = Weather.NONE;
        populate();

        showStatus();
    }
    
    /**
     * Randomly populate the field with animals and plants
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                populateLocation(rand, row, col);
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    /**
    * assign a weather condition to the current step of the simulation
     */
    private void simulateWeather() {
        Random rand = Randomizer.getRandom();
        switch (rand.nextInt(5)) {
            case 0:
                weather = Weather.RAIN;
                break;
            case 1:
                weather = Weather.FLOOD;
                break;
            case 2:
                weather = Weather.DROUGHT;
                break;
            case 3:
                weather = Weather.FOG;
                break;
            default:
                weather = Weather.NONE;
                break;
        }
    }

    /**
     * reset the weather conditions
     * by setting fog field of all animals to false
     * and setting rain field of all plants to false
     */
    private void resetWeatherEffects() {
        applyToAnimals(Animal::resetFog);
        applyToPlants(Plant::resetRain);
    }

    /**
     * simulate disease by calling giveDisease on every animal
     * in the simulation
     */
    public void simulateDisease() {
        applyToAnimals(Animal::giveDisease);
    }

    /**
     * reset the disease for all the animals in the simulation
     */
    private void resetDisease() {
        applyToAnimals(Animal::resetDisease);
    }

    private void applyWeatherEffects() {
        resetWeatherEffects();

        switch (weather) {
            case RAIN:
                applyRain();
                break;
            case FLOOD:
                applyFlood();
                break;
            case DROUGHT:
                applyDrought();
                break;
            case FOG:
                applyFog();
                break;
            case NONE:
                break;
        }
    }

    private void applyRain() {
        applyToPlants(Plant::setRain);
    }

    private void applyFlood() {
        eliminateRandomOrganisms(selectAnimals(animal -> animal instanceof Ant || animal instanceof Rat), 5);
    }

    private void applyDrought() {
        eliminateRandomOrganisms(new ArrayList<>(plants), 5);
    }

    private void applyFog() {
        applyToAnimals(Animal::setFog);
    }

    private void eliminateRandomOrganisms(List<? extends Organism> candidates, int divisor) {
        Collections.shuffle(candidates, Randomizer.getRandom());
        int organismsToRemove = candidates.size() / divisor;
        for (int i = 0; i < organismsToRemove; i++) {
            candidates.get(i).setDead();
        }
    }

    private void showStatus() {
        view.showStatus(step, field, time, weather.getDisplayName());
    }

    private void configureViewColors() {
        for (PopulationRule<? extends Organism> rule : populationRules) {
            view.setColor(rule.getOrganismClass(), rule.getDisplayColor());
        }
    }

    private List<PopulationRule<? extends Organism>> createPopulationRules() {
        List<PopulationRule<? extends Organism>> rules = new ArrayList<>();
        rules.add(new PopulationRule<>(DINGO_CREATION_PROBABILITY, Dingo.class, Color.ORANGE,
                location -> new Dingo(true, field, location), animals));
        rules.add(new PopulationRule<>(ANT_CREATION_PROBABILITY, Ant.class, Color.GRAY,
                location -> new Ant(true, field, location), animals));
        rules.add(new PopulationRule<>(SNAKE_CREATION_PROBABILITY, Snake.class, Color.BLACK,
                location -> new Snake(true, field, location), animals));
        rules.add(new PopulationRule<>(RAT_CREATION_PROBABILITY, Rat.class, Color.PINK,
                location -> new Rat(true, field, location), animals));
        rules.add(new PopulationRule<>(EAGLE_CREATION_PROBABILITY, Eagle.class, Color.RED,
                location -> new Eagle(true, field, location), animals));
        rules.add(new PopulationRule<>(EMU_CREATION_PROBABILITY, Emu.class, Color.YELLOW,
                location -> new Emu(true, field, location), animals));
        rules.add(new PopulationRule<>(ACACIA_CREATION_PROBABILITY, Acacia.class, Color.GREEN,
                location -> new Acacia(field, location), plants));
        rules.add(new PopulationRule<>(GRASS_CREATION_PROBABILITY, Grass.class, Color.CYAN,
                location -> new Grass(field, location), plants));
        return rules;
    }

    private void populateLocation(Random random, int row, int col) {
        Location location = new Location(row, col);
        for (PopulationRule<? extends Organism> rule : populationRules) {
            if (rule.tryPopulate(random, location)) {
                return;
            }
        }
    }

    private void applyToAnimals(Consumer<Animal> action) {
        for (Animal animal : animals) {
            action.accept(animal);
        }
    }

    private void applyToPlants(Consumer<Plant> action) {
        for (Plant plant : plants) {
            action.accept(plant);
        }
    }

    private List<Animal> selectAnimals(Predicate<Animal> predicate) {
        List<Animal> selected = new ArrayList<>();
        for (Animal animal : animals) {
            if (predicate.test(animal)) {
                selected.add(animal);
            }
        }
        return selected;
    }

    private <T extends Organism> void updatePopulation(List<T> population, List<T> newborns,
            BiConsumer<T, List<T>> action) {
        for (Iterator<T> it = population.iterator(); it.hasNext(); ) {
            T organism = it.next();
            action.accept(organism, newborns);
            if(!organism.isAlive()) {
                it.remove();
            }
        }
        population.addAll(newborns);
    }
}
