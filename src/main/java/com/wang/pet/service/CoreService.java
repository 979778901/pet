package com.wang.pet.service;
 
import com.wang.pet.entity.Articles;
import com.wang.pet.entity.TextMessage;
import com.wang.pet.util.MessageUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
 

/**
 * 核心服务类
 * 
 * @author caoxinyu
 * @date 2013-05-20
 */
public class CoreService {
	//openid
	//曹新宇 o0HBkszCMXjebo9IaLYAiiUj7R78
	//王志强 o0HBks0D6ajbePd9tQXepqWswA4g
	/**
	 * 处理微信发来的请求
	 *
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		String respMessage = null;
		try {
			// 默认返回的文本消息内容
			String respContent = "请求处理异常，请稍候尝试！";
 
			// xml请求解析
			Map<String, String> requestMap = MessageUtil.xmlToMap(request);

			System.out.println(requestMap);

			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			//消息文本
			String content = requestMap.get("Content");
 
			// 回复文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.MESSAGE_TEXT);
			textMessage.setFuncFlag(0);
 
			// 文本消息
			if (msgType.equals(MessageUtil.MESSAGE_TEXT)) {
				return MessageUtil.initText(toUserName,fromUserName,MessageUtil.messageText(content));
			}
			// 图片消息
			else if (msgType.equals(MessageUtil.MESSAGE_IMAGE)) {
				respContent = "您发送的是图片消息！";
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtil.MESSAGE_LOCATION)) {
				respContent = "您发送的是地理位置消息！";
			}
			// 链接消息
			else if (msgType.equals(MessageUtil.MESSAGE_LINK)) {
				respContent = "您发送的是链接消息！";
			}
			// 音频消息
			else if (msgType.equals(MessageUtil.MESSAGE_VOICE)) {
				respContent = "您发送的是音频消息！";
			}
			// 事件推送
			else if (msgType.equals(MessageUtil.MESSAGE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				// 订阅
				if (eventType.equals(MessageUtil.EVENT_SUB)) {
					return MessageUtil.initText(toUserName,fromUserName,MessageUtil.subscribeText());
				}
				// 取消订阅
				else if (eventType.equals(MessageUtil.EVENT_UNSUB)) {
					// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
				}
				// 自定义菜单点击事件
				else if (eventType.equals(MessageUtil.EVENT_CLICK)) {
					// 事件KEY值，与创建自定义菜单时指定的KEY值对应
					String eventKey = requestMap.get("EventKey");
 
					if (eventKey.equals("11")) {
						respContent = "欢迎您使用寄养服务，请点以下链接进行操作！";
					} else if (eventKey.equals("12")) {
						respContent = "寄养通知菜单项被点击！";
					} else if (eventKey.equals("13")) {
						respContent = "信息回馈菜单项被点击！";
					} else if (eventKey.equals("21")) {
						List<Articles> articles = new ArrayList<>();
						articles.add(new Articles("公司简介","","http://fdqhh.oss-cn-shanghai.aliyuncs.com/home/default/mall1584348155735.jpg?Expires=1899708147&OSSAccessKeyId=LTAILQKwfbgAWM2N&Signature=rg7qbbR0JAulCfFOWMW6Q%2F7gDl0%3D","https://mp.weixin.qq.com/s?__biz=MzAwOTcwNDE0NA==&mid=100000017&idx=1&sn=58ec36b54e3ca501807ca9c4b005861e&scene=19#wechat_redirect"));
						articles.add(new Articles("太谷青牧养殖有限公司是以实验动物繁殖出售为主，饲料白鼠，白鼠血清生产、蛋鸡、肉兔繁育、宠物寄养销售为辅的综合服务公司。我们公司主要进行的就是小型实验动物小白鼠的自繁，血清的采集销售、另外包括白兔，肉鸡，蛋鸡等经济动物的养殖合作。","","http://fdqhh.oss-cn-shanghai.aliyuncs.com/home/default/mall1584348155735.jpg?Expires=1899708147&OSSAccessKeyId=LTAILQKwfbgAWM2N&Signature=rg7qbbR0JAulCfFOWMW6Q%2F7gDl0%3D","https://mp.weixin.qq.com/s?__biz=MzAwOTcwNDE0NA==&mid=100000017&idx=1&sn=58ec36b54e3ca501807ca9c4b005861e&scene=19#wechat_redirect"));
						return MessageUtil.initNewsMessage(articles,toUserName,fromUserName);
					} else if (eventKey.equals("22")) {
						respContent = "白鼠销售菜单项被点击！";
					} else if (eventKey.equals("31")) {
						respContent = "闲谈小屋菜单项被点击！";
					}
				}
			}
 
			textMessage.setContent(respContent);
			respMessage = MessageUtil.textMessageToXml(textMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return respMessage;
	}
}