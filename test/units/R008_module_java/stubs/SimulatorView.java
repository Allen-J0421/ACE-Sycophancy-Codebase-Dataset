import java.awt.Color;
import javax.swing.JButton;

/**
 * Headless test stub that replaces the Swing-based SimulatorView (GUI-only
 * class; extends JFrame in the subject, which throws HeadlessException under
 * -Djava.awt.headless=true).  It reproduces exactly the public API that
 * Simulator uses at every iteration of this experiment:
 *   - SimulatorView(int height, int width, JButton[] buttons)
 *   - setColor(Class, Color, Color)
 *   - isViable(Field)
 *   - showStatus(Field)
 * All rendering is a no-op; isViable always answers true (the driver invokes
 * simulateOneStep() directly and never relies on the viability early-exit).
 * Installed with STUB_OVERRIDE=1 so the same stub is used for the baseline
 * and every iteration alike.
 */
public class SimulatorView
{
    public SimulatorView(int height, int width, JButton[] buttons)
    {
    }

    public void setColor(Class newClass, Color classColor, Color textColor)
    {
    }

    public boolean isViable(Field field)
    {
        return true;
    }

    public void showStatus(Field field)
    {
    }
}
