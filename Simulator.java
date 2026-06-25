import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;


/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing forest animals.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a Wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.03;
    // The probability that a Possum will be created in any given grid position.
    private static final double POSSUM_CREATION_PROBABILITY = 0.15;
    // The probability that a otter will be created in any given grid position.
    private static final double OTTER_CREATION_PROBABILITY = 0.18;
    // The probability that a muskrat will be created in any given grid position.
    private static final double MUSKRAT_CREATION_PROBABILITY = 0.25;
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.65;
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.9;

    // List of animals in the field.
    private List<Animal> animals;
    //List of plants in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    //The current time of the ecosystem
    private Time time = new Time();
    
    // ArrayList of all weathertypes
    private ArrayList<Weather> weatherTypes;
    //A variable for checking the state of the previous day.
    private boolean previousDayCheck;
    // The current weather of the simulation
    private Weather currentWeather;
    
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
        
        //list of all animals in the simulation
        animals = new ArrayList<>();
        //list of all plants in the simulation
        plants = new ArrayList<>();
        
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Possum.class, Color.PINK);
        view.setColor(Wolf.class, Color.BLUE);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Mouse.class, Color.ORANGE);
        view.setColor(Muskrat.class, Color.RED);
        view.setColor(Otter.class, Color.CYAN);
        
        //List of all weather types
        weatherTypes = new ArrayList<>();
        
        //Create weather type objects
        Sunny sunny = new Sunny();
        Rainy rainy = new Rainy();
        Cloudy cloudy = new Cloudy();
        
        //Add weather type objects to list
        weatherTypes.add(sunny);
        weatherTypes.add(rainy);
        weatherTypes.add(cloudy);
        
        //Initialise weather
        changeWeather();
        
        
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
     * Run the simulation from its current state for the given number of days.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numDays The number of days to run for.
     */
    public void simulate(int numDays)
    {
        int numSteps = numDays * 8;
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(500);   // if commented possible epilepsy warning
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each of the organisms.
     */
    public void simulateOneStep()
    {
        step++;
        
        //increment the step counter in time class
        time.incrementStep();
        
        view.changeDayNightColor();

        // Provide space for newborn animals and plants.
        List<Animal> newAnimals = new ArrayList<>();
        List<Plant> newPlants = new ArrayList<>();
        
        //changeWeather();
        checkTimeHasChanged();
        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if (time.isDay()){
                animal.actDay(newAnimals);
            }
            else{
                animal.actNight(newAnimals);
            }
            
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        //let all the plants act
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            if (time.isDay()){
                plant.actDay(newPlants,currentWeatherMultiplier());
            }
            else{
                plant.actNight(newPlants,currentWeatherMultiplier());
            }

            if(! plant.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born animals and plants to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);

        //show the state of the view
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populate();
        time.resetTime();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with animals.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                // Chooses an animal to spawn in based on a random probability
                
                if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    //Based on luck a wolf is spawned with or without disease
                    if (rand.nextInt(21) < 19){
                        Wolf wolf = new Wolf(true, field, location);
                        animals.add(wolf);
                    }
                    else{
                        Wolf wolf = new Wolf(true, field, location, getRandomAnimalDisease());
                        animals.add(wolf);
                    }
                    
                }
                else if(rand.nextDouble() <= POSSUM_CREATION_PROBABILITY) {
                    
                    Location location = new Location(row, col);
                    if (rand.nextInt(21) < 19){
                        Possum possum = new Possum(true, field, location);
                        animals.add(possum);
                    }
                    else{
                        Possum possum = new Possum(true, field, location,getRandomAnimalDisease());
                        animals.add(possum);
                    }
                    
                }
                else if(rand.nextDouble() <= OTTER_CREATION_PROBABILITY) {
                    
                    Location location = new Location(row, col);
                    if (rand.nextInt(21) < 19){
                        Otter otter = new Otter(true, field, location);
                        animals.add(otter);
                    }
                    else{
                        Otter otter = new Otter(true, field, location,getRandomAnimalDisease());
                        animals.add(otter);
                    }
                    
                }
                else if(rand.nextDouble() <= MUSKRAT_CREATION_PROBABILITY) {
                    
                    Location location = new Location(row, col);
                    if (rand.nextInt(21) < 19){
                        Muskrat muskrat = new Muskrat(true, field, location);
                        animals.add(muskrat);
                    }
                    else{
                        Muskrat muskrat = new Muskrat(true, field, location,getRandomAnimalDisease());
                        animals.add(muskrat);
                    }
                    
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    
                    Location location = new Location(row, col);
                    if (rand.nextInt(11) < 9){
                        Mouse mouse = new Mouse(true, field, location);
                        animals.add(mouse);
                    }
                    else{
                        Mouse mouse = new Mouse(true, field, location,getRandomAnimalDisease());
                        animals.add(mouse);
                    }
                    
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    
                    Location location = new Location(row, col);
                    if (rand.nextInt(21) < 19){
                        Grass grass = new Grass(true,field , location);
                        plants.add(grass);
                    }
                    else{
                        Grass grass = new Grass(true,field , location, getRandomPlantDisease());
                        plants.add(grass);
                    }
                    
                }
            }
        }
    }
    
    /**
     * Checks if the next day has come, based on if the current day is different to the previous
     */
    private void checkTimeHasChanged()
    {
        // If the current time of day and the previous day are opposite then change the weather.
        if (time.isDay() ^ previousDayCheck){
            changeWeather();
            previousDayCheck = time.isDay();
        }
    }
    
    /**
     * Changes the weather by choosing a random weather type
     */
    private void changeWeather()
    {
        Random rand = new Random();
        
        //Choose a random weather type, however if it is night it cannot be sunny
        if(time.isDay()){
            int randomNum = rand.nextInt(weatherTypes.size());
            currentWeather = weatherTypes.get(randomNum);
        }
        else{
            boolean isSunny = true;
            while(isSunny){
                int randomNum = rand.nextInt(weatherTypes.size());
                currentWeather = weatherTypes.get(randomNum);
                if (!(currentWeather instanceof Sunny)){
                    isSunny = false;
                }
            }
        }
        
        //Change the view on the GUI to reflect the current weather type 
        view.changeWeather(currentWeather.getClass().getSimpleName());
    }
    
    /**
     * Returns the weather multiplier on plant growth based on the current weather
     * @Return float The weather multiplier
     */
    private float currentWeatherMultiplier()
    {
        float multiplier = currentWeather.getMultiplier();
        return multiplier;
    }
        
    /**
     * Returns a random plant disease
     * @Return currentDisease The randomly chosen  plant disease
     */
    private PlantDisease getRandomPlantDisease()
    {
        // Lists of all the plant diseases
        List<PlantDisease> plantDiseases = new ArrayList<>();
        
        // create plant disease objects
        Mould mould = new Mould();
        
        
        //add to plantdiseases arraylist
        plantDiseases.add(mould);
        
        //Choose a random disease from plant diseases list
        Random rand = new Random();
        int randomNum = rand.nextInt(plantDiseases.size());
        PlantDisease currentDisease = plantDiseases.get(randomNum);
        
        return currentDisease;
    }
    
    /**
     * Choose a random animal disease
     * @Return currentDisease The randomly chosen Animal disease
     */
    private AnimalDisease getRandomAnimalDisease()
    {
        //
        List<AnimalDisease> animalDiseases = new ArrayList<>();
        // create plant disease objects
        Influenza influenza = new Influenza();
        
        
        //add to plantdiseases arraylist
        animalDiseases.add(influenza);
        
        
        Random rand = new Random();
        int randomNum = rand.nextInt(animalDiseases.size());
        AnimalDisease currentDisease = animalDiseases.get(randomNum);
        return currentDisease;
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
}
