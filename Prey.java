/**
 * Base class for prey animals.
 * Wires PreyMovement and HerbivoreDiet into the Animal; concrete species
 * supply reproduction parameters and species-level constants.
 *
 * @version 26/02/2022
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey animal.
     *
     * @param field        The field currently occupied.
     * @param location     The location within the field.
     * @param infected     Initial infection state.
     * @param immune       Initial immune state.
     * @param reproduction Species-specific breeding parameters.
     */
    protected Prey(Field field, Location location, boolean infected, boolean immune,
                   ReproductiveBehaviour reproduction)
    {
        super(field, location, infected, immune,
              new PreyMovement(),
              new HerbivoreDiet(),
              reproduction);
    }
}
