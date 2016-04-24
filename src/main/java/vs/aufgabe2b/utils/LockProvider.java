package vs.aufgabe2b.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Thread save basic implementation of the {@link Lockable} interface.
 *
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public abstract class LockProvider {

    /**
     * Internal lock.
     */
    private final Semaphore semaphore;

    /**
     * Creates the internal lock.
     */
    public LockProvider() {
	// use a fair (first in - first out) semaphore with one access at a time
	// for locking.
	this.semaphore = new Semaphore(1, true);
    }

    /**
     * Locks this instance. This method blocks until the instance was locked.
     * 
     * Uses {@link #lock(long, TimeUnit)} with a timeout of <code>100</code>
     * {@link TimeUnit#MILLISECONDS}.
     * 
     * @throws InterruptedException
     *             If the thread was interrupted.
     * @return <code>true</code> if this instance could be locked,
     *         <code>false</code> otherwise.
     * @see #lock(long, TimeUnit)
     */
    public boolean lock() throws InterruptedException {
	// use default timeout of 1 second
	return this.lock(100, TimeUnit.MILLISECONDS);
    }

    /**
     * Locks this instance. This method blocks until the instance was locked or
     * the timeout occurs.
     * 
     * @param timeout
     *            Timeout amount.
     * @param unit
     *            Timeout time unit.
     * @throws InterruptedException
     *             If the thread was interrupted.
     * @return <code>true</code> if this instance could be locked,
     *         <code>false</code> otherwise.
     */
    public synchronized boolean lock(final long timeout, final TimeUnit unit) throws InterruptedException {
	return this.semaphore.tryAcquire(timeout, unit);
    }

    /**
     * Unlocks this instance.
     */
    public synchronized void unlock() {
	this.semaphore.release();
    }
}
