import java.util.NavigableMap;
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
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private final NavigableMap<Integer, Integer> infectionCounts;
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
        infectionCounts = new TreeMap<>();
        reset();
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
        int infectionCount = 0;
        int[] totalInfections = {0};
        currentStep++;
        field.forEachAnimalBlock(block -> {
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
            double pValue = block.isEmpty() ? 0.0 : ((double) densityIndex / (double) block.size());
            for(Animal animal : uninfectedAnimals) {
                double randNumber = rand.nextDouble();
                if(randNumber < pValue ) {
                    animal.setInfectionTimestamp(currentStep);
                }
            }
            totalInfections[0] += densityIndex;
        });
        infectionCount = totalInfections[0];
        infectionCounts.put(currentStep, infectionCount);
    }

    /**
     * Reset the disease state for a fresh simulation run.
     */
    public void reset()
    {
        currentStep = 0;
        infectionCounts.clear();
    }

    /**
     * Returns the tracked infection counts over time.
     */
    public NavigableMap<Integer, Integer> getInfectionCounts()
    {
        return infectionCounts;
    }
}
