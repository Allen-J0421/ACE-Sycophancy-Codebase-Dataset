/**
 * Display abstraction for simulation status updates.
 *
 * @version 2022.03.02
 */
public interface SimulationDisplay
{
    void setColor(Species species, DisplayColor color);

    void setInfoText(String text);

    void showStatus(int step, FieldSnapshot snapshot);

    boolean isViable(FieldSnapshot snapshot);
}
