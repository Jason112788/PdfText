package com.jason;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * 文件工具
 * @author zhuzhenhao
 * @version 1.0.0
 * @date 2019/6/22 22:03
 */
public class FileUtil {

    /**
     * 获得文件路径下所有PDF文件
     * @param filePath
     * @param resultList
     * @return: java.util.List<java.io.File>
     * @date: 2019/6/22 13:31
     */
    public static void getAllPdfFile(String filePath, List<File> resultList) {
        File rootFile = new File(filePath);
        if (!rootFile.exists()) {
            // 不存在
            return;
        }
        // 该文件目录下文件全部放入数组
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) {
                    // 文件夹，进行递归
                    getAllPdfFile(files[i].getAbsolutePath(), resultList);
                } else if (fileName.endsWith("pdf")) {
                    // pdf文件
                    resultList.add(files[i]);
                }
            }
        }
    }

    /**
     * 日志持久化
     * @param logFatherPath
     * @param log
     * @return: void
     * @date: 2019/6/23 9:58
     */
    public static void writeToLogFile(String logFatherPath, String log) {
        // 方便debug，控制台也打印
        System.out.println(log);
        try {
            File file = new File(logFatherPath + "\\处理日志.txt");
            if (!file.exists()) {
                // 不存在就创建
                file.createNewFile();
            }
            Writer out =new FileWriter(file, true);
            out.write(log + "\r\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除日志
     * @param filePath
     * @return: void
     * @date: 2019/6/23 10:23
     */
    public static void deleteLogFile(String filePath) {
        File file = new File(filePath + "\\处理日志.txt");
        if (file.exists()) {
            // 存在就删除
            file.delete();
        }
    }

}
