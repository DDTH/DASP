package ddth.dasp.handlersocket.bo.hs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryConfig implements Cloneable {

	@Override
	public QueryConfig clone() {
		try {
			QueryConfig obj = (QueryConfig) super.clone();
			obj.props = new HashMap<String, Object>();
			obj.columns = null;
			obj.columnNames = null;
			obj.populate(this.props);
			return obj;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	// private final static String[] EMPTY_LIST = new String[0];
	private final static String PROP_DB_NAME = "db";
	private final static String PROP_TABLE_NAME = "table";
	private final static String PROP_INDEX_NAME = "index";
	private final static String PROP_COLUMNS = "columns";

	private String[] columnNames;
	private ColumnConfig[] columns;
	private Map<String, Object> props = new HashMap<String, Object>();

	public void populate(Map<String, Object> props) {
		this.props.putAll(props);
	}

	protected String getProperty(final String key) {
		Object value = props.get(key);
		return value != null ? value.toString() : null;
	}

	protected void setProperty(final String key, final Object value) {
		props.put(key, value);
	}

	public String getDbName() {
		return getProperty(PROP_DB_NAME);
	}

	public void setDbName(String dbName) {
		setProperty(PROP_DB_NAME, dbName);
	}

	public String getTableName() {
		return getProperty(PROP_TABLE_NAME);
	}

	public void setTableName(String tableName) {
		setProperty(PROP_TABLE_NAME, tableName);
	}

	public String getIndexName() {
		return getProperty(PROP_INDEX_NAME);
	}

	public void setIndexName(String indexName) {
		setProperty(PROP_INDEX_NAME, indexName);
	}

	public String[] getColumnNames() {
		if (columnNames == null) {
			ColumnConfig[] columns = getColumns();
			columnNames = new String[columns.length];
			for (int i = 0; i < columns.length; i++) {
				columnNames[i] = columns[i].getName();
			}
		}
		return columnNames;
	}

	@SuppressWarnings("unchecked")
	public ColumnConfig[] getColumns() {
		if (columns == null) {
			Map<String, String>[] colData;
			Object temp = props.get(PROP_COLUMNS);
			if (temp instanceof List<?>) {
				colData = ((List<?>) temp).toArray(new Map[0]);
			} else if (temp instanceof Object[]) {
				colData = (Map<String, String>[]) temp;
			} else {
				colData = new Map[0];
			}
			columns = new ColumnConfig[colData.length];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = new ColumnConfig(colData[i].get("name"),
						colData[i].get("mapped_name"), colData[i].get("type"));
			}
		}
		return columns;
	}
}
