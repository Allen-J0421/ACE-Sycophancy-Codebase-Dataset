import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * An organism in the forest
 *
 * @version 2022.03.02
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    // The age of the organism
    protected int age;
    // The organism's current disease, if null the organism has no disease.
    private Disease currentDisease;
    
    /**
     * Constructor for objects of class Organism
     * @Param field The field of the organism
     * @Param location The location of the organism
     */
    public Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Return the organism's age
     * @Return The organism's age
     */
    protected int getAge()
    {
        return age;
    }
    
    /**
     * Returns whether the animal has a disease
     * @Return boolean Whether the animal has a disease
     */
    protected boolean hasDisease()
    {
        if (currentDisease == null){
            return false;
        }
        else{
            return true;
        }
    }
    
    /**
     * Transmits current disease to adjacent animals
     */
    protected void transmit()
    {
        if (isAlive()){
            //gets adjacent locations of the current animal
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            
            //iterate through all adjacent animals
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()){
                Location where = it.next();
                Object organism = field.getObjectAt(where);
                //if the organism is an animal:
                if (organism instanceof Animal){
                    Animal animal = (Animal) organism;
                    //runs if the animal to transmit has a disease and the the animal to be transmitted to does not also have a disease
                    if (getCurrentDisease() != null && !animal.hasDisease()){
                        // random chance to transmit the disease based on the disease contagiousness
                        Random rand = new Random();
                        if (rand.nextDouble() <= getCurrentDisease().getContagiousness()){
                            animal.contractDisease(getCurrentDisease());
                        }
                    }
                }
                else if(organism instanceof Plant){
                    Plant plant = (Plant) organism;
                    //Runs if the plant has a disease and the adjacent plant does not.
                    if (getCurrentDisease() != null && !plant.hasDisease()){
                        Random rand = new Random();
                        //Runs if the randomly chosen double is less than or equal to the probability of spreading the disease
                        if (rand.nextDouble() <= getCurrentDisease().getContagiousness()){
                            plant.contractDisease(getCurrentDisease());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Random chance for the plant to die due to infection - based on how deadly the disease is
     */
     protected void checkDieFromInfection()
    {
        Random rand = new Random();
        if(hasDisease() && rand.nextDouble() <= currentDisease.getDeathProbability()){
            setDead();
        }
    }
    
    /**
     * Let the organism contract a disease
     * @param disease The disease to be contracted
     */
    protected void contractDisease(Disease disease){
        currentDisease = disease;
    }
    
    /**
     * Return the current disease of the organism
     * @Return currentDisease The current disease of the organism
     */
    protected Disease getCurrentDisease(){
        return currentDisease;
    }
}
