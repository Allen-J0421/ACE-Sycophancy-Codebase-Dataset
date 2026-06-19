import java.awt.*;


public abstract class Entity implements Movable {


	private Field field;

	private Location location;


	public Entity(Field field, Location location) {
		this.field = field;
		setLocation(location);
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
			field.updateOccupancy(this, location, newLocation);
		}
		location = newLocation;
	}


	protected void setLocationNull() {
		location = null;
	}

}
