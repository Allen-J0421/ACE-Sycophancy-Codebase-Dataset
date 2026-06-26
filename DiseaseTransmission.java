import java.util.*;

/**
 * Static utility for disease transmission between organisms.
 * Centralises all three transmission routes — proximity, foodborne, and sexual —
 * so that Animal does not need to own those mechanics.
 *
 * @version 2022.03.02
 */
public class DiseaseTransmission
{
    private static final Random rand = Randomizer.getRandom();

    private DiseaseTransmission() {}

    /**
     * Scan adjacent locations for proximity (non-contact) diseases and infect
     * the target if transmission is triggered.
     * @param target   The organism that may become infected.
     * @param adjacent The locations to scan.
     * @param field    The field used to look up occupants.
     */
    public static void scanForProximity(Organism target, List<Location> adjacent, Field field)
    {
        for (Location loc : adjacent) {
            Object obj = field.getObjectAt(loc);
            if (obj instanceof Organism) {
                Organism other = (Organism) obj;
                if (other.isDiseased()
                        && other.getDisease().getDiseaseType() != DiseaseType.CONTACT
                        && other.getDisease().getPropagationRate() <= rand.nextDouble()) {
                    target.setDisease(other.getDisease());
                    break;
                }
            }
        }
    }

    /**
     * Attempt foodborne disease transmission from prey to predator.
     * @param predator The organism eating the food.
     * @param prey     The organism being eaten.
     */
    public static void tryFoodborne(Organism predator, Organism prey)
    {
        if (prey.isDiseased()
                && prey.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && prey.getDisease().getPropagationRate() <= rand.nextDouble()) {
            predator.setDisease(prey.getDisease());
        }
    }

    /**
     * Attempt sexually transmitted disease transmission from mate to animal during breeding.
     * @param animal The potentially infected organism.
     * @param mate   The mate that may carry an STD.
     */
    public static void trySexual(Organism animal, Animal mate)
    {
        if (mate.isDiseased() && mate.getDisease().getDiseaseType() == DiseaseType.SEXUAL) {
            animal.setDisease(mate.getDisease());
        }
    }
}
