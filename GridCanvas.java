import java.awt.Color;

/**
 * Abstraction for a drawable simulation grid surface.
 */
public interface GridCanvas
{
    void preparePaint();

    void drawMark(int x, int y, Color color);

    void repaint();
}
