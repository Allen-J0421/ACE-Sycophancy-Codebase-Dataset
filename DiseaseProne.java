import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Mixin for organisms that can catch, spread, and recover from disease.
 *
 * @version 26/02/2022
 */
public interface DiseaseProne extends OrganismContext
{
    Random RANDOM = Randomizer.getRandom();

    DiseaseState getDiseaseState();

    default void updateDiseaseState()
    {
        DiseaseState diseaseState = getDiseaseState();

        if (!diseaseState.isImmune() && diseaseState.isInfected()) 
        {
            if(RANDOM.nextDouble() <= diseaseState.getDeathFromInfectionProbability()) 
            {
                markDead();            
            }
            else if(RANDOM.nextDouble() <= diseaseState.getImmuneProbability()) {
                diseaseState.setImmune(true);
                diseaseState.setInfected(false);
            }
        }
        else
        {
            if(RANDOM.nextDouble() <= (diseaseState.getImmuneProbability() / 15)) {
                diseaseState.setImmune(false);
            }
        }
    }

    default void exposeToDisease()
    {
        DiseaseState diseaseState = getDiseaseState();

        if(!diseaseState.isImmune() && !diseaseState.isInfected())
        {
            if (surroundingsInfected() && RANDOM.nextDouble() <= diseaseState.getDiseaseSpreadProbability())
            {
                diseaseState.setInfected(true);
            }
            else if (RANDOM.nextDouble() <= diseaseState.getDiseaseProbability()) 
            {
                diseaseState.setInfected(true);           
            }
        }
    }

    default boolean surroundingsInfected()
    {
        Field field = currentField();
        List<Location> adjacent = field.adjacentLocations(currentLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext()) 
        {
            Location where = it.next();
            Animal animal = field.getObjectAt(where, Animal.class);

            if (animal != null && animal.getDiseaseState().isInfected()) 
            {
                return true;
            }
        }
        
        return false;
    }
}
