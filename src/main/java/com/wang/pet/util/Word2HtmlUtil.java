package com.wang.pet.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import fr.opensagres.xdocreport.converter.FileURIResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;


/**
 * Created by LTmei on 2018/10/10 10:00
 */
@Slf4j
public class Word2HtmlUtil {

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\DELL\\Desktop\\公众号\\协议\\";

        //docx(filePath ,"协议_17316348423_20200319154822788.docx","协议_17316348423_20200319154822788.htm");
        String html = getContent(filePath, "协议_17316348423_20200319161218652.htm");
        String replace = html.replace("word/media/image1.png", "http://s21478v644.iok.la/getSeal");
        System.out.println(replace);
        addFile(replace,filePath + "协议_17316348423_20200319161218652.htm");
    }

    //File对象定位数据源
    public static String getContent(String filePath,String fileName) throws IOException {
        try ( BufferedReader br = new BufferedReader(new FileReader(filePath + fileName))) {
            StringBuffer sb = new StringBuffer();
            while (br.ready()) {
                sb.append(br.readLine());
            }
            return sb.toString();
        }
    }

    public static boolean addFile(String string,String path) {

        PrintStream stream=null;
        try {
            stream=new PrintStream(path);//写入的文件path
            stream.print(string);//写入的字符串
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            stream.close();
        }
        return false;
    }

    /**
     * 转换docx
     * @param filePath
     * @param fileName
     * @param htmlName
     * @throws Exception
     */
    public static void docx(String filePath ,String fileName,String htmlName) throws Exception{
        final String file = filePath + fileName;
        File f = new File(file);
        // ) 加载word文档生成 XWPFDocument对象
        InputStream in = new FileInputStream(f);
        XWPFDocument document = new XWPFDocument(in);
        // ) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
        File imageFolderFile = new File(filePath);
        XHTMLOptions options = XHTMLOptions.create();
        options.setExtractor(new FileImageExtractor(imageFolderFile));
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);
        // ) 将 XWPFDocument转换成XHTML
        OutputStream out = new FileOutputStream(new File(filePath + htmlName));
        XHTMLConverter.getInstance().convert(document, out, options);
        out.close();
        in.close();
    }

}