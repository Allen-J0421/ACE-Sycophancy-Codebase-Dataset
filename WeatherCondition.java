/**
 * Represents a single weather condition that can be applied to simulation actors.
 *
 * Subclasses encapsulate the per-actor effect logic by overriding applyEffect().
 * Weather drives the shared iteration loop; the condition handles only what
 * changes with each weather type.
 *
 * @version 2022.03.01
 */
public abstract class WeatherCondition
{
    private final int duration;
    private final boolean generatesWater;

    protected WeatherCondition(int duration, boolean generatesWater)
    {
        this.duration = duration;
        this.generatesWater = generatesWater;
    }

    /**
     * Apply this condition's effect to a single actor.
     * @param actor The actor to affect.
     */
    public abstract void applyEffect(Actor actor);

    /** @return How many steps this condition lasts. */
    public int getDuration()     { return duration; }

    /** @return Whether this condition triggers new water source generation. */
    public boolean generatesWater() { return generatesWater; }
}
