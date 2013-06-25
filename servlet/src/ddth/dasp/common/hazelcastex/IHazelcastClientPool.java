package ddth.dasp.common.hazelcastex;

public interface IHazelcastClientPool {
    public IHazelcastClient borrowHazelcastClient();

    public void returnHazelcastClient(IHazelcastClient hazelcastClient);
}
