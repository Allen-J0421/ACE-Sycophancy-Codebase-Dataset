/**
 * Default strategy for formatting simulator status text and computing viability.
 */
public class DefaultStatusTextStrategy implements StatusTextStrategy
{
    private static final String DAYTIME_TEXT = "daytime";
    private static final String NIGHT_TEXT = "night";

    private final FieldStats stats;

    public DefaultStatusTextStrategy()
    {
        stats = new FieldStats();
    }

    @Override
    public String formatInfoText(SimulationContext context)
    {
        return "It is: " + (context.isTimeOfDay() ? DAYTIME_TEXT : NIGHT_TEXT)
            + "        Oxygen Level: " + (int)(context.getOxygenLevel() * 100) + "%"
            + "        Storm: " + (context.getWeather().getStormStart() ? "exists" : "subsides");
    }

    @Override
    public String formatPopulationText(SimulationContext context)
    {
        return stats.getPopulationDetails(context.getField());
    }

    @Override
    public boolean isViable(SimulationContext context)
    {
        return stats.isViable(context.getField());
    }
}
