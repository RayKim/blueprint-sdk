/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.queue;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Simple Message Queue.
 * 
 * @author Sangmin Lee
 * @since 2013. 9. 4.
 */
public class MessageQueue implements Queue<String> {
	/** actual queue */
	protected LinkedList<Element> queue = new LinkedList<Element>();

	/** waiting consumers */
	protected LinkedList<Thread> waiters = new LinkedList<Thread>();

	public MessageQueue() {
		super();
	}

	@Override
	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}

	@Override
	public void push(String element) {
		if (element == null) {
			throw new NullPointerException("Can't push null");
		}

		Element item = new Element();
		item.uuid = UUID.randomUUID().toString();
		item.content = element;

		synchronized (queue) {
			queue.push(item);
		}
		notifyWaiter();
	}

	/**
	 * Retrieves an element from queue.
	 * 
	 * @return queue element or null(queue is empty)
	 */
	public String pop() {
		String result = null;

		try {
			Element element;
			synchronized (queue) {
				element = queue.pop();
			}

			if (element != null) {
				result = element.content;
			}
		} catch (NoSuchElementException ignored) {
			// just return null
		}

		return result;
	}

	@Override
	public String take() {
		String result = null;
		Thread current = null;

		result = pop();

		if (result == null) {
			current = Thread.currentThread();
			synchronized (waiters) {
				waiters.add(current);
			}

			synchronized (current) {
				try {
					current.wait();
				} catch (InterruptedException ignored) {
				}

				result = pop();
			}
		}

		return result;
	}

	/**
	 * Wake up a blocked thread
	 */
	protected void notifyWaiter() {
		try {
			Thread consumer = null;
			synchronized (waiters) {
				consumer = waiters.pop();
			}

			if (consumer != null) {
				synchronized (consumer) {
					consumer.notifyAll();
				}
			}
		} catch (NoSuchElementException ignored) {
		}
	}

	/**
	 * release all threads which is block by {@link MessageQueue#take()}
	 */
	public void release() {
		synchronized (waiters) {
			while (!waiters.isEmpty()) {
				Thread waiter = waiters.pop();
				if (waiter != null) {
					synchronized (waiter) {
						waiter.notifyAll();
					}
				}
			}
		}
	}

	@Override
	public int size() {
		synchronized (queue) {
			return queue.size();
		}
	}
}

/**
 * Internal element of MessageQueue
 * 
 * @author Sangmin Lee
 * @since 2013. 8. 27.
 */
class Element {
	/** UUID of content */
	public String uuid;
	/** actual queue content */
	public String content;
}