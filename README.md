# redis客户端

> 缓存机制会有很多模块需要使用，当多个项目分布式时，考虑需要创建多个redis，使得重复redis客户端的出现，且可能版本不一，会出现许多不可逆的BUG；

> 封装一个基于jedis的客户端，jedis也是一个框架，内置封装了redis，且性能稳定，可单机使用或者集群，统一封装使用，避免多个客户端多个版本的事情发生；封装的客户端中，实现了通用且常见的redis方法。

> 客户端中有个方法参数是个接口，`INullTarget`，这个接口用于回调，当redis的值为空时，执行数据库查询

## 使用坏境

1. JDK：1.8

2. maven：3.x.x

3. Spring Boot：1.5.9

## 引入pom

将代码拉取到本地，运行maven命令打成jar包，在其他项目的pom文件中引入如下：

```xml
        <dependency>
            <groupId>com.ruxuanwo</groupId>
            <artifactId>redis</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```

## 基本使用

1. 在`application.properties`文件中加入配置信息

   ```properties
   #redis单机配置
   # Redis数据库索引（一般一共为16个，默认为0，即使用第一个）
   spring.jedis.database=0
   # Redis服务器地址
   spring.jedis.host=redis所在服务器地址

   # Redis服务器连接端口
   spring.jedis.port=6379
   # Redis服务器连接密码（默认为空）
   spring.jedis.password=
   # 连接池中的最大空闲连接(默认为8)
   spring.jedis.maxIdle=100
   # 连接池中的最小空闲连接(默认为0)
   spring.jedis.minIdle=10
   #连接超时时间（毫秒）
   spring.jedis.timeout=3000
   # 连接池最大连接数（使用负值表示没有限制）
   spring.jedis.maxTotal= 100
   # 连接池最大阻塞等待时间（毫秒）（使用负值表示没有限制）
   spring.jedis.maxWaitMillis = 100000
   ```

2. 在需要使用到的Spring Bean中添加

   ```java
       @Autowired
       private RedisClient redisClient;
   ```

## 方法使用

```java
    /**
     * 存入缓存中，默认永不过期

     * @param key 

     * @param value
     * @param <T>
     * @return
     */
    <T> Boolean set(String key, T value);

    /**
     * 该命令会移除原来的key的expire time
     *
     * @param key
     * @param value
     * @param seconds 时间，以秒为单位
     * @param <T>
     * @return
     */
    <T> Boolean set(String key, T value, int seconds);

    /**
     * 存储数据到缓存中，并制定过期时间和当Key存在时是否覆盖。
     *
     * @param key
     * @param value
     * @param nxxx   nxxx的值只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，
     *               则只有当key已经存在时才进行set
     * @param expx   expx的值只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
     * @param expire 过期时间，单位是expx所代表的单位。
     * @return
     */
    <T> Boolean set(String key, T value, String nxxx, String expx,
                    final Long expire);

    /**
     * 根据key获取缓存中的信息
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T get(String key);

    /**
     * 根据key获取缓存中string类型的信息
     *
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 根据key获取给定类型的结果
     *
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    <T> T get(String key, Class<T> t);

    /**
     * 获取数据，当数据不存在，可实现INullTarget查询数据库

     *
     * @param key
     * @param t
     * @param target
     * @param second 过期时间
     * @param <T>
     * @return
     */
    <T> T get(String key, Class<T> t, INullTarget target, int second);

    /**
     * 获取数据，当数据不存在，可实现INullTarget查询数据库

     * @param key
     * @param t
     * @param target
     * @param <T>
     * @return
     */
    <T> T get(String key, Class<T> t, INullTarget target);

    /**
     * 获取数据集合

     * @param key
     * @param t
     * @param target
     * @param <T>
     * @return
     */
    <T> List<T> getList(String key, Class<T> t, INullTarget target);

    /**
     * @param key
     * @param t
     * @param target
     * @param second 过期时间
     * @param <T>
     * @return
     */
    <T> List<T> getList(String key, Class<T> t, INullTarget target, int second);

    /**
     * redis批量删除以某字符串前缀的key
     *
     * @param kyePrefixes
     */
    void batchDelByKyePrefixes(String kyePrefixes);

    /**
     * redis删除key
     *
     * @param key
     */
    void delByKey(String key);

    /**
     * 返回所有的keys
     *
     * @return
     */
    Set<String> getKeys();

    /**
     * 返回所有以某字符串前缀的的keys
     *
     * @return
     */
    Set<String> getKeysByPrefixes(String kyePrefixes);

    /**
     * 设置key的过期时间，单位：秒
     *
     * @param key
     * @param seconds
     * @return
     */
    void expire(String key, int seconds);

    /**
     * redis批量设置以某字符串前缀的key的过期时间
     *
     * @param kyePrefixes
     */
    void batchExpireByKyePrefixes(String kyePrefixes, int seconds);

    /**
     * 检查key是否存在
     *
     * @param key
     * @return
     */
    Boolean existsKey(String key);

    /**
     * 返回key剩余的过期时间 - 1:永不过期 -2：key不存在，否则，以毫秒为单位，返回 key 的剩余生存时间
     *
     * @param key
     * @return -1:永不过期 -2：key:不存在
     */
    Long pttl(String key);

    /**
     * 将 key 中储存的数字值增一
     *
     * @param key
     * @return
     */
    Long incr(String key);

    /**
     * 返回给定 key 的剩余生存时间
     *
     * @param key
     * @return
     */
    Long ttl(String key);

    /**
     * 返回哈希表 key 中给定域 field 的值
     *
     * @param key
     * @param field
     * @return
     */
    String hget(String key, String field);

    /**
     * 将哈希表 key 中的域 field 的值设为 value
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    Long hset(String key, String field, String value);

    /**
     * 删除哈希表 key 中的一个或多个指定域
     *
     * @param key
     * @param fields
     * @return
     */
    Long hdel(String key, String... fields);

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key
     * @param filed
     * @return
     */
    Boolean hexists(String key, String filed);

    /**
     * 返回哈希表 key 中，所有的域和值
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> Map<String, T> hgetall(String key);

    /**
     * 返回哈希表 key 中的所有域
     *
     * @param key
     * @return
     */
    Set<String> hkeys(String key);

    /**
     * 返回哈希表 key 中域的数量
     *
     * @param key
     * @return
     */
    Long hlen(String key);

    /**
     * 返回哈希表 key 中，一个或多个给定域的值
     *
     * @param key
     * @param fields
     * @return
     */
    List<String> hmget(String key, String... fields);

    /**
     * 清空当前数据库中的所有 key
     *
     * @return
     */
    void flushDB();

    /**
     * 清空整个 Redis 服务器的数据(删除所有数据库的所有 key )
     *
     * @return
     */
    void flushAll();
```


