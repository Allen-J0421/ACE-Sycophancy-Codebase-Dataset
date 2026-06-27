import java.util.List;
import java.util.Random;

/**
 * A simple model of a mouse.
 * Mice age, move, eat seed, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Mouse extends BreedingAnimal
{
    private static final AnimalProfile PROFILE = new AnimalProfile(125, 5, 5, 5);
    private static final BreedingProfile BREEDING_PROFILE = new BreedingProfile(5, 0.25, 10);
    // The likelihood of a mouse infect by disease.
    private static final double INFECT_PROBABILITY = 0.01;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // The mouse infected by deisease.
    // 0 - not infect, 1-3 degree of infection.
    private int infect;
    // chance of a mice to recover or deteriorate from disease.
    private static final double recover_probability = 0.3 ;
    private static final double deteriorate_probability = 0.15 ;
    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE, BREEDING_PROFILE);
    }

    @Override
    protected Animal createYoung(Field field, Location location)
    {
        return new Mouse(false, field, location);
    }

    @Override
    protected void updateStatusAfterBurn(SimulationStep step)
    {
        checkInfectLevel();
    }

    @Override
    protected void handleAliveStep(List<Organism> newMice, SimulationStep step)
    {
        if(getInfected() != 0) {
            forEachAdjacent(Mouse.class, 1, mouse -> {
                if(mouse.getInfected() == 0) {
                    mouse.infect();
                }
            });
            diseaseRecover();
        }
        else {
            breed(newMice);
            infect();
        }
    }

    /**
     * Look for grass adjacent to the current location.
     * Only the first seed (belong to grass) will be located.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    protected Location selectMoveLocation(SimulationStep step)
    {
        return findFood();
    }

    private Location findFood()
    {
        return findAdjacentLocation(Grass.class, 1, grass -> {
            if(grass.isAlive()) {
                changeFoodLevel(2);
                return true;
            }
            return false;
        });
    }
    
    /**
     * @return true if the mouse is infected by disease, false otherwise.
     */
    private int getInfected()
    {
        return infect;
    }

    /**
     * A mouse can be infected by disease.
     */
    private void infect()
    {
        if(rand.nextDouble() <= INFECT_PROBABILITY) {
            infect = 1;
        }        
    }
    /**
     * mouse will die from disease when the infect indicator reach 3.
     */
    private void checkInfectLevel()
    {
        if(infect == 3) {
            setDead();
        }
    }

    /**
     * A mouse can recover or deteriorate from disease
     */
    private void diseaseRecover()
    {
        if(rand.nextDouble() <= deteriorate_probability) {
            infect ++ ;
        }  
        else if (rand.nextDouble() <= recover_probability) {
            infect -- ;
        }  
    }

}
