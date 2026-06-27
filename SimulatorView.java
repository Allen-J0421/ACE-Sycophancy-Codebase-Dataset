import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2016.02.29
 */
@SuppressWarnings({"serial", "this-escape"})
public final class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private static final String STEP_PREFIX = "Step: ";
    private static final String POPULATION_PREFIX = "Population: ";
    private final JLabel stepLabel;
    private final JLabel population;
    private final JLabel infoLabel;
    private final FieldView fieldView;
    
    private boolean night;
    
    // A map for storing colors for participants in the simulation
    private final Map<Class<?>, Color> colors = new LinkedHashMap<>();

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        setTitle("Animal Ecosystem Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
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
     * Define a color to be used for a given class of living being.
     * @param beingClass The living being's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> beingClass, Color color)
    {
        colors.put(beingClass, color);
    }

    /**
     * Set whether the simulation is currently showing night-time conditions.
     */
    public void setNight(boolean night)
    {
        this.night = night;
        infoLabel.setText(night ? "Night" : "Day");
    }

    /**
     * @return The color to be used for a given class of living being.
     */
    private Color getColor(Class<?> beingClass)
    {
        return colors.getOrDefault(beingClass, UNKNOWN_COLOR);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                LivingBeing being = field.getLivingBeingAt(row, col);
                if(being != null) {
                    fieldView.drawMark(col, row, getColor(being.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, night ? Color.GRAY : EMPTY_COLOR);
                }
            }
        }

        population.setText(POPULATION_PREFIX + FieldStats.getPopulationDetails(field));
        fieldView.repaint();
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private static final class FieldView extends JPanel
    {
        private static final int GRID_VIEW_SCALING_FACTOR = 6;

        private final int gridWidth;
        private final int gridHeight;
        private int xScale, yScale;
        private Dimension size = new Dimension(0, 0);
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        private FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        @Override
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
                fieldImage = createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = Math.max(size.width / gridWidth, GRID_VIEW_SCALING_FACTOR);
                yScale = Math.max(size.height / gridHeight, GRID_VIEW_SCALING_FACTOR);
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
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
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
