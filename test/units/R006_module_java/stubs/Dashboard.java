import java.util.HashMap;
import java.util.TreeMap;

/**
 * Test stub replacing the JavaFX Dashboard (javafx is not installed, so the
 * real file is auto-excluded from compilation). Same constructor and
 * updateDashboard() signature as the baseline; does nothing. The Dashboard is
 * only created when the user presses the view's "Dashboard" button, so the
 * simulation never reaches this stub during a driver run.
 */
public class Dashboard
{
    public Dashboard(HashMap<Class, Counter> counters, TreeMap<Integer, Integer> diseaseStats)
    {
    }

    public void updateDashboard()
    {
    }
}
