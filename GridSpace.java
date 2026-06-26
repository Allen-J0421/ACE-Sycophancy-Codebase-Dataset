import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The grid is made up of GridSpaces which stores the current
 * Animal and Plant which is stored at this location.
 *
 * @version 26/02/2022
 */
public class GridSpace 
{
    private static final Class<?>[] SUPPORTED_TYPES = {Animal.class, Plant.class};

    private final Map<Class<?>, Object> occupants;
    
    /**
     * Initialise the space to null.
     */
    public GridSpace() 
    {
        occupants = new HashMap<>();
    }
    
    /**
     * @return Returns the animal in this grid space.
     */
    public Animal getAnimal() 
    {
        return getObject(Animal.class);
    }
    
    /**
     * @return Returns the plant in this grid space.
     */
    public Plant getPlant() 
    {
        return getObject(Plant.class);
    }
    
    /**
     * @return Returns the Object of the class type passed in.
     * 
     * @param The class type of the object that is wanted.
     */
    public <T> T getObject(Class<T> objectType) 
    {
        Object exactMatch = occupants.get(objectType);
        if (objectType.isInstance(exactMatch)) {
            return objectType.cast(exactMatch);
        }

        for (Object occupant : occupants.values()) {
            if (objectType.isInstance(occupant)) {
                return objectType.cast(occupant);
            }
        }

        return null;
    }
    
    /**
     * Adds the animal passed in into the grid space.
     * 
     * @param The animal to store in this grid space.
     */
    public void setAnimal(Animal animal) 
    {
        storeObject(Animal.class, animal);
    }
    
    /**
     * Adds the plant passed in into the grid space.
     * 
     * @param The plant to store in this grid space.
     */
    public void setPlant(Plant plant) {
        storeObject(Plant.class, plant);
    }
    
    /**
     * Adds the object passed in into the grid space into its respective
     * space.
     * 
     * @param Object to store in this grid space.
     */
    public Object setObject(Object object) 
    {
        if (object == null) {
            return null;
        }

        Class<?> storageType = resolveStorageType(object.getClass());
        return occupants.put(storageType, object);
    }
    
    /**
     * Clears all objects in this grid space.
     */
    public void clear() {
        occupants.clear();
    }
    
    /**
     * Clears the object at the type specified.
     * 
     * @param Class type of the object to clear.
     */
    public void clear(Class<?> objectType) 
    {
        for (Iterator<Map.Entry<Class<?>, Object>> it = occupants.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Class<?>, Object> entry = it.next();

            if (objectType.isInstance(entry.getValue())) {
                it.remove();
            }
        }
    }

    /**
     * Remove and return the stored object matching the requested type.
     */
    public <T> T removeObject(Class<T> objectType)
    {
        T existingObject = getObject(objectType);
        if (existingObject != null) {
            clear(objectType);
        }

        return existingObject;
    }

    /**
     * Store a supported object type in its dedicated slot.
     */
    private <T> void storeObject(Class<T> objectType, T object)
    {
        if (object == null) {
            occupants.remove(objectType);
        }
        else {
            occupants.put(objectType, object);
        }
    }

    /**
     * Resolve which supported slot should store this object class.
     */
    private Class<?> resolveStorageType(Class<?> objectType)
    {
        for (Class<?> supportedType : SUPPORTED_TYPES) {
            if (supportedType.isAssignableFrom(objectType)) {
                return supportedType;
            }
        }

        throw new IllegalArgumentException("Unsupported GridSpace object type: " + objectType.getName());
    }
}
