package com.example.mychecker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    /**
     * 模板自带无用类
     */
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}