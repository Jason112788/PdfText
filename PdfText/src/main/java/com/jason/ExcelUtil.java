package com.jason;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 * @author zhuzhenhao
 * @version 1.0.0
 * @date 2019/6/22 17:25
 */
public class ExcelUtil {

    public static void main(String[] args) {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("id", "03300765000397867212");
        dataMap.put("signDate", "2019/7/12");
        dataMap.put("endDate", "2019/8/8");
        dataMap.put("content", "tesetssssssssssssssssssssssss");
        dataMap.put("sheetName", "北京市金龙腾装饰股份有限公司");
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(dataMap);
        writeExcel("C:\\Users\\saoProgrammer\\Desktop\\excel.xlsx", list);

    }

    /**
     * 写入excel数据
     * @param filePath
     * @param dataList
     * @return: void
     * @date: 2019/6/22 17:30
     */
    public static void writeExcel(String filePath, List<Map<String, String>> dataList) {
        OutputStream out = null;
        try {
            File excelFile = new File(filePath);
            if (excelFile == null || !excelFile.exists()) {
                // 容错
                FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, "Excel路径不存在");
                return;
            }
            // 读取Excel文档
            Workbook workBook = getWorkbok(excelFile);
            if (workBook == null) {
                // 容错
                FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, "Excel不存在");
                return;
            }
            // 切换sheet
            for (Map dataMap : dataList) {
                String dataId = (String) dataMap.get("id");
                String signDate = (String) dataMap.get("signDate");
                String endDate = (String) dataMap.get("endDate");
                String content = (String) dataMap.get("content");
                String sheetName = (String) dataMap.get("sheetName");
                // 通过名字获得工作页
                Sheet sheet = null;
                for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
                    sheet = workBook.getSheetAt(i);
                    String name = sheet.getSheetName();
                    if (sheetName.contains(name)) {
                        // sheet名匹配成功
                        break;
                    }
                }
                if (sheet == null) {
                    // 容错
                    FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, "和" + sheetName + "相关的sheet不存在");
                    return;
                }
                // 通过id索引插入数据的行
                int targetRow = 0;
                // 总行数
                int tiotalRawNum = sheet.getLastRowNum() + 1;
                for (int rowNum = 1; rowNum < tiotalRawNum; rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    // 取id列，默认是第二列
                    Cell cell = row.getCell(1);
                    String id = cell.getStringCellValue();
                    if (dataId.equals(id)) {
                        // 找到行
                        targetRow = rowNum;
                        break;
                    }
                }
                if (targetRow == 0) {
                    // 没有找到
                    FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, sheetName + "找不到对应行，数据未录入，编号:" + dataId);
                    continue;
                }
                Row row = sheet.getRow(targetRow);
                row.createCell(2).setCellValue(signDate);
                row.createCell(3).setCellValue(endDate);
                row.createCell(6).setCellValue(content);
                FileUtil.writeToLogFile(Constants.LOG_FATHER_PATH, sheet.getSheetName() + "录入成功");
            }
            // 持久化
            out = new FileOutputStream(filePath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断Excel的版本,获取Workbook
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith(Constants.EXCEL_XLS)) {
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(Constants.EXCEL_XLSX)) {
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }
}
