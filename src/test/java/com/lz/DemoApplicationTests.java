package com.lz;

import com.lz.aliyun.ALiYunPropertiesUtil;
import com.lz.entity.User;
import com.lz.service.UserService;
import com.lz.util.RandomUtil;
import com.lz.util.RedisUtil;
import com.lz.util.securityutil.SHA256Util;
import com.lz.util.SpringBeanUtil;
import com.lz.util.securityutil.SystemUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class DemoApplicationTests {


    @Autowired
    UserService userService;

    @Autowired
    RedisUtil redisUtil;


    @Test
    void testTemplateCode(){
        System.out.println(ALiYunPropertiesUtil.MESSAGE_TEMPLATE_CODE);
    }
    @Test
    void testCacheAble(){

    }
    @Test
    void testRandom(){
        System.out.println(RandomUtil.getFourBitRandom());
        System.out.println(RandomUtil.getSixBirRandom());
    }

    @Test
    void testDistinct() {
        List<String> list = Arrays.asList("AA", "BB", "CC", "BB", "CC", "AA", "AA");
        long l = list.stream().distinct().count();
        System.out.println("No. of distinct elements:" + l);
        String output = list.stream().distinct().collect(Collectors.joining(","));
        System.out.println(output);
    }


    @Test
    void testSpringBeanUtil() {
        System.out.println(SpringBeanUtil.getApplicationContext());
        System.out.println(SpringBeanUtil.getBean("redisTemplate", RedisTemplate.class));
    }

    @Test
    void contextLoads() {
        List<User> list = userService.list();
        list.forEach(System.out::println);
    }

    @Test
    void testHash() {
        System.out.println(SHA256Util.sha256("123456", "test").length());
        System.out.println(SHA256Util.sha256("123456", "test"));
    }

    @Test
    void testScan() {
        Set<String> sessions = redisUtil.scan("shiro:session:*");
        sessions.forEach(System.out::println);

        System.out.println("-----------------------------------");
        Long sCnt = redisUtil.scanSize("shiro:session:*");
        System.out.println("session数目：" + sCnt);
    }

    @Test
    void testDel() {
        redisUtil.del("shiro:session:f3bd6ce1-358d-4fd8-a2a3-0c7915f1b0be");
        testScan();
    }

    @Test
    void testMD5() {
        System.out.println(SystemUtil.md5("123456", "test"));
    }

    @Test
    void testGetSalt() throws NoSuchAlgorithmException {
        System.out.println(SHA256Util.getSalt());
        System.out.println(SHA256Util.getSalt().length());
        System.out.println(SHA256Util.getSalt());
        System.out.println(SHA256Util.getSalt().length());
    }

}
