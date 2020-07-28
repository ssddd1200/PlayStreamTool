package com.yjs.msg;

import com.yjs.MainApp;
import com.yjs.controller.StreamController;
import com.yjs.model.TableModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Date;
import java.util.Properties;

public class StreamInfo {

    public static void display(StreamController controller, int index, TableModel tableModel){
        Stage window = new Stage();
        window.setTitle("视频流转化信息");

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(500.0);
        window.setMinHeight(300.0);

        Label label2 = new Label("输出流地址");
        label2.setPrefSize(100, 30);
        label2.setLayoutX(20);
        label2.setLayoutY(55);
        TextField textField1 = new TextField();
        textField1.setPrefSize(360, 30);
        textField1.setLayoutX(120);
        textField1.setLayoutY(55);
        Label msgLabel = new Label("rtmp协议：以rtmp://开头；hls协议：以http://开头，.m3u8结尾");
        msgLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 10px");
        msgLabel.setPrefSize(460, 20);
        msgLabel.setLayoutX(20);
        msgLabel.setLayoutY(85);
        Label label3 = new Label("输入流地址");
        label3.setPrefSize(100, 30);
        label3.setLayoutX(20);
        label3.setLayoutY(115);
        TextField textField2 = new TextField();
        textField2.setPrefSize(360, 30);
        textField2.setLayoutX(120);
        textField2.setLayoutY(115);
        textField2.setText("rtsp://");
        Label label1 = new Label("直播流协议");
        label1.setPrefSize(100, 30);
        label1.setLayoutX(20);
        label1.setLayoutY(20);
        ChoiceBox cbox = new ChoiceBox();
        cbox.getItems().addAll("rtmp","hls");
        cbox.getSelectionModel().selectFirst();
        cbox.setLayoutX(120);
        cbox.setLayoutY(20);
        cbox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (cbox.getItems().get((Integer) newValue).equals("rtmp")){
                    textField1.setText("rtmp://localhost:9080/live/{id}");
                }else if (cbox.getItems().get((Integer) newValue).equals("hls")){
                    textField1.setText("http://localhost:9080/hls/main.m3u8");
                }
            }
        });

        Button saveBtn = new Button("保存");
        saveBtn.setPrefSize(50,26);
        saveBtn.setOnAction(e -> {
            String outStream = textField1.getText();
            String inputStream = textField2.getText();
            String pro = cbox.getSelectionModel().getSelectedItem().toString();
//            StreamInfo.saveInfo(outStream+";"+inputStream+";"+pro);
            window.close();
            controller.setData(index, new TableModel(outStream, inputStream, pro));
        });

        Button closeBtn = new Button("关闭");
        closeBtn.setPrefSize(50,26);
        closeBtn.setOnAction(e -> window.close());

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5,0,5,0));
        hBox.getChildren().addAll(saveBtn, closeBtn);

        Pane pane = new Pane();
        pane.getChildren().addAll(label1, msgLabel, label2, label3, textField1, textField2, cbox);

        BorderPane layer = new BorderPane();
        layer.setCenter(pane);
        layer.setBottom(hBox);

        Scene scene = new Scene(layer);
        scene.getStylesheets().add(MainApp.class.getClassLoader().getResource("FX/application.css").toExternalForm());
        window.setScene(scene);
        if(tableModel != null){
            textField1.setText(tableModel.outStream);
            textField2.setText(tableModel.inputStream);
            cbox.getSelectionModel().select(tableModel.pro);
        }
        window.showAndWait();
    }

    private static void saveInfo(String info){
        String profile = System.getProperty("user.dir") + "/conf";
        File file = new File(profile);
        if (!file.exists()){
            file.mkdirs();
        }
        try {
            FileWriter writer = new FileWriter(profile+"/info.txt", true);
            writer.write(info+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
