import java.awt.GraphicsEnvironment;

/**
 * Default simulation view facade that selects a Swing or headless display.
 * 
 * @version 2022.03.02
 */
public class SimulatorView implements SimulationDisplay
{
    private final SimulationDisplay delegate;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, SimulationRulesEngine rules)
    {
        if(GraphicsEnvironment.isHeadless()) {
            delegate = new HeadlessSimulationView(rules);
        }
        else {
            delegate = new SwingSimulationView(height, width, rules);
        }
    }
    
    public void setColor(Species species, DisplayColor color)
    {
        delegate.setColor(species, color);
    }

    public void setInfoText(String text)
    {
        delegate.setInfoText(text);
    }

    public void showStatus(int step, FieldSnapshot snapshot)
    {
        delegate.showStatus(step, snapshot);
    }

    public boolean isViable(FieldSnapshot snapshot)
    {
        return delegate.isViable(snapshot);
    }
}
