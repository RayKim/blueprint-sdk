/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.spool;

import java.io.IOException;

import org.apache.log4j.Logger;

import blueprint.sdk.google.gcm.GcmResponse;
import blueprint.sdk.google.gcm.GcmResponseDetail;
import blueprint.sdk.google.gcm.GcmSender;
import blueprint.sdk.google.gcm.bind.Request;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles GCM error
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 11.
 */
public class GcmErrorHandler {
	private static final Logger L = Logger.getLogger(GcmErrorHandler.class);

	/** Jackson ObjectMapper */
	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * Handles errors in GCM's response
	 * 
	 * @param request
	 *            request message to GCM (json)
	 * @param response
	 *            return value of {@link GcmSender}
	 */
	public void handlerGcmError(String request, GcmResponse response) {
		if (response.failure > 0) {
			String[] regIds = getRegIds(request);

			for (int i = 0; i < response.results.size(); i++) {
				GcmResponseDetail detail = response.results.get(i);

				if (!detail.success) {
					logGcmError(regIds[i], detail);
				}
			}
		}
	}

	protected void logGcmError(String regId, GcmResponseDetail detail) {
		L.info("GCM error on id '" + regId + "' - " + detail.message);
	}

	/**
	 * Handles I/O error with GCM
	 * 
	 * @param request
	 *            request message to GCM (json)
	 * @param exIo
	 *            related exception
	 */
	public void handleIoError(String request, IOException exIo) {
		String[] regIds = getRegIds(request);

		for (String regId : regIds) {
			logIoError(exIo, regId);
		}
	}

	protected void logIoError(IOException exIo, String regId) {
		L.info("Can't send to id '" + regId + "' due to " + exIo.getMessage());
	}

	/**
	 * @param request
	 *            request message to GCM (json)
	 * @return array of registration id
	 */
	private String[] getRegIds(String request) {
		String[] result = null;

		try {
			Request binding = mapper.readValue(request, Request.class);
			result = binding.registration_ids;
		} catch (IOException e) {
			L.warn("Can't read json - " + request, e);
			result = new String[] {};
		}

		return result;
	}
}
