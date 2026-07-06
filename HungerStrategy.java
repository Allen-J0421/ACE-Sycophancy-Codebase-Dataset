public interface HungerStrategy {

	Location findFood(Animal animal);

	default void tick(Animal animal) {
		animal.setFoodLevel(animal.getFoodLevel() - 1);
		if (animal.getFoodLevel() <= 0) {
			animal.setDead();
		}
	}

}
