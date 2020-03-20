package com.wang.pet.controller;

import com.wang.pet.common.BaseResponse;
import com.wang.pet.entity.Type;
import com.wang.pet.service.ITypeService;
import com.wang.pet.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/pet")
public class SavePetController {

    @Autowired
    private ITypeService typeService;


    @Value("${folder.path}")
    private String folderPath;

    @GetMapping("/savePet")
    public BaseResponse savePet() throws Exception {
        List<Type> list = typeService.list();
        String filePath = folderPath + "images\\";
        String s = null;
        for (Type type : list) {
            String url = "http://www.ichong123.com/";
            String prefix = "";
            try {
                if(type.getParentId() == 1){
                     prefix = "gougou/";
                }else if(type.getParentId() == 2){
                     prefix = "maomao/";
                }else if(type.getParentId() == 3){
                     prefix = "laoshu/";
                }else if(type.getParentId() == 4){
                     prefix = "qita/";
                }else if(type.getParentId() == 5){
                     prefix = "wugui/";
                }else if(type.getParentId() == 6){
                     prefix = "chongwumore/";
                }else if(type.getParentId() == 7){
                     prefix = "chongwumore/";
                }else if(type.getParentId() == 8){
                     prefix = "chongwumore/";
                }else if(type.getParentId() == 9){
                     prefix = "chongwumore/";
                }else if(type.getParentId() == 10){
                     prefix = "chongwumore/";
                }else if(type.getParentId() == 11){
                     prefix = "eyu/";
                }else if(type.getParentId() == 12){
                     prefix = "huli/";
                }else if(type.getParentId() == 13){
                     prefix = "zhu/";
                }else if(type.getParentId() == 14){
                     prefix = "chongwumore/";
                }
                if(type.getParentId()!=0){
                    url = url + prefix + type.getEnName();
                    System.out.println(type.getId()+"--->"+url);
                    s = HttpUtil.doGet(url, null);
                    String index  = "<p class=\"sintro\">";
                    String substring = s.substring(s.indexOf(index)+index.length());
                    String substring1 = substring.substring(0, substring.indexOf("</p>"));
                    String replace = substring1.replaceAll("<.*?>", "");
                    type.setInfo(replace);
                    typeService.updateById(type);
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("报错"+type.getId());
            }
        }
        return BaseResponse.ok("操作成功");
    }

}
