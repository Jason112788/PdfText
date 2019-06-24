package com.jason;

import javax.swing.*;

/**
 * GUI界面
 * @author zhuzhenhao
 * @version 1.0.0
 * @date 2019/6/22 22:51
 */
public class SwingMain {

    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("pdf数据提取处理");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        placeComponents(panel);
        // 设置界面可见
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("待处理的文件集合路径：");
        userLabel.setBounds(20,20,150,25);
        panel.add(userLabel);

        final JTextField userText = new JTextField(200);
        userText.setBounds(160,20,300,25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Excel文件路径：");
        passwordLabel.setBounds(20,50,150,25);
        panel.add(passwordLabel);

        final JTextField userText2 = new JTextField(200);
        userText2.setBounds(160,50,300,25);
        panel.add(userText2);

        JButton loginButton = new JButton("开始处理");
        loginButton.setBounds(10, 80, 150, 25);
        panel.add(loginButton);
        // 增加监听事件
        loginButton.addActionListener((ActionEvent) ->
            PdfHandler.handle(userText.getText(), userText2.getText())
        );
    }



}
