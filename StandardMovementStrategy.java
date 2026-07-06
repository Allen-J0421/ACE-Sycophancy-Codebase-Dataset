public class StandardMovementStrategy implements MovementStrategy {


	public void move(Animal animal, Location foodLocation) {
		Location newLocation = foodLocation;
		if (newLocation == null) {
			newLocation = animal.getField().freeAnimalAdjacentLocation(animal.getLocation());
		}
		if (newLocation != null) {
			animal.setLocation(newLocation);
		} else {
			animal.setDead();
		}
	}

}
