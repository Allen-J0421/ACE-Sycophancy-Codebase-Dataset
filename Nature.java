import java.util.List;
/**
 * A class representing shared characteristics of nature elements.
 *
 * @version 2022.25.02
 */
public abstract class Nature
{
    // Whether the animal is alive or not.
    private boolean isAlive;
    // Natures's field.
    private NatureField natureField;
    // Animal's field
    private Field field;
    // The animal's position in the field.
    private Location location;
    
    /**
     * Create a nature element at a location in field.
     * 
     * @param field The field currently occupied.
     * @param natureField The nature field currently occupied
     * @param location The location within the field.
     */
    public Nature(Field field, NatureField natureField, Location location)
    {
        isAlive = true;
        this.natureField = natureField;
        this.field = field;
        setNatureFieldLocation(location);
        
        
    }
    
    // Default constructor for dummy objects
    public Nature()
    {
    }

    /**
     * Make nature act - that is: make it do
     * whatever it wants/needs to do.
     * @param newNature A list to receive newly created nature.
     * @param isRaining Whether there is currently rain in the environment
     */
    abstract public void act(List<Nature> newNature, boolean isRaining);
    
    /**
     * Check whether the nature element is alive or not.
     * @return true if the nature element is still alive.
     */
    public boolean getIsAlive()
    {
        return isAlive;
    }
    
    /**
     * Indicate that the nature element is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        isAlive = false;
        if(location != null) {
            natureField.clear(location);
            location = null;
            natureField = null;
        }
    }
    
    /**
     * Return the nature element's location.
     * @return The nature element's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the nature element at the new location in the given field.
     * @param newLocation The nature elements's new location.
     */
    protected void setFieldLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Place the nature element at the new location in the given nature field.
     * @param newLocation The nature element's new location
     */
    protected void setNatureFieldLocation(Location newLocation)
    {
        if(location != null) {
            natureField.clear(location);
        }
        location = newLocation;
        natureField.place(this, newLocation);
    }
    
    /**
     * Obtains the current nature field.
     * @return The curret nature field.
     */
    public NatureField getNatureField()
    {
        return natureField;
    }
}
