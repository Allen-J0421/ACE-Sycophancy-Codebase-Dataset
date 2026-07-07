import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;
import java.awt.Color;

/**
 * A simple predator-prey simulator of a safari, based on a rectangular field
 * containing lions, cheetahs, jaguars, zebras, gazelles, hunters and grass.
 *
 * @version 2022.03.01
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 175;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 150;

    /**
     * Per-species creation probability, keyed by actor class.
     * Insertion order must match the original if-else chain so that the
     * rand.nextDouble() call sequence per cell is preserved exactly.
     */
    private static final Map<Class<? extends Actor>, Double> CREATION_PROBABILITIES;

    /**
     * Per-species factory functions that produce a new actor given a field and location.
     * Keys and insertion order must match CREATION_PROBABILITIES.
     */
    private static final Map<Class<? extends Actor>, BiFunction<Field, Location, Actor>> ACTOR_FACTORIES;

    static {
        CREATION_PROBABILITIES = new LinkedHashMap<>();
        CREATION_PROBABILITIES.put(Lion.class,    0.017);
        CREATION_PROBABILITIES.put(Cheetah.class, 0.032);
        CREATION_PROBABILITIES.put(Gazelle.class, 0.2);
        CREATION_PROBABILITIES.put(Jaguar.class,  0.011);
        CREATION_PROBABILITIES.put(Grass.class,   0.55);
        CREATION_PROBABILITIES.put(Zebra.class,   0.4998);
        CREATION_PROBABILITIES.put(Hunter.class,  0.01);

        ACTOR_FACTORIES = new LinkedHashMap<>();
        ACTOR_FACTORIES.put(Lion.class,    (f, loc) -> new Lion(true, f, loc));
        ACTOR_FACTORIES.put(Cheetah.class, (f, loc) -> new Cheetah(true, f, loc));
        ACTOR_FACTORIES.put(Gazelle.class, (f, loc) -> new Gazelle(true, f, loc));
        ACTOR_FACTORIES.put(Jaguar.class,  (f, loc) -> new Jaguar(true, f, loc));
        ACTOR_FACTORIES.put(Grass.class,   (f, loc) -> new Grass(true, f, loc));
        ACTOR_FACTORIES.put(Zebra.class,   (f, loc) -> new Zebra(true, f, loc));
        ACTOR_FACTORIES.put(Hunter.class,  (f, loc) -> new Hunter(f, loc));
    }

    // List of animals and plants which can not be stood on in the field.
    private List<Actor> animals;
    // List of plants that can be stood on in the field.
    private List<Actor> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;

    private boolean isDay;
    //check the random weather method and the use of the random weather method in one step method
    private Weather weather;
    private int numberOfDays;
    private static final Random rand = Randomizer.getRandom();
    private int noOfGrass; // keeps count of the no.of grass species in the field.
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
        view = new SimulatorView(depth, width, this);
        view.setColor(Gazelle.class, Color.ORANGE);
        view.setColor(Jaguar.class, Color.BLUE);
        view.setColor(Cheetah.class, Color.RED);
        view.setColor(Lion.class, Color.PINK);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Zebra.class, Color.BLACK);
        view.setColor(Hunter.class, Color.MAGENTA);
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
            //delay(120);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * actor in the field. One  day lasts 4 steps, with day and night alternating every 2 steps.
     * Buttons are disabled before the simulation starts and enabled after the simulation is finsihed.
     */
    public void simulateOneStep()
    {
        view.disableButton();
        advanceTimeAndWeather();
        updateAnimals();
        updatePlants();
        finaliseStep();
        view.enableButton();
    }

    /**
     * Advance the step counter, toggle day/night every two steps, and roll a
     * new weather value (re-rolling at night until a non-sunny result is drawn).
     */
    private void advanceTimeAndWeather()
    {
        step++;
        if(step % 2 == 0){
            isDay = !isDay;
            if(isDay){
                numberOfDays++;
            }
        }
        getRandomWeather();
        while(!isDay && weather == weather.SUNNY){
            getRandomWeather();
        }
    }

    /**
     * Let every animal and hunter act, collect offspring, and remove the dead.
     */
    private void updateAnimals()
    {
        List<Actor> newAnimals = new ArrayList<>();
        for(Iterator<Actor> it = animals.iterator(); it.hasNext(); ) {
            Actor animal = it.next();
            animal.act(newAnimals, this);
            if(!animal.isActive()) {
                it.remove();
            }
        }
        animals.addAll(newAnimals);
    }

    /**
     * Let every plant act, collect offspring, remove the dead, and recount grass.
     */
    private void updatePlants()
    {
        List<Actor> newGrass = new ArrayList<>();
        noOfGrass = 0;
        for(Iterator<Actor> it = plants.iterator(); it.hasNext(); ) {
            Actor plant = it.next();
            plant.act(newGrass, this);
            if(!plant.isActive()) {
                it.remove();
            } else if(plant instanceof Plants) {
                noOfGrass++;
            }
        }
        plants.addAll(newGrass);
    }

    /**
     * Trigger periodic infection events and refresh the view.
     */
    private void finaliseStep()
    {
        if(step % 500 == 0){
            introduceInfection();
        }
        view.showStatus(field);
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
        isDay = true;
        numberOfDays = 0;
        getRandomWeather();
        // Show the starting state in the view.
        view.showStatus(field);
    }

    /**
     * Randomly populate the field with all the actors.
     * For each cell, species are tried in CREATION_PROBABILITIES insertion order.
     * Each species draws one rand.nextDouble() independently; the first match wins.
     * Adding a new species requires only adding entries to the two static maps.
     */
    private void populate()
    {
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                for (Map.Entry<Class<? extends Actor>, Double> entry : CREATION_PROBABILITIES.entrySet()) {
                    if (rand.nextDouble() <= entry.getValue()) {
                        Location location = new Location(row, col);
                        Actor actor = ACTOR_FACTORIES.get(entry.getKey()).apply(field, location);
                        if (actor instanceof Plants) {
                            plants.add(actor);
                        } else {
                            animals.add(actor);
                        }
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
     * Return the total number of steps the simulation has run for.
     * @return The total steps the simualtion ran for.
     */
    public int getSteps(){
        return step;
    }

    /**
     * Randomly generates a weather from the enum class.
     * @return A random weather.
     */
    private void getRandomWeather(){
        int value = rand.nextInt(weather.values().length);
        weather = weather.values()[value];
    }

    /**
     * Returns the List which contains all the plants in the simualtion.
     * @return A list of all the alive plants in the simulation.
     */
    public List<Actor> getGrass(){
        return plants;
    }

    /**
     * Chooses a random animal to infect. This method is automatically called after 100 steps,
     * or it can be called by pressing the introduce infection button.
     */
    public void introduceInfection(){
        if(animals.size() != 0){
            int value = rand.nextInt(animals.size());
            if(animals.get(value) instanceof Animal){
                Animal animal = (Animal)animals.get(value);
                animal.setUnhealthy();
            }
        }
    }

    /**
     * Returns the number of grass in the plants arrayList.
     * @return number of grass in arraylist.
     */
    public int getNoOfGrass(){
        return noOfGrass;
    }

    /**
     * Returns the number of days.
     * @return  number of days.
     */
    public int  getNoOfDays(){
        return numberOfDays;
    }

    /**
     * Returns if it is currently day or night.
     * @return true if it is day.
     */
    public boolean  isDay(){
        return isDay;
    }

    /**
     * Return the weather in the simulation.
     * @return weather The current weather in the simulation.
     */
    public Weather getWeather(){
        return weather;
    }
}
