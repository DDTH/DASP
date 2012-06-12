package ddth.dasp.id.spring;

import ddth.dasp.framework.api.AbstractApiHandler;
import ddth.dasp.id.IdGenerator;

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
