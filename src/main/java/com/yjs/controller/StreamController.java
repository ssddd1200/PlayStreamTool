package com.yjs.controller;

import com.yjs.model.TableModel;
import com.yjs.msg.AlertBox;
import com.yjs.FFmpeg.FFmpegMsg;
import com.yjs.msg.StreamInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.*;
import java.util.Properties;

public class StreamController {

    private FFmpegMsg msg = new FFmpegMsg();
    private ObservableList<TableModel> tableData = FXCollections.observableArrayList();

    @FXML
    private TableView tableView;

    @FXML
    private TableColumn<TableModel, String> col1;

    @FXML
    private TableColumn<TableModel, String> col2;

    @FXML
    private TableColumn<TableModel, String> col3;

    @FXML
    private Button startServer;

    @FXML
    private Button stopServer;

    @FXML
    private void initialize(){
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().inputStream));
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().pro));
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().outStream));

        //绑定数据到TableView
        tableView.setItems(tableData);

        readData();

        if (isExistProcess("nginx.exe")){
            startServer.setDisable(true);
        }else{
            stopServer.setDisable(true);
        }
    }

    @FXML
    private void addInfo(){
        StreamInfo.display(this, -1, null);
    }

    @FXML
    private void editInfo(){
        int editIndex = tableView.getSelectionModel().getFocusedIndex();
        StreamInfo.display(this, editIndex, tableData.get(editIndex));
    }

    @FXML
    private void delInfo(){
        int delIndex = tableView.getSelectionModel().getFocusedIndex();
        tableData.remove(delIndex);
        rewriteFile();
    }

    @FXML
    private void startNginx(){
        String nginxAddr = getConfInfo("serverAdd");
        if(isExistProcess("nginx.exe")){
            AlertBox.display("消息", "nginx服务器已启动");
        }else{
            Process p;
            Runtime rt = Runtime.getRuntime();
            try {
                String[] cmd = {"cmd.exe","/C","start","nginx.exe"};
                p = rt.exec(cmd,null, new File(nginxAddr));
                startServer.setDisable(true);
                stopServer.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * nginx.exe -s stop
     */
    @FXML
    private void stopNginx(){
        killProc("nginx.exe");
        startServer.setDisable(false);
        stopServer.setDisable(true);
    }

    @FXML
    private void startPushStream(){
        int selectIndex = tableView.getSelectionModel().getFocusedIndex();
        TableModel tableModel = tableData.get(selectIndex);
        String ffmpeg = getConfInfo("ffmpegAdd");
        String[] cmd = null;
        if (tableModel.pro.equals("rtmp")){
            cmd = new String[]{ffmpeg, "-rtsp_transport", "tcp", "-i", tableModel.inputStream, "-f", "flv", "-r", "25", "-s", "640*480", "-an", tableModel.outStream};
        }else if (tableModel.pro.equals("hls")){
            cmd = new String[]{ffmpeg, "-rtsp_transport", "tcp", "-i", tableModel.inputStream, "-c", "copy", "-f", "hls", "-hls_time", "2.0", "-hls_list_size", "1", "-hls_warp", "15", tableModel.outStream};
        }

        msg.start(String.valueOf(selectIndex), cmd);
    }

    @FXML
    private void stopPushStream(){
        int selectIndex = tableView.getSelectionModel().getFocusedIndex();
        msg.stop(String.valueOf(selectIndex));
    }

    private void killProc(String processName){
        BufferedReader bfReader = null;
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        try {
            process = rt.exec("taskkill /F /IM "+processName);
            bfReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
            String line = null;
            StringBuilder build = new StringBuilder();
            while ((line = bfReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isExistProcess(String processName){
        BufferedReader bfReader = null;
        String[] cmd = {"tasklist", "-fi", "\"imagename eq "+processName+"\""};
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            bfReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = bfReader.readLine()) != null) {
                if (line.contains(processName)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bfReader != null) {
                try {
                    bfReader.close();
                } catch (Exception ex) {}
            }
        }
    }

    private String getConfInfo(String key){
        String result = "";
        String profile = "";
        Properties prop = new Properties();
        String confPath = getClass().getClassLoader().getResource("conf.properties").getPath();
        if (confPath.startsWith("jar:file")){
            profile = ".conf/conf.properties";
        }else {
            profile = confPath;
        }
        try {
            prop.load(new FileInputStream(profile));
            result = prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void rewriteFile(){
        String profile = System.getProperty("user.dir") + "/conf";
        File file = new File(profile);
        if (!file.exists()){
            file.mkdirs();
        }
        try {
            FileWriter writer = new FileWriter(profile+"/info.txt", false);
            for (int i = 0; i < tableData.size(); i++) {
                writer.write(tableData.get(i).toString()+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readData(){
        String profile = System.getProperty("user.dir") + "/conf";
        File file = new File(profile);
        BufferedReader br = null;
        if (!file.exists()){
            file.mkdirs();
        }
        try {
            br = new BufferedReader(new FileReader(profile+"/info.txt"));
            String line = "";
            while ((line = br.readLine()) != null){
                String[] data = line.split(";");
                tableData.addAll(new TableModel(data[0], data[1], data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setData(int index, TableModel tableModel) {
        if (index != -1){
            tableData.set(index, tableModel);
        }else{
            tableData.add(tableModel);
        }
        rewriteFile();
    }
}
