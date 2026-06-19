import java.awt.*;


public abstract class Entity {

	private final EntityController controller;

	private Location location;


	public Entity(EntityController controller, Location location) {
		this.controller = controller;
		controller.place(this, location);
	}


	protected abstract Color getObjectColor(Climate climate);


	protected FieldEnvironment getField() {
		return controller.getFieldEnvironment();
	}


	protected EntityController getController() {
		return controller;
	}


	protected Location getLocation() {
		return location;
	}


	protected void requestMove(Location newLocation) {
		controller.move(this, newLocation);
	}


	protected void requestRemoval() {
		controller.remove(this);
	}


	void updateLocation(Location newLocation) {
		location = newLocation;
	}
}
