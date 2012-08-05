package ddth.dasp.id.api;

import java.util.Map;

import ddth.dasp.common.id.IdGenerator;

public class Id48HexApihandler extends AbstractIdApiHandler {

    private final static int PADDING = 12;

    public Id48HexApihandler() {
    }

    public Id48HexApihandler(IdGenerator idGen) {
        super(idGen);
    }

    @Override
    protected Object internalHandleApiCall(Object params, String authKey) {
        StringBuffer hex = new StringBuffer(getIdGenerator().generateId48Hex());
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
