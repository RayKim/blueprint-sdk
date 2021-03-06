/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for ByteArrayPool
 * 
 * @author Sangmin Lee
 * @since 2009. 1. 20.
 */
public class ByteArrayFactory {
	private static Map<Integer, ByteArrayPool> poolMap = new HashMap<Integer, ByteArrayPool>();

	/**
	 * @param arraySize
	 *            each array's size
	 * @param maxArrays
	 *            maximum number of arrays to preserve
	 * @return
	 */
	public static synchronized ByteArrayPool getInstance(final int arraySize, final int maxArrays) {
		ByteArrayPool result;

		if (poolMap.containsKey(arraySize)) {
			result = poolMap.get(arraySize);
		} else {
			result = new ByteArrayPool(arraySize, maxArrays);
			poolMap.put(arraySize, result);
		}

		return result;
	}
}
