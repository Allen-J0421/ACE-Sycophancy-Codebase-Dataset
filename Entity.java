import java.awt.*;


public abstract class Entity implements FieldOccupant {


	private Field field;

	private Location location;

	private boolean alive;


	public Entity(Field field, Location location) {
		this.field = field;
		alive = true;
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
			field.removeOccupant(this, location);
		}
		location = newLocation;
		field.place(this, newLocation);
	}


	protected void setLocationNull() {
		location = null;
	}


	protected boolean isAlive() {
		return alive;
	}


	protected void setDead() {
		alive = false;
		if (location != null) {
			field.removeOccupant(this, location);
			setLocationNull();
			setFieldNull();
		}
	}

}
