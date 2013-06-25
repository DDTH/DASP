package ddth.dasp.common.hazelcastex;

public interface IMessageListener<E> {
    public void unsubscribe();

    public void onMessage(E message);
}
