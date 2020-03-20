package com.wang.pet.controller;

import com.alibaba.fastjson.JSONObject;
import com.wang.pet.common.BaseResponse;
import com.wang.pet.util.HttpUtil;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/no")
public class NoController {

    @GetMapping("/noIsTrue")
    public BaseResponse noIsTrue(@RequestParam("no") String no) {
        NameValuePair nameValuePair = new NameValuePair();
        nameValuePair.setName("uid");
        nameValuePair.setValue(no);
        NameValuePair[]nameValuePairs = {nameValuePair};
        String post = HttpUtil.methodPost( "http://cw.sxau.edu.cn/student/student/loginQuick.asp",nameValuePairs);
        return BaseResponse.ok(post.length()>200);
    }

}
