package safari;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * A simple predator-prey simulator of a safari, based on a rectangular field
 * containing lions, cheetahs, jaguars, zebras, gazelles, hunters and grass.
 *
 * @version 2022.03.01
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    private static final Properties SIMULATOR_PROPERTIES = ConfigLoader.loadProperties("config/simulator.properties");
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = loadDefaultWidth();
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = loadDefaultDepth();

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
        step++;
        if(step % 2 == 0){
            isDay  = !isDay;
            if(isDay){
                numberOfDays++;
            }
        }

        // Provide space for newborn actors.
        List<Actor> newAnimals = new ArrayList<>();  
        List<Actor> newGrass = new ArrayList<>();  

        getRandomWeather();
        while(!isDay && weather == weather.SUNNY){
            getRandomWeather();
        }
        // Let all actors act.
        //iterates through all the animals and hunters

        for(Iterator<Actor> it = animals.iterator(); it.hasNext(); ) {
            Actor animal = it.next();
            animal.act(newAnimals,this);
            if(! animal.isActive()) {
                it.remove();
            }
        }
        animals.addAll(newAnimals);
        for(Iterator<Actor> it = plants.iterator(); it.hasNext(); ) {
            Actor plants = it.next();
            plants.act(newGrass,this);
            if(! plants.isActive()) {
                it.remove();
            }
        }
        plants.addAll(newGrass);

        if(step % 500 == 0){
            introduceInfection();
        }

        view.showStatus(field);
        view.enableButton();
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
     */
    private void populate()
    {
        //Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Actor actor = SpawnRegistry.seedActor(field, row, col, rand);
                if(actor instanceof Plants plant) {
                    plants.add(plant);
                }
                else if(actor != null) {
                    animals.add(actor);
                }
                // else leave the location empty.
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

    private static int loadDefaultWidth()
    {
        return ConfigLoader.requiredInt(SIMULATOR_PROPERTIES, "simulator.defaultWidth");
    }

    private static int loadDefaultDepth()
    {
        return ConfigLoader.requiredInt(SIMULATOR_PROPERTIES, "simulator.defaultDepth");
    }
}
