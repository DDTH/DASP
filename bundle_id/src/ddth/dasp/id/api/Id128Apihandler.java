package ddth.dasp.id.api;

import ddth.dasp.common.id.IdGenerator;

public class Id128Apihandler extends AbstractIdApiHandler {

    public Id128Apihandler() {
    }

    public Id128Apihandler(IdGenerator idGen) {
        super(idGen);
    }

    @Override
	protected Object internalCallApi(Object params, String authKey,
			String remoteAddr) {
        return getIdGenerator().generateId128().toString();
    }
}
