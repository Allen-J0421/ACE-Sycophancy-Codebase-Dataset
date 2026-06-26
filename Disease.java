import java.util.List;

/**
 * Models a disease outbreak: randomly infects a fraction of the animal population,
 * then spreads animal-to-animal via proximity during normal movement.
 * Manages its own spread lifecycle — Simulator does not need to inspect or set state directly.
 *
 * @version 2022/03/02
 */
public class Disease
{
    // The probability that a nearby animal may be infected via contact.
    public static final double INFECTION_RATE = 0.3;
    // The probability that an infected animal dies each step.
    public static final double MORTALITY_RATE = 0.05;
    // Steps an infected animal must survive before gaining immunity.
    public static final int STEPS_TO_IMMUNITY = 3;

    // Probability that a new outbreak begins on any given step (only when none is active).
    private static final double OUTBREAK_PROBABILITY = 0.2;

    // True while at least one infected, non-immune animal exists.
    private boolean isSpread;

    public Disease()
    {
        isSpread = false;
    }

    /** Reset disease state; call when the simulation resets between runs. */
    public void reset()
    {
        isSpread = false;
    }

    /**
     * Potentially trigger a new outbreak. If no disease is currently spreading and the
     * random threshold is met, infect each animal independently at INFECTION_RATE probability.
     * @param creatures All creatures currently in the simulation.
     * @param step      The current simulation step (recorded as each animal's infection start).
     */
    public void tryStartOutbreak(List<Creature> creatures, int step)
    {
        if(isSpread || Randomizer.getRandom().nextDouble() > OUTBREAK_PROBABILITY) {
            return;
        }
        for(Creature creature : creatures) {
            if(creature instanceof Animal) {
                Animal animal = (Animal) creature;
                if(Randomizer.getRandom().nextDouble() <= INFECTION_RATE) {
                    animal.infect(step);
                    isSpread = true;
                }
            }
        }
    }

    /**
     * Check whether the current outbreak has run its course.
     * The disease is considered over when no infected, non-immune animal remains.
     * Should be called once per step after all creatures have acted.
     * @param creatures All creatures currently in the simulation.
     */
    public void updateSpreadState(List<Creature> creatures)
    {
        if(!isSpread) return;
        for(Creature creature : creatures) {
            if(creature instanceof Animal) {
                Animal ani = (Animal) creature;
                if(ani.isInfected() && !ani.isImmune()) {
                    return; // at least one infected, non-immune animal remains
                }
            }
        }
        isSpread = false;
    }
}
