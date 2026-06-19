import java.awt.Color;


/**
 * Something that occupies a location on the field: an {@link Animal} or a {@link Plant}.
 */
public abstract class Entity {


	private Field field;

	private Location location;


	public Entity(Field field, Location location) {
		this.field = field;
		setLocation(location);
	}


	protected abstract Color getObjectColor(Climate climate);


	/** Record this entity's presence on the appropriate layer of the field. */
	protected abstract void placeInField(Field field, Location location);


	protected Field getField() {
		return field;
	}


	protected void setFieldNull() {
		field = null;
	}


	protected Location getLocation() {
		return location;
	}


	protected void setLocation(Location newLocation) {
		if (location != null) {
			field.clear(location);
		}
		location = newLocation;
		placeInField(field, newLocation);
	}


	protected void setLocationNull() {
		location = null;
	}

}
