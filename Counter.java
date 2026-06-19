
public class Counter {

	private String name;

	private int count;


	public Counter(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public int getCount() {
		return count;
	}


	public void increment() {
		count++;
	}


	public void reset() {
		count = 0;
	}
}
