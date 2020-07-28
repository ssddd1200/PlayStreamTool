package com.yjs.controller;

import com.yjs.msg.AlertBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class ConfigController {

    @FXML
    private TextField serverAdd;

    @FXML
    private TextField ffmpegAdd;

    @FXML
    private void chose1(ActionEvent e){
        dirChose(serverAdd);
    }

    @FXML
    private void chose2(ActionEvent e){
        fileChose(ffmpegAdd);
    }

    @FXML
    private void initialize(){
        Properties prop = new Properties();
        String propfile = getPropertiesPath();
        try {
            InputStream is = new FileInputStream(propfile);
            prop.load(is);
            serverAdd.setText(prop.getProperty("serverAdd"));
            ffmpegAdd.setText(prop.getProperty("ffmpegAdd"));
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox.display("错误", e.getMessage());
        }
    }

    @FXML
    private void saveConf(ActionEvent e){
        Properties prop = new Properties();
        String propfile = getPropertiesPath();
        try {
            prop.setProperty("serverAdd", serverAdd.getText());
            prop.setProperty("ffmpegAdd", ffmpegAdd.getText());
            FileOutputStream fos = new FileOutputStream(propfile);
            prop.store(fos, null);
            fos.close();
            fos.flush();
            AlertBox.display("","保存成功");
        } catch (IOException e1) {
            e1.printStackTrace();
            AlertBox.display("错误", e1.getMessage());
        }

    }

    private String getPropertiesPath(){
        String profile = "";
        String confPath = getClass().getClassLoader().getResource("conf.properties").getPath();
        if (confPath.startsWith("jar:file")){
            profile = ".conf/conf.properties";
        }else {
            profile = confPath;
        }
        return profile;
    }

    // 选择单个文件
    private void fileChose(TextField textField){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        if(textField.getText() != null){
            String dir = textField.getText().substring(0, textField.getText().lastIndexOf("\\"));
            fileChooser.setInitialDirectory(new File(dir));
        }
        fileChooser.setTitle("选择文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("可执行文件","*.exe")
        );
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            textField.setText(file.toString());
        }
    }

    // 选择单个文件夹
    private void dirChose(TextField textField){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择一个文件夹");
        if (textField.getText() != null){
            dirChooser.setInitialDirectory(new File(textField.getText()));
        }
        File file = dirChooser.showDialog(new Stage());
        if(file != null){
            textField.setText(file.toString());
        }
    }
}
