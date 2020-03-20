package com.wang.pet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;


public class WordUtil {

    public static void main(String[] args) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name","曹新宇");
        templateWrite("C:\\Users\\DELL\\Desktop\\公众号\\协议\\协议1.docx","C:\\Users\\DELL\\Desktop\\公众号\\协议\\协议2.docx",param);
    }


    /**
     * 用一个docx文档作为模板，程序替换其中的内容，再写入目标文档中。
     * @param filePath
     * @param outFile
     * @param params
     * @throws Exception
     */
    public static void templateWrite(String filePath, String outFile,
                              Map<String, Object> params) throws Exception {
        InputStream is = new FileInputStream(filePath);
        //System.out.println(filePath);
        XWPFDocument doc = new XWPFDocument(is);
        // 替换段落里面的变量
        replaceInPara(doc, params);
        // 替换多个表格里面的变量并插入数据
        insertValueToTables(doc, params);
        OutputStream os = new FileOutputStream(outFile);
        doc.write(os);
        close(os);
        close(is);
    }

    /**
     * 替换段落里面的变量
     *
     * @param doc
     *            要替换的文档
     * @param params
     *            参数
     */
    private static void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            replaceInPara(para, params);
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param para
     *            要替换的段落
     * @param params
     *            参数
     */
    private static boolean replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        boolean data = false;
        List<XWPFRun> runs;
        Matcher matcher;
        if (matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();
            for (int i = 0; i < runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String runText = run.toString();
                matcher = matcher(runText);
                if (matcher.find()) {
                    while ((matcher = matcher(runText)).find()) {
                        String str=String.valueOf(params.get(matcher.group(1)));
                        //System.out.println("----"+runText);
                        //System.out.println("----"+str);
                        runText = matcher.replaceFirst(str);
                    }
                    Boolean isBold = run.isBold();
                    Boolean isItalic = run.isItalic();
                    Boolean isStrike = run.isStrike();
                    UnderlinePatterns Underline = run.getUnderline();
                    String Color = run.getColor();
                    int TextPosition = run.getTextPosition();
                    int FontSize = run.getFontSize();
                    String FontFamily = run.getFontFamily();
                    CTR ctr =run.getCTR();
                    para.removeRun(i);
                    //para.insertNewRun(i).setText(runText);
                    XWPFRun newrun = para.insertNewRun(i);
                    newrun.setText(runText);
                    try {
                        // 复制格式
                        newrun.setBold(isBold);
                        newrun.setItalic(isItalic);
                        newrun.setStrike(isStrike);
                        newrun.setUnderline(Underline);
                        newrun.setColor(Color);
                        newrun.setTextPosition(TextPosition);
                        if (FontSize != -1) {
                            newrun.setFontSize(FontSize);
                            CTRPr rpr = newrun.getCTR().isSetRPr() ? newrun.getCTR().getRPr() : newrun.getCTR().addNewRPr();
                            CTFonts fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
                            fonts.setAscii(FontFamily);
                            fonts.setEastAsia(FontFamily);
                            fonts.setHAnsi(FontFamily);
                        }
                        if (FontFamily != null) {
                            newrun.setFontFamily(FontFamily);
                        }
                        if (ctr != null) {
                            Boolean flat = false;
                            try {
                                flat = ctr.isSetRPr();
                            } catch (Exception e) {
                            }
                            if (flat) {
                                CTRPr tmpRPr = ctr.getRPr();
                                if (tmpRPr.isSetRFonts()) {
                                    CTFonts tmpFonts = tmpRPr.getRFonts();
                                    CTRPr cellRPr = newrun.getCTR().isSetRPr() ? newrun
                                            .getCTR().getRPr() : newrun
                                            .getCTR().addNewRPr();
                                    CTFonts cellFonts = cellRPr.isSetRFonts() ? cellRPr
                                            .getRFonts() : cellRPr
                                            .addNewRFonts();
                                    cellFonts.setAscii(tmpFonts.getAscii());
                                    cellFonts.setAsciiTheme(tmpFonts
                                            .getAsciiTheme());
                                    cellFonts.setCs(tmpFonts.getCs());
                                    cellFonts.setCstheme(tmpFonts.getCstheme());
                                    cellFonts.setEastAsia(tmpFonts
                                            .getEastAsia());
                                    cellFonts.setEastAsiaTheme(tmpFonts
                                            .getEastAsiaTheme());
                                    cellFonts.setHAnsi(tmpFonts.getHAnsi());
                                    cellFonts.setHAnsiTheme(tmpFonts
                                            .getHAnsiTheme());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            data = true;
        } else if (matcherRow(para.getParagraphText())) {
            runs = para.getRuns();
            // System.out.println("run  " + runs);
            data = true;
        }
        return data;
    }

    /**
     * 按模版行样式填充数据,暂未实现特殊样式填充(如列合并)，只能用于普通样式(如段落间距 缩进 字体 对齐)
     * @param doc
     *            要替换的文档
     * @param params
     *            参数
     * @throws Exception
     */
    private static void insertValueToTables(XWPFDocument doc, Map<String, Object> params)
            throws Exception {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table = null;
        int z =1;
        while (iterator.hasNext()) {
            //行
            List<XWPFTableRow> rows = null;
            //列
            List<XWPFTableCell> cells = null;
            List<XWPFParagraph> paras;
            table = iterator.next();
            System.out.println("-------解析出第 "+z+" 个表------开始处理");
            //获取表格行数据list
            rows = table.getRows();
            XWPFTableRow tmpRow = null;
            //第二行为模板行
            tmpRow = rows.get(1);
            //模版列
            List<XWPFTableCell> tmpCells = null;
            XWPFTableCell tmpCell = null;
            tmpCells = tmpRow.getTableCells();
            List<Map> tablist =null;
            List<String> listkey = new ArrayList<String>();
            for (int i = 1; i <= rows.size(); i++) {
                cells = rows.get(i - 1).getTableCells();
                //获取当前行所有列
                if(i==1){
                    int intcell = 1;
                    //遍历列
                    for (XWPFTableCell cell : cells) {
                        if (intcell == 1) {
                            //取第一行第一列表标识并替代${ tab1} 姓名 值 为姓名  map里取对应表list数据
                            String flagtemp = cell.getText();
                            flagtemp = flagtemp.substring(flagtemp.indexOf("{")+1, flagtemp.lastIndexOf("}"));
                            System.out.println("###表标识值：" +flagtemp);
                            tablist = (List<Map>) params.get(flagtemp);
                            paras = cell.getParagraphs();
                            for (XWPFParagraph para : paras) {
                                List<XWPFRun> runs;
                                runs = para.getRuns();
                                for (int m = 0; m < runs.size(); m++) {
                                    XWPFRun run = runs.get(m);
                                    String runText = run.toString();
                                    System.out.println("----"+runText);
                                    Matcher matcher;
                                    matcher = matcher(runText);
                                    if (matcher.find()) {
                                        while ((matcher = matcher(runText)).find()) {
                                            runText = matcher.replaceFirst("");
                                        }
                                        para.removeRun(m);
                                        para.insertNewRun(m).setText(runText);
                                    }
                                }
                            }
                        }
                        intcell++;
                        break;
                    }
                }else if(i==2){
                    //第二行替代值并创建list key用
                    int intcell = 1;
                    for (XWPFTableCell cell : cells) {
                        System.out.println("第"+intcell + "列：" + cell.getText());
                        //Map mapth = tablist.get(0);
                        paras = cell.getParagraphs();
                        for (XWPFParagraph para : paras) {
                            //读取的值去掉${}
                            String keystr = para.getParagraphText();
                            keystr = keystr.substring(keystr.indexOf("{")+1, keystr.lastIndexOf("}"));
                            listkey.add(keystr);
                        }
                        intcell++;
                    }
                }
            }
            //开始动态创建表
            for (int i = 0; i < tablist.size(); i++) {
                System.out.println("开始复制第" + i + "行");
                XWPFTableRow row = table.createRow();
                row.setHeight(tmpRow.getHeight());
                Map<String,String> tempmap = tablist.get(i);
                cells = row.getTableCells();
                // 插入的行会填充与表格第一行相同的列数
                for (int k = 0 ; k < cells.size(); k++) {
                    tmpCell = tmpCells.get(k);
                    XWPFTableCell cell = cells.get(k);
                    setCellText(tmpCell, cell, tablist.get(i).get(listkey.get(k)).toString());
                    System.out.println("第"+(k+1)+"列：" +tablist.get(i).get(listkey.get(k)).toString());
                }
                // 继续写剩余的列
                for (int j = cells.size(); j < listkey.size(); j++) {
                    tmpCell = tmpCells.get(j);
                    XWPFTableCell cell = row.addNewTableCell();
                    setCellText(tmpCell, cell, tablist.get(i).get(listkey.get(j)).toString());
                    System.out.println("第"+(j+1)+"列：" +tablist.get(i).get(listkey.get(j)).toString());
                }
            }
            // 删除表标识行
            table.removeRow(1);
            System.out.println("-------解析出第 "+z+" 个表------结束处理");
            z++;
        }
    }

    public static void setCellText(XWPFTableCell tmpCell, XWPFTableCell cell,
                            String text) throws Exception {
        CTTc cttc2 = tmpCell.getCTTc();
        CTTcPr ctPr2 = cttc2.getTcPr();

        CTTc cttc = cell.getCTTc();
        CTTcPr ctPr = cttc.addNewTcPr();
        cell.setColor(tmpCell.getColor());
        // cell.setVerticalAlignment(tmpCell.getVerticalAlignment());
        if (ctPr2.getTcW() != null) {
            ctPr.addNewTcW().setW(ctPr2.getTcW().getW());
        }
        if (ctPr2.getVAlign() != null) {
            ctPr.addNewVAlign().setVal(ctPr2.getVAlign().getVal());
        }
        if (cttc2.getPList().size() > 0) {
            CTP ctp = cttc2.getPList().get(0);
            if (ctp.getPPr() != null) {
                if (ctp.getPPr().getJc() != null) {
                    cttc.getPList().get(0).addNewPPr().addNewJc()
                            .setVal(ctp.getPPr().getJc().getVal());
                }
            }
        }
        if (ctPr2.getTcBorders() != null) {
            ctPr.setTcBorders(ctPr2.getTcBorders());
        }
        XWPFParagraph tmpP = tmpCell.getParagraphs().get(0);
        XWPFParagraph cellP = cell.getParagraphs().get(0);
        XWPFRun tmpR = null;
        if (tmpP.getRuns() != null && tmpP.getRuns().size() > 0) {
            tmpR = tmpP.getRuns().get(0);
            String runText = tmpR.toString();
            System.out.println(runText);
        }
        XWPFRun cellR = cellP.createRun();
        cellR.setText(text);
        // 复制字体信息
        if (tmpR != null) {
            cellR.setBold(tmpR.isBold());
            cellR.setItalic(tmpR.isItalic());
            cellR.setStrike(tmpR.isStrike());
            cellR.setUnderline(tmpR.getUnderline());
            cellR.setColor(tmpR.getColor());
            cellR.setTextPosition(tmpR.getTextPosition());
            if (tmpR.getFontSize() != -1) {
                cellR.setFontSize(tmpR.getFontSize());
            }
            if (tmpR.getFontFamily() != null) {
                cellR.setFontFamily(tmpR.getFontFamily());
            }
            if (tmpR.getCTR() != null) {
                if (tmpR.getCTR().isSetRPr()) {
                    CTRPr tmpRPr = tmpR.getCTR().getRPr();
                    if (tmpRPr.isSetRFonts()) {
                        CTFonts tmpFonts = tmpRPr.getRFonts();
                        CTRPr cellRPr = cellR.getCTR().isSetRPr() ? cellR
                                .getCTR().getRPr() : cellR.getCTR().addNewRPr();
                        CTFonts cellFonts = cellRPr.isSetRFonts() ? cellRPr
                                .getRFonts() : cellRPr.addNewRFonts();
                        cellFonts.setAscii(tmpFonts.getAscii());
                        cellFonts.setAsciiTheme(tmpFonts.getAsciiTheme());
                        cellFonts.setCs(tmpFonts.getCs());
                        cellFonts.setCstheme(tmpFonts.getCstheme());
                        cellFonts.setEastAsia(tmpFonts.getEastAsia());
                        cellFonts.setEastAsiaTheme(tmpFonts.getEastAsiaTheme());
                        cellFonts.setHAnsi(tmpFonts.getHAnsi());
                        cellFonts.setHAnsiTheme(tmpFonts.getHAnsiTheme());
                    }
                }
            }
        }
        // 复制段落信息
        cellP.setAlignment(tmpP.getAlignment());
        cellP.setVerticalAlignment(tmpP.getVerticalAlignment());
        cellP.setBorderBetween(tmpP.getBorderBetween());
        cellP.setBorderBottom(tmpP.getBorderBottom());
        cellP.setBorderLeft(tmpP.getBorderLeft());
        cellP.setBorderRight(tmpP.getBorderRight());
        cellP.setBorderTop(tmpP.getBorderTop());
        cellP.setPageBreak(tmpP.isPageBreak());
        if (tmpP.getCTP() != null) {
            if (tmpP.getCTP().getPPr() != null) {
                CTPPr tmpPPr = tmpP.getCTP().getPPr();
                CTPPr cellPPr = cellP.getCTP().getPPr() != null ? cellP
                        .getCTP().getPPr() : cellP.getCTP().addNewPPr();
                // 复制段落间距信息
                CTSpacing tmpSpacing = tmpPPr.getSpacing();
                if (tmpSpacing != null) {
                    CTSpacing cellSpacing = cellPPr.getSpacing() != null ? cellPPr
                            .getSpacing() : cellPPr.addNewSpacing();
                    if (tmpSpacing.getAfter() != null) {
                        cellSpacing.setAfter(tmpSpacing.getAfter());
                    }
                    if (tmpSpacing.getAfterAutospacing() != null) {
                        cellSpacing.setAfterAutospacing(tmpSpacing
                                .getAfterAutospacing());
                    }
                    if (tmpSpacing.getAfterLines() != null) {
                        cellSpacing.setAfterLines(tmpSpacing.getAfterLines());
                    }
                    if (tmpSpacing.getBefore() != null) {
                        cellSpacing.setBefore(tmpSpacing.getBefore());
                    }
                    if (tmpSpacing.getBeforeAutospacing() != null) {
                        cellSpacing.setBeforeAutospacing(tmpSpacing
                                .getBeforeAutospacing());
                    }
                    if (tmpSpacing.getBeforeLines() != null) {
                        cellSpacing.setBeforeLines(tmpSpacing.getBeforeLines());
                    }
                    if (tmpSpacing.getLine() != null) {
                        cellSpacing.setLine(tmpSpacing.getLine());
                    }
                    if (tmpSpacing.getLineRule() != null) {
                        cellSpacing.setLineRule(tmpSpacing.getLineRule());
                    }
                }
                // 复制段落缩进信息
                CTInd tmpInd = tmpPPr.getInd();
                if (tmpInd != null) {
                    CTInd cellInd = cellPPr.getInd() != null ? cellPPr.getInd()
                            : cellPPr.addNewInd();
                    if (tmpInd.getFirstLine() != null) {
                        cellInd.setFirstLine(tmpInd.getFirstLine());
                    }
                    if (tmpInd.getFirstLineChars() != null) {
                        cellInd.setFirstLineChars(tmpInd.getFirstLineChars());
                    }
                    if (tmpInd.getHanging() != null) {
                        cellInd.setHanging(tmpInd.getHanging());
                    }
                    if (tmpInd.getHangingChars() != null) {
                        cellInd.setHangingChars(tmpInd.getHangingChars());
                    }
                    if (tmpInd.getLeft() != null) {
                        cellInd.setLeft(tmpInd.getLeft());
                    }
                    if (tmpInd.getLeftChars() != null) {
                        cellInd.setLeftChars(tmpInd.getLeftChars());
                    }
                    if (tmpInd.getRight() != null) {
                        cellInd.setRight(tmpInd.getRight());
                    }
                    if (tmpInd.getRightChars() != null) {
                        cellInd.setRightChars(tmpInd.getRightChars());
                    }
                }
            }
        }
    }

    /**
     * 正则匹配字符串
     *
     * @param str
     * @return
     */
    private static Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 正则匹配字符串
     *
     * @param str
     * @return
     */
    private static boolean matcherRow(String str) {
        Pattern pattern = Pattern.compile("\\$\\[(.+?)\\]",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 关闭输入流
     *
     * @param is
     */
    private static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    private static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
