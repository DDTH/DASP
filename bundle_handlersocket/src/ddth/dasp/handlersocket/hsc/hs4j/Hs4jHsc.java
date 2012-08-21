package ddth.dasp.handlersocket.hsc.hs4j;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.ModifyStatement;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.impl.HSClientImpl;

import ddth.dasp.handlersocket.hsc.IHsc;

public class Hs4jHsc implements IHsc {

	private String server;
	private int port;
	private boolean readWrite;
	private HSClient hsClient;
	private Map<String, IndexSession> indexSessions = new HashMap<String, IndexSession>();

	public Hs4jHsc(String server, int port, boolean readWrite) {
		this.server = server;
		this.port = port;
		this.readWrite = readWrite;
	}

	public boolean isReadWrite() {
		return readWrite;
	}

	@Override
	public void init() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		try {
			hsClient = new HSClientImpl(server, port, availableProcessors);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			hsClient.shutdown();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			hsClient = null;
		}
	}

	protected String calcHash(String dbName, String tableName,
			String tableIndexName, String[] columns) {
		HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
		hcb.append(dbName);
		hcb.append(tableName);
		hcb.append(tableIndexName);
		hcb.append(columns);
		int hashCode = hcb.hashCode();
		return String.valueOf(hashCode);
	}

	protected IndexSession openIndex(String dbName, String tableName,
			String tableIndexName, String[] columns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		String hash = calcHash(dbName, tableName, tableIndexName, columns);
		synchronized (indexSessions) {
			IndexSession indexSession = indexSessions.get(hash);
			if (indexSession == null) {
				hsClient.openIndexSession(dbName, tableName, tableIndexName,
						columns);
			}
			return indexSession;
		}
	}

	protected void assignValue(ModifyStatement stm, int index, Object value) {
		if (value instanceof Boolean) {
			stm.setBoolean(index, (Boolean) value);
		} else if (value instanceof Byte) {
			stm.setByte(index, (Byte) value);
		} else if (value instanceof byte[]) {
			stm.setBytes(index, (byte[]) value);
		} else if (value instanceof Double) {
			stm.setDouble(index, (Double) value);
		} else if (value instanceof Float) {
			stm.setFloat(index, (Float) value);
		} else if (value instanceof Integer) {
			stm.setInt(index, (Integer) value);
		} else if (value instanceof Long) {
			stm.setLong(index, (Long) value);
		} else if (value instanceof Short) {
			stm.setShort(index, (Short) value);
		} else {
			stm.setString(index, value != null ? value.toString() : null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean insert(String dbName, String tableName,
			String tableIndexName, String[] columns, Object[] values)
			throws SQLException {
		if (!isReadWrite()) {
			String msg = "This Handler Socket connection is read-only!";
			throw new SQLException(msg);
		}
		try {
			IndexSession indexSession = openIndex(dbName, tableName,
					tableIndexName, columns);
			ModifyStatement stm = indexSession.createStatement();
			for (int i = 0; i < columns.length; i++) {
				Object value = columns[i];
				assignValue(stm, i + 1, value);
			}
			return stm.insert();
		} catch (InterruptedException e) {
			throw new SQLException(e);
		} catch (TimeoutException e) {
			throw new SQLException(e);
		} catch (HandlerSocketException e) {
			throw new SQLException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(String dbName, String tableName, String tableIndexName,
			String[] columns, Object[] values, Object[] findValues)
			throws SQLException {
		if (!isReadWrite()) {
			String msg = "This Handler Socket connection is read-only!";
			throw new SQLException(msg);
		}
		try {
			IndexSession indexSession = openIndex(dbName, tableName,
					tableIndexName, columns);
			ModifyStatement stm = indexSession.createStatement();
			for (int i = 0; i < columns.length; i++) {
				Object value = columns[i];
				assignValue(stm, i + 1, value);
			}
			String[] keys = new String[findValues.length];
			for (int i = 0; i < findValues.length; i++) {
				keys[i] = findValues[i] != null ? findValues[i].toString()
						: null;
			}
			return stm.update(keys, FindOperator.EQ);
		} catch (InterruptedException e) {
			throw new SQLException(e);
		} catch (TimeoutException e) {
			throw new SQLException(e);
		} catch (HandlerSocketException e) {
			throw new SQLException(e);
		}
	}
}
