import java.util.List;
import java.util.Random;

/**
 * Models disease spread, infection progression, mortality, and immunity.
 *
 * @version 2022/03/02
 */
public class Disease
{
    // The possibility that an animal may be infected by a disease.
    private static final double INFECTION_RATE = 0.3;
    // The possibility an animal may die of a disease.
    private static final double MORTALITY_RATE = 0.05;
    // The steps an animal needs to withstand in order to get immunity.
    private static final int NUMBER_OF_STEPS_TO_WITHSTAND = 3;
    // The probability that the disease may occur.
    private static final double DISEASE_OCCURRENCE_PROBABILITY = 0.2;

    private static final Random rand = Randomizer.getRandom();

    // Identify if a disease has started to spread.
    private boolean isSpread;

    public Disease()
    {
        isSpread = false;
    }

    public boolean getIsSpread()
    {
        return isSpread;
    }

    public void setIsSpread(boolean isSpread)
    {
        this.isSpread = isSpread;
    }

    /**
     * Reset this disease to its initial non-spreading state.
     */
    public void reset()
    {
        isSpread = false;
    }

    /**
     * Create the source of infection if the disease starts this step.
     * @param creatures The current simulation creatures.
     * @param step The current simulation step.
     */
    protected void creationSourceOfInfection(List<Creature> creatures, int step)
    {
        if(isSpread || rand.nextDouble() > DISEASE_OCCURRENCE_PROBABILITY) {
            return;
        }

        for(Creature creature : creatures) {
            if(creature instanceof Animal && rand.nextDouble() <= INFECTION_RATE) {
                if(infect((Animal) creature, step)) {
                    isSpread = true;
                }
            }
        }
    }

    /**
     * Give an animal a chance to become infected after exposure.
     * @param animal The exposed animal.
     * @param step The current simulation step.
     */
    public void expose(Animal animal, int step)
    {
        if(!animal.isImmuneToDisease() && rand.nextDouble() <= INFECTION_RATE) {
            infect(animal, step);
        }
    }

    /**
     * Progress an animal's infection by applying mortality and immunity rules.
     * @param animal The animal whose infection should progress.
     * @param step The current simulation step.
     * @return true if the animal died from disease.
     */
    public boolean progressInfection(Animal animal, int step)
    {
        if(!animal.hasActiveInfection()) {
            return false;
        }

        if(rand.nextDouble() <= MORTALITY_RATE) {
            animal.dieOfDisease();
            return true;
        }

        if(step - animal.getInfectionStartStep() >= NUMBER_OF_STEPS_TO_WITHSTAND) {
            animal.grantDiseaseImmunity();
        }
        return false;
    }

    /**
     * Update whether this disease is still spreading among the current animals.
     * @param creatures The current simulation creatures.
     */
    public void updateSpreadState(List<Creature> creatures)
    {
        if(isSpread) {
            isSpread = hasActiveInfections(creatures);
        }
    }

    private boolean infect(Animal animal, int step)
    {
        return animal.infect(step);
    }

    private boolean hasActiveInfections(List<Creature> creatures)
    {
        for(Creature creature : creatures) {
            if(creature instanceof Animal && ((Animal) creature).hasActiveInfection()) {
                return true;
            }
        }
        return false;
    }
}
