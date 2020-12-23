package com.lz.cache;

import com.lz.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * @author 乐。
 */
@Slf4j
public class MyRedisCache implements Cache {

    private String cacheName;

    public MyRedisCache() {
    }
    public MyRedisCache(String name){
        this.cacheName = name;
    }

    @Override
    public Object get(Object o) throws CacheException {
        log.debug("get cache form key: {}",o);
        return getRedisTemplate().opsForValue().get(o.toString());
    }

    @Override
    public Object put(Object o, Object o2) throws CacheException {
        log.debug("put cache : key->{} value->{}",o,o2);
        getRedisTemplate().opsForValue().set(o.toString(),o2);
        return null;
    }

    @Override
    public Object remove(Object o) throws CacheException {
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set keys() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }

    private RedisTemplate getRedisTemplate(){
        return SpringBeanUtil.getBean("redisTemplate",RedisTemplate.class);
    }
}
