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
    // Colors used for empty locations:
    private static final Color EMPTY_COLOR = Color.white;
    // Color used for objects that have no defined color:
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population, infoLabel, weatherPropertiesLabel, dayLabel;
    private FieldView fieldView;

    // A map for storing colors for participants in the simulation:
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information:
    private FieldStats stats;
    // Stable reference used by StatisticsView to access the stats instance:
    private static SimulatorView instance;
    private Simulator simulator;

    // Threads for each method called by the buttons:
    private Thread runLongSimulationThread;
    private Thread resetThread;
    private Thread simulateOneStepThread;

    /**
     * Create a view of the given width and height.
     *
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(Simulator simulator, int height, int width)
    {
        instance = this;
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        this.simulator = simulator;
        setTitle("Australian Savannah Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        dayLabel = new JLabel("Day : 0", JLabel.CENTER);
        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridLayout(3, 0));

        JButton longSimButton = new JButton("4000 Steps");
        longSimButton.addActionListener(e -> {
            if (anyAlive(runLongSimulationThread, simulateOneStepThread)) return;
            runLongSimulationThread = startThread(simulator::runLongSimulation);
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            if (anyAlive(resetThread, runLongSimulationThread, simulateOneStepThread)) return;
            resetThread = startThread(simulator::reset);
        });

        JButton oneSimButton = new JButton("One Step");
        oneSimButton.addActionListener(e -> {
            if (anyAlive(simulateOneStepThread, runLongSimulationThread)) return;
            simulateOneStepThread = startThread(simulator::simulateOneStep);
        });

        buttonGrid.add(longSimButton);
        buttonGrid.add(resetButton);
        buttonGrid.add(oneSimButton);
        weatherPropertiesLabel = new JLabel("");
        updateWeatherPropertiesLabel();
        updateDayLabel();

        setLocation(100, 50);

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
        contents.add(buttonGrid, BorderLayout.WEST);
        pack();
        setVisible(true);
    }

    /**
     * @return The FieldStats instance owned by the current SimulatorView.
     *         Used by StatisticsView to read live population counters.
     */
    public static FieldStats getStats() { return instance.stats; }

    /** @return True if any of the given threads is non-null and alive. */
    private static boolean anyAlive(Thread... threads)
    {
        for (Thread t : threads)
            if (t != null && t.isAlive()) return true;
        return false;
    }

    /** Start a new thread for the given task and return it. */
    private static Thread startThread(Runnable task)
    {
        Thread t = new Thread(task);
        t.start();
        return t;
    }

    /**
     * Update the text displayed in the weather properties
     * label to reflect the actual weather properties.
     */
    private void updateWeatherPropertiesLabel()
    {
        String text;

        if (WeatherSystem.getIsRaining()) text = "RAINING";
        else                              text = "NOT RAINING";

        weatherPropertiesLabel.setText(text);
    }

    public void updateDayLabel()
    {
        String text;
        text = "Day : " + TimeSystem.getCurrentDay();
        dayLabel.setText(text);
    }

    /**
     * Define a color to be used for a given class of animal.
     *
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);

        // No color defined for this class:
        if (col == null) return UNKNOWN_COLOR;
        else             return col;
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
        drawFieldContents(field);
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));

        updateWeatherPropertiesLabel();
        updateDayLabel();
        fieldView.repaint();
    }

    /**
     * Draw every cell of the field into the field view, incrementing
     * per-class counts as each occupied cell is visited.
     *
     * @param field The field to render.
     */
    private void drawFieldContents(Field field)
    {
        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Object animal = field.getObjectAt(row, col);

                if (animal != null)
                {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else
                {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
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
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
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
