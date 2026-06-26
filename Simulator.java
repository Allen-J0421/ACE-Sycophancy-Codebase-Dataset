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
    @FunctionalInterface
    private interface ActorFactory
    {
        Actor create(Location location);
    }

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
        configureViewColors();
        // Setup a valid starting point.
        reset();
    }

    /**
     * Configure the display color used for each actor type.
     */
    private void configureViewColors()
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
        // Let all actors act.
        // iterates through all the animals and hunters
        updateActors(animals, newAnimals, false);
        noOfGrass = updateActors(plants, newGrass, true);

        if(step % 500 == 0){
            introduceInfection();
        }

        view.showStatus(field);
        view.enableButton();
    }

    /**
     * Advance the simulation clock and update whether it is day or night.
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
     * Select the next weather state, avoiding sunny nights.
     */
    private void updateWeather()
    {
        getRandomWeather();
        while(!isDay && weather == Weather.SUNNY){
            getRandomWeather();
        }
    }

    /**
     * Update a group of actors for a single step.
     * @param currentActors The current actors to iterate over.
     * @param newActors The list receiving newly created actors.
     * @param countPlants True when active plants should be counted.
     * @return The number of active plants seen during the update.
     */
    private int updateActors(List<Actor> currentActors, List<Actor> newActors, boolean countPlants)
    {
        int activePlantCount = 0;
        for(Iterator<Actor> it = currentActors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, this);
            if(!actor.isActive()) {
                it.remove();
            }
            else if(countPlants && actor instanceof Plants) {
                activePlantCount++;
            }
        }
        currentActors.addAll(newActors);
        return activePlantCount;
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
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                populateLocation(new Location(row, col));
            }
        }
    }

    /**
     * Randomly populate a single field location.
     * The creation checks are evaluated in the existing priority order.
     * @param location The location to populate.
     */
    private void populateLocation(Location location)
    {
        if(tryPopulate(animals, location, LION_CREATION_PROBABILITY,
            loc -> new Lion(true, field, loc))) {
            return;
        }
        if(tryPopulate(animals, location, CHEETAH_CREATION_PROBABILITY,
            loc -> new Cheetah(true, field, loc))) {
            return;
        }
        if(tryPopulate(animals, location, GAZELLE_CREATION_PROBABILITY,
            loc -> new Gazelle(true, field, loc))) {
            return;
        }
        if(tryPopulate(animals, location, JAGUAR_CREATION_PROBABILITY,
            loc -> new Jaguar(true, field, loc))) {
            return;
        }
        if(tryPopulate(plants, location, PLANT_CREATION_PROBABILITY,
            loc -> new Grass(true, field, loc))) {
            return;
        }
        if(tryPopulate(animals, location, ZEBRA_CREATION_PROBABILITY,
            loc -> new Zebra(true, field, loc))) {
            return;
        }
        tryPopulate(animals, location, HUNTER_CREATION_PROBABILITY,
            loc -> new Hunter(field, loc));
    }

    /**
     * Try to add a randomly created actor to the target population.
     * @param population The destination population list.
     * @param location The location to populate.
     * @param creationProbability The probability threshold for creation.
     * @param factory The actor factory to use when the random check succeeds.
     * @return true if an actor was created and added.
     */
    private boolean tryPopulate(List<Actor> population, Location location,
        double creationProbability, ActorFactory factory)
    {
        if(rand.nextDouble() <= creationProbability) {
            population.add(factory.create(location));
            return true;
        }
        return false;
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
