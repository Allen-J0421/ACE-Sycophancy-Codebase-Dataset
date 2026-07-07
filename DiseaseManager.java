import java.util.List;
import java.util.Random;

/**
 * Manages all disease-related logic for an Animal: random contraction,
 * contact/foodborne/sexual transmission, and per-step lethality checks.
 *
 * @version 2022.03.02
 */
public class DiseaseManager
{
    private static final double RANDOM_CONTRACTION_RATE = 0.002;
    private static final Random rand = Randomizer.getRandom();

    private final Animal animal;

    public DiseaseManager(Animal animal)
    {
        this.animal = animal;
    }

    /**
     * Randomly infects the animal with a new Disease at the base contraction rate.
     */
    public void processRandomContraction()
    {
        if (rand.nextDouble() <= RANDOM_CONTRACTION_RATE) {
            animal.setDisease(new Disease());
        }
    }

    /**
     * Checks adjacent locations for diseased organisms and potentially
     * transmits a non-contact disease to the animal.
     * @param adjacent The list of adjacent locations to scan.
     * @param field    The field containing the organisms.
     */
    public void checkContactSpread(List<Location> adjacent, Field field)
    {
        for (Location loc : adjacent) {
            Object obj = field.getObjectAt(loc);
            if (obj != null && !(obj instanceof Hunter)) {
                Organism organism = (Organism) obj;
                if (organism.isDiseased()
                        && organism.getDisease().getDiseaseType() != DiseaseType.CONTACT
                        && organism.getDisease().getPropagationRate() <= rand.nextDouble()) {
                    animal.setDisease(organism.getDisease());
                    break;
                }
            }
        }
    }

    /**
     * Potentially transmits a foodborne disease from eaten food to the animal.
     * @param food The organism being consumed.
     */
    public void checkFoodborneSpread(Organism food)
    {
        if (food.isDiseased()
                && food.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && food.getDisease().getPropagationRate() <= rand.nextDouble()) {
            animal.setDisease(food.getDisease());
        }
    }

    /**
     * Potentially transmits a sexually-transmitted disease from a mate.
     * @param mate The animal being bred with.
     */
    public void checkSexualSpread(Animal mate)
    {
        if (mate.isDiseased() && mate.getDisease().getDiseaseType() == DiseaseType.SEXUAL) {
            animal.setDisease(mate.getDisease());
        }
    }

    /**
     * Returns true if the animal's disease kills it this step.
     */
    public boolean isLethalStep()
    {
        return animal.isDiseased()
                && animal.getDisease().getLethalityRate() <= rand.nextDouble();
    }
}
