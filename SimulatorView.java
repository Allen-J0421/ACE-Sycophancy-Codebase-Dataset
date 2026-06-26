import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2022/03/02
 */
public class SimulatorView extends JFrame
{
    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;
    private static final Color INFECTED_COLOR = Color.green;

    private static final String STEP_PREFIX = "Step: ";
    private static final String POPULATION_PREFIX = "Population: ";
    private static final String TIME_PREFIX = "It is: ";
    private static final String DISEASE_PREFIX = "Population died of disease: ";

    private JLabel stepLabel;
    private JLabel populationLabel;
    private JLabel infoLabel;
    private JLabel diseaseLabel;
    private FieldView fieldView;

    // A map for storing colors for participants in the simulation
    private Map<Class<?>, Color> colors;
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

        setTitle("Underwater Environment Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        diseaseLabel = new JLabel(DISEASE_PREFIX, JLabel.CENTER);
        populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        setLocation(100, 50);

        fieldView = new FieldView(height, width);
        addPanels();
        pack();
        setVisible(true);
    }

    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param weather the object of weather class
     * @param oxygenLevel the oxygen level that is to be displayed in the current step.
     */
    public void showStatus(SimulationSnapshot snapshot)
    {
        ensureVisible();
        updateLabels(snapshot);

        stats.reset();
        fieldView.preparePaint();
        renderField(snapshot.getField());
        if(snapshot.getWeather().getStormStart()) {
            drawStormOverlay(snapshot.getWeather());
        }

        stats.countFinished();
        populationLabel.setText(POPULATION_PREFIX + stats.getPopulationDetails(snapshot.getField()));
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

    private void addPanels()
    {
        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(diseaseLabel, BorderLayout.EAST);

        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(populationLabel, BorderLayout.SOUTH);
    }

    private void ensureVisible()
    {
        if(!isVisible()) {
            setVisible(true);
        }
    }

    private void updateLabels(SimulationSnapshot snapshot)
    {
        stepLabel.setText(STEP_PREFIX + snapshot.getStep());
        infoLabel.setText(TIME_PREFIX + (snapshot.isAtDayTime() ? "daytime" : "night")
                          + "        Oxygen Level: " + (int) (snapshot.getOxygenLevel() * 100) + "%"
                          + "        Storm: " + (snapshot.getWeather().getStormStart() ? "exists" : "subsides"));
        diseaseLabel.setText(DISEASE_PREFIX + snapshot.getDiseaseDeaths());
    }

    private void renderField(Field field)
    {
        field.forEachLocation((location, creature) ->
            drawLocation(creature, location.getRow(), location.getCol()));
    }

    private void drawLocation(Creature creature, int row, int col)
    {
        if(creature == null) {
            fieldView.drawMark(col, row, EMPTY_COLOR);
            return;
        }

        stats.incrementCount(creature.getClass());
        fieldView.drawMark(col, row, getCreatureColor(creature));
    }

    private void drawStormOverlay(Weather weather)
    {
        for(Location location : weather.getStormArea()) {
            fieldView.drawMark(location.getCol(), location.getRow(), getColor(Weather.class));
        }
    }

    private Color getCreatureColor(Creature creature)
    {
        if(creature instanceof Animal) {
            Animal animal = (Animal) creature;
            if(animal.getIsInfected() && !animal.getIsImmuned()) {
                return INFECTED_COLOR;
            }
        }
        return getColor(creature.getClass());
    }

    private Color getColor(Class<?> animalClass)
    {
        Color color = colors.get(animalClass);
        if(color == null) {
            return UNKNOWN_COLOR;
        }
        return color;
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
        private static final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth;
        private int gridHeight;
        private int xScale;
        private int yScale;
        private Dimension size;
        private Graphics graphics;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        FieldView(int height, int width)
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
            if(!size.equals(getSize())) {
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                graphics = fieldImage.getGraphics();

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
            graphics.setColor(color);
            graphics.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    graphics.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    graphics.drawImage(fieldImage, 0, 0,
                                       currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
