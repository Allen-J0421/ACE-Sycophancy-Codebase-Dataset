package logging;


/**
 * A {@link Logger} that writes to standard output. This is the single place in
 * the codebase that touches the console, so swapping the destination (file,
 * test buffer, silence) is a one-line change at the composition root.
 */
public class ConsoleLogger implements Logger {

	@Override
	public void log(String message) {
		System.out.println(message);
	}
}
