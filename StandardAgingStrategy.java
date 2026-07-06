public class StandardAgingStrategy implements AgingStrategy {


	private final int maxAge;

	private int age;


	public StandardAgingStrategy(int initialAge, int maxAge) {
		this.age = initialAge;
		this.maxAge = maxAge;
	}


	public void tick(Animal animal) {
		age++;
		if (age > maxAge) {
			animal.setDead();
		}
	}


	public int getAge() {
		return age;
	}

}
