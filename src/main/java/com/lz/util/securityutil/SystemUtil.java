package com.lz.util.securityutil;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * @author 乐。
 */
public class SystemUtil {

    public final static String HASH_ALGORITHM_NAME="md5";

    public final static int HASH_ITERATIONS = 2;

    private SystemUtil(){};

    public static String md5(String password,String salt){
       return new SimpleHash(HASH_ALGORITHM_NAME,password, salt,HASH_ITERATIONS)
               .toHex();
    }
}
