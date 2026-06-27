import java.util.Iterator;
import java.util.List;
/**
 * Shared disease behaviour for animals that can both catch and spread infection.
 *
 * @version 2022.03.02
 */
public abstract class InfectiousAnimal extends Animal implements Infectable
{
    public InfectiousAnimal(boolean randomAge, Field field, Location location,
                            AnimalAttributes attributes)
    {
        super(randomAge, field, location, attributes);
    }

    /**
     * Decides whether this animal will infect another eligible animal.
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Organism organism = (Organism) field.getOccupantAt(where);
            if (organism != null) {
                if (organism instanceof Infectable && organism.isAlive()
                    && getRand().nextDouble() <= SPREAD_PROBABILITY) {
                    Animal target = (Animal) organism;
                    target.infect();
                }
            }
        }
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
}
