package com.lz.controller;

import com.lz.dto.result.R;
import com.lz.service.MsmService;
import com.lz.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 乐。
 */
@RestController
@Api(tags = "登录注册接口")
public class LoginRegisterController {

    @Autowired
    MsmService msmService;
    @Autowired
    RedisUtil redisUtil;

    @PostMapping("/login")
    @ApiOperation(value = "登录",httpMethod = "POST")
    public R login(String username, String password){
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Object message = session.getAttribute("message");
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try {
            subject.login(usernamePasswordToken);
            return R.ok();
        }catch (Exception e){
            return R.error();
        }

    }
    @GetMapping("/toLogin")
    public String toLogin(){
        return "toLogin";
    }

   /* @GetMapping("/sendCode/{phone}")
    @ApiOperation(value = "发送短信验证码")
    public R sendCode(@PathVariable String phone){
        String code = (String) redisUtil.get(phone);
        if(!StringUtils.isBlank(code)) return R.ok();

        HashMap<String, Object> param = new HashMap<>(1);
        code = RandomUtil.getFourBitRandom();
        param.put("code", code);
        boolean isSend = msmService.send(param, phone);
        if(isSend){
            redisUtil.set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error().message("验证码发送失败");
        }
    }*/

    @GetMapping("/logout")
    public R logout(){
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        }catch (Exception e){
            return R.error();
        }
        return R.ok();
    }

}
