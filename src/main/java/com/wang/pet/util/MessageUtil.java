package com.wang.pet.util;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wang.pet.entity.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    public static final String MESSAGE_TEXT = "text";
    public static final String MESSAGE_IMAGE = "image";
    public static final String MESSAGE_NEWS = "news";
    public static final String MESSAGE_VOICE = "voice";
    public static final String MESSAGE_VIDEO = "video";
    public static final String MESSAGE_LINK = "link";
    public static final String MESSAGE_LOCATION = "location";
    public static final String MESSAGE_EVENT = "event";

    public static final String EVENT_SUB = "subscribe";
    public static final String EVENT_UNSUB = "unsubscribe";
    public static final String EVENT_CLICK = "CLICK";
    public static final String EVENT_VIEW = "VIEW";

    /**
     * xml转为map
     * @param request
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request ) throws DocumentException, IOException
    {
        Map<String,String> map = new HashMap<String, String>();

        SAXReader reader = new SAXReader();

        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);

        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        ins.close();
        return map;
    }

    public static String textMessageToXml(TextMessage textMessage){
        //XStream xstream = new XStream();
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
    public static String initText(String toUserName, String fromUserName, String content){
        TextMessage text = new TextMessage();
        text.setFromUserName(toUserName);
        text.setToUserName(fromUserName);
        text.setMsgType(MESSAGE_TEXT);
        text.setCreateTime(new Date().getTime());
        text.setContent(content);
        return textMessageToXml(text);
    }

    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有 xml 节点的转换都增加 CDATA 标记
                boolean cdata = true;

                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 图文
     * @param newsMessage
     * @return
     */
    private static String newsMessageMessageToXml(NewsMessage newsMessage){

        xstream.alias("xml", newsMessage.getClass());//置换根节点
        xstream.alias("item",new Articles().getClass());
        return xstream.toXML(newsMessage);
    }

    /**
     * 组装图文xml
     * @MethodName：initImageMessage
     *@author:caoxinyu
     *@ReturnType:String
     *@return
     */
    public static String initNewsMessage(List<Articles> articles,String toUserName,String fromUserName){
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setArticles(articles);
        newsMessage.setArticleCount(articles.size());
        newsMessage.setCreateTime(new Date().getTime());
        newsMessage.setFromUserName(toUserName);
        newsMessage.setToUserName(fromUserName);
        newsMessage.setMsgType(MESSAGE_NEWS);
        return newsMessageMessageToXml(newsMessage);
    }

    /**
     * 图片转成xml
     * @MethodName：textMessageToXml
     *@author:caoxinyu
     *@ReturnType:String
     *@return
     */
    public static String imageMessageToXml(ImageMessage imageMessage){

        //XStream xstream = new XStream();//xstream.jar,xmlpull.jar
        xstream.alias("xml", imageMessage.getClass());//置换根节点
        return xstream.toXML(imageMessage);

    }
    /**
     * 组装图片xml
     * @MethodName：initImageMessage
     *@author:caoxinyu
     *@ReturnType:String
     *@param MediaId
     *@param toUserName
     *@param fromUserName
     *@return
     */
    public static String initImageMessage(String MediaId,String toUserName,String fromUserName){
        String message = null;
        Image image = new Image();
        ImageMessage imageMessage = new ImageMessage();
        image.setMediaId(MediaId);
        imageMessage.setFromUserName(toUserName);
        imageMessage.setToUserName(fromUserName);
        imageMessage.setCreateTime(new Date().getTime());
        imageMessage.setImage(image);
        imageMessage.setMsgType(MESSAGE_IMAGE);
        message = imageMessageToXml(imageMessage);
        return message;
    }

    public static String subscribeText(){
        StringBuffer sb = new StringBuffer();
        sb.append("您好，欢迎关注青牧养殖，我们将不定期更新关于养殖方面的小知识，另外，若有其他需要，此公众号工作人员看到会及时回复。");
        /*sb.append("欢迎关注【青牧养殖】!\n");
        sb.append("该公众号已实现以下功能：\n");
        sb.append("回复“宠物寄养”、“闲谈小屋” 将有该功能的介绍与使用，\n");
        sb.append("如您在使用该公众有任何宝贵意见，欢迎反馈！\n\n");
        sb.append("反馈邮箱：m17316348423@163.com");*/
        return sb.toString();
    }


    public static String messageText(String content) {
        StringBuffer sb = new StringBuffer();
        if(content.contains("宠物寄养")){
            sb.append("这是一条宠物寄养的消息");
        }else if (content.contains("了解更多")){
            sb.append("这是一条了解更多的消息");
        }else {
            sb.append("您的问题太高深了,老王正在努力学习中。。。");
        }
        return sb.toString();
    }
}

