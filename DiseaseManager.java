import java.util.List;
import java.util.Random;

/**
 * Manages all disease-related logic for a DiseaseAware host: random
 * contraction, contact/foodborne/sexual transmission, and per-step
 * lethality checks. Operates on the DiseaseAware interface so it is
 * not coupled to any specific Animal or Organism subclass.
 *
 * @version 2022.03.02
 */
public class DiseaseManager
{
    private static final double RANDOM_CONTRACTION_RATE = 0.002;
    private static final Random rand = Randomizer.getRandom();

    private final DiseaseAware host;

    public DiseaseManager(DiseaseAware host)
    {
        this.host = host;
    }

    /**
     * Randomly infects the host with a new Disease at the base contraction rate.
     */
    public void processRandomContraction()
    {
        if (rand.nextDouble() <= RANDOM_CONTRACTION_RATE) {
            host.setDisease(new Disease());
        }
    }

    /**
     * Checks adjacent locations for diseased entities and potentially
     * transmits a non-contact disease to the host.
     * Uses instanceof DiseaseAware to skip non-disease-carrying actors (e.g. Hunter).
     * @param adjacent The list of adjacent locations to scan.
     * @param field    The field containing the entities.
     */
    public void checkContactSpread(List<Location> adjacent, Field field)
    {
        for (Location loc : adjacent) {
            Object obj = field.getObjectAt(loc);
            if (obj instanceof DiseaseAware) {
                DiseaseAware neighbour = (DiseaseAware) obj;
                if (neighbour.isDiseased()
                        && neighbour.getDisease().getDiseaseType() != DiseaseType.CONTACT
                        && neighbour.getDisease().getPropagationRate() <= rand.nextDouble()) {
                    host.setDisease(neighbour.getDisease());
                    break;
                }
            }
        }
    }

    /**
     * Potentially transmits a foodborne disease from consumed food to the host.
     * @param food The entity being consumed.
     */
    public void checkFoodborneSpread(DiseaseAware food)
    {
        if (food.isDiseased()
                && food.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && food.getDisease().getPropagationRate() <= rand.nextDouble()) {
            host.setDisease(food.getDisease());
        }
    }

    /**
     * Potentially transmits a sexually-transmitted disease from a mate.
     * @param mate The entity being bred with.
     */
    public void checkSexualSpread(DiseaseAware mate)
    {
        if (mate.isDiseased() && mate.getDisease().getDiseaseType() == DiseaseType.SEXUAL) {
            host.setDisease(mate.getDisease());
        }
    }

    /**
     * Returns true if the host's disease kills it this step.
     */
    public boolean isLethalStep()
    {
        return host.isDiseased()
                && host.getDisease().getLethalityRate() <= rand.nextDouble();
    }
}
