import java.util.List;
import java.util.Iterator;

/**
 * Models a disease outbreak: randomly infects a fraction of the animal population,
 * then spreads animal-to-animal via proximity during normal movement.
 *
 * @version 2022/03/02
 */
public class Disease
{
    // The probability that a nearby animal may be infected.
    public static final double INFECTION_RATE = 0.3;
    // The probability an infected animal dies each step.
    public static final double MORTALITY_RATE = 0.05;
    // Steps an animal must remain infected before gaining immunity.
    public static final int NUMBER_OF_STEP_TO_WITHSTAND = 3;

    // Probability that a new outbreak begins on any given step (only when none is active).
    private static final double DISEASE_OCCURENCE_PROBABILITY = 0.2;

    // True while at least one infected, non-immune animal exists.
    private boolean isSpread;

    public Disease()
    {
        isSpread = false;
    }

    public boolean getIsSpread()           { return isSpread; }
    public void    setIsSpread(boolean bl) { isSpread = bl;   }

    /**
     * Potentially trigger a new outbreak. If no disease is currently spreading and the
     * random threshold is met, infect each animal independently at INFECTION_RATE probability.
     * @param creatures All creatures currently in the simulation.
     * @param step      The current simulation step (recorded as each animal's infection start).
     */
    protected void creationSourceOfInfection(List<Creature> creatures, int step)
    {
        if(getIsSpread() || Randomizer.getRandom().nextDouble() > DISEASE_OCCURENCE_PROBABILITY) {
            return;
        }
        for(Creature creature : creatures) {
            if(creature instanceof Animal) {
                Animal animal = (Animal) creature;
                if(Randomizer.getRandom().nextDouble() <= INFECTION_RATE) {
                    animal.setIsInfected(true);
                    animal.infectionStartStep = step;
                    setIsSpread(true);
                }
            }
        }
    }
}
