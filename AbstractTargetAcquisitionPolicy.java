import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Shared helper base for target-acquisition policies.
 */
public abstract class AbstractTargetAcquisitionPolicy implements TargetAcquisitionPolicy
{
    protected static final Random rand = Randomizer.getRandom();

    protected final Location findAdjacentTarget(Organism forager, Predicate<Object> matcher)
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
}
