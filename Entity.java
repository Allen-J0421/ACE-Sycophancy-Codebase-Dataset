import java.awt.*;


public abstract class Entity {


	private Field field;

	private Location location;


	public Entity(Field field, Location location) {
		this.field = field;
		setLocation(location);
	}


	protected abstract Color getObjectColor(Climate climate);

	protected abstract void placeInField(Field field, Location location);


	protected Field getField() {
		return field;
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


	protected void detach() {
		location = null;
		field = null;
	}

}
