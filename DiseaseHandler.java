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
    private final Random rand = Randomizer.getRandom();
    private final Field field;
    private final TreeMap<Integer, Integer> infectionHistory;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private int currentStep;
    
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
        infectionHistory = new TreeMap<>();
        currentStep = 0;
    }
    
    /*///////////////////////////////////////////////////////////////
                          DISEASE SIMULATION LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Simulates the disease by iterating through every block in the field, and computing the density of the number of ppl infected 
     * in the given block to determine the likelyhood of being infected in the block
     */
    public void simulateDiseaseStep()
    {
        // fetch the animal per blocks eg : [block1 : [animal1,animal2, animal3]. block 2: [animal1, animal 2, animal3]...]
        List<List<Animal>> blocks = field.getAnimalsPerBlock();
        int infectionCount = 0;
        currentStep++;
        for (List<Animal> block : blocks) {
            int densityIndex = 0;
            List<Animal> uninfectedAnimals = new ArrayList<>();
            for(Animal animal : block) {
                if(animal.getInfectionTimestamp() != null && currentStep - animal.getInfectionTimestamp() < DISINFECTION_DURATION) {
                    // increase the counter of number of animals infected within this block.
                    densityIndex++;
                    continue;
                }
                if(animal.getInfectionTimestamp() == null) {
                    uninfectedAnimals.add(animal);
                }
            }
            // compute probability of being infected.
            double pValue = (double)((double)densityIndex/(double)block.size());
            for(Animal animal : uninfectedAnimals) {
                double randNumber = rand.nextDouble();
                if(randNumber < pValue ) {
                    animal.setInfectionTimestamp(currentStep);
                }
            }
            infectionCount += densityIndex;
        }
        infectionHistory.put(currentStep, infectionCount);
    }

    /**
     * Expose the accumulated infection history for reporting.
     *
     * @return the infection history by step.
     */
    public Map<Integer, Integer> getInfectionHistory()
    {
        return infectionHistory;
    }
}
