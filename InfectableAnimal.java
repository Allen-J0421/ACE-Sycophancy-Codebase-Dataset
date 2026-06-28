import java.util.Iterator;
import java.util.List;

/**
 * Shared behavior for animals that can spread and receive infections.
 */
public abstract class InfectableAnimal extends Animal implements Infectable
{
    /**
     * Create a new infectable animal at a location in the field.
     *
     * @param randomAge If true, the animal will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param breedingAge The breeding age.
     * @param maxAge The maximum age.
     * @param breedingProbability The breeding probability.
     * @param maxLitterSize The maximum litter size.
     * @param isDiurnal Whether the animal is diurnal.
     * @param maxHealth The maximum health.
     * @param animalClass The concrete species type.
     * @param foodSources The food sources available to the species.
     */
    protected InfectableAnimal(boolean randomAge, Field field, Location location,
            int breedingAge, int maxAge, double breedingProbability,
            int maxLitterSize, boolean isDiurnal, int maxHealth,
            Class<? extends Animal> animalClass, java.util.HashSet<Class> foodSources)
    {
        super(randomAge, field, location, breedingAge, maxAge, breedingProbability,
                maxLitterSize, isDiurnal, maxHealth, animalClass, foodSources);
    }

    /**
     * Decides whether this animal will infect another eligible animal.
     */
    public void spreadDisease() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (object != null) {
                Organism organism = (Organism) object;
                if (organism instanceof Infectable && organism.isAlive() && getRand().nextDouble() <= SPREAD_PROBABILITY ) {
                    Animal target = (Animal) organism;
                    target.infect();
                }
            }
        }
    }
    
    /**
     * Makes the animal take extra damage if it is infected.
     */
    public void illness() {
        if (getIsInfected()) {
            incrementHealth();
        }
    }

    /**
     * Performs the infection-specific portion of a turn.
     *
     * @param newOrganisms A list to receive newborn animals.
     */
    protected void performInfectableAct(List<Organism> newOrganisms)
    {
        illness();
        if (isAlive()) {
            spreadDisease();
            performStandardAct(newOrganisms);
        }
    }
}
