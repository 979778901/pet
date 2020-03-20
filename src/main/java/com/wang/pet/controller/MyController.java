package com.wang.pet.controller;

import com.wang.pet.util.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@Slf4j
public class MyController {
    @GetMapping("/test")
    public void test(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    }
}
