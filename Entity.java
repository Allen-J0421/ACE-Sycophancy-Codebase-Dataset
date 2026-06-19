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


	protected Location getLocation() {
		return location;
	}


	protected void setLocation(Location newLocation) {
		if (location != null) {
			field.clear(location);
		}
		location = newLocation;

		if (this instanceof Animal) {
			field.placeAnimal(this, newLocation);
		} else {
			field.placePlant(this, newLocation);
		}
	}


	protected void detach() {
		location = null;
		field = null;
	}

}
