package com.lz.service;

import java.util.Map;

/**
 * @author 乐。
 */
public interface MsmService {

    boolean send(Map<String,Object> param,String phone);

}
