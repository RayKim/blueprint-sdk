/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import blueprint.sdk.util.Validator;

/**
 * Creates java.sql.Connection instance for MDB file(MS Access)
 * 
 * @author Sangmin Lee
 * @since 2007. 08. 07
 */
public class MdbConnector {
	private static final String JDBC_DRIVER_CLASS = "sun.jdbc.odbc.JdbcOdbcDriver";
	private static final String EMPTY_STRING = "";
	private static final String URL_PREFIX = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";

	private transient Connection connection = null;

	public boolean connect(final String mdbPath) throws ClassNotFoundException {
		// Initialize the JdbcOdbc Bridge Driver
		Class.forName(JDBC_DRIVER_CLASS);

		boolean result = false;
		try {
			// DSN & Property set
			connection = getAccessDBConnection(mdbPath);
			result = true;
		} catch (SQLException se) {
			close();
		}

		return result;
	}

	private Connection getAccessDBConnection(final String strDBPath) throws SQLException {
		String path = strDBPath.replace('\\', '/').trim();
		String databaseURL = URL_PREFIX + path;

		return DriverManager.getConnection(databaseURL, EMPTY_STRING, EMPTY_STRING);
	}

	public Connection getConnection() {
		return connection;
	}

	public void close() {
		try {
			if (Validator.isNotNull(connection)) {
				connection.close();
			}
		} catch (SQLException ignored) {
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();

		super.finalize();
	}
}