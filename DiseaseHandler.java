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
    
    public static TreeMap<Integer, Integer> count;
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
        count = new TreeMap<>();
        this.field = field;
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
        List<List<Animal>> blocks = field.getAnimalsPerBlock();
        int infectionCount = 0;
        currentStep++;
        for (List<Animal> block : blocks) {
            infectionCount += simulateBlock(block);
        }
        count.put(currentStep, infectionCount);
    }

    /**
     * Simulate infection spread within a single field block.
     *
     * @return the number of currently infectious animals in the block.
     */
    private int simulateBlock(List<Animal> block)
    {
        int infectiousCount = 0;
        List<Animal> susceptibleAnimals = new ArrayList<>();

        for(Animal animal : block) {
            if(isInfectious(animal)) {
                infectiousCount++;
            }
            else if(isSusceptible(animal)) {
                susceptibleAnimals.add(animal);
            }
        }

        infectSusceptibleAnimals(susceptibleAnimals, infectionProbability(infectiousCount, block.size()));
        return infectiousCount;
    }

    /**
     * Determine whether an animal is actively infectious.
     */
    private boolean isInfectious(Animal animal)
    {
        return animal.getInfectionTimestamp() != null &&
               currentStep - animal.getInfectionTimestamp() < DISINFECTION_DURATION;
    }

    /**
     * Determine whether an animal can be infected this step.
     */
    private boolean isSusceptible(Animal animal)
    {
        return animal.getInfectionTimestamp() == null;
    }

    /**
     * Calculate infection probability from local block density.
     */
    private double infectionProbability(int infectiousCount, int blockSize)
    {
        if(blockSize == 0) {
            return 0.0;
        }
        return (double) infectiousCount / blockSize;
    }

    /**
     * Infect susceptible animals according to a per-block probability.
     */
    private void infectSusceptibleAnimals(List<Animal> susceptibleAnimals, double probability)
    {
        for(Animal animal : susceptibleAnimals) {
            if(rand.nextDouble() < probability) {
                animal.setInfectionTimestamp(currentStep);
            }
        }
    }
}
