package field;

/**
 * Anything that is representable on the field.
 *
 */
public abstract class Entity {
    protected Location location;
    protected int[] rgbColour;
    protected boolean isAlive;

    private final String ID;

    /**
     * Constructor for Entity
     * @param ID the ID of the entity
     * @param location the location 
     * @param rgbColour its RGB colour (red = rgb[0], green = rgb[1], blue = rgb[2])
     */
    public Entity(String ID, Location location, int[] rgbColour) {
        this.ID = ID;
        this.location = location;
        this.rgbColour = rgbColour;

        isAlive = true;
    }

    public abstract void die();

    /* Getters */

    public Location getLocation() { 
        return location; 
    }

    public int[] getRgbColour() {
        return rgbColour;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public String getID() {
        return ID;
    }

    /* Setters */

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDead() {
        isAlive = false;
    }   
}
