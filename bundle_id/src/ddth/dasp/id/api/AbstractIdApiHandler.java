package ddth.dasp.id.api;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.framework.api.AbstractApiHandler;
import ddth.dasp.framework.osgi.IServiceAutoRegister;

public abstract class AbstractIdApiHandler extends AbstractApiHandler implements
		IServiceAutoRegister {

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

	@Override
	public String getClassName() {
		return IApiHandler.class.getName();
	}
}
