import java.util.List;

/**
 * An animal that can catch and spread a disease. This intermediate class holds
 * the infection behavior (suffering illness and infecting neighbours) that was
 * previously copy-pasted into each infectable animal, and wires it into the
 * standard life cycle defined in {@link Animal#act}.
 *
 * Concrete infectable animals (e.g. Bear, Sloth) extend this class instead of
 * {@link Animal} directly.
 *
 * @version 2022.03.02
 */
public abstract class InfectableAnimal extends Animal implements Infectable
{
    /**
     * Create a new infectable animal at location in field.
     *
     * @param randomAge If true, the animal will have a random age and health.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public InfectableAnimal(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * While alive, an infectable animal suffers the effects of illness each step.
     */
    @Override
    protected void applyIllness()
    {
        illness();
    }

    /**
     * While alive, an infectable animal may spread disease to its neighbours.
     */
    @Override
    protected void attemptSpreadDisease()
    {
        spreadDisease();
    }

    /**
     * Makes the animal take extra damage if it is infected.
     */
    public void illness()
    {
        if (getIsInfected()) {
            incrementHealth();
        }
    }

    /**
     * Decides whether this animal will infect another eligible (infectable) animal
     * in an adjacent location.
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object object = field.getObjectAt(where);
            if (object != null) {
                Organism organism = (Organism) object;
                if (organism instanceof Infectable && organism.isAlive() && getRand().nextDouble() <= SPREAD_PROBABILITY) {
                    Animal target = (Animal) organism;
                    target.infect();
                }
            }
        }
    }
}
