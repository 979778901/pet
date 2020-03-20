package com.wang.pet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Articles {

    private String Title;

    private String Description;

    private String PicUrl;

    private String Url;

}
