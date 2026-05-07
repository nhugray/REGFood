package com.finalterm.regfood.features.foodrecognition.data.api;

public final class ServerConfig {
    private ServerConfig() {
    }

    public static final String BASE_URL = "http://192.168.1.31:5000/";
    public static final String RECOGNIZE_PATH = "api/recognize";
    public static final String PREDICT_PATH = "api/predict";
}
