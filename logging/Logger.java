package logging;


/**
 * A destination for human-readable log lines. Abstracting the sink lets the
 * rest of the codebase record messages without knowing (or caring) whether they
 * end up on the console, in a file, or nowhere at all.
 */
@FunctionalInterface
public interface Logger {
	void log(String message);
}
