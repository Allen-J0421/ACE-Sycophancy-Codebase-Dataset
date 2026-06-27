/**
 * Strategy for formatting status text and computing viability.
 */
public interface StatusTextStrategy
{
    String formatInfoText(SimulationDisplayContext context);

    String formatPopulationText(Field field);

    boolean isViable(Field field);
}
