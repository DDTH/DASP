package ddth.dasp.id.api;

import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.framework.api.AbstractApiHandler;

public abstract class AbstractIdApiHandler extends AbstractApiHandler {

	private IdGenerator idGen;

	public AbstractIdApiHandler() {
	}

	public AbstractIdApiHandler(IdGenerator idGen) {
		this.idGen = idGen;
	}

	public void setIdGenerator(IdGenerator idGen) {
		this.idGen = idGen;
	}

	protected IdGenerator getIdGenerator() {
		return idGen;
	}
}
