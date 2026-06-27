import java.util.List;
import java.util.Random;

/**
 * A simple model of a Grass.
 * Deers age, move, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Grass extends Plant
{
    // Characteristics shared by all Grass (class variables).

    // The age to which a Grass can live.
    private static final int MAX_SIZE = 50;
    // The maximum grow rate.
    private static final int MAX_GROW_RATE = 20;
    // The likelihood of grass get steppe fire.
    private static final double STEPPE_FIRE_PROBABILITY = 0.02;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The Grass's age.
    private int size;
    // The Grass's steppe fire status
    private boolean steppeFired;

    /**
     * Create a new Grass. A Grass may be created with size
     * 50 or with a random size.
     * 
     * @param randomSize If true, the Grass will have a random size.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomSize, Field field, Location location)
    {
        super(field, location);
        
        if(randomSize) {
            size = rand.nextInt(MAX_SIZE);
        }
        else {
            size = 50;
        }
        steppeFired = false;
    }
    
    /**
     * This is what the Grass does most of the time - it grows 
     * around. Sometimes it will breed or die of steppeFire. If
     * the grass catches fire, it will also burn the unit adjacent to it 
     * @param step The current simulation step.
     */
    public void act(List<Organism> newOrganisms, SimulationStep step)
    {
        if(isAlive()) {
            if(step.getWeather() == Weather.RAINY) {
                incrementSize();
                steppeFired = false;
            }
            else {
                ignite();
                if(steppeFire()) {
                    forEachAdjacent(Grass.class, 1, Grass::ignite);
                    forEachAdjacent(Animal.class, 1, Animal::burn);
                    decrementSize();                
                }
            }
        }
    }

    /**
     * Increase the size.
     */
    private void incrementSize()
    {
        size = size + rand.nextInt(MAX_GROW_RATE)+20;
        if(size > MAX_SIZE) {
            size = MAX_SIZE;
        }
    }
    
    /**
     * Decrease the size.
     */
    public void decrementSize()
    {
        size = size -1;
        if(size == 0) {
            setDead();
        }
    }

    /**
     * Grass can become steppe fire.
     */
    private void ignite()
    {
        if(rand.nextDouble() <= STEPPE_FIRE_PROBABILITY) {
            steppeFired = true;
        }        
    }

    /**
     * Return steppe fire status.
     * @return true if the grass become steppe fire, false otherwise.
     */
    public boolean steppeFire()
    {
        return steppeFired;
    }

    /**
     * Return size status.
     * @return size The current size of the grass.
     */
    public int getSize()
    {
        return size;
    }
     
}
