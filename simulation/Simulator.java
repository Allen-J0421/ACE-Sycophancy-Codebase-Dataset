package simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import configuration.Configuration;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants
 *
 * @version 01.03.22
 */
public class Simulator
implements SimulationContext
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
    private final Configuration config;
    // Event bus used by entities to notify the simulator.
    private final SimulationEventBus eventBus;
    // Births captured during the current step or initial population.
    private List<BirthEvent> pendingBirths;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(Configuration.defaults());
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
    public Simulator(Configuration config) {
        this.config = config;
        this.eventBus = new SimulationEventBus();
        this.eventBus.addListener(new SimulationStateListener());
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        pendingBirths = new ArrayList<>();

        weather = Weather.NONE;

        field = new Field(config.simulation().getDepth(), config.simulation().getWidth());

        // Create a view of the state of each location in the field.
        view = new SimulatorView(config.simulation().getDepth(), config.simulation().getWidth());
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
    private static Configuration createConfig(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return Configuration.defaults();
        }
        return Configuration.defaults().withFieldSize(depth, width);
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
            if (time == config.simulation().getDayLengthHours()) {
                time = 0;
            }
            if (step % config.simulation().getTimeAdvanceIntervalSteps() == 0) {
                time++;
            }
            if (step % config.simulation().getWeatherIntervalSteps() == 0) {
                simulateWeather();
            }
            if (step % config.simulation().getDiseaseIntervalSteps() == 0) {
                resetDisease();
                simulateDisease();
            }

            delay(config.simulation().getStepDelayMillis());   // uncomment this to run more slowly
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
        pendingBirths = new ArrayList<>();

        applyWeatherEffects();

        // Let all ants act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(time);
            if(! animal.isAlive()) {
                it.remove();
            }
        }

        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act();
            if(! plant.isAlive()) {
                it.remove();
            }
        }

               
        // Add the newly born animals and plants to the main lists.
        flushPendingBirths();

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
        pendingBirths.clear();
        populate();
        flushPendingBirths();
        
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
                for (Configuration.PopulationRule rule : config.simulation().getPopulationRules()) {
                    if (rand.nextDouble() <= rule.getProbability()) {
                        spawnPopulation(rule.getKind(), row, col);
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

    /**
     * Create an entity for a populated location.
     * @param kind The configured population kind.
     * @param row The row to populate.
     * @param col The column to populate.
     */
    private void spawnPopulation(Configuration.PopulationKind kind, int row, int col) {
        Location location = new Location(row, col);
        LivingEntity entity;
        switch (kind) {
            case DINGO:
                entity = new Dingo(true, this, location);
                break;
            case ANT:
                entity = new Ant(true, this, location);
                break;
            case SNAKE:
                entity = new Snake(true, this, location);
                break;
            case RAT:
                entity = new Rat(true, this, location);
                break;
            case EAGLE:
                entity = new Eagle(true, this, location);
                break;
            case EMU:
                entity = new Emu(true, this, location);
                break;
            case ACACIA:
                entity = new Acacia(this, location);
                break;
            case GRASS:
                entity = new Grass(this, location);
                break;
            default:
                entity = null;
                break;
        }
        if (entity != null) {
            emit(new BirthEvent(this, entity, location));
        }
    }

    @Override
    public List<Location> adjacentLocations(Location location) {
        return field.adjacentLocations(location);
    }

    @Override
    public List<Location> getFreeAdjacentLocations(Location location) {
        return field.getFreeAdjacentLocations(location);
    }

    @Override
    public Location freeAdjacentLocation(Location location) {
        return field.freeAdjacentLocation(location);
    }

    @Override
    public Object getObjectAt(Location location) {
        return field.getObjectAt(location);
    }

    @Override
    public void emit(SimulationEvent event) {
        eventBus.publish(event);
    }

    /**
     * Place any births captured during the current cycle into the field.
     */
    private void flushPendingBirths() {
        for (BirthEvent birth : pendingBirths) {
            LivingEntity offspring = birth.getOffspring();
            field.place(offspring, birth.getLocation());
            if (offspring instanceof Animal) {
                animals.add((Animal) offspring);
            }
            else if (offspring instanceof Plant) {
                plants.add((Plant) offspring);
            }
        }
        pendingBirths.clear();
    }

    /**
     * Listener that applies simulation events to the field state.
     */
    private final class SimulationStateListener implements SimulationEventListener
    {
        @Override
        public void onSimulationEvent(SimulationEvent event) {
            switch (event.getType()) {
                case BIRTH:
                    pendingBirths.add((BirthEvent) event);
                    break;
                case DEATH:
                    handleDeath((DeathEvent) event);
                    break;
                case MOVED:
                    handleMovement((MovementEvent) event);
                    break;
                case FOOD_CONSUMED:
                default:
                    break;
            }
        }

        private void handleDeath(DeathEvent event) {
            Location location = event.getLocation();
            if (location != null) {
                field.clear(location);
            }
        }

        private void handleMovement(MovementEvent event) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (from != null) {
                field.clear(from);
            }
            if (to != null) {
                field.place(event.getEntity(), to);
            }
        }
    }

}
