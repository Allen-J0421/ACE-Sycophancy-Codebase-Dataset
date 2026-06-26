import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
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

    // The probability that each actor will be created in any given grid position.
    private static final double JAGUAR_CREATION_PROBABILITY = 0.011; 
    private static final double GAZELLE_CREATION_PROBABILITY = 0.2;
    private static final double LION_CREATION_PROBABILITY = 0.017 ; 
    private static final double PLANT_CREATION_PROBABILITY = 0.55; 
    private static final double CHEETAH_CREATION_PROBABILITY =0.032;
    private static final double ZEBRA_CREATION_PROBABILITY = 0.4998; 
    private static final double HUNTER_CREATION_PROBABILITY = 0.01; 

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
        setViewColors();
        // Setup a valid starting point.
        reset();
    }

    /**
     * Configure how each actor type is shown in the view.
     */
    private void setViewColors()
    {
        view.setColor(Gazelle.class, Color.ORANGE);
        view.setColor(Jaguar.class, Color.BLUE);
        view.setColor(Cheetah.class, Color.RED);
        view.setColor(Lion.class, Color.PINK);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Zebra.class, Color.BLACK);
        view.setColor(Hunter.class, Color.MAGENTA);
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
        advanceTime();

        // Provide space for newborn actors.
        List<Actor> newAnimals = new ArrayList<>();
        List<Actor> newGrass = new ArrayList<>();

        updateWeather();
        actOnAnimals(newAnimals);
        actOnPlants(newGrass);
        introduceScheduledInfection();

        view.showStatus(field);
        view.enableButton();
    }

    /**
     * Advance the simulation clock and switch between day and night.
     */
    private void advanceTime()
    {
        step++;
        if(step % 2 == 0){
            isDay  = !isDay;
            if(isDay){
                numberOfDays++;
            }
        }
    }

    /**
     * Select weather for this step.
     */
    private void updateWeather()
    {
        getRandomWeather();
        while(!isDay && weather == Weather.SUNNY){
            getRandomWeather();
        }
    }

    /**
     * Let all animals and hunters act.
     * @param newAnimals A list to receive new animals and hunters.
     */
    private void actOnAnimals(List<Actor> newAnimals)
    {
        for(Iterator<Actor> it = animals.iterator(); it.hasNext(); ) {
            Actor animal = it.next();
            animal.act(newAnimals,this);
            if(! animal.isActive()) {
                it.remove();
            }
        }
        animals.addAll(newAnimals);
    }

    /**
     * Let all plants act and update the grass count.
     * @param newGrass A list to receive new grass.
     */
    private void actOnPlants(List<Actor> newGrass)
    {
        noOfGrass = 0;
        for(Iterator<Actor> it = plants.iterator(); it.hasNext(); ) {
            Actor plant = it.next();
            plant.act(newGrass,this);
            if(! plant.isActive()) {
                it.remove();
            }else if(plant instanceof Plants){
                noOfGrass++;
            }
        }
        plants.addAll(newGrass);
    }

    /**
     * Introduce infection on scheduled steps.
     */
    private void introduceScheduledInfection()
    {
        if(step % 500 == 0){
            introduceInfection();
        }
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
                populateLocation(new Location(row, col));
            }
        }
    }

    /**
     * Randomly populate a single field location.
     * @param location The location to populate.
     */
    private void populateLocation(Location location)
    {
        if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
            animals.add(new Lion(true, field, location));
        } else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY) {
            animals.add(new Cheetah(true, field, location));
        } else if(rand.nextDouble() <= GAZELLE_CREATION_PROBABILITY) {
            animals.add(new Gazelle(true, field, location));
        } else if(rand.nextDouble() <= JAGUAR_CREATION_PROBABILITY) {
            animals.add(new Jaguar(true, field, location));
        } else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
            plants.add(new Grass(true, field, location));
        } else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
            animals.add(new Zebra(true, field, location));
        } else if(rand.nextDouble() <= HUNTER_CREATION_PROBABILITY) {
            animals.add(new Hunter(field, location));
        }
        // else leave the location empty.
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
