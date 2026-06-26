import java.util.*;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants
 *
 * @version 01.03.22
 */
public class Simulator
{
    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
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
    // Configuration for simulation timing, population, and field sizing.
    private final SimulationConfig config;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(SimulationConfig.defaultConfig());
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        this(createConfig(depth, width));
    }

    /**
     * Create a simulation from an explicit configuration.
     * @param config The simulation configuration to use.
     */
    public Simulator(SimulationConfig config) {
        this.config = config;
        animals = new ArrayList<>();
        plants = new ArrayList<>();

        weather = Weather.NONE;

        field = new Field(config.getDepth(), config.getWidth());

        // Create a view of the state of each location in the field.
        view = new SimulatorView(config.getDepth(), config.getWidth());
        view.setColor(Ant.class, Color.GRAY);
        view.setColor(Dingo.class, Color.ORANGE);
        view.setColor(Eagle.class, Color.RED);
        view.setColor(Snake.class, Color.BLACK);
        view.setColor(Rat.class, Color.PINK);
        view.setColor(Emu.class, Color.YELLOW);
        view.setColor(Acacia.class, Color.GREEN);
        view.setColor(Grass.class, Color.CYAN);

        
        // Setup a valid starting point.
        reset();
    }

    /**
     * Create a configuration for the supplied field size.
     * @param depth The requested depth.
     * @param width The requested width.
     * @return A config with validated field dimensions.
     */
    private static SimulationConfig createConfig(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return SimulationConfig.defaultConfig();
        }
        return SimulationConfig.defaultConfig().withFieldSize(depth, width);
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
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            if (time == config.getDayLengthHours()) {
                time = 0;
            }
            if (step % config.getTimeAdvanceIntervalSteps() == 0) {
                time++;
            }
            if (step % config.getWeatherIntervalSteps() == 0) {
                simulateWeather();
            }
            if (step % config.getDiseaseIntervalSteps() == 0) {
                resetDisease();
                simulateDisease();
            }

            delay(config.getStepDelayMillis());   // uncomment this to run more slowly
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

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all ants act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals,time);
            if(! animal.isAlive()) {
                it.remove();
            }
        }

        //provide space for new created plants
        List<Plant> newPlants = new ArrayList<>();
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(newPlants);
            if(! plant.isAlive()) {
                it.remove();
            }
        }

               
        // Add the newly born animals and plants to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);

        view.showStatus(step, field, time, weather);
    }



    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        weather = Weather.NONE;
        animals.clear();
        plants.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field, time, weather);
    }
    
    /**
     * Randomly populate the field with animals and plants
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                for (SimulationConfig.PopulationRule rule : config.getPopulationRules()) {
                    if (rule.tryPopulate(rand, field, row, col, animals, plants)) {
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
        weather = Weather.randomWeather(Randomizer.getRandom());
    }

    /**
     * Apply the current weather condition to the simulation.
     * Weather effects last for a single step, so the existing flags
     * are cleared before the active weather is applied.
     */
    private void applyWeatherEffects() {
        if (weather == Weather.NONE) {
            return;
        }

        resetWeather();

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
            default:
                break;
        }
    }

    /**
     * Make all plants temporarily rain-affected.
     */
    private void applyRain() {
        for (int i = 0; i < plants.size(); i++) {
            plants.get(i).setRain();
        }
    }

    /**
     * Kill a random subset of ants and rats.
     */
    private void applyFlood() {
        List<Animal> vulnerableAnimals = new ArrayList<>();
        for (int i = 0; i < animals.size(); i++) {
            Animal animal = animals.get(i);
            if (animal instanceof Ant || animal instanceof Rat) {
                vulnerableAnimals.add(animal);
            }
        }
        Collections.shuffle(vulnerableAnimals);
        for (int i = 0; i < vulnerableAnimals.size() / 5; i++) {
            vulnerableAnimals.get(i).setDead();
        }
    }

    /**
     * Kill a random subset of plants.
     */
    private void applyDrought() {
        List<Plant> vulnerablePlants = new ArrayList<>(plants);
        Collections.shuffle(vulnerablePlants);
        for (int i = 0; i < vulnerablePlants.size() / 5; i++) {
            vulnerablePlants.get(i).setDead();
        }
    }

    /**
     * Mark all animals as fog-affected.
     */
    private void applyFog() {
        for (int i = 0; i < animals.size(); i++) {
            animals.get(i).setFog();
        }
    }

    /**
     * reset the weather conditions
     * by setting fog field of all animals to false
     * and setting rain field of all plants to false
     */
    private void resetWeather() {
        for (int i = 0; i < animals.size(); i++) {
            animals.get(i).resetFog();
        }
        for (int i = 0; i < plants.size(); i++) {
            plants.get(i).resetRain();
        }
    }

    /**
     * simulate disease by calling giveDisease on every animal
     * in the simulation
     */
    public void simulateDisease() {
        for (int i = 0; i < animals.size(); i++){
            animals.get(i).giveDisease();
        }
    }

    /**
     * reset the disease for all the animals in the simulation
     */
    private void resetDisease() {
        for (int i = 0; i < animals.size(); i++) {
            animals.get(i).resetDisease();
        }
    }

}
