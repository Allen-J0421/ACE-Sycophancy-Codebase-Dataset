import java.awt.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid. The view displays a colored
 * rectangle for each location representing its contents. It uses a default
 * background color. Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class SimulatorView extends JFrame
{
    private static final int BUTTON_ROWS = 3;
    private static final int GRID_VIEW_SCALING_FACTOR = 6;
    private static final int INITIAL_WINDOW_X = 100;
    private static final int INITIAL_WINDOW_Y = 50;

    // Colors used for empty locations:
    private static final Color EMPTY_COLOR = Color.white;
    // Color used for objects that have no defined color:
    private static final Color UNKNOWN_COLOR = Color.gray;

    private static final String STEP_PREFIX = "Step: ";
    private static final String POPULATION_PREFIX = "Population: ";
    private static final String DAY_PREFIX = "Day : ";

    private final JLabel stepLabel;
    private final JLabel population;
    private final JLabel infoLabel;
    private final JLabel weatherPropertiesLabel;
    private final JLabel dayLabel;
    private final FieldView fieldView;
    
    // A map for storing colors for participants in the simulation:
    private final Map<Class<? extends Actor>, Color> colors;
    // A statistics object computing and storing simulation information:
    private final FieldStats stats;
    private final Simulator simulator;
    
    // The currently running simulation task, if any.
    private Thread simulationThread;
    
    /**
     * Create a view of the given width and height.
     *
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(Simulator simulator, FieldStats stats, int height, int width)
    {
        this.stats = stats;
        colors = new LinkedHashMap<>();
        this.simulator = simulator;

        setTitle("Australian Savannah Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        weatherPropertiesLabel = new JLabel("");
        dayLabel = new JLabel(DAY_PREFIX + "0", JLabel.CENTER);
        updateWeatherPropertiesLabel();
        updateDayLabel();

        setLocation(INITIAL_WINDOW_X, INITIAL_WINDOW_Y);
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(weatherPropertiesLabel, BorderLayout.EAST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(dayLabel);
        
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        contents.add(createButtonPanel(), BorderLayout.WEST);

        pack();
        setVisible(true);
    }

    /**
     * Create the panel containing the simulation control buttons.
     *
     * @return The control button panel.
     */
    private JPanel createButtonPanel()
    {
        JPanel buttonGrid = new JPanel(new GridLayout(BUTTON_ROWS, 0));

        buttonGrid.add(createButton("4000 Steps", "run-long-simulation", simulator::runLongSimulation));
        buttonGrid.add(createButton("Reset", "reset-simulation", simulator::reset));
        buttonGrid.add(createButton("One Step", "simulate-one-step", simulator::simulateOneStep));

        return buttonGrid;
    }

    /**
     * Create a control button that runs a simulation task when clicked.
     *
     * @param label The button label.
     * @param threadName The worker thread name.
     * @param task The simulation task to run.
     * @return The configured button.
     */
    private JButton createButton(String label, String threadName, Runnable task)
    {
        JButton button = new JButton(label);
        button.addActionListener(e -> runSimulationTask(threadName, task));
        return button;
    }

    /**
     * Run a simulation task if no other simulation task is active.
     *
     * @param threadName The name for the worker thread.
     * @param task The simulation task to execute.
     */
    private synchronized void runSimulationTask(String threadName, Runnable task)
    {
        if (isSimulationTaskRunning())
        {
            return;
        }

        simulationThread = new Thread(task, threadName);
        simulationThread.start();
    }

    /**
     * @return Whether a simulation task is currently running.
     */
    private synchronized boolean isSimulationTaskRunning()
    {
        return simulationThread != null && simulationThread.isAlive();
    }
    
    /**
     * Update the text displayed in the weather properties
     * label to reflect the actual weather properties.
     */
    private void updateWeatherPropertiesLabel()
    {
        weatherPropertiesLabel.setText(WeatherSystem.getIsRaining() ? "RAINING" : "NOT RAINING");
    }

    /**
     * Update the displayed day label.
     */
    public void updateDayLabel()
    {   
        dayLabel.setText(DAY_PREFIX + TimeSystem.getCurrentDay());
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * 
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<? extends Actor> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class<? extends Actor> animalClass)
    {
        Color col = colors.get(animalClass);
        
        // No color defined for this class:
        return col == null ? UNKNOWN_COLOR : col;
    }

    /**
     * Show the current status of the field.
     * 
     * @param step  Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if (!isVisible()) setVisible(true);
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                drawActor(field.getActorAt(row, col), row, col);
            }
        }
        
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        
        updateWeatherPropertiesLabel();
        updateDayLabel();
        fieldView.repaint();
    }

    /**
     * Draw a single field location and update statistics for its actor.
     *
     * @param actor The actor at the location, or null if empty.
     * @param row The row coordinate.
     * @param col The column coordinate.
     */
    private void drawActor(Actor actor, int row, int col)
    {
        if (actor == null)
        {
            fieldView.drawMark(col, row, EMPTY_COLOR);
            return;
        }

        stats.incrementCount(actor.getClass());
        fieldView.drawMark(col, row, getColor(actor.getClass()));
    }

    /**
     * Determine whether the simulation should continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int gridWidth;
        private final int gridHeight;
        private int xScale, yScale;
        private Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            // If the size has changed:
            if (!size.equals(getSize()))
            {
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                
                if (xScale < 1) xScale = GRID_VIEW_SCALING_FACTOR;
                
                yScale = size.height / gridHeight;
                
                if (yScale < 1) yScale = GRID_VIEW_SCALING_FACTOR;
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if (fieldImage != null)
            {
                Dimension currentSize = getSize();
                
                if (size.equals(currentSize)) g.drawImage(fieldImage, 0, 0, null);
                // Else, rescale the previous image:
                else g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }
}
