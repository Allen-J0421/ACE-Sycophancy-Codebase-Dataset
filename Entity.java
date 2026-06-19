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
			field.clear(location);
		}
		location = newLocation;

		if (newLocation != null) {
			if (this instanceof Animal) {
				field.placeAnimal((Animal) this, newLocation);
			} else {
				field.placePlant((Plant) this, newLocation);
			}
		}
	}


	protected void setLocationNull() {
		location = null;
	}

}
