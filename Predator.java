/**
 * Base class for predatory animals.
 * Wires PredatorMovement and a CarnivoreDiet (parameterised by catch
 * probability) into the Animal; concrete species supply reproduction
 * parameters and species-level constants.
 *
 * @version 25/02/2022
 */
public abstract class Predator extends Animal
{
    /**
     * Create a new predator.
     *
     * @param field                  The field currently occupied.
     * @param location               The location within the field.
     * @param isInfected             Initial infection state.
     * @param isImmune               Initial immune state.
     * @param preyCatchingProbability Probability of successfully catching adjacent prey.
     * @param reproduction           Species-specific breeding parameters.
     */
    protected Predator(Field field, Location location, boolean isInfected, boolean isImmune,
                       double preyCatchingProbability, ReproductiveBehaviour reproduction)
    {
        super(field, location, isInfected, isImmune,
              new PredatorMovement(),
              new CarnivoreDiet(preyCatchingProbability),
              reproduction);
    }
}
