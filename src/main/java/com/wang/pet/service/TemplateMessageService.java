package com.wang.pet.service;
 
 
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wang.pet.entity.AccessToken;
import com.wang.pet.util.HttpUtil;
import com.wang.pet.util.WeixinUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
 
public class TemplateMessageService {
 
    public static void sendTemp( String accessToken, String title, String state, String remark, String openId) {
        // 封装要发送的json
        Map<String, Object> map = new HashMap();
        map.put("touser", openId);//你要发送给某个用户的openid 前提是已关注该公众号,该openid是对应该公众号的，不是普通的openid
        map.put("template_id", "OTwlQq8eTci_hh8xLDuypRZihTJjXMe2uQTskZ_QFUA");//模板消息id
        //map.put("url","https://www.vipkes.cn");//用户点击模板消息，要跳转的地址
        // 封装miniprogram 跳转小程序用,不跳不要填
        Map<String, String> mapA = new HashMap<>();
        mapA.put("appid", ""); //小程序appid
        mapA.put("pagepath", ""); //小程序页面pagepath
        map.put("miniprogram", mapA);
 
        // 以下就是根据模板消息的格式封装好，我模板的是：问题反馈结果通知  可以和我一样试试
        // 封装first
        Map firstMap = new HashMap();
        firstMap.put("value", title); //内容
        firstMap.put("color", "#173177"); //字体颜色
 
        // 封装keyword1 提交的问题
        Map keyword1Map = new HashMap();
        keyword1Map.put("value", state);
        keyword1Map.put("color", "#173177");
 
        // 封装keyword2此处也可以是商品名
        Map keyword2Map = new HashMap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword2Map.put("value", simpleDateFormat.format(new Date()));
        keyword2Map.put("color", "#173177");
 

        Map remarkMap = new HashMap();
        remarkMap.put("value", remark);
        remarkMap.put("color", "#173177");

        // 封装data
        Map dataMap = new HashMap();
        dataMap.put("first", firstMap);
        dataMap.put("keynote1", keyword1Map);
        dataMap.put("keynote2", keyword2Map);
        dataMap.put("remark", remarkMap);
 
        map.put("data", dataMap);
        String r = HttpUtil.getJsonData(JSONObject.parseObject(JSON.toJSONString(map)), "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken); //发送模板消息，这里有个工具类，我给你哟
        System.out.println("-->" + r);
    }

    public static void main(String[] args) {
        // 第三方用户唯一凭证
        String appId = "wxa9d15e765cbb1b24";
        // 第三方用户唯一凭证密钥
        String appSecret = "e1d8d824ba5d73b26c7b52bbd853ef3d";

        // 调用接口获取access_token
        //AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        //System.out.println(at);
        sendTemp(
                "31_Wu0HKX0gWkWysOWKzDgbaQAO6WmLR9lJSLA3_5lmunGgQzD1dQR9kEIPkJHEtOFKxlPpTyqhB_nfil_Lf8nbQrqYpOO8KtwZL4Y4vFESjGjWy7ZxXZOEwGbHj7JvhXpi3ZXa7_3PRvqPms8OXGScAAAFOA",
                "青牧养殖有新的预约,请尽快处理!",
                "新生成",
                "请尽快与用户联系确定服务时间。",
                "o0HBks5OrE19J7c2RWBVjeCYLd3k"
        );
    }
 
}