package com.wang.pet.entity;

import lombok.Data;

import java.util.List;

@Data
public class NewsMessage extends BaseMessage {

    private Integer ArticleCount;

    private List<Articles> Articles;



}
