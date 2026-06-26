import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * Each grid location is rendered as a colored rectangle.
 * Species colors are registered with setColor(); infected animals are shown in green;
 * active storm areas are overlaid in blue after all cells are drawn.
 *
 * @version 2022/03/02
 */
public class SimulatorView extends JFrame
{
    private static final Color EMPTY_COLOR    = Color.white;
    private static final Color UNKNOWN_COLOR  = Color.gray;
    private static final Color STORM_COLOR    = Color.blue;
    private static final Color INFECTED_COLOR = Color.green;

    private static final String STEP_PREFIX                       = "Step: ";
    private static final String POPULATION_PREFIX                 = "Population: ";
    private static final String POPULATION_DIE_OF_DISEASE_PREFIX = "Population died of disease: ";

    private JLabel stepLabel, population, infoLabel, diseaseLabel;
    private FieldView fieldView;

    private Map<Class<?>, Color> colors;
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats  = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Underwater Environment Simulation");
        stepLabel    = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel    = new JLabel("", JLabel.CENTER);
        diseaseLabel = new JLabel(POPULATION_DIE_OF_DISEASE_PREFIX, JLabel.CENTER);
        population   = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        setLocation(100, 50);
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel,    BorderLayout.WEST);
        infoPane.add(infoLabel,    BorderLayout.CENTER);
        infoPane.add(diseaseLabel, BorderLayout.EAST);
        contents.add(infoPane,     BorderLayout.NORTH);
        contents.add(fieldView,    BorderLayout.CENTER);
        contents.add(population,   BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    /**
     * Register a display color for a species class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Show the current status of the field.
     */
    public void showStatus(int step, Field field, boolean timeOfDay, Weather weather, double oxygenLevel)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        infoLabel.setText("It is: " + (timeOfDay ? "daytime" : "night ")
                + "        Oxygen Level: " + (int)(oxygenLevel * 100) + "%"
                + "        Storm: " + (weather.isStormActive() ? "exists" : "subsides"));
        diseaseLabel.setText(POPULATION_DIE_OF_DISEASE_PREFIX + Animal.getDiseaseDeathCount());

        stats.reset();
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object creature = field.getObjectAt(row, col);
                if(creature != null) {
                    stats.incrementCount(creature.getClass());
                    fieldView.drawMark(col, row, colorFor(creature));
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        // Storm overlay is applied once after all cells are drawn, not once per cell.
        if(weather.isStormActive()) {
            drawStormOverlay(field, weather);
        }

        stats.countFinished();
        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true if more than one species is still alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Return the display color for a creature: infected animals render green,
     * all others use their registered species color (or gray if unregistered).
     */
    private Color colorFor(Object creature)
    {
        if(creature instanceof Animal) {
            Animal ani = (Animal) creature;
            if(ani.isInfected() && !ani.isImmune()) {
                return INFECTED_COLOR;
            }
        }
        return colors.getOrDefault(creature.getClass(), UNKNOWN_COLOR);
    }

    /**
     * Paint all cells within the storm's scope in the storm color.
     * Called once per step, after the main cell rendering pass.
     */
    private void drawStormOverlay(Field field, Weather weather)
    {
        for(Location loc : field.adjacentLocationsIncludingSelf(weather.getStormCenter(), Weather.STORM_SCOPE)) {
            fieldView.drawMark(loc.getCol(), loc.getRow(), STORM_COLOR);
        }
    }

    /**
     * Return the registered color for the given class, or UNKNOWN_COLOR if none.
     */
    private Color getColor(Class<?> animalClass)
    {
        return colors.getOrDefault(animalClass, UNKNOWN_COLOR);
    }

    // -----------------------------------------------------------------------
    // Nested FieldView component
    // -----------------------------------------------------------------------

    /**
     * A custom Swing component that renders the field grid as colored rectangles.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth  = width;
            size = new Dimension(0, 0);
        }

        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth  * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        public void preparePaint()
        {
            if(!size.equals(getSize())) {
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) xScale = GRID_VIEW_SCALING_FACTOR;
                yScale = size.height / gridHeight;
                if(yScale < 1) yScale = GRID_VIEW_SCALING_FACTOR;
            }
        }

        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                } else {
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
