import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Write a description of class Disease here.
 *
 * @version (a version number or a date)
 */
public class Disease
{
    
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
     * create the source of infection
     */
    protected void creationSourceOfInfection(List<Creature> creatures, int step){
       
        if(!getIsSpread() && Randomizer.getRandom().nextDouble() <= DISEASE_OCCURENCE_PROBABILITY){
            ArrayList<Animal> animalCollection = new ArrayList<>();
            Iterator<Creature> it = creatures.iterator();
            while(it.hasNext()){
                Creature creature = it.next();
                if(creature instanceof Animal){
                    Animal animal = (Animal)creature;
                        animalCollection.add(animal);
                }
            }
            
            for(Animal ani: animalCollection){
                if(Randomizer.getRandom().nextDouble() <= 0.3){
                    ani.setIsInfected(true);
                    ani.infectionStartStep = step;
                    setIsSpread(true);

                }
            }
            

        }

    }
    
   
    
    
            
}
    
    

