import java.awt.Color;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Grass.
 * Deers age, move, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Grass extends Plant
{
    // (All species parameters are defined in SpeciesConfig.GRASS.)
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
    public Grass(boolean randomSize, Field<Entity> field, Location location)
    {
        super(field, location);

        if(randomSize) {
            size = rand.nextInt(SpeciesConfig.GRASS.maxSize);
        }
        else {
            size = SpeciesConfig.GRASS.maxSize;
        }
        steppeFired = false;
    }

    @Override public Color getDisplayColor() { return Color.GREEN; }
    
    /**
     * This is what the Grass does most of the time - it grows 
     * around. Sometimes it will breed or die of steppeFire. If
     * the grass catches fire, it will also burn the unit adjacent to it 
     * @param step The current step
     * @param weather The current weather.
     */
    public void act(List<Entity> newEntities, int step, String weather)
    {
        if(isAlive()) {
            if("Rainy".equals(weather)) {
                incrementSize();
                steppeFired = false;
            }
            else {
                getSteppeFire();
                if (steppeFire() && getLocation() != null) {                
                    Field<Entity> field = getField();
                    List<Location> adjacent = getNavigator().adjacentLocations(getLocation(), 1);
                    Iterator<Location> it = adjacent.iterator();
                    while(it.hasNext()) {
                        Location where = it.next();
                        Entity creature = field.getObjectAt(where);
                        if(creature instanceof Grass) {
                            Grass grass = (Grass) creature;
                            grass.getSteppeFire();                        
                        }
                        if(creature instanceof Animal) {
                            Animal animal = (Animal) creature;
                            animal.burn();                        
                        }
                    }
            
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
        size = size + rand.nextInt(SpeciesConfig.GRASS.maxGrowRate) + SpeciesConfig.GRASS.maxGrowRate;
        if(size > SpeciesConfig.GRASS.maxSize) {
            size = SpeciesConfig.GRASS.maxSize;
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
    private void getSteppeFire()
    {
        if(rand.nextDouble() <= SpeciesConfig.GRASS.steppFireProbability) {
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
