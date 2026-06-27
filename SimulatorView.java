import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location
 * representing its contents.
 *
 * <p>Each entity provides its own display color via {@link Viewable#getDisplayColor()},
 * so this class no longer maintains a class-to-color map or depends on
 * specific entity types.
 *
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Color used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    private final String STEP_PREFIX = "Step: ";
    private final String DAYZONE_PREFIX = " Day Zone: ";
    private final String WEATHER_PREFIX = " Weather: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population, infoLabel;
    private FieldView fieldView;

    // A statistics object computing and storing simulation information.
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();

        setTitle("Predator/Prey Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        setLocation(60, 50);

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param weather The current weather condition.
     */
    public void showStatus(int step, Field<Entity> field, String weather)
    {
        String dayzone = "";

        if (!isVisible()) {
            setVisible(true);
        }

        switch (step % 4) {
            case 1: dayzone = "Morning ";   break;
            case 2: dayzone = "Afternoon "; break;
            case 3: dayzone = "Evening ";   break;
            case 0: dayzone = "Night ";     break;
        }

        stepLabel.setText(STEP_PREFIX + step + DAYZONE_PREFIX + dayzone + WEATHER_PREFIX + weather);
        stats.reset();

        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Entity entity = field.getObjectAt(row, col);
                if (entity != null) {
                    stats.incrementCount(entity);
                    fieldView.drawMark(col, row, entity.getDisplayColor());
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one animal species alive.
     */
    public boolean isViable(Field<Entity> field)
    {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 20;

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
            if (!size.equals(getSize())) {
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if (xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if (yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint one grid location on this field in a given color.
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
            if (fieldImage != null) {
                Dimension currentSize = getSize();
                if (size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                } else {
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
