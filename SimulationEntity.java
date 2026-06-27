import java.util.List;

/**
 * Abstract base class for all simulation entities that interact with the field
 * and perform actions each simulation step.
 *
 * Organism, Disease, and WaterSources all extend this class rather than
 * independently implementing the Actor interface.
 *
 * @version 2022.03.01
 */
public abstract class SimulationEntity implements Actor
{
    // The field in which this entity exists.
    private Field field;

    /**
     * @param field The field this entity is part of.
     */
    public SimulationEntity(Field field)
    {
        this.field = field;
    }

    /**
     * @return The field this entity belongs to.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Update the stored field reference. Used by subclasses when an entity
     * is removed from the simulation (e.g. setting to null on death).
     * @param f The new field reference.
     */
    protected void setField(Field f)
    {
        this.field = f;
    }
}
