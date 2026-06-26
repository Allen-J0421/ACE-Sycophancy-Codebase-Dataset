package savannah.model;

import savannah.engine.SimulationContext;

/**
 * A simple model of a cheetah.
 * cheetahs age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Cheetah extends Predator
{
    /**
     * Create a cheetah with the shared simulation context.
     * 
     * @param context Shared simulation context.
     * @param randomAge If true, the cheetah will have random age and hunger level.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Cheetah(SimulationContext context, boolean randomAge, Location location, boolean isInfected, boolean isImmune)
    {
        super(context, location, randomAge, isInfected, isImmune, SpeciesType.CHEETAH);
    }
}
