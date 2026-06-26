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
     * Simulates disease spread by evaluating each field block independently.
     */
    public void simulateDiseaseStep()
    {
        currentStep++;
        InfectionTally infectionTally = new InfectionTally();
        field.forEachAnimalBlock(block -> {
            infectionTally.addInfected(evaluateBlock(block));
        });
        infectionCounts.put(currentStep, infectionTally.getInfectedCount());
    }

    private int evaluateBlock(List<Animal> block)
    {
        int infectedCount = countActiveInfections(block);
        infectSusceptibleAnimals(block, infectedCount);
        return infectedCount;
    }

    private int countActiveInfections(List<Animal> block)
    {
        int infectedCount = 0;
        for(Animal animal : block) {
            if(isActivelyInfected(animal)) {
                infectedCount++;
            }
        }
        return infectedCount;
    }

    private void infectSusceptibleAnimals(List<Animal> block, int infectedCount)
    {
        double infectionProbability = calculateInfectionProbability(block.size(), infectedCount);
        if(infectionProbability == 0.0) {
            return;
        }

        for(Animal animal : findSusceptibleAnimals(block)) {
            if(rand.nextDouble() < infectionProbability) {
                animal.setInfectionTimestamp(currentStep);
            }
        }
    }

    private List<Animal> findSusceptibleAnimals(List<Animal> block)
    {
        List<Animal> susceptibleAnimals = new ArrayList<>();
        for(Animal animal : block) {
            if(animal.getInfectionTimestamp() == null) {
                susceptibleAnimals.add(animal);
            }
        }
        return susceptibleAnimals;
    }

    private boolean isActivelyInfected(Animal animal)
    {
        Integer infectionTimestamp = animal.getInfectionTimestamp();
        return infectionTimestamp != null
            && currentStep - infectionTimestamp < DISINFECTION_DURATION;
    }

    private double calculateInfectionProbability(int blockSize, int infectedCount)
    {
        if(blockSize == 0) {
            return 0.0;
        }
        return (double) infectedCount / (double) blockSize;
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

    private static class InfectionTally
    {
        private int infectedCount;

        void addInfected(int amount)
        {
            infectedCount += amount;
        }

        int getInfectedCount()
        {
            return infectedCount;
        }
    }
}
