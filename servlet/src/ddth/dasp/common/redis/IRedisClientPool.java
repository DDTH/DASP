package ddth.dasp.common.redis;

public interface IRedisClientPool {
    public IRedisClient borrowRedisClient();

    public void returnRedisClient(IRedisClient redisClient);
}
