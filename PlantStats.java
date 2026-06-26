/**
 * Immutable configuration bundle for a plant species.
 * Centralises all species-specific constants so that each
 * Plant subclass only needs to provide a single static STATS instance
 * rather than setting protected fields directly.
 *
 * @version 2022.03.02
 */
public class PlantStats
{
    private final int numberOfStages;
    private final int stepsPerStage;

    public PlantStats(int numberOfStages, int stepsPerStage)
    {
        this.numberOfStages = numberOfStages;
        this.stepsPerStage = stepsPerStage;
    }

    public int getNumberOfStages() { return numberOfStages; }
    public int getStepsPerStage()  { return stepsPerStage; }
}
