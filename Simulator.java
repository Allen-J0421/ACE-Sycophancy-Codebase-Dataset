import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import javax.swing.JButton;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 26/02/2022
 */
public class Simulator
{
    // Field dimensions.
    private static final int DEFAULT_WIDTH = 270;
    private static final int DEFAULT_DEPTH = 180;

    // Initial population probabilities per grid cell.
    private static final double LION_CREATION_PROBABILITY    = 0.0125;
    private static final double CHEETAH_CREATION_PROBABILITY = 0.0125;
    private static final double ZEBRA_CREATION_PROBABILITY   = 0.08;
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.08;
    private static final double LEMUR_CREATION_PROBABILITY   = 0.081;

    // Named indices into the buttons array.
    private static final int BTN_PLAY_PAUSE = 0;
    private static final int BTN_RUN_LONG   = 1;
    private static final int BTN_STEP       = 2;
    private static final int BTN_RESET      = 3;
    private static final int BTN_QUIT       = 4;

    // List of animals in the field.
    private List<LivingOrganism> animals;
    // List of plants in the field
    private List<LivingOrganism> plants;
    // The current state of the field.
    private Field field;
    // A graphical view of the simulation.
    private SimulatorView view;
    // List of buttons that are used to control the simulation.
    private JButton[] buttons;
    
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
        buttons = new JButton[5];

        JButton playPause = new JButton("Play");
        playPause.addActionListener(e -> new Thread(() -> playPauseButton()).start());
        buttons[BTN_PLAY_PAUSE] = playPause;

        JButton runLongSim = new JButton("Run Long Sim");
        runLongSim.addActionListener(e -> new Thread(() -> runLongSimulation()).start());
        buttons[BTN_RUN_LONG] = runLongSim;

        JButton simOneStep = new JButton("Sim One Step");
        simOneStep.addActionListener(e -> simulateOneStep());
        buttons[BTN_STEP] = simOneStep;

        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> reset());
        buttons[BTN_RESET] = reset;

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> quit());
        buttons[BTN_QUIT] = quit;
        
        // Sets the visibility of the buttons in accordance to the current state of the simulation.
        buttonToggle();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, buttons);
        view.setColor(Zebra.class, Color.BLACK, Color.WHITE);
        view.setColor(Giraffe.class, Color.YELLOW, Color.BLACK);
        view.setColor(Lemur.class, Color.BLUE, Color.WHITE);
        view.setColor(Lion.class, Color.RED, Color.WHITE);
        view.setColor(Cheetah.class, Color.ORANGE, Color.BLACK);
        view.setColor(Plant.class, Color.GREEN, Color.BLACK);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Controls the functionality of the Play/Pause button. This depends
     * on whether the simulation is paused or not.
     */
    private void playPauseButton() 
    {
        boolean isPaused = Time.getIsPaused();
        
        Time.toggleIsPaused();
        
        if (isPaused) {
            if (! Time.getIsFinished()) {
                simulate();
            }
        }
        
        buttonToggle();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period: 500 steps.
     */
    private void runLongSimulation()
    {
        simulate(500);
    }
    
    /**
     * Run the simulation from its current state for the given number of 
     * steps.
     * @param numSteps The number of steps to run for.
     */
    private void simulate(int numSteps)
    {
        // Sets the number of steps that the simulation run started at
        Time.setStepsToRunFor(numSteps);
        simulate();
    }
    
    /**
     * Run the simulation from its current state for the given number of 
     * steps in the Time Class.
     * Stop before the given number of steps if it ceases to be viable.
     * This is considered to be when only one species remains.
     */
    private void simulate() 
    {
        Time.setIsPaused(false);
        
        // Adjust button visibility.
        buttonToggle();
        
        while (! Time.getIsPaused() && ! Time.getIsFinished() && view.isViable(field)) {
            simulateOneStep();
        }
        
        Time.setIsPaused(true);
        buttonToggle();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * predator, prey, and plant.
     */
    private void simulateOneStep()
    {
        Time.incrementStep();
        Weather.updateWeather();

        // Provide space for newborn animals and plants;
        List<LivingOrganism> newAnimals = new ArrayList<>();
        List<LivingOrganism> newPlants = new ArrayList<>();
        
        // Let all animals act.
        for(Iterator<LivingOrganism> it = animals.iterator(); it.hasNext(); )
        {
            Animal animal = (Animal) it.next();
            animal.act(newAnimals);
            if(!animal.isAlive())
            {
                it.remove();
            }
        }

        // Let all plants act.
        for(Iterator<LivingOrganism> it = plants.iterator(); it.hasNext(); )
        {
            Plant plant = (Plant) it.next();
            plant.act(newPlants);
            if(!plant.isAlive())
            {
                it.remove();
            }
        }
        
        // Add the newly born animals and plants to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants); 

        // Show the current state in the view.
        view.showStatus(field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    private void reset()
    {
        // Resets the visibility of the buttons and resets the step number
        Time.resetStep();
        buttonToggle();
        
        // Clears all current animals and plants then repopulates the area
        animals.clear();
        plants.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(field);
    }
    
    /**
     * Randomly populate the field with predators, prey, and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                //plants will fill the entire field initially.
                Location location = new Location(row, col);
                Plant plant = new Plant(true, field, location);
                plants.add(plant);
                
                Animal animal = null;

                if(rand.nextDouble() <= LION_CREATION_PROBABILITY)
                {
                    animal = new Lion(true, field, location, false, false);
                }
                else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY)
                {
                    animal = new Cheetah(true, field, location, false, false);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY)
                {
                    animal = new Zebra(true, field, location, false, false);
                }
                else if(rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY)
                {
                    animal = new Giraffe(true, field, location, false, false);
                }
                else if(rand.nextDouble() <= LEMUR_CREATION_PROBABILITY)
                {
                    animal = new Lemur(true, field, location, false, false);
                }
                
                if(animal != null) 
                {
                    animals.add(animal);
                }
            }
        }
    }
    
    /**
     * Sets the visibility of the buttons in accordance with the current
     * state of the simulation.
     */
    private void buttonToggle()
    {
        boolean isFinished = Time.getIsFinished();
        boolean isStopped = Time.getIsPaused() || isFinished;

        buttons[BTN_PLAY_PAUSE].setEnabled(!isFinished);
        buttons[BTN_RUN_LONG].setEnabled(isStopped);
        buttons[BTN_STEP].setEnabled(isStopped);
        buttons[BTN_RESET].setEnabled(isStopped);

        buttons[BTN_PLAY_PAUSE].setText(isStopped ? "Play" : "Pause");
    }
    
    /**
     * Used by the quit button to close the application.
     */
    private void quit()
    {
        System.exit(0);
    }
}
