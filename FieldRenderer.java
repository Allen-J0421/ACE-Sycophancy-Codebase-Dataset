import java.awt.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates a Field's current state into pixels on a Swing canvas.
 * Owns the color map and the canvas component; updates population counts
 * during the same single field scan to avoid a second traversal.
 */
public class FieldRenderer
{
    private static final Color EMPTY_COLOR   = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final Map<Class, Color> colors;
    private final FieldView canvas;

    /**
     * Create a renderer for a field of the given dimensions.
     *
     * @param height Grid height in cells.
     * @param width  Grid width in cells.
     */
    public FieldRenderer(int height, int width)
    {
        colors = new LinkedHashMap<>();
        canvas = new FieldView(height, width);
    }

    /**
     * Register a display color for the given actor class.
     *
     * @param actorClass The class to associate with the color.
     * @param color      The color to display.
     */
    public void setColor(Class actorClass, Color color)
    {
        colors.put(actorClass, color);
    }

    /**
     * @return The Swing panel to embed in the window layout.
     */
    public JPanel getPanel() { return canvas; }

    /**
     * Scan the field, paint each cell, and update population counts — all in
     * one pass. Resets and finalises {@code stats} as part of the render.
     *
     * @param field The field to render.
     * @param stats The stats object to reset and repopulate during the scan.
     */
    public void render(Field field, PopulationStats stats)
    {
        stats.reset();
        canvas.preparePaint();

        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Object actor = field.getObjectAt(row, col);
                if (actor != null)
                {
                    stats.incrementCount(actor.getClass());
                    canvas.drawMark(col, row, colorFor(actor.getClass()));
                }
                else
                {
                    canvas.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        stats.countFinished();
        canvas.repaint();
    }

    private Color colorFor(Class actorClass)
    {
        Color color = colors.get(actorClass);
        return (color != null) ? color : UNKNOWN_COLOR;
    }

    // -------------------------------------------------------------------------
    // Canvas component
    // -------------------------------------------------------------------------

    private class FieldView extends JPanel
    {
        private static final int GRID_VIEW_SCALING_FACTOR = 6;

        private final int gridWidth;
        private final int gridHeight;
        private int xScale, yScale;
        private Dimension size;
        private Graphics g;
        private Image fieldImage;

        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth  = width;
            size = new Dimension(0, 0);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth  * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        public void preparePaint()
        {
            if (!size.equals(getSize()))
            {
                size = getSize();
                fieldImage = createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if (xScale < 1) xScale = GRID_VIEW_SCALING_FACTOR;

                yScale = size.height / gridHeight;
                if (yScale < 1) yScale = GRID_VIEW_SCALING_FACTOR;
            }
        }

        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            if (fieldImage != null)
            {
                Dimension currentSize = getSize();
                if (size.equals(currentSize))
                    g.drawImage(fieldImage, 0, 0, null);
                else
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }
}
