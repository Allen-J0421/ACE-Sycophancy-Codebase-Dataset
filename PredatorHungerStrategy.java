import java.util.Iterator;
import java.util.List;


public class PredatorHungerStrategy implements HungerStrategy {


	private final int additionalFoodValue;

	private final boolean cannibal;


	public PredatorHungerStrategy(int additionalFoodValue, boolean cannibal) {
		this.additionalFoodValue = additionalFoodValue;
		this.cannibal = cannibal;
	}


	public Location findFood(Animal animal) {
		Field field = animal.getField();
		List<Location> adjacent = field.adjacentAnimalLocations(animal.getLocation());
		Iterator<Location> it = adjacent.iterator();
		while (it.hasNext()) {
			Location where = it.next();
			Object obj = field.getAnimalAt(where);
			if (obj instanceof Animal) {
				Animal near = (Animal) obj;

				if (near.getFoodChainLevel() < animal.getFoodChainLevel()) {
					if (near.isAlive()) {
						near.setDead();
						animal.setFoodLevel(near.getFoodValue() + additionalFoodValue);
						return where;
					}
				}

				if (near.getFoodChainLevel() == animal.getFoodChainLevel()) {
					if (near.isAlive() && cannibal) {
						if (animal.getFoodLevel() < 2 && near.getClass().equals(animal.getClass())) {
							near.setDead();
							animal.setFoodLevel(near.getFoodValue() + additionalFoodValue);
							return where;
						}
					}
				}
			}
		}
		return null;
	}

}
