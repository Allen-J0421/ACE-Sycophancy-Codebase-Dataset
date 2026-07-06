import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Swing panel that renders a Field as a grid of colored rectangles.
 * Owns the species-to-color registry, the off-screen image buffer, and
 * the scale computation. The calling layer supplies a Field and this
 * class handles all canvas work.
 *
 * @version 2022.03.02
 */
public class GridRenderer extends JPanel
{
    // Color painted into cells that contain no organism.
    private static final Color EMPTY_COLOR = Color.white;
    // Fallback color for species with no registered color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    // Pixels per grid cell at the default (pre-resize) scale.
    private static final int GRID_VIEW_SCALING_FACTOR = 6;

    private final int gridWidth;
    private final int gridHeight;

    // Off-screen image buffer for flicker-free rendering.
    private Image fieldImage;
    // Graphics context for the off-screen buffer.
    private Graphics bufferGraphics;
    // Last known component size — used to detect resizes.
    private Dimension size;
    // Current cell-to-pixel scale factors.
    private int xScale, yScale;

    // Species-class → display color registry.
    private final Map<Class, Color> colors;

    /**
     * Create a renderer sized for a grid of the given dimensions.
     *
     * @param height Number of rows in the grid.
     * @param width  Number of columns in the grid.
     */
    public GridRenderer(int height, int width)
    {
        this.gridHeight = height;
        this.gridWidth  = width;
        this.size   = new Dimension(0, 0);
        this.colors = new LinkedHashMap<>();
    }

    /**
     * Register the display color for a species class.
     *
     * @param speciesClass The runtime class of the species.
     * @param color        The color to use when painting cells of that species.
     */
    public void setColor(Class speciesClass, Color color)
    {
        colors.put(speciesClass, color);
    }

    /**
     * Render the current state of the field onto the canvas.
     * Prepares the off-screen buffer, paints every cell, then schedules
     * a Swing repaint to copy the buffer to the screen.
     *
     * @param field The field whose contents are to be painted.
     */
    public void render(Field field)
    {
        preparePaint();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object occupant = field.getObjectAt(row, col);
                Color cellColor = (occupant != null) ? getColor(occupant.getClass()) : EMPTY_COLOR;
                drawCell(col, row, cellColor);
            }
        }
        repaint();
    }

    // -----------------------------------------------------------------------
    // JPanel overrides
    // -----------------------------------------------------------------------

    /**
     * Report the preferred canvas size to the Swing layout manager.
     */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                             gridHeight * GRID_VIEW_SCALING_FACTOR);
    }

    /**
     * Copy the off-screen buffer to the screen, rescaling if the component
     * has been resized since the last render.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        if (fieldImage != null) {
            Dimension currentSize = getSize();
            if (size.equals(currentSize)) {
                g.drawImage(fieldImage, 0, 0, null);
            }
            else {
                // Rescale the previous image to fit the new component size.
                g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Look up the registered color for a species, falling back to
     * {@link #UNKNOWN_COLOR} if none has been registered.
     */
    private Color getColor(Class speciesClass)
    {
        Color col = colors.get(speciesClass);
        return (col != null) ? col : UNKNOWN_COLOR;
    }

    /**
     * Ensure the off-screen buffer and scale factors match the current
     * component size. Reallocates the buffer on first call or after a resize.
     */
    private void preparePaint()
    {
        if (!size.equals(getSize())) {
            size = getSize();
            fieldImage    = createImage(size.width, size.height);
            bufferGraphics = fieldImage.getGraphics();

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
     * Fill one grid cell in the off-screen buffer.
     *
     * @param col   Column index of the cell (x direction).
     * @param row   Row index of the cell (y direction).
     * @param color The color to paint.
     */
    private void drawCell(int col, int row, Color color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fillRect(col * xScale, row * yScale, xScale - 1, yScale - 1);
    }
}
