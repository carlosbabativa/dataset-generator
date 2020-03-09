package edu.data.generator.config;

public interface Config {
    String SOURCE_DIR = "./dataset/seeds/renamed/darken_Copy";
    String BACKGROUND_DIR = "./dataset/grain/renamed/sub/corrected";
    String TARGET_DIR = "./dataset/output";
    String DATASET_DIR = "/VOCDataSet/";
    String IMAGES_DIR = "/JPEGImages/";
    String ANNOTATIONS_DIR = "/Annotations/";
    String DARKNET_LABELS = "/labels/";
    String DARKNET_CONFIG_DIR = "/DarknetConf/";
    String VOC_LABELS = "/VOCLabels/";
    Integer IMAGE_WIDTH = 2160; //1664;
    Integer IMAGE_HEIGHT = 2880;//1664;
    Integer ROWS = 5;
    Integer COLS = 4;
    Integer DATA_SET_SIZE = 400;
    Integer VAL_SET_SIZE = 80;
    Integer MARGIN_AROUND = 60; // 10px space around the playground
    Integer MARGIN_BETWEEN = 60; // 10px space between the cards
    Integer MAX_ANGLE_TO_ROTATE = 350;
    boolean BLUR = true;
    boolean DEBUG = false;
    boolean DARKNET = true; // Generates labels and train-val.txt for darknet
    String[] CLASS_LABELS = {"BS", "BSPair", "BSSingle"};
}
