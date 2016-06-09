package vs.gerriet.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Queue that handles added elements asynchronous using an consumer.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 *
 * @param <T>
 *            Queue element type.
 */
public abstract class AsyncConsumerQueue<T> {
    /**
     * Consumer used to process added objects asynchronous.
     */
    protected Consumer<T> consumer = null;

    /**
     * Executor service containing the actual queue and taking care about
     * executing the consumer for all elements in the order they where added.
     * This service also only processes one element at a time.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Creates a new asynchronous queue with the given consumer. Added objects
     * are handled one after another in order they where added.
     *
     * @param consumer
     *            Consumer to process elements from the queue.
     */
    public AsyncConsumerQueue(final Consumer<T> consumer) {
        this.consumer = consumer;
    }

    /**
     * Creates a new asynchronous queue without a consumer. You have to set the
     * {@link #consumer} manually before pushing elements.
     */
    protected AsyncConsumerQueue() {
        this.consumer = null;
    }

    /**
     * Adds the given object to this queue to be processed by the contained
     * consumer.
     *
     * @param object
     *            Object to be processed.
     */
    public synchronized void push(final T object) {
        this.executor.submit(() -> {
            this.consumer.accept(object);
        });
    }
}
