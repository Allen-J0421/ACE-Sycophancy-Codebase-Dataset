import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Shared helper base for target-acquisition policies.
 */
public abstract class AbstractTargetAcquisitionPolicy implements TargetAcquisitionPolicy
{
    protected static final Random rand = Randomizer.getRandom();

    protected final Location findAdjacentTarget(MobileForager forager, Predicate<Object> matcher)
    {
        return AdjacentTargetSearch.findMatchingLocation(forager.getField(), forager.getLocation(), matcher);
    }

    protected final void contractDiseaseFromAdjacentOrganisms(Animal consumer, Predicate<Object> infectableFilter)
    {
        Field field = consumer.getField();
        List<Location> adjacent = field.adjacentLocations(consumer.getLocation());
        for(Location loc : adjacent){
            Object occupant = field.getObjectAt(loc);
            if(occupant != null && infectableFilter.test(occupant)) {
                Organism organism = (Organism) occupant;
                if (organism.isDiseased()
                        && organism.getDisease().getDiseaseType() != DiseaseType.CONTACT
                        && organism.getDisease().getPropagationRate() <= rand.nextDouble()){
                    consumer.setDisease(organism.getDisease());
                    break;
                }
            }
        }
    }

    protected final void consumePrey(Animal consumer, Organism prey)
    {
        if (prey.isDiseased()
                && prey.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && prey.getDisease().getPropagationRate() <= rand.nextDouble())
        {
            consumer.setDisease(prey.getDisease());
        }
        if(prey.isAlive())
        {
            prey.setDead();
            int newFoodLevel = consumer.foodLevel + ((Edible) prey).getFoodValue();
            consumer.foodLevel = Math.min(newFoodLevel, consumer.MAX_FOOD_LEVEL());
        }
    }

    protected final void consumePrey(Organism prey)
    {
        if(prey.isAlive()) {
            prey.setDead();
        }
    }
}
