package ddth.dasp.id.api;

import java.util.Map;

import ddth.dasp.common.id.IdGenerator;

public class Id64HexApihandler extends AbstractIdApiHandler {

	private final static int PADDING = 16;

	public Id64HexApihandler() {
	}

	public Id64HexApihandler(IdGenerator idGen) {
		super(idGen);
	}

	@Override
	protected Object internalCallApi(Object params, String authKey,
			String remoteAddr) {
		StringBuffer hex = new StringBuffer(getIdGenerator().generateId64Hex());
		if (params instanceof Map<?, ?>) {
			Map<?, ?> tempMap = (Map<?, ?>) params;
			if (tempMap.get("padding") != null) {
				while (hex.length() < PADDING) {
					hex.insert(0, '0');
				}
			}
		}
		return hex.toString();
	}
}
