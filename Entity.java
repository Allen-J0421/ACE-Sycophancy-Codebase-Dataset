import java.awt.*;


public abstract class Entity {


	private Field field;

	private Location location;

	private boolean alive;


	public Entity(Field field, Location location) {
		this.field = field;
		alive = true;
		setLocation(location);
	}


	protected abstract Color getObjectColor(Climate climate);


	protected abstract void placeInField(Location location);


	protected abstract void clearFromField(Location location);


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
			clearFromField(location);
		}
		location = newLocation;
		placeInField(newLocation);
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
			clearFromField(location);
			setLocationNull();
			setFieldNull();
		}
	}

}
