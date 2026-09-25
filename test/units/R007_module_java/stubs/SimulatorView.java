import java.awt.Color;

/**
 * Headless test stub that replaces the Swing-based SimulatorView (GUI-only
 * class; extends JFrame in the subject, which throws HeadlessException under
 * -Djava.awt.headless=true).  It reproduces exactly the public API that
 * Simulator uses at every iteration of this experiment:
 *   - SimulatorView(int height, int width)
 *   - setColor(Class, Color)
 *   - isViable(Field)
 *   - showStatus(int, Field, boolean, Weather, double)
 * All rendering is a no-op; isViable always answers true (the driver invokes
 * simulateOneStep() directly and never relies on the viability early-exit).
 * Installed with STUB_OVERRIDE=1 so the same stub is used for the baseline
 * and every iteration alike.
 */
public class SimulatorView
{
    public SimulatorView(int height, int width)
    {
    }

    public void setColor(Class animalClass, Color color)
    {
    }

    public boolean isViable(Field field)
    {
        return true;
    }

    public void showStatus(int step, Field field, boolean timeOfDay, Weather weather, double oxygenLevel)
    {
    }
}
