public interface SicknessStrategy {

	void tick(Animal animal);

	boolean isSick();

	void becomeSick();

}
