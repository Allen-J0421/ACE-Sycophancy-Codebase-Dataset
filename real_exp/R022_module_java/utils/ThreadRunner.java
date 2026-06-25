package utils;

import controller.Simulator;

/**
 * ThreadRunner so you can start the simulation from the GUI.
 *
 */
public class ThreadRunner implements Runnable {
    private boolean isRunning;
    private Simulator simulator;

    /**
     * Constructor for ThreadRunner
     * @param simulator the driver of the simulation
     */
    public ThreadRunner(Simulator simulator) {
        this.simulator = simulator;
    }

    /**
     * Start the run if it's not running already
     */
    public void startRun() {
        if (!isRunning) {
            new Thread(this).start();
        }
    }

    /**
     * Stop the run
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Return whether the simulation is running.
     */
    public boolean getRunning() {
        return isRunning;
    }

    /**
     * Run the simulation concurrently.
     */
    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            simulator.simulateStep();

            try {
				Thread.sleep(10);
			} catch (Exception e) {
            	System.out.println(e.getStackTrace());
			}
        }
        isRunning = false;
    }
}
