import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/**
 * A graphical component that renders the simulation field.
 */
public class FieldView extends JPanel
{
    private static final int GRID_VIEW_SCALING_FACTOR = 6;

    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;
    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    // Colour used for objects that are infected by disease.
    private static final Color INFECTED_COLOR = Color.green;

    private final int gridWidth;
    private final int gridHeight;
    private int xScale;
    private int yScale;
    private Dimension size;
    private Graphics graphics;
    private Image fieldImage;

    // A map for storing colors for participants in the simulation.
    private final Map<Class, Color> colors;

    /**
     * Create a new FieldView component.
     */
    public FieldView(int height, int width)
    {
        gridHeight = height;
        gridWidth = width;
        size = new Dimension(0, 0);
        colors = new LinkedHashMap<>();
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
     * Render the current field contents.
     * @param field The field whose contents should be drawn.
     * @param weather The current weather state.
     * @param stats Statistics to update while traversing the field.
     */
    public void render(Field field, Weather weather, FieldStats stats)
    {
        preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object creature = field.getObjectAt(row, col);
                drawFieldLocation(col, row, creature, weather.getStormStart(), stats);
            }
        }

        if(weather.getStormStart()) {
            drawStormColor(field, weather);
        }

        repaint();
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
    private void preparePaint()
    {
        if(fieldImage == null || !size.equals(getSize())) {
            size = getSize();
            fieldImage = new BufferedImage(Math.max(1, size.width), Math.max(1, size.height),
                                           BufferedImage.TYPE_INT_RGB);
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
     * Draw a single field location and update population statistics.
     */
    private void drawFieldLocation(int col, int row, Object creature, boolean stormStarted, FieldStats stats)
    {
        if(creature == null) {
            drawMark(col, row, EMPTY_COLOR);
            return;
        }

        stats.incrementCount(creature.getClass());
        if(!stormStarted && creature instanceof Animal) {
            Animal animal = (Animal) creature;
            if(animal.getIsInfected() && !animal.getIsImmuned()) {
                drawMark(col, row, INFECTED_COLOR);
                return;
            }
        }

        drawMark(col, row, getColor(creature.getClass()));
    }

    /**
     * Draw the colour of the storm.
     */
    private void drawStormColor(Field field, Weather weather)
    {
        List<Location> stormLocation =
            field.adjacentLocationsIncludingSelf(weather.getRandomLocation(), weather.getStormScope());
        for(Location location : stormLocation) {
            drawMark(location.getCol(), location.getRow(), getColor(Weather.class));
        }
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color color = colors.get(animalClass);
        return color == null ? UNKNOWN_COLOR : color;
    }

    /**
     * Paint one grid location on this field in a given color.
     */
    private void drawMark(int x, int y, Color color)
    {
        graphics.setColor(color);
        graphics.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
    }

    /**
     * The field view component needs to be redisplayed. Copy the
     * internal image to screen.
     */
    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        if(fieldImage != null) {
            Dimension currentSize = getSize();
            if(size.equals(currentSize)) {
                graphics.drawImage(fieldImage, 0, 0, null);
            }
            else {
                // Rescale the previous image.
                graphics.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }
}
