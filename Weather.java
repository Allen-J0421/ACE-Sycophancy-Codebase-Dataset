
public enum Weather {
	SUN,
	CLOUD,
	RAIN;


	public boolean buildsHumidity() {
		return this == SUN || this == CLOUD;
	}
}
