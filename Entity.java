import java.awt.*;


public abstract class Entity implements Movable {


	private Field field;

	private Location location;


	public Entity(Field field) {
		this.field = field;
		location = null;
	}


	protected abstract Color getObjectColor(Climate climate);

	public abstract OccupancyLayer getOccupancyLayer();


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
		if (field != null) {
			field.relocate(this, location, newLocation);
		}
		bindLocation(newLocation);
	}


	final void bindLocation(Location newLocation) {
		location = newLocation;
	}


	protected void setLocationNull() {
		location = null;
	}

}
