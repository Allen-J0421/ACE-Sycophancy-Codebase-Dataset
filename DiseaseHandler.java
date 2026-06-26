import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
/**
 * Class responsible for handling the simulation of the disease.
 *
 * @version 1.0
 */
public class DiseaseHandler
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    // cooldown before the animal achieves natural immunity
    private static final int DISINFECTION_DURATION = 5;
    private static final Random rand = Randomizer.getRandom();
    private final Field field;

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private final TreeMap<Integer, Integer> count = new TreeMap<>();

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new disease handler
     * @param field The field to later get the animals per block and compute the density
     */
    public DiseaseHandler(Field field)
    {
        this.field = field;
    }

    /*///////////////////////////////////////////////////////////////
                          DISEASE SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Returns an unmodifiable view of the simulation step → infection count map.
     */
    public Map<Integer, Integer> getCount()
    {
        return Collections.unmodifiableMap(count);
    }

    /**
     * Simulates the disease for the given simulation step. Iterates through every
     * 20x20 block in the field and uses the local infection density to determine
     * the likelihood of each uninfected animal becoming infected.
     *
     * @param step The current simulation step number.
     */
    public void simulateDiseaseStep(int step)
    {
        List<List<Animal>> blocks = field.getAnimalsPerBlock();
        int infectionCount = 0;
        for (List<Animal> block : blocks) {
            int densityIndex = 0;
            List<Animal> uninfectedAnimals = new ArrayList<>();
            for(Animal animal : block) {
                if(animal.getInfectionTimestamp() != null && step - animal.getInfectionTimestamp() < DISINFECTION_DURATION) {
                    densityIndex++;
                    continue;
                }
                if(animal.getInfectionTimestamp() == null) {
                    uninfectedAnimals.add(animal);
                }
            }
            if(block.isEmpty()) continue;
            double pValue = (double) densityIndex / block.size();
            for(Animal animal : uninfectedAnimals) {
                if(rand.nextDouble() < pValue) {
                    animal.setInfectionTimestamp(step);
                }
            }
            infectionCount += densityIndex;
        }
        count.put(step, infectionCount);
    }
}
