import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service that manages the disease lifecycle for a simulation run. Owns the
 * spread state and both spread-related operations, while SimulationEngine
 * retains control over when each is invoked.
 *
 * @version 2022/03/02
 */
public class DiseaseManager
{
    // Probability that a disease outbreak begins on any given step.
    private static final double DISEASE_OCCURENCE_PROBABILITY = 0.2;

    // The Disease parameter object passed to Animal methods.
    private final Disease params;
    // Whether the disease has started spreading through the population.
    private boolean isSpread;

    public DiseaseManager()
    {
        params = new Disease();
        isSpread = false;
    }

    /** Returns the disease parameters consumed by Animal infection methods. */
    public Disease getParams() { return params; }

    public boolean getIsSpread() { return isSpread; }
    public void setIsSpread(boolean bl) { isSpread = bl; }

    /**
     * Attempt to start an initial disease outbreak if none has occurred yet.
     * Each animal in the population has a 30% chance of becoming patient zero.
     * Called by SimulationEngine at the start of each step.
     *
     * @param creatures The current creature population.
     * @param step      The current simulation step (recorded as each animal's infection start).
     */
    public void trySpread(List<Creature> creatures, int step)
    {
        if (!isSpread && Randomizer.getRandom().nextDouble() <= DISEASE_OCCURENCE_PROBABILITY) {
            ArrayList<Animal> animals = new ArrayList<>();
            Iterator<Creature> it = creatures.iterator();
            while (it.hasNext()) {
                Creature c = it.next();
                if (c instanceof Animal) {
                    animals.add((Animal) c);
                }
            }
            for (Animal ani : animals) {
                if (Randomizer.getRandom().nextDouble() <= 0.3) {
                    ani.setIsInfected(true);
                    ani.infectionStartStep = step;
                    isSpread = true;
                }
            }
        }
    }

    /**
     * Returns true if the disease is still actively spreading: at least one
     * animal remains infected and non-immune. Called by SimulationEngine after
     * each step's act loop to decide whether to clear the spread flag.
     *
     * @param creatures The current creature population.
     * @return true if the disease is still active, false if it has stopped.
     */
    public boolean isStillActive(List<Creature> creatures)
    {
        boolean existDisease = true;
        Iterator<Creature> it = creatures.iterator();
        while (it.hasNext()) {
            Creature creature = it.next();
            if (creature instanceof Animal) {
                Animal ani = (Animal) creature;
                if (!ani.getIsInfected() || ani.getIsImmuned()) {
                    existDisease = false;
                } else {
                    existDisease = true;
                }
            }
        }
        return existDisease;
    }
}
