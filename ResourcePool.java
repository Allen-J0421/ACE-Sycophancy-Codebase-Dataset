import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResourcePool<T> {
    private final BlockingQueue<T> available;
    private final ResourceFactory<T> factory;
    private final int maxSize;
    private int created = 0;

    public interface ResourceFactory<T> {
        T create();
        void destroy(T resource);
    }

    public ResourcePool(ResourceFactory<T> factory, int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        this.factory = factory;
        this.maxSize = maxSize;
        this.available = new LinkedBlockingQueue<>(maxSize);
        Logger.info("ResourcePool created with max size: " + maxSize);
    }

    public T acquire() throws InterruptedException {
        T resource = available.poll();
        if (resource == null) {
            if (created < maxSize) {
                resource = factory.create();
                created++;
                Logger.debug("Resource created, total: " + created);
            } else {
                Logger.debug("Waiting for available resource...");
                resource = available.take();
            }
        }
        return resource;
    }

    public T acquire(long timeout, TimeUnit unit) throws InterruptedException {
        T resource = available.poll();
        if (resource == null) {
            if (created < maxSize) {
                resource = factory.create();
                created++;
            } else {
                resource = available.poll(timeout, unit);
            }
        }
        return resource;
    }

    public void release(T resource) {
        if (resource != null) {
            if (!available.offer(resource)) {
                factory.destroy(resource);
                Logger.debug("Resource discarded due to pool overflow");
            }
        }
    }

    public void shutdown() {
        T resource;
        while ((resource = available.poll()) != null) {
            factory.destroy(resource);
        }
        Logger.info("ResourcePool shutdown complete");
    }

    public int getAvailableCount() {
        return available.size();
    }

    public int getCreatedCount() {
        return created;
    }
}
