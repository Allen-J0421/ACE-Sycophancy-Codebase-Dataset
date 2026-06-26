import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Write a description of class Disease here.
 *
 * @version (a version number or a date)
 */
public class Disease
{
    private static final Random RAND = Randomizer.getRandom();
    
    // The possibility that an animal may be infected by a disease.
    public static final double INFECTION_RATE = 0.3;
    // The possibility an animal may die of a disease.
    public static final double MORTALITY_RATE = 0.05;
    // The steps an animal need to withstand in order to get immunity
    public static final int NUMBER_OF_STEP_TO_WITHSTAND = 3;
    // The probability that the disease may occur
    private static final double DISEASE_OCCURENCE_PROBABILITY = 0.2;
    
    // Identify if a disease start to spread
    private boolean isSpread;

    public Disease()
    {
         isSpread = false;
    }

   
    public boolean getIsSpread(){
        return isSpread;
    }
    
    public void setIsSpread(boolean bl){
        isSpread = bl;
    }
    
    /**
     * Attempt to start a new disease spread event.
     */
    protected void beginSpreadIfPossible(List<Creature> creatures, int step)
    {
        if(!getIsSpread() && RAND.nextDouble() <= DISEASE_OCCURENCE_PROBABILITY){
            List<Animal> animals = collectAnimals(creatures);
            for(Animal animal : animals) {
                if(RAND.nextDouble() <= INFECTION_RATE) {
                    animal.infect(step);
                    setIsSpread(true);
                }
            }
        }
    }

    /**
     * Refresh whether the disease is still actively spreading.
     */
    protected void refreshSpreadState(List<Creature> creatures)
    {
        if(getIsSpread()) {
            setIsSpread(hasActiveInfections(creatures));
        }
    }

    private boolean hasActiveInfections(List<Creature> creatures)
    {
        for(Animal animal : collectAnimals(creatures)) {
            if(animal.getIsInfected() && !animal.getIsImmuned()) {
                return true;
            }
        }
        return false;
    }

    private List<Animal> collectAnimals(List<Creature> creatures)
    {
        List<Animal> animals = new ArrayList<>();
        for(Creature creature : creatures) {
            if(creature instanceof Animal) {
                animals.add((Animal) creature);
            }
        }
        return animals;
    }
}
    
    
