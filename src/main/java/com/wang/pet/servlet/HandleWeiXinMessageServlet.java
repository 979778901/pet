package com.wang.pet.servlet;

import com.wang.pet.service.CoreService;
import com.wang.pet.util.CheckUtil;
import com.wang.pet.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wuming on 2017/9/15.
 */
@Slf4j
@WebServlet("/user/servlet")
public class HandleWeiXinMessageServlet extends HttpServlet {

    /**
     * VersionUID
     */
    private static final long serialVersionUID = 1L;


    /*
     * URL验证
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        log.info("echostr" + echostr);
        PrintWriter out = resp.getWriter();
        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            out.write(echostr);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            String message = CoreService.processRequest(req);
            System.out.println("返回数据：   "+message);
            out.print(message);
        } catch (Exception e) {
            e.printStackTrace();
            out.close();
        }

    }

}