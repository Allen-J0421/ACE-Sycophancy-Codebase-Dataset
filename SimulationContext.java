/**
 * Immutable snapshot of the simulation state needed by creatures during one step.
 */
public class SimulationContext
{
    private final boolean atDayTime;
    private final double oxygenLevel;
    private final Disease disease;
    private final int step;

    public SimulationContext(boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        this.atDayTime = atDayTime;
        this.oxygenLevel = oxygenLevel;
        this.disease = disease;
        this.step = step;
    }

    public boolean isAtDayTime()
    {
        return atDayTime;
    }

    public double getOxygenLevel()
    {
        return oxygenLevel;
    }

    public Disease getDisease()
    {
        return disease;
    }

    public int getStep()
    {
        return step;
    }
}
