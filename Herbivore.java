/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A prey animal that feeds on plants.
 *
 * @version 2022.03.02
 */
public abstract class Herbivore extends Prey {

    /**
     * Constructor for a herbivore in the simulation.
     *
     * @param foodValue The value of food of this herbivore.
     * @param randomAge Whether we assign this herbivore a random age or not.
     * @param field The field in which this herbivore resides.
     * @param location The location in which this herbivore is spawned into.
     */
    public Herbivore(HerbivoreAttributes attributes, boolean randomAge, Field field,
                     Location location, OrganismFactory offspringFactory) {
        super(attributes.getInitialFoodValue(), attributes, randomAge, field, location, offspringFactory);
    }

    @Override
    public Location findFood() {
        return findPlantFood();
    }
}
