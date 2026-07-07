import java.util.Random;

/**
 * Tracks the growth stage of a plant entity. increment() advances the stage and
 * returns whether growth occurred; the caller can react (e.g. increasing food value).
 * Starts at a random stage so newly created plants vary in maturity.
 *
 * @version 2022.03.02
 */
public class GrowthComponent
{
    private static final Random rand = Randomizer.getRandom();

    private int stageOfGrowth;
    private final int numberOfStages;
    private final int stepsPerStage;

    /**
     * @param numberOfStages Total number of growth stages.
     * @param stepsPerStage  Simulation steps between each stage advance (used by engine).
     */
    public GrowthComponent(int numberOfStages, int stepsPerStage)
    {
        this.numberOfStages = numberOfStages;
        this.stepsPerStage  = stepsPerStage;
        this.stageOfGrowth  = rand.nextInt(numberOfStages);
    }

    /**
     * Advance the growth stage by one, if not yet at full maturity.
     * @return true if the stage actually increased.
     */
    public boolean increment()
    {
        if (stageOfGrowth < numberOfStages) {
            stageOfGrowth++;
            return true;
        }
        return false;
    }

    public int getStage()         { return stageOfGrowth; }
    public int getStepsPerStage() { return stepsPerStage; }
}
