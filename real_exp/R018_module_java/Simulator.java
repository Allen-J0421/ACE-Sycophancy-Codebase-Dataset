import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.HashMap;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing bears, panthers, monkeys, carp, possums, algae and leaves.
 *
 * @version 15/03/2022
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 240;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 160;
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.02;
    // The probability that a panther will be created in any given grid position.
    private static final double PANTHER_CREATION_PROBABILITY = 0.02;    
    // The probability that a monkey will be created in any given grid position.
    private static final double MONKEY_CREATION_PROBABILITY = 0.08;  
    // The probability that a possum will be created in any given grid position.
    private static final double POSSUM_CREATION_PROBABILITY = 0.1;  
    // The probability that a carp will be created in any given grid position.
    private static final double CARP_CREATION_PROBABILITY = 0.2; 
    // The probability that leaves will be created in any given grid position.
    private static final double LEAVES_CREATION_PROBABILITY = 0.5;   
    // The probability that algae will be created in any given grid position.
    private static final double ALGAE_CREATION_PROBABILITY = 0.03;

    // List of organisms in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // An ArrayList of the weather options.
    private ArrayList<String> weatherOptions;
    // The current step of the simulation.
    private int step;
    // The current time translated to minutes.
    public int minutes;
    // A flag used to indicate the start of the clock.
    private boolean startTime = true;
    // The current weather condition.
    private String weather;
    // A flag used to indicate when to change the weather.
    private boolean changeWeather = true;
    // The time interval, in minutes, before the weather changes again.
    private int nextChange;
    // Indicates if a disease is active.
    private boolean disease;
    // The clock display.
    private String time;
    // A graphical view of the simulation.
    private List<SimulatorView> views;
    // The difference between 00:00 and the starting time of the simulation, in minutes.
    private int offsettedMins;
    // A flag used to indicate if the offsetted minutes have been calculated.
    private boolean offsetPlaced = true;
    
    
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
        
        organisms = new ArrayList<>();
        field = new Field(depth, width);

        // Initialise weather options.
        weatherOptions = new ArrayList<String>();
        weatherOptions.add("Sunny");
        weatherOptions.add("Windy");
        weatherOptions.add("Stormy");
        weatherOptions.add("Foggy");
        weatherOptions.add("Raining");
        weatherOptions.add("Snowing");
        
        views = new ArrayList<>();
        
        // Create a grid view of the state of each location in the field.
        SimulatorView view = new GridView(depth, width);
        view.setColor(Bear.class, Color.BLUE);
        view.setColor(Panther.class, Color.MAGENTA);
        view.setColor(Possum.class, Color.CYAN);
        view.setColor(Monkey.class, Color.PINK);
        view.setColor(Carp.class, Color.ORANGE);
        view.setColor(Leaves.class, Color.GREEN);
        view.setColor(Algae.class, Color.YELLOW);
        views.add(view);
        
        // Create a graph view of the state of each organism in the field.
        view = new GraphView(750, 200, 750);
        view.setColor(Bear.class, Color.BLUE);
        view.setColor(Panther.class, Color.MAGENTA);
        view.setColor(Possum.class, Color.CYAN);
        view.setColor(Monkey.class, Color.PINK);
        view.setColor(Carp.class, Color.ORANGE);
        view.setColor(Leaves.class, Color.GREEN);
        view.setColor(Algae.class, Color.YELLOW);
        views.add(view);

        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (1000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && views.get(0).isViable(field); step++) {
            simulateOneStep();
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
    {
        generateWeather(minutes);
        
        generateDisease();
        
        minutes++;
        
        // Reset clock back to 00:00 after 23:59.
        if (minutes == 1440) {
            minutes = 0;
        }
        
        // Convert the current number of minutes into a clock format to display.
        convertToTime(minutes);
        
        step++;

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms);
            organism.setClock(minutes+1);
            organism.setTime(minutes-offsettedMins+1);
            organism.setWeather(weather);
            if(! organism.isAlive()) {
                it.remove();
            }
        }
        
        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);

        updateViews();
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        for (SimulatorView view : views) {
            view.reset();
        }
        // Start simulation at a random time of day.
        if (startTime == true) {
            Random rand = Randomizer.getRandom();
            minutes = rand.nextInt(1439); // Will select a random number between 0 and 1438; if 1438 is assigned, 'minutes++' will increment it to 1439 which represents 23:59.
            startTime = false;
        }
        convertToTime(minutes);
        generateWeather(minutes);
        generateDisease();
        populate();
        
        // Show the starting state in the views.
        updateViews();
    }
    
    /**
     * Update all existing views.
     */
    private void updateViews()
    {
        for (SimulatorView view : views) {
            view.showStatus(step, field, weather, updateDiseaseCounter(), time);
        }
    }
    
    /**
     * Changes the weather at random points in the day.
     * @param currentMins The current time translated to minutes.
     */
    private void generateWeather(int currentMins) 
    {
        if (changeWeather) {
            Random rand = Randomizer.getRandom();
            weather = weatherOptions.get(rand.nextInt(6));
            nextChange = currentMins + rand.nextInt(241) + 120; // Change the weather at a random interval between 120 and 360 minutes.
            changeWeather = false;
        }
        if (currentMins == nextChange-1 || currentMins == nextChange-1439) { // Ensure that the next change happens the next day if it is due after midnight.
            changeWeather = true;
        }
    }
    
    /**
     * At every step, there is a 1% chance of any animal catching a disease.
     */
    private void generateDisease() 
    {
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext();){
            Organism organism = it.next();
            if (organism instanceof Animal){
                if (Math.random() < 0.01) {
                    Animal animal = (Animal) organism;
                    animal.setDisease(true);
                }
            }
        }
    }
    
    /**
     * Counts up the total number of animals with a disease.
     * @return The total number of animals with a disease.
     */
    private int updateDiseaseCounter()
    {
        int counter = 0;
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            if (organism instanceof Animal){
                Animal animal = (Animal) organism;
                if (animal.hasDisease()) {
                    counter++;
                }
            }
        }
        return counter;
    }
    
    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    organisms.add(bear);
                }
                else if (rand.nextDouble() <= PANTHER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Panther panther = new Panther(true, field, location);
                    organisms.add(panther);
                }
                else if(rand.nextDouble() <= MONKEY_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Monkey monkey = new Monkey(true, field, location);
                    organisms.add(monkey);
                }
                else if(rand.nextDouble() <= POSSUM_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Possum possum = new Possum(true, field, location);
                    organisms.add(possum);
                }
                else if(rand.nextDouble() <= CARP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Carp carp = new Carp(true, field, location);
                    organisms.add(carp);
                }
                else if (rand.nextDouble() <= LEAVES_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Leaves leaves = new Leaves(true, field, location);
                    organisms.add(leaves);
                }
                else if (rand.nextDouble() <= ALGAE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Algae algae = new Algae(true, field, location);
                    organisms.add(algae);
                }
                // Else leave the location empty.
            }
        }
    }
    
    /**
     * Calculates the current time of day.
     * @param minutes The current time translated to minutes.
     * @return The current time of day.
     */
    private String timeOfDay(int minutes) 
    {
        String timeOfDay;
        if (minutes >= 300 && minutes < 720) { // Between 05:00 and 11:59.
            timeOfDay = "Morning";
        }
        else if (minutes >= 720 && minutes < 1080) { // Between 12:00 and 17:59.
            timeOfDay = "Afternoon";
        }
        else if (minutes >= 1080 && minutes < 1260) { // Between 18:00 and 20:59.
            timeOfDay = "Evening";
        }
        else {
            timeOfDay = "Night"; // Between 21:00 and 04:59.
        }
        return timeOfDay;
    }
    
    /**
     * Convert minutes to a clock display.
     * @param minutes The number of minutes that have passed since midnight.
     */
    private void convertToTime(int minutes) 
    {        
        // Calculate the number of hours to display on the clock.
        String displayHours = "" + (int)Math.floor(minutes/60);
        
        // The remainder is the number of minutes to display on the clock.
        String displayMinutes = "" + minutes%60;
        
        // If the number of hours to display is a single digit, add a 0 to the front of it.
        if (displayHours.length() == 1) {
            displayHours = "0" + displayHours;
        }
        
        // If the number of minutes to display is a single digit, add a 0 to the front of it.
        if (displayMinutes.length() == 1) {
            displayMinutes = "0" + displayMinutes;
        }
        
        // If offset has been calculated already, do not do it again.
        if (offsetPlaced) { 
            offset((int)Math.floor(minutes/60), minutes%60);
            offsetPlaced = false;
        }
        
        // Format to a clock to display in the window.
        time = timeOfDay(minutes) + "   Time: " + displayHours + ":" + displayMinutes;
    }
    
    /**
     * Calculate the number of minutes to offset from midnight.
     * @param displayHours The number of hours displayed in the window.
     * @param displayMinutes The number of minutes displayed in the window.
     * @return The number of offsetted minutes.
     */
    private int offset(int displayHours, int displayMinutes) {
        offsettedMins = (displayHours * 60) + displayMinutes;
        return offsettedMins;
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
            // Wake up.
        }
    }
}
