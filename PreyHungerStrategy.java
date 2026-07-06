public class PreyHungerStrategy implements HungerStrategy {


	public Location findFood(Animal animal) {
		Field field = animal.getField();
		Location where = animal.getLocation();
		Object plantObj = field.getPlantAt(where);
		if (plantObj instanceof Plant) {
			Plant nearPlant = (Plant) plantObj;
			if (nearPlant.canEat()) {
				nearPlant.reduceStage();
				animal.setFoodLevel(8);
			}
		}
		return null;
	}

}
