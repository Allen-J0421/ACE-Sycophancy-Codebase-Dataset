import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * 
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR_DAY = Color.white;
    private static final Color EMPTY_COLOR_NIGHT = Color.LIGHT_GRAY;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String TIME_PREFIX = "Current Time: ";
    private final String TIME_END_PREFIX = ":00";
    private final String DAYTIME_PREFIX = "Day";
    private final String NIGHTTIME_PREFIX = "Night";
    private final String WEATHER_PREFIX = "Weather: ";
    private JLabel stepLabel, population, infoLabel, timeLabel, weatherLabel;
    private FieldView fieldView;

    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Predator & Prey Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);

        setLocation(100, 50);

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(timeLabel, BorderLayout.EAST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        mainPanel.add(infoPane, BorderLayout.NORTH);
        mainPanel.add(fieldView, BorderLayout.CENTER);
        mainPanel.add(population, BorderLayout.SOUTH);
        
        contents.add(mainPanel, BorderLayout.CENTER);
        contents.add(createTestButtons(), BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    /**
     * Create some buttons to test
     */
    public Simulator simulator;
    private JPanel createTestButtons(){
        JPanel panel = new JPanel();
        JButton reset = new JButton("Reset");
        JButton run = new JButton("One Step");
        JButton run10 = new JButton("10 Step");
        JButton run50 = new JButton("50 Step");
        panel.add(reset);
        panel.add(run);
        panel.add(run10);
        panel.add(run50);

        reset.addActionListener(e->simulator.reset());
        run.addActionListener(e->simulator.simulate(1));
        run10.addActionListener(e->simulator.simulate(10));
        run50.addActionListener(e->simulator.simulate(50));

        return panel;
    }

    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
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
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
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
     */
    public void showStatus(int step, int hour, boolean isDay, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step + " ");
        infoLabel.setText(WEATHER_PREFIX + field.getWeather().name());
        if (hour < 10){
            if (isDay) {
                timeLabel.setText(DAYTIME_PREFIX + "   " + TIME_PREFIX + "0" + hour + TIME_END_PREFIX + " ");
            } else {timeLabel.setText(NIGHTTIME_PREFIX + "   " + TIME_PREFIX + "0" + hour + TIME_END_PREFIX + " ");}
        } else {
            if (isDay) {
                timeLabel.setText(DAYTIME_PREFIX + "   " + TIME_PREFIX + hour + TIME_END_PREFIX + " ");
            } else {timeLabel.setText(NIGHTTIME_PREFIX + "   " + TIME_PREFIX + hour + TIME_END_PREFIX + " ");}
        }

        stats.reset();
        fieldView.preparePaint();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawSpecies(col, row, animal);
                }
                else {
                    if (isDay) {
                        fieldView.drawMark(col, row, EMPTY_COLOR_DAY);
                    } else {
                        fieldView.drawMark(col, row, EMPTY_COLOR_NIGHT);
                    }
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
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
            if(! size.equals(getSize())) {  // if the size has changed...
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
         * Paint on grid location on this field by species's color.
         */
        public void drawSpecies(int x, int y, Object obj)
        {
            Color color = getColor(obj.getClass());
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
            if(obj instanceof Animal){
                Animal animal = (Animal)obj;
                if(animal.isInfected()){
                    g.setColor(Color.BLACK);
                    g.fillRect(x * xScale, y * yScale + yScale - 3, 2, 2);
                }
                g.setColor(animal.getGender() == 1 ? Color.BLUE : Color.RED);
                g.fillRect(x * xScale, y * yScale, 2, 2);
            }
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
