package ddth.dasp.id.spring;

import ddth.dasp.id.IdGenerator;

public class Id64HexApihandler extends AbstractIdApiHandler {

	private final static int PADDING = 16;

	public Id64HexApihandler() {
	}

	public Id64HexApihandler(IdGenerator idGen) {
		super(idGen);
	}

	@Override
	protected Object internalHandleApiCall(Object params, String authKey)
			throws Exception {
		long id = getIdGenerator().generateId64();
		StringBuffer hex = new StringBuffer(Long.toHexString(id));
		while (hex.length() < PADDING) {
			hex.insert(0, '0');
		}
		return hex.toString();
	}
}
