package sorting.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
public interface SortEventPublisher {
    void subscribe(Consumer<SortingEvent> listener);

    class DefaultPublisher implements SortEventPublisher {
        private final List<Consumer<SortingEvent>> subscribers;

        public DefaultPublisher() {
            this.subscribers = Collections.synchronizedList(new ArrayList<>());
        }

        @Override
        public void subscribe(Consumer<SortingEvent> listener) {
            subscribers.add(listener);
        }

        public void publish(SortingEvent event) {
            subscribers.forEach(subscriber -> subscriber.accept(event));
        }

        public int subscriberCount() {
            return subscribers.size();
        }

        public void clear() {
            subscribers.clear();
        }
    }
}
