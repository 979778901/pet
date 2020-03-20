package com.wang.pet.controller;

import com.alibaba.fastjson.JSONObject;
import com.wang.pet.util.ShouquanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user/wechat")
public class WeChatController {

    @PostMapping("/getPhone")
    public JSONObject getPhone(
            @RequestParam("encryptedData")String encryptedData,
            @RequestParam("iv")String iv,
            @RequestParam("code")String code
    ){
        String appid = "wx198e524778b7d6f2";
        String secret = "6f5d27019d2f263dd9459b0cce5cb6e6";
        String grantType = "authorization_code";
        String shouquanUrl = "https://api.weixin.qq.com/sns/jscode2session";
        return ShouquanUtil.wechatShouquanGetMobile(
                appid,
                secret,
                grantType,
                shouquanUrl,
                code,
                encryptedData,
                iv
        );
    }

}
