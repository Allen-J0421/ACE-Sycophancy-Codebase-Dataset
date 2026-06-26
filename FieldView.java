import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * A reusable component for drawing the simulation field.
 *
 * @version 26/02/2022
 */
public class FieldView extends JPanel
{
    private static final int GRID_VIEW_SCALING_FACTOR = 6;

    private final int gridWidth;
    private final int gridHeight;

    private double xScale;
    private double yScale;
    private Dimension size;
    private Graphics canvasGraphics;
    private Image fieldImage;

    /**
     * Create a new field view component.
     */
    public FieldView(int height, int width)
    {
        gridHeight = height;
        gridWidth = width;
        size = new Dimension(0, 0);
    }

    /**
     * Tell the GUI manager how big we would like to be.
     *
     * @return Dimensions
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                             gridHeight * GRID_VIEW_SCALING_FACTOR);
    }

    /**
     * Prepare for a new round of drawing and recompute scale if needed.
     */
    public void beginFrame(Color backgroundColor)
    {
        setBackground(backgroundColor);

        Dimension currentSize = getSize();
        if (currentSize.width <= 0 || currentSize.height <= 0) {
            currentSize = getPreferredSize();
        }

        if(fieldImage == null || canvasGraphics == null || !size.equals(currentSize)) {
            size = currentSize;
            fieldImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            canvasGraphics = fieldImage.getGraphics();

            xScale = ((double) size.width) / gridWidth;
            if(xScale < 1.0) {
                xScale = GRID_VIEW_SCALING_FACTOR;
            }
            yScale = ((double) size.height) / gridHeight;
            if(yScale < 1.0) {
                yScale = GRID_VIEW_SCALING_FACTOR;
            }

            // Keep grid squares proportional.
            if(xScale > yScale) {
                xScale = yScale;
            }
            else if(xScale < yScale) {
                yScale = xScale;
            }
        }

        canvasGraphics.setColor(backgroundColor);
        canvasGraphics.fillRect(0, 0, size.width, size.height);
    }

    /**
     * Paint one grid location in a given color.
     *
     * @param x X-position to draw the mark
     * @param y Y-position to draw the mark
     * @param color The colour to draw the mark
     */
    public void drawCell(int x, int y, Color color)
    {
        canvasGraphics.setColor(color);
        canvasGraphics.fillRect((int) (x * xScale), (int) (y * yScale),
                                (int) (xScale - 1.0), (int) (yScale - 1.0));
    }

    /**
     * Finish drawing the current frame.
     */
    public void endFrame()
    {
        repaint();
    }

    /**
     * The field view component needs to be redisplayed. Copy the
     * internal image to screen.
     *
     * @param graphics Graphics
     */
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        if(fieldImage != null) 
        {
            Dimension currentSize = getSize();

            if(size.equals(currentSize)) 
            {
                graphics.drawImage(fieldImage, 0, 0, null);
            }
            else 
            {
                graphics.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }
}
