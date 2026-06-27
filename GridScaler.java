import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Translates grid cell coordinates into screen pixel coordinates.
 *
 * The scaler is initialised with the fixed grid dimensions and a default
 * scale factor used before the component is first laid out. When the
 * component changes size, call {@link #update(Dimension)} to recompute the
 * scale; all subsequent {@link #toCellBounds} calls will use the new values.
 */
class GridScaler
{
    private static final int DEFAULT_SCALE = 6;

    private final int gridWidth;
    private final int gridHeight;
    private int xScale = DEFAULT_SCALE;
    private int yScale = DEFAULT_SCALE;

    /**
     * @param gridWidth  number of grid columns
     * @param gridHeight number of grid rows
     */
    GridScaler(int gridWidth, int gridHeight)
    {
        this.gridWidth  = gridWidth;
        this.gridHeight = gridHeight;
    }

    /**
     * Recompute scale factors from the current pixel size of the component.
     * Falls back to {@code DEFAULT_SCALE} if the component is too small.
     * @param pixelSize the current pixel dimensions of the drawing area
     */
    void update(Dimension pixelSize)
    {
        xScale = pixelSize.width  / gridWidth;
        if (xScale < 1) xScale = DEFAULT_SCALE;

        yScale = pixelSize.height / gridHeight;
        if (yScale < 1) yScale = DEFAULT_SCALE;
    }

    /**
     * Return the pixel bounding rectangle for the cell at grid position
     * ({@code gridCol}, {@code gridRow}).
     * @param gridCol column index in the grid
     * @param gridRow row index in the grid
     * @return pixel rectangle suitable for both {@code drawRect} and
     *         {@code fillRect}
     */
    Rectangle toCellBounds(int gridCol, int gridRow)
    {
        return new Rectangle(gridCol * xScale, gridRow * yScale,
                             xScale - 1,       yScale - 1);
    }

    /**
     * Return the preferred pixel size for the full grid, using the default
     * scale factor.
     * @return preferred component size
     */
    Dimension getPreferredSize()
    {
        return new Dimension(gridWidth * DEFAULT_SCALE, gridHeight * DEFAULT_SCALE);
    }
}
