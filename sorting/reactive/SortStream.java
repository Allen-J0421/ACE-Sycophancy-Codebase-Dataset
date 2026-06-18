package sorting.reactive;

import java.util.Comparator;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class SortStream<T extends Comparable<T>> {
    private final int batchSize;
    private final SubmissionPublisher<T[]> publisher;

    public SortStream(int batchSize) {
        this.batchSize = batchSize;
        this.publisher = new SubmissionPublisher<>();
    }

    public void subscribe(Consumer<T[]> consumer) {
        publisher.subscribe(new Flow.Subscriber<T[]>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription sub) {
                this.subscription = sub;
                subscription.request(1);
            }

            @Override
            public void onNext(T[] item) {
                consumer.accept(item);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Stream error: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Stream complete");
            }
        });
    }

    public void publishBatch(T[] batch) {
        publisher.submit(batch);
    }

    public void close() {
        publisher.close();
    }

    public static class ReactiveSort<T extends Comparable<T>> {
        private final SortStream<T> stream;
        private final Comparator<T> comparator;

        public ReactiveSort(int batchSize, Comparator<T> comparator) {
            this.stream = new SortStream<>(batchSize);
            this.comparator = comparator;
        }

        public void subscribeTo(Consumer<T[]> processor) {
            stream.subscribe(array -> {
                sortBatch(array);
                processor.accept(array);
            });
        }

        public void processBatch(T[] batch) {
            stream.publishBatch(batch);
        }

        public void close() {
            stream.close();
        }

        private void sortBatch(T[] array) {
            if (array.length > 1) {
                java.util.Arrays.sort(array, comparator);
            }
        }
    }
}
