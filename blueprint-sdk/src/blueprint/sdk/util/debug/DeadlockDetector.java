/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.debug;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

/**
 * Periodically checks MXBean for deadlock.
 * 
 * @author Sangmin Lee
 * @since 2013. 11. 6.
 */
public class DeadlockDetector extends Thread {
	private static final Logger L = Logger.getLogger(DeadlockDetector.class);

	public DeadlockDetector() {
		super();

		setDaemon(true);
	}

	@Override
	public void run() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();

		while (true) {
			// Returns null if no threads are deadlocked.
			long[] threadIds = bean.findDeadlockedThreads();

			if (threadIds != null) {
				ThreadInfo[] infos = bean.getThreadInfo(threadIds);

				for (ThreadInfo info : infos) {
					StackTraceElement[] stack = info.getStackTrace();

					for (StackTraceElement stackTraceElement : stack) {
						L.warn(stackTraceElement.toString());
					}
				}
			}

			try {
				Thread.sleep(30000);
			} catch (InterruptedException ignore) {
			}
		}
	}
}
