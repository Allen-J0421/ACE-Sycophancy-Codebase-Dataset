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
    // Configuration data for this simulation instance.
    private final SimulationConfig config;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    //the current weather of the simulation
    private WeatherEvent currentWeather;
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
    public Simulator() {
        this(SimulationConfig.DEFAULT);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        this(validatedConfig(depth, width));
    }

    private Simulator(SimulationConfig config) {
        this.config = config;
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        currentWeather = new NoWeather();
        field = new Field(config.depth, config.width);
        view = new SimulatorView(config.depth, config.width);
        view.setColor(Ant.class, Color.GRAY);
        view.setColor(Dingo.class, Color.ORANGE);
        view.setColor(Eagle.class, Color.RED);
        view.setColor(Snake.class, Color.BLACK);
        view.setColor(Rat.class, Color.PINK);
        view.setColor(Emu.class, Color.YELLOW);
        view.setColor(Acacia.class, Color.GREEN);
        view.setColor(Grass.class, Color.CYAN);
        reset();
    }

    private static SimulationConfig validatedConfig(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return SimulationConfig.DEFAULT;
        }
        return SimulationConfig.DEFAULT.withDimensions(depth, width);
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

        currentWeather.apply(animals, plants);

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

        view.showStatus(step, field, time, currentWeather.getName());
    }



    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        animals.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field, time, currentWeather.getName());
    }
    
    /**
     * Randomly populate the field with animals and plants
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= config.dingoProbability) {
                    Location location = new Location(row, col);
                    Dingo dingo = new Dingo(true, field, location);
                    animals.add(dingo);
                }
                else if(rand.nextDouble() <= config.antProbability) {
                    Location location = new Location(row, col);
                    Ant ant = new Ant(true, field, location);
                    animals.add(ant);
                }
                else if(rand.nextDouble() <= config.snakeProbability) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, field, location);
                    animals.add(snake);
                }
                else if(rand.nextDouble() <= config.ratProbability) {
                    Location location = new Location(row, col);
                    Rat rat = new Rat(true, field, location);
                    animals.add(rat);
                }
                else if(rand.nextDouble() <= config.eagleProbability) {
                    Location location = new Location(row, col);
                    Eagle eagle = new Eagle(true, field, location);
                    animals.add(eagle);
                }
                else if(rand.nextDouble() <= config.emuProbability) {
                    Location location = new Location(row, col);
                    Emu emu = new Emu(true, field, location);
                    animals.add(emu);
                }
                else if(rand.nextDouble() <= config.acaciaProbability) {
                    Location location = new Location(row, col);
                    Acacia acacia = new Acacia(field, location);
                    plants.add(acacia);
                }
                else if(rand.nextDouble() <= config.grassProbability) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(field, location);
                    plants.add(grass);
                }

                // else leave the location empty.
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
     * Randomly assign a weather condition for the current period.
     */
    private void simulateWeather() {
        Random rand = Randomizer.getRandom();
        if (rand.nextInt(5) == 0){
            currentWeather = new RainWeather();
        }
        else if (rand.nextInt(5) == 1){
            currentWeather = new FloodWeather();
        }
        else if (rand.nextInt(5) == 2){
            currentWeather = new DroughtWeather();
        }
        else if (rand.nextInt(5) == 3){
            currentWeather = new FogWeather();
        }
        else if (rand.nextInt(5) == 4){
            currentWeather = new NoWeather();
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
