package edu.data.generator.config;

public interface Config {
    String SOURCE_DIR = "./dataset/seeds/lot_2/darken"; //renamed/darken_Copy-hole_added
    String BACKGROUND_DIR = "./dataset/grain/bedstraw_land_validated";
    String BACKGROUND_BASE_XMLS_DIR = "./dataset/grain/bases_validated";
    String TARGET_DIR = "./dataset/output-test-bs_land_valid";
    String DATASET_DIR = "/VOCDataSet/";
    String IMAGES_DIR = "/JPEGImages/";
    String ANNOTATIONS_DIR = "/Annotations/";
    String DARKNET_LABELS = "/labels/";
    String DARKNET_CONFIG_DIR = "/DarknetConf/";
    String VOC_LABELS = "/VOCLabels/";
    Integer IMAGE_WIDTH = 2560; //1664;
    Integer IMAGE_HEIGHT = 1920;//1664;
    Integer ROWS = 2;
    Integer COLS = 3;
    Integer DATA_SET_SIZE = 40;
    Integer VAL_SET_SIZE = 10;
    Integer MARGIN_AROUND = 60; // 10px space around the playground
    Integer MARGIN_BETWEEN = 60; // 10px space between the cards
    Integer MAX_ANGLE_TO_ROTATE = 350;
    boolean BLUR = true;
    boolean DEBUG = false;
    boolean DARKNET = true; // Generates labels and train-val.txt for darknet
    String[] CLASS_LABELS = {"BS", "BSHole", "BSPair", "BSSingle"};
}
