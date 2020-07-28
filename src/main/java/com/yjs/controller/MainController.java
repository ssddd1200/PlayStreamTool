package com.yjs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label stream;

    @FXML
    private Label config;

    @FXML
    private void handShowView1(MouseEvent e){
        stream.setStyle("-fx-background-color: #ddd;-fx-text-fill: #10dd0e");
        config.setStyle("-fx-background-color: #10dd0e;-fx-text-fill: #fff");
        loadFXML(getClass().getClassLoader().getResource("FX/Stream.fxml"));
    }

    @FXML
    private void handShowView2(MouseEvent e){
        stream.setStyle("-fx-background-color: #10dd0e;-fx-text-fill: #fff");
        config.setStyle("-fx-background-color: #ddd;-fx-text-fill: #10dd0e");
        loadFXML(getClass().getClassLoader().getResource("FX/Config.fxml"));
    }

    private void loadFXML(URL url){
        try {
            Pane pane = FXMLLoader.load(url);
            mainBorderPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
