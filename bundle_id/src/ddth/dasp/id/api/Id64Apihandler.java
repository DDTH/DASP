package ddth.dasp.id.api;

import ddth.dasp.common.id.IdGenerator;

public class Id64Apihandler extends AbstractIdApiHandler {

	public Id64Apihandler() {
	}

	public Id64Apihandler(IdGenerator idGen) {
		super(idGen);
	}

	@Override
	protected Object internalHandleApiCall(Object params, String authKey,
			String remoteAddr) {
		return getIdGenerator().generateId64();
	}
}
