import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2022.02.xx 
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid. 
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid. 
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.01;
    // The probability that a rabbit will be created in any given grid position.
    private static final double DEER_CREATION_PROBABILITY = 0.02;    
    // The probability that a owl will be created in any given grid position.
    private static final double OWL_CREATION_PROBABILITY = 0.05;
    // The probability that a cat will be created in any given grid position.
    private static final double CAT_CREATION_PROBABILITY = 0.05;
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.04;    
    // The probability that a grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.4;    

    // List of animals in the field.
    private List<Animal> animals;
    // List of animals in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current weather of the simulation.
    private Weather weather;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Species definitions used for population and color registration.
    private List<SpeciesDefinition> speciesDefinitions;
    
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
        
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        speciesDefinitions = createSpeciesDefinitions();
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
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        updateWeather();
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, step, weather);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(step, weather);
            if(! plant.isAlive()) {
                it.remove();
            }
        }
        
        view.showStatus(step, field, weather);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();
        updateWeather();
        
        // Show the starting state in the view.
        view.showStatus(step, field, weather);
    }
    
    /**
     * Randomly populate the field with Lions, Cats, Owls, Deers and mouses.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for(SpeciesDefinition species : speciesDefinitions) {
                    if(species.tryPopulate(rand, location)) {
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

    /**
     * 
     * 
     */
    private void updateWeather()
    {
        weather = Weather.random(Randomizer.getRandom());
    }

    /**
     * Configure display colors for each species.
     */
    private void configureViewColors()
    {
        for(SpeciesDefinition species : speciesDefinitions) {
            species.configureView(view);
        }
    }

    /**
     * Create the species definitions used by the simulation.
     */
    private List<SpeciesDefinition> createSpeciesDefinitions()
    {
        return Arrays.asList(
            animalSpecies(LION_CREATION_PROBABILITY, Lion.class, Color.RED,
                          location -> new Lion(true, field, location)),
            animalSpecies(DEER_CREATION_PROBABILITY, Deer.class, Color.BLUE,
                          location -> new Deer(true, field, location)),
            animalSpecies(OWL_CREATION_PROBABILITY, Owl.class, Color.ORANGE,
                          location -> new Owl(true, field, location)),
            animalSpecies(MOUSE_CREATION_PROBABILITY, Mouse.class, Color.YELLOW,
                          location -> new Mouse(true, field, location)),
            animalSpecies(CAT_CREATION_PROBABILITY, Cat.class, Color.PINK,
                          location -> new Cat(true, field, location)),
            plantSpecies(GRASS_CREATION_PROBABILITY, Grass.class, Color.GREEN,
                         location -> new Grass(true, field, location))
        );
    }

    /**
     * Create an animal species definition.
     */
    private <T extends Animal> SpeciesDefinition animalSpecies(double probability, Class<T> type,
                                                               Color color,
                                                               Function<Location, T> factory)
    {
        return new SpeciesDefinition(probability, type, color, factory,
                                     organism -> animals.add(type.cast(organism)));
    }

    /**
     * Create a plant species definition.
     */
    private <T extends Plant> SpeciesDefinition plantSpecies(double probability, Class<T> type,
                                                             Color color,
                                                             Function<Location, T> factory)
    {
        return new SpeciesDefinition(probability, type, color, factory,
                                     organism -> plants.add(type.cast(organism)));
    }

    /**
     * The population and presentation settings for one species.
     */
    private final class SpeciesDefinition
    {
        private final double creationProbability;
        private final Class<? extends Organism> organismClass;
        private final Color color;
        private final Function<Location, ? extends Organism> factory;
        private final Consumer<Organism> registrar;

        private SpeciesDefinition(double creationProbability, Class<? extends Organism> organismClass,
                                  Color color, Function<Location, ? extends Organism> factory,
                                  Consumer<Organism> registrar)
        {
            this.creationProbability = creationProbability;
            this.organismClass = organismClass;
            this.color = color;
            this.factory = factory;
            this.registrar = registrar;
        }

        /**
         * Register the species color with the view.
         */
        private void configureView(SimulatorView simulatorView)
        {
            simulatorView.setColor(organismClass, color);
        }

        /**
         * Try to create a new organism at the given location.
         */
        private boolean tryPopulate(Random rand, Location location)
        {
            if(rand.nextDouble() <= creationProbability) {
                registrar.accept(factory.apply(location));
                return true;
            }
            return false;
        }
    }
}
