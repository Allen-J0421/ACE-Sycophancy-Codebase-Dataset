import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location representing its contents.
 * Colors for each type of species can be defined using the setColor method.
 * 
 * Reference: Object First with Java A Practical Introduction
 * Using BlueJ 6th Edition Chapter 12
 *
 * @version 15/03/2022
 */
public class GridView extends JFrame implements SimulatorView
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = " Step: ";
    private final String WEATHER_PREFIX = "  Weather: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DISEASED_PREFIX = "Infected Animals: ";
    private JLabel stepLabel, populationLabel, infoLabel, clockLabel, weatherLabel, diseaseLabel;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation.
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information.
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public GridView(int height, int width)
    {
        stats = new FieldStats();
        colors = new HashMap<>();

        setTitle("Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("", JLabel.CENTER);
        populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        clockLabel = new JLabel("", JLabel.CENTER);
        weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        diseaseLabel = new JLabel(DISEASED_PREFIX, JLabel.CENTER);
        diseaseLabel.setForeground(Color.red);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel topPane = new JPanel(new BorderLayout());
            topPane.add(stepLabel, BorderLayout.WEST);
            topPane.add(infoLabel, BorderLayout.CENTER);
            topPane.add(clockLabel, BorderLayout.EAST);
            topPane.add(weatherLabel, BorderLayout.CENTER);
        
        JPanel bottomPane = new JPanel(new BorderLayout());
            bottomPane.add(weatherLabel, BorderLayout.WEST);
            bottomPane.add(populationLabel, BorderLayout.CENTER);
            bottomPane.add(diseaseLabel, BorderLayout.EAST);
        
        contents.add(topPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(bottomPane, BorderLayout.SOUTH);
        
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class of organism.
     * @param organismClass The organism's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> organismClass, Color color)
    {
        colors.put(organismClass, color);
    }

    /**
     * @return The color to be used for a given class of organism.
     */
    private Color getColor(Class<?> organismClass)
    {
        Color col = colors.get(organismClass);
        if(col == null) {
            // No color defined for this class.
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param diseaseCounter The total number of animals with a disease.
     * @param time The current time translated to minutes.
     */
    public void showStatus(int step, Field field, String weather, int diseaseCounter, String time)
    {
        if(!isVisible()) {
            setVisible(true);
        }
        
        stepLabel.setText(STEP_PREFIX + step);
        clockLabel.setText(time);
        weatherLabel.setText(WEATHER_PREFIX + weather);
        diseaseLabel.setText(DISEASED_PREFIX + diseaseCounter + "  ");
        stats.reset();
        
        fieldView.preparePaint();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object organism = field.getObjectAt(row, col);
                if(organism != null) {
                    Class<?> cls = organism.getClass();
                    stats.incrementCount(cls);
                    fieldView.drawMark(col, row, getColor(cls));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        
        populationLabel.setText(POPULATION_PREFIX + stats.getTotalPopulation() + " " + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Prepare for a new run.
     */
    public void reset()
    {
        stats.reset();
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
            if(! size.equals(getSize())) {  // If the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
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
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
