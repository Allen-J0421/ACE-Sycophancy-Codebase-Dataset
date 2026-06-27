import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Shared disease behaviour for animals that can both catch and spread infection.
 *
 * @version 2022.03.02
 */
public abstract class InfectiousAnimal extends Animal implements Infectable
{
    public InfectiousAnimal(boolean randomAge, Field field, Location location,
                            Class<? extends Animal> speciesClass,
                            Set<Class<?>> foodSources,
                            Set<Class<?>> killable)
    {
        super(randomAge, field, location, speciesClass, foodSources, killable);
    }

    /**
     * Infectious animals lose an extra health point when sick and may then
     * spread the disease to nearby infectable organisms.
     */
    protected void applyStepEffects()
    {
        illness();
        if(isAlive()) {
            spreadDisease();
        }
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
            Object object = field.getObjectAt(where);
            if (object != null) {
                Organism organism = (Organism) object;
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
