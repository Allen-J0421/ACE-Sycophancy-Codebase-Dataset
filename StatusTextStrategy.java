/**
 * Strategy for formatting status text and computing viability.
 */
public interface StatusTextStrategy
{
    String formatInfoText(SimulationContext context);

    String formatPopulationText(SimulationContext context);

    boolean isViable(SimulationContext context);
}
