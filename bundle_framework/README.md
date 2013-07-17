DASP - Framework to develop bundles for DASP

## History ##

### v0.2.0 (2013-07-17) ###

- New class *ddth.dasp.framework.utils.SerializeUtils*: serialize/deserialize Java objects using [Jboss Serialization](http://www.jboss.org/serialization).
- *RedisCache* now uses *ddth.dasp.framework.utils.SerializeUtils* to serialize/deserialize cache entries.
- *IJdbcFactory*: *getDataSource()* and *getDataSourceInfo()* now need a parameter of type *DbcpInfo*.
- New classes C3p0JdbcFactory and BoneCpJdbcFactory

### v0.1.2 (2013-06-25) ###

- *ddth.dasp.framework.cache.hazelcast*: rework this package to be compliant with *ddth.dasp.common.hazelcastex*.

### v0.1.1 (2013-06-20) ###

- *ddth.dasp.framework.cache.redis*: [Redis](http://redis.io)-based application cache.

### v0.1.0 (too old to remember) ###
