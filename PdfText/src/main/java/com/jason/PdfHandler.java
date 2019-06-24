package com.jason;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pdf操作类
 * @author zhuzhenhao
 * @version 1.0.0
 * @date 2019/6/22 13:11
 */
public class PdfHandler {

    /** 待处理文件根目录 */
    private static String filePath = "";

    /** Excel文件地址 */
    private static String excelPath = "";

    public static void main(String[] args) {
        // main方法调试
        handle(filePath, excelPath);
    }

    public static void handle(String filePath, String excelPath) {
        // 删除之前的日志文件
        FileUtil.deleteLogFile(Constants.LOG_FATHER_PATH);
        // 指定操作文件目录
        Constants.LOG_FATHER_PATH = filePath;

        List<File> fileList = new ArrayList<File>();
        FileUtil.getAllPdfFile(filePath, fileList);
        // 待录入的数据
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        // 循环处理
        fileList.forEach(file -> {
            try {
                // pdf提炼为txt
                File txtFile = pdf2Txt(file.getAbsolutePath());
                if (txtFile == null) {
                    // 生成txt失败
                    FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, filePath + " 生成txt失败");
                    return;
                }
                // 读取txt
                BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile));
                String line = "";
                String id = "";
                String signDate = "";
                String endDate = "";
                String name = "";
                String content = "";
                boolean contentStart = false;
                while ((line = bufferedReader.readLine()) != null) {
                    // 逐行读取
                    if (id.isEmpty() && line.contains("登记证明编号：")) {
                        id = line.substring(line.indexOf("登记证明编号：") + 7);
                        id = id.replace(" ", "");
                    } else if (signDate.isEmpty() && line.contains("登记时间：")) {
                        signDate = line.substring(line.indexOf("登记时间：") + 5);
                        signDate = signDate.replace("-", "/");
                        signDate = signDate.split(" ")[0];
                    } else if (endDate.isEmpty() && line.contains("登记到期日")) {
                        endDate = line.substring(line.indexOf("登记到期日") + 5);
                        endDate = endDate.replace(" ", "");
                        endDate = endDate.replace("-", "/");
                    } else if (name.isEmpty() && line.contains("填表人名称")) {
                        name = line.substring(line.indexOf("填表人名称") + 5);
                        name = name.replace(" ", "");
                    } else if (!contentStart && line.contains("转让财产描述")) {
                        // 标志录入内容开始
                        contentStart = true;
                    } else if (contentStart && line.contains("转让财产信息附件")) {
                        // 标志录入内容结束
                        contentStart = false;
                    }
                    if (contentStart) {
                        if (!line.contains("页") && !line.contains("<完>") && !line.contains("登记证明编号") && !line.contains("代理登记机构") && !line.contains("转让财产描述")) {
                            content += line + "\r\n";
                        }
                    }
                }
                // 从文件路径读取sheetName
                String[] split = file.getAbsolutePath().split("\\\\");
                String sheetName = split[1];
                Map<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("id", id);
                dataMap.put("signDate", signDate);
                dataMap.put("endDate", endDate);
                dataMap.put("content", content);
                dataMap.put("sheetName", sheetName);
                dataList.add(dataMap);
//            System.out.println(id + " " + signDate + " " + endDate + " " + name + " " + content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 数据批量写入
        ExcelUtil.writeExcel(excelPath, dataList);
        FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, "导入数据完成！！！！");
    }

    /**
     * pdf转txt文件
     * @param file
     * @throws Exception
     */
    public static File pdf2Txt(String file) throws Exception {
        // 是否排序
        boolean sort = false;
        // pdf文件名
        String pdfFile = file;
        // 输入文本文件名称
        String textFile = null;
        // 编码方式
        String encoding = "UTF-8";
        // 开始提取页数
        int startPage = 1;
        // 结束提取页数
        int endPage = Integer.MAX_VALUE;
        // 文件输入流，生成文本文件
        Writer output = null;
        // 内存中存储的PDF Document
        PDDocument document = null;
        try {
            try {
                // 首先当作一个URL来装载文件，如果得到异常再从本地文件系统//去装载文件
                URL url = new URL(pdfFile);
                //注意参数已不是以前版本中的URL.而是File。
                document = PDDocument.load(new File(pdfFile));
                // 获取PDF的文件名
                String fileName = url.getFile();
                // 以原来PDF的名称来命名新产生的txt文件
                textFile = fileName + ".txt";
            } catch (MalformedURLException e) {
                // 如果作为URL装载得到异常则从文件系统装载
                //注意参数已不是以前版本中的URL.而是File。
                document = PDDocument.load(new File(pdfFile));
                textFile = pdfFile + ".txt";
            }
            // 文件输入流，写入文件倒textFile
            output = new OutputStreamWriter(new FileOutputStream(textFile), encoding);
            // PDFTextStripper来提取文本
            PDFTextStripper stripper = null;
            stripper = new PDFTextStripper();
            // 设置是否排序
            stripper.setSortByPosition(sort);
            // 设置起始页
            stripper.setStartPage(startPage);
            // 设置结束页
            stripper.setEndPage(endPage);
            // 调用PDFTextStripper的writeText提取并输出文本
            stripper.writeText(document, output);
            FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, textFile + " 生成成功！");
            return new File(textFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                // 关闭输出流
                output.close();
            }
            if (document != null) {
                // 关闭PDF Document
                document.close();
            }
        }
        return null;
    }

}
