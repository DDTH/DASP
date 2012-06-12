package ddth.dasp.id.spring;

import ddth.dasp.id.IdGenerator;

public class Id128Apihandler extends AbstractIdApiHandler {

	public Id128Apihandler() {
	}

	public Id128Apihandler(IdGenerator idGen) {
		super(idGen);
	}

	@Override
	protected Object internalHandleApiCall(Object params, String authKey)
			throws Exception {
		return getIdGenerator().generateId128().toString();
	}
}
