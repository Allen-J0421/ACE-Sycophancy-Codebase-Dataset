package savannah.model;

import savannah.engine.SimulationContext;

/**
 * A simple model of a lion.
 * lions age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Lion extends Predator
{
    /**
     * Create a lion with the shared simulation context.
     * 
     * @param context Shared simulation context.
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Lion(SimulationContext context, boolean randomAge, Location location, boolean isInfected, boolean isImmune)
    {
        super(context, location, randomAge, isInfected, isImmune, SpeciesType.LION);
    }
}
