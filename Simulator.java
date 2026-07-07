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
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 270;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 180;

    // Orchestrates the time-stepped simulation logic and organism state.
    private SimulationEngine engine;
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
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        engine  = new SimulationEngine(depth, width);
        buttons = new JButton[5];

        // Creates the buttons for the view
        JButton playPause = new JButton("Play");
            playPause.addActionListener(e -> new Thread(() -> playPauseButton()).start());
            buttons[0] = playPause;

        JButton runLongSim = new JButton("Run Long Sim");
            runLongSim.addActionListener(e -> new Thread(() -> runLongSimulation()).start());
            buttons[1] = runLongSim;

        JButton simOneStep = new JButton("Sim One Step");
            simOneStep.addActionListener(e -> simulateOneStep());
            buttons[2] = simOneStep;

        JButton reset = new JButton("Reset");
            reset.addActionListener(e -> reset());
            buttons[3] = reset;

        JButton quit = new JButton("Quit");
            quit.addActionListener(e -> quit());
            buttons[4] = quit;

        // Sets the visibility of the buttons in accordance to the current state of the simulation.
        buttonToggle();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, buttons);
        view.setColor(Zebra.class,   Color.BLACK,  Color.WHITE);
        view.setColor(Giraffe.class, Color.YELLOW, Color.BLACK);
        view.setColor(Lemur.class,   Color.BLUE,   Color.WHITE);
        view.setColor(Lion.class,    Color.RED,    Color.WHITE);
        view.setColor(Cheetah.class, Color.ORANGE, Color.BLACK);
        view.setColor(Plant.class,   Color.GREEN,  Color.BLACK);

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
            if (!Time.getIsFinished()) {
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
     * Run the simulation from its current state for the given number of steps.
     * @param numSteps The number of steps to run for.
     */
    private void simulate(int numSteps)
    {
        Time.setStepsToRunFor(numSteps);
        simulate();
    }

    /**
     * Run the simulation from its current state for the number of steps stored
     * in the Time class. Stops early if only one species remains.
     */
    private void simulate()
    {
        Time.setIsPaused(false);

        buttonToggle();

        while (!Time.getIsPaused() && !Time.getIsFinished() && view.isViable(engine.getField())) {
            simulateOneStep();
        }

        Time.setIsPaused(true);
        buttonToggle();
    }

    /**
     * Advance the simulation by one step and refresh the view.
     */
    private void simulateOneStep()
    {
        engine.simulateOneStep();
        view.showStatus(engine.getField());
    }

    /**
     * Reset the simulation to a starting position.
     */
    private void reset()
    {
        Time.resetStep();
        buttonToggle();

        engine.reset();

        view.showStatus(engine.getField());
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
     * Sets the visibility of the buttons in accordance with the current
     * state of the simulation.
     */
    private void buttonToggle()
    {
        boolean isPaused   = Time.getIsPaused();
        boolean isFinished = Time.getIsFinished();

        boolean isStopped = isPaused || isFinished;

        buttons[0].setEnabled(!isFinished);
        buttons[1].setEnabled(isStopped);
        buttons[2].setEnabled(isStopped);
        buttons[3].setEnabled(isStopped);

        if (isStopped) {
            buttons[0].setLabel("Play");
        }
        else {
            buttons[0].setLabel("Pause");
        }
    }

    /**
     * Used by the quit button to close the application.
     */
    private void quit()
    {
        System.exit(0);
    }
}
