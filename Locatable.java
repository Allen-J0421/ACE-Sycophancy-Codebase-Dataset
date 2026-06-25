
/**
 * This abstract class contains all the logic requried for a player (subclass) to be located in a field. 
 * 
 * @version 12.6.4
 */
public abstract class Locatable  {

	// The animal's position in the field.
    private Location location;

    // The animal's field.
    private Field field;


    /**
     * Initalise the locatable class
     * @param field the field with which the subclass should be placed on 
     * @param location the location wich the subclass should be placed on 
     */ 
    public Locatable(Field newField, Location newLocation) {
    	location = newLocation;
    	field = newField;

    	setLocation(newLocation);
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {

        return field;
    }

    /**
     * Remove the current subclass from the field 
     */
    protected void removeFromField() {
    	if(getLocation() != null) {
            getField().clear(getLocation());
            clearField();
            clearLocation();
        }
    }


    /**
     * Clear the subclasses field variable 
     */
    protected void clearField() {
    	field = null;

    }

    /**
     * Clear the subclasses location variable 
     */
    protected void clearLocation() {
    	location = null;
    }


}