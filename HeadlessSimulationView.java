import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Headless display implementation for tests and non-GUI execution.
 *
 * @version 2022.03.02
 */
public class HeadlessSimulationView implements SimulationDisplay
{
    private final Map<Species, DisplayColor> colors;
    private final FieldStats stats;
    private String infoText;
    private String populationText;
    private int step;

    public HeadlessSimulationView(SimulationRulesEngine rules)
    {
        colors = new LinkedHashMap<>();
        stats = new FieldStats(rules);
        infoText = "";
        populationText = "";
    }

    public void setColor(Species species, DisplayColor color)
    {
        colors.put(species, color);
    }

    public void setInfoText(String text)
    {
        infoText = text;
    }

    public void showStatus(int step, FieldSnapshot snapshot)
    {
        this.step = step;
        populationText = stats.getPopulationDetails(snapshot, step);
    }

    public boolean isViable(FieldSnapshot snapshot)
    {
        return stats.isViable(snapshot);
    }

    public String getInfoText()
    {
        return infoText;
    }

    public String getPopulationText()
    {
        return populationText;
    }

    public int getStep()
    {
        return step;
    }
}
