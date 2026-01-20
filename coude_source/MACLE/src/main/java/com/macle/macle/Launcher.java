package com.macle.macle;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        System.setProperty("glass.win.uiScale", "1.0");
        Application.launch(MacleApplication.class, args);

    }
}