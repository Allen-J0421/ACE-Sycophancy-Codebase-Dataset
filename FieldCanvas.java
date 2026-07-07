import java.awt.*;
import javax.swing.*;

/**
 * A custom Swing panel that renders the simulation grid as a scaled bitmap.
 * Each grid cell is painted as a filled rectangle; colors are supplied by
 * the caller via {@link #drawMark(int, int, Color)}.
 */
public class FieldCanvas extends JPanel {
    private static final int GRID_VIEW_SCALING_FACTOR = 6;

    private final int gridWidth;
    private final int gridHeight;
    private int xScale;
    private int yScale;
    private Dimension size;
    private Graphics g;
    private Image fieldImage;

    /**
     * Create a new FieldCanvas for a grid of the given dimensions.
     * @param height Number of rows in the simulation grid.
     * @param width  Number of columns in the simulation grid.
     */
    public FieldCanvas(int height, int width) {
        gridHeight = height;
        gridWidth = width;
        size = new Dimension(0, 0);
    }

    /**
     * Tell the GUI manager how big we would like to be.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                             gridHeight * GRID_VIEW_SCALING_FACTOR);
    }

    /**
     * Prepare for a new round of painting. Recomputes scaling if the
     * component has been resized since the last paint.
     */
    public void preparePaint() {
        if(!size.equals(getSize())) {
            size = getSize();
            fieldImage = this.createImage(size.width, size.height);
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
     * Paint one grid cell at (x, y) in the given color.
     */
    public void drawMark(int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
    }

    /**
     * Copy the internal off-screen image to the screen.
     */
    @Override
    public void paintComponent(Graphics g) {
        if(fieldImage != null) {
            Dimension currentSize = getSize();
            if(size.equals(currentSize)) {
                g.drawImage(fieldImage, 0, 0, null);
            }
            else {
                g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }
}
