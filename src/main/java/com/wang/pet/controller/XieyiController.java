package com.wang.pet.controller;

import com.wang.pet.common.BaseResponse;
import com.wang.pet.util.UploadUtil;
import com.wang.pet.util.Word2HtmlUtil;
import com.wang.pet.util.WordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class XieyiController {

    @Value("${folder.path}")
    private String folderPath;

    @GetMapping("/getContract")
    public void getFile(HttpServletRequest request , HttpServletResponse response,String name) throws IOException {
        //读取路径下面的文件
        File file = new File(folderPath+name+".htm");
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(result.toString());
    }

    @GetMapping("/getSeal")
    public void getSeal(HttpServletRequest request , HttpServletResponse response) throws IOException {
        //读取路径下面的文件
        UploadUtil.queryImage("seal.png",folderPath+"images\\",request,response);
    }

    @GetMapping("/getImage")
    public void getImage(HttpServletRequest request , HttpServletResponse response,String name) throws IOException {
        //读取路径下面的文件
        UploadUtil.queryImage(name + ".jpg",folderPath+"images\\",request,response);
    }

    @PostMapping("agreeContract")
    public BaseResponse agreeContract(
            @RequestParam("username")       String username,
            @RequestParam("userAddress")    String userAddress,
            @RequestParam("userPhone")      String userPhone,
            @RequestParam("petName")        String petName,
            @RequestParam("petAge")         String petAge,
            @RequestParam("varColor")       String varColor,
            @RequestParam("petSex")         String petSex,
            @RequestParam("petIsHealth")    String petIsHealth,
            @RequestParam("petIsRut")       String petIsRut,
            @RequestParam("petIsPregnancy") String petIsPregnancy,
            @RequestParam("startTime")      String startTime,
            @RequestParam("endTime")        String endTime,
            @RequestParam("money")          BigDecimal money
    ) throws ParseException {
        Map<String, Object> param = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        param.put("username",username);
        param.put("userAddress",userAddress);
        param.put("userPhone",userPhone);
        param.put("petName",petName);
        param.put("petAge",petAge);
        param.put("varColor",varColor);
        param.put("petSex",petSex);
        param.put("petIsHealth",petIsHealth);
        param.put("petIsRut",petIsRut);
        param.put("petIsHuaiYun",petIsPregnancy);
        param.put("money",money);
        param.put("startYear",startTime.split("-")[0]);
        param.put("startMonth",startTime.split("-")[1]);
        param.put("startDate",startTime.split("-")[2]);
        param.put("endYear",endTime.split("-")[0]);
        param.put("endMonth",endTime.split("-")[1]);
        param.put("endDate",endTime.split("-")[2]);
        long days = (sdf.parse(endTime).getTime() - sdf.parse(startTime).getTime()) / 1000 / 60 /60 /24;
        param.put("days",days);
        param.put("sumMoney",money.multiply(new BigDecimal(days)));
        param.put("currentYear",sdf.format(new Date()).split("-")[0]);
        param.put("currentMonth",sdf.format(new Date()).split("-")[1]);
        param.put("currentDate",sdf.format(new Date()).split("-")[2]);
        try {
            SimpleDateFormat _sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String prefix = "协议_" +userPhone +"_" + _sdf.format(new Date());
            String path = folderPath + "xieyi\\";
            WordUtil.templateWrite(path + "协议_通用模板.docx",path + prefix +".docx",param);
            Word2HtmlUtil.docx(path ,prefix + ".docx",prefix + ".htm");
            String html = Word2HtmlUtil.getContent(path, prefix+".htm");
            String replace = html.replace("word/media/image1.png", "http://s21478v644.iok.la/getSeal");
            Word2HtmlUtil.addFile(replace,path + prefix + ".htm");
            return BaseResponse.ok(prefix);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.err("操作异常");
        }
    }

}
