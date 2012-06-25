package ddth.dasp.id.api;

import java.math.BigInteger;
import java.util.Map;

import ddth.dasp.common.id.IdGenerator;

public class Id128HexApihandler extends AbstractIdApiHandler {

    private final static int PADDING = 32;

    public Id128HexApihandler() {
    }

    public Id128HexApihandler(IdGenerator idGen) {
        super(idGen);
    }

    @Override
    protected Object internalHandleApiCall(Object params, String authKey) {
        BigInteger id = getIdGenerator().generateId128();
        StringBuffer hex = new StringBuffer(id.toString(16));
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
