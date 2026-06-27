/**
 * Immutable snapshot of simulation state emitted after each step.
 * Passed to registered SimulationObservers so they can update without
 * holding a direct reference to the Simulator.
 *
 * @version 2022.03.01
 */
public class SimulationEvent
{
    private final int step;
    private final Field field;
    private final boolean nightTime;

    public SimulationEvent(int step, Field field, boolean nightTime)
    {
        this.step = step;
        this.field = field;
        this.nightTime = nightTime;
    }

    public int getStep()      { return step; }
    public Field getField()   { return field; }
    public boolean isNightTime() { return nightTime; }
}
