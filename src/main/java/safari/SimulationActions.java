package safari;

/**
 * UI callbacks exposed by the controller.
 */
public interface SimulationActions
{
    void stepRequested();

    void resetRequested();

    void infectionRequested();
}
