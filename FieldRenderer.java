import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 * A JPanel that renders a Field grid as a scaled pixel image and manages
 * the per-species color map. Consumers call render() each simulation step;
 * the panel handles preparePaint, cell drawing, stats accumulation, and repaint.
 *
 * @version 2022.03.02
 */
class FieldRenderer extends JPanel
{
    static final Color EMPTY_COLOR   = Color.white;
    static final Color UNKNOWN_COLOR = Color.gray;

    private static final int GRID_VIEW_SCALING_FACTOR = 4;

    private final int gridWidth, gridHeight;
    private int xScale, yScale;
    private Dimension size;
    private Graphics g;
    private Image fieldImage;

    private final Map<Class, Color> colors = new LinkedHashMap<>();

    FieldRenderer(int height, int width)
    {
        gridHeight = height;
        gridWidth  = width;
        size = new Dimension(0, 0);
    }

    /** Set the display color for a species class. */
    public void setColor(Class cls, Color color)
    {
        colors.put(cls, color);
    }

    /** Return the display color for a species class, or UNKNOWN_COLOR if unset. */
    public Color getColor(Class cls)
    {
        Color col = colors.get(cls);
        return col != null ? col : UNKNOWN_COLOR;
    }

    /**
     * Synchronise the renderer color for one species to match a checkbox state.
     * Un-checking hides the species (white); re-checking restores the default color.
     */
    public void updateColorForClass(Class cls, boolean isChecked)
    {
        if (!isChecked) {
            setColor(cls, Color.white);
        }
        else if (getColor(cls).equals(Color.white) || getColor(cls).equals(UNKNOWN_COLOR)) {
            setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }
    }

    /**
     * Scan every cell of the field: accumulate counts into stats, draw each cell,
     * mark counting complete, then trigger a Swing repaint.
     *
     * @param field The field to scan.
     * @param stats The FieldStats object to accumulate counts into.
     */
    public void render(Field field, FieldStats stats)
    {
        preparePaint();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    stats.incrementCount(actor.getClass());
                    if(actor instanceof Organism && ((Organism) actor).isDiseased()) {
                        stats.incrementDiseasedCount();
                    }
                    drawMark(col, row, getColor(actor.getClass()));
                }
                else {
                    drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        repaint();
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(gridWidth  * GRID_VIEW_SCALING_FACTOR,
                             gridHeight * GRID_VIEW_SCALING_FACTOR);
    }

    @Override
    public void paintComponent(Graphics g)
    {
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

    private void preparePaint()
    {
        if(!size.equals(getSize())) {
            size = getSize();
            fieldImage = createImage(size.width, size.height);
            g = fieldImage.getGraphics();
            xScale = size.width / gridWidth;
            if(xScale < 1) xScale = GRID_VIEW_SCALING_FACTOR;
            yScale = size.height / gridHeight;
            if(yScale < 1) yScale = GRID_VIEW_SCALING_FACTOR;
        }
    }

    private void drawMark(int x, int y, Color color)
    {
        g.setColor(color);
        g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
    }
}
