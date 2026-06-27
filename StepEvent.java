/** Published after each simulation step and after reset. */
public class StepEvent {
    public final int step;
    public final Field field;
    public final int time;
    public final String weather;

    public StepEvent(int step, Field field, int time, String weather) {
        this.step = step;
        this.field = field;
        this.time = time;
        this.weather = weather;
    }
}
