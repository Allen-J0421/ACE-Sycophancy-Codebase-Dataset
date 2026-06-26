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
    /**
     * Factory for creating one occupant at a field location.
     */
    private interface OrganismFactory
    {
        Object create(Field field, Location location);
    }

    /**
     * Population and display settings for one species.
     */
    private static class SpeciesConfig
    {
        private Class<?> speciesClass;
        private Color color;
        private double creationProbability;
        private OrganismFactory factory;

        public SpeciesConfig(Class<?> speciesClass, Color color,
                             double creationProbability, OrganismFactory factory) {
            this.speciesClass = speciesClass;
            this.color = color;
            this.creationProbability = creationProbability;
            this.factory = factory;
        }

        public Class<?> getSpeciesClass() {
            return speciesClass;
        }

        public Color getColor() {
            return color;
        }

        public boolean shouldCreate(Random rand) {
            return rand.nextDouble() <= creationProbability;
        }

        public Object create(Field field, Location location) {
            return factory.create(field, location);
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
    //the current weather of the simulation
    private String weather;
    // The current state of the field.
    private Field  field;
    // The current step of the simulation.
    private int step;
    // The current daytime of the simulation
    private int time;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Species that can be created when the simulation is populated.
    private List<SpeciesConfig> speciesConfigs;
    
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

        weather = "none";

        field = new Field(depth, width);
        speciesConfigs = createSpeciesConfigs();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        setSpeciesColors();

        
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
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            if (time == 24) {
                time = 0;
            }
            if (step % 5 == 0) {
                time++;
            }
            if (step % 50 == 0) {
                simulateWeather();
            }
            if (step % 100 == 0) {
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

        if(weather.equals("rain")){
            resetWeather();
            for (int i = 0; i < plants.size(); i++) {
                plants.get(i).setRain();
            }
        }
        else if(weather.equals("flood")){
            resetWeather();
            List<Animal> randomRatsAnts = new ArrayList<>();
            for (int i = 0; i < animals.size(); i++){
                if ((animals.get(i) instanceof Ant || (animals.get(i) instanceof Rat))) {
                    randomRatsAnts.add(animals.get(i));
                }
            }
            Collections.shuffle(randomRatsAnts);
            for (int i = 0; i < ((randomRatsAnts.size())/5); i++){
                animals.get(i).setDead();
            }
        }
        else if (weather.equals("drought")){
            resetWeather();
            List<Plant> randomPlants = new ArrayList<>();
            for (int i = 0; i < plants.size(); i++){
                randomPlants.add(plants.get(i));
            }
            Collections.shuffle(randomPlants);
            for (int i = 0; i < ((plants.size())/5); i++){
                plants.remove(plants.get(i));
            }
        }
        else if (weather.equals("fog")) {
            resetWeather();
            for (int i = 0; i < animals.size(); i++) {
                animals.get(i).setFog();
            }
        }

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
                populateLocation(rand, new Location(row, col));
            }
        }
    }

    /**
     * Try to create one registered species at the given location.
     */
    private void populateLocation(Random rand, Location location) {
        for(SpeciesConfig speciesConfig : speciesConfigs) {
            if(speciesConfig.shouldCreate(rand)) {
                addOrganism(speciesConfig.create(field, location));
                return;
            }
        }
        // else leave the location empty.
    }

    /**
     * Add an organism to the list that owns its lifecycle.
     */
    private void addOrganism(Object organism) {
        if(organism instanceof Animal) {
            animals.add((Animal) organism);
        }
        else if(organism instanceof Plant) {
            plants.add((Plant) organism);
        }
    }

    /**
     * Define every species that can appear in the simulation.
     */
    private List<SpeciesConfig> createSpeciesConfigs() {
        List<SpeciesConfig> configs = new ArrayList<>();
        configs.add(new SpeciesConfig(Dingo.class, Color.ORANGE, DINGO_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Dingo(true, field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Ant.class, Color.GRAY, ANT_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Ant(true, field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Snake.class, Color.BLACK, SNAKE_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Snake(true, field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Rat.class, Color.PINK, RAT_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Rat(true, field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Eagle.class, Color.RED, EAGLE_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Eagle(true, field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Emu.class, Color.YELLOW, EMU_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Emu(true, field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Acacia.class, Color.GREEN, ACACIA_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Acacia(field, location);
                    }
                }));
        configs.add(new SpeciesConfig(Grass.class, Color.CYAN, GRASS_CREATION_PROBABILITY,
                new OrganismFactory() {
                    public Object create(Field field, Location location) {
                        return new Grass(field, location);
                    }
                }));
        return configs;
    }

    /**
     * Register display colors for every configured species.
     */
    private void setSpeciesColors() {
        for(SpeciesConfig speciesConfig : speciesConfigs) {
            view.setColor(speciesConfig.getSpeciesClass(), speciesConfig.getColor());
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
        if (rand.nextInt(5) == 0){
            weather = "rain";
        }
        else if (rand.nextInt(5) == 1){
            weather = "flood";
        }
        else if (rand.nextInt(5) == 2){
            weather = "drought";
        }
        else if (rand.nextInt(5) == 3){
            weather = "fog";
        }
        else if (rand.nextInt(5) == 4){
            weather = "none";
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
