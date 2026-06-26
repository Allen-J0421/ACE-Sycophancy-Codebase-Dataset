import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays current simulation status information.
 */
public class SimulationStatusPanel extends JPanel
{
    private static final String STEP_PREFIX = "Step: ";
    private static final String WEATHER_PREFIX = "Weather: ";
    private static final String TIME_PREFIX = "Time: ";

    private final JLabel stepLabel;
    private final JLabel infoLabel;
    private final JLabel weatherLabel;
    private final JLabel timeLabel;

    public SimulationStatusPanel()
    {
        super(new GridLayout(1, 4));
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);

        add(stepLabel);
        add(infoLabel);
        add(weatherLabel);
        add(timeLabel);
    }

    public void updateStatus(int step, String weather, String time)
    {
        stepLabel.setText(STEP_PREFIX + step);
        weatherLabel.setText(WEATHER_PREFIX + weather);
        timeLabel.setText(TIME_PREFIX + time);
    }

    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }
}
