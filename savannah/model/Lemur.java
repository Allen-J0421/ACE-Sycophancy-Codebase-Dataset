package savannah.model;

import savannah.engine.SimulationContext;

/**
 * A simple model of a Lemur.
 * Lemurs age, move, breed, eat, and die.
 *
 * @version 25/02/2022
 */
public class Lemur extends Prey
{
    /**
     * Create a new Lemur with the shared simulation context.
     * 
     * @param context Shared simulation context.
     * @param randomAge If true, the lemur will have a random age.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Lemur(SimulationContext context, boolean randomAge, Location location, boolean isInfected, boolean isImmune)
    {
        super(context, location, randomAge, isInfected, isImmune, SpeciesType.LEMUR);
    }
}
