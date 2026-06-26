import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Graphical component that renders the simulation field.
 */
public class FieldView extends JPanel
{
    private static final int GRID_VIEW_SCALING_FACTOR = 4;

    private final int gridWidth;
    private final int gridHeight;
    private int xScale;
    private int yScale;
    private Dimension size;
    private Graphics graphics;
    private Image fieldImage;

    public FieldView(int height, int width)
    {
        gridHeight = height;
        gridWidth = width;
        size = new Dimension(0, 0);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                             gridHeight * GRID_VIEW_SCALING_FACTOR);
    }

    public void preparePaint()
    {
        if(!size.equals(getSize())) {
            size = getSize();
            fieldImage = createImage(size.width, size.height);
            graphics = fieldImage.getGraphics();

            xScale = Math.max(size.width / gridWidth, GRID_VIEW_SCALING_FACTOR);
            yScale = Math.max(size.height / gridHeight, GRID_VIEW_SCALING_FACTOR);
        }
    }

    public void drawMark(int x, int y, Color color)
    {
        graphics.setColor(color);
        graphics.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
    }

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
                graphics.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
    }
}
