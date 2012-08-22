package ddth.dasp.handlersocket.hsc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class AbstractHscFactory implements IHscFactory {

    private Map<String, IHsc> handlersocketConnections = new HashMap<String, IHsc>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        synchronized (handlersocketConnections) {
            IHsc[] conns = handlersocketConnections.values().toArray(new IHsc[0]);
            for (IHsc conn : conns) {
                releaseConnection(conn);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHsc getConnection(String server, int port) {
        return getConnection(server, port, true);
    }

    protected String calcHash(String server, int port, boolean readWrite) {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        long hashCode = hcb.append(server).append(port).append(readWrite).hashCode();
        return String.valueOf(hashCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHsc getConnection(String server, int port, boolean readWrite) {
        String hash = calcHash(server, port, readWrite);
        synchronized (handlersocketConnections) {
            IHsc conn = handlersocketConnections.get(hash);
            if (conn == null) {
                conn = buildConnection(hash, server, port, readWrite);
            }
            handlersocketConnections.put(hash, conn);
            return conn;
        }
    }

    /**
     * Establishes a HandlerSocket connection.
     * 
     * @param connName
     * @param server
     * @param port
     * @param readWrite
     * @return
     */
    protected abstract IHsc buildConnection(String connName, String server, int port,
            boolean readWrite);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean releaseConnection(IHsc conn) {
        String connName = conn.getName();
        synchronized (handlersocketConnections) {
            conn = handlersocketConnections.remove(connName);
            if (conn != null) {
                conn.destroy();
                return true;
            }
        }
        return false;
    }
}
