import java.awt.*;


public abstract class Entity {


	private Field field;

	private Location location;


	public Entity(Field field, Location location) {
		this.field = field;
		setLocation(location);
	}


	protected abstract Color getObjectColor(Climate climate);


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
			clearFieldLocation(field, location);
		}
		location = newLocation;
		placeInField(field, newLocation);
	}


	protected void setLocationNull() {
		location = null;
	}


	protected abstract void placeInField(Field field, Location location);


	protected abstract void clearFieldLocation(Field field, Location location);
}
