import java.util.ArrayList;

/**
 * The grid is made up of GridSpaces which stores the current
 * Animal and Plant which is stored at this location.
 *
 * @version 26/02/2022
 */
public class GridSpace 
{
    private Animal animal;
    private Plant plant;
    
    /**
     * Initialise the space to null.
     */
    public GridSpace() 
    {
        animal = null;
        plant = null;
    }
    
    /**
     * @return Returns the Object of the class type passed in.
     * 
     * @param The class type of the object that is wanted.
     */
    public Object getObject(Class objectType) 
    {
        Object object = null;

        if (objectType.equals(Animal.class)) {
            object = animal;
        }
        else if (objectType.equals(Plant.class)) {
            object = plant;
        }
        
        return object;
    }
    
    /**
     * Adds the animal passed in into the grid space.
     * 
     * @param The animal to store in this grid space.
     */
    public void setAnimal(Animal animal) 
    {
        this.animal = animal;
    }
    
    /**
     * Adds the plant passed in into the grid space.
     * 
     * @param The plant to store in this grid space.
     */
    public void setPlant(Plant plant) {
        this.plant = plant;
    }
    
    /**
     * Adds the object passed in into the grid space into its respective
     * space.
     * 
     * @param Object to store in this grid space.
     */
    public void setObject(Object object) 
    {
        if (object instanceof Animal) {
            setAnimal((Animal) object);
        }
        else if (object instanceof Plant) {
            setPlant((Plant) object);
        }
    }
    
    /**
     * Clears all objects in this grid space.
     */
    public void clear() {
        animal = null;
        plant = null;
    }
    
    /**
     * Clears the object at the type specified.
     * 
     * @param Class type of the object to clear.
     */
    public void clear(Class objectType) 
    {
        if (objectType.equals(Animal.class)) {
            animal = null;
        }
        else if (objectType.equals(Plant.class)) {
            plant = null;
        }
    }
}