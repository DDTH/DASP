package ddth.dasp.id.spring;

import java.math.BigInteger;

import ddth.dasp.id.IdGenerator;

public class Id128HexApihandler extends AbstractIdApiHandler {

	private final static int PADDING = 32;

	public Id128HexApihandler() {
	}

	public Id128HexApihandler(IdGenerator idGen) {
		super(idGen);
	}

	@Override
	protected Object internalHandleApiCall(Object params, String authKey)
			throws Exception {
		BigInteger id = getIdGenerator().generateId128();
		StringBuffer hex = new StringBuffer(id.toString(16));
		while (hex.length() < PADDING) {
			hex.insert(0, '0');
		}
		return hex.toString();
	}
}
