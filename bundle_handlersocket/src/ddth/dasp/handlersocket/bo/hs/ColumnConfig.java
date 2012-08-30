package ddth.dasp.handlersocket.bo.hs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ColumnConfig {
	public enum ColumnType {
		INT, LONG, FLOAT, DOUBLE, STRING, BINARY
	}

	private final static Map<String, ColumnType> COLUMN_TYPE_MAPPING = new HashMap<String, ColumnType>();
	static {
		COLUMN_TYPE_MAPPING.put("int", ColumnType.INT);
		COLUMN_TYPE_MAPPING.put("Int", ColumnType.INT);
		COLUMN_TYPE_MAPPING.put("INT", ColumnType.INT);
		COLUMN_TYPE_MAPPING.put("integer", ColumnType.INT);
		COLUMN_TYPE_MAPPING.put("Integer", ColumnType.INT);
		COLUMN_TYPE_MAPPING.put("INTEGER", ColumnType.INT);
		COLUMN_TYPE_MAPPING.put("long", ColumnType.LONG);
		COLUMN_TYPE_MAPPING.put("Long", ColumnType.LONG);
		COLUMN_TYPE_MAPPING.put("LONG", ColumnType.LONG);
		COLUMN_TYPE_MAPPING.put("float", ColumnType.FLOAT);
		COLUMN_TYPE_MAPPING.put("Float", ColumnType.FLOAT);
		COLUMN_TYPE_MAPPING.put("FLOAT", ColumnType.FLOAT);
		COLUMN_TYPE_MAPPING.put("double", ColumnType.DOUBLE);
		COLUMN_TYPE_MAPPING.put("Double", ColumnType.DOUBLE);
		COLUMN_TYPE_MAPPING.put("DOUBLE", ColumnType.DOUBLE);
		COLUMN_TYPE_MAPPING.put("binary", ColumnType.BINARY);
		COLUMN_TYPE_MAPPING.put("Binary", ColumnType.BINARY);
		COLUMN_TYPE_MAPPING.put("BINARY", ColumnType.BINARY);
		COLUMN_TYPE_MAPPING.put("bin", ColumnType.BINARY);
		COLUMN_TYPE_MAPPING.put("Bin", ColumnType.BINARY);
		COLUMN_TYPE_MAPPING.put("BIN", ColumnType.BINARY);
	}

	private String name, mappedName, type;

	public ColumnConfig(String name, String mappedName, String type) {
		this.name = name;
		this.mappedName = mappedName;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getMappedName() {
		return mappedName != null ? mappedName : name;
	}

	public ColumnType getType() {
		ColumnType colType = COLUMN_TYPE_MAPPING.get(type);
		return colType != null ? colType : ColumnType.STRING;
	}

	/**
	 * Gets column value from a {@link ResultSet}.
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public Object getValue(ResultSet rs) throws SQLException {
		ColumnType colType = getType();
		switch (colType) {
		case BINARY:
			return rs.getBytes(name);
		case DOUBLE:
			return rs.getDouble(name);
		case FLOAT:
			return rs.getDouble(name);
		case INT:
			return rs.getInt(name);
		case LONG:
			return rs.getLong(name);
		case STRING:
		default:
			return rs.getString(name);
		}
	}
}
