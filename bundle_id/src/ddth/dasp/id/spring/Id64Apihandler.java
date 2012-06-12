package ddth.dasp.id.spring;

import ddth.dasp.id.IdGenerator;

public class Id64Apihandler extends AbstractIdApiHandler {

	public Id64Apihandler() {
	}

	public Id64Apihandler(IdGenerator idGen) {
		super(idGen);
	}

	@Override
	protected Object internalHandleApiCall(Object params, String authKey)
			throws Exception {
		return getIdGenerator().generateId64();
	}
}
